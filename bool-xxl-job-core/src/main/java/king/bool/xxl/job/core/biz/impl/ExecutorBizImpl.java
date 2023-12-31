package king.bool.xxl.job.core.biz.impl;

import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.model.*;
import king.bool.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import king.bool.xxl.job.core.executor.XxlJobExecutor;
import king.bool.xxl.job.core.glue.GlueFactory;
import king.bool.xxl.job.core.glue.GlueTypeEnum;
import king.bool.xxl.job.core.handler.IJobHandler;
import king.bool.xxl.job.core.handler.ScriptJobHandler;
import king.bool.xxl.job.core.handler.impl.GlueJobHandler;
import king.bool.xxl.job.core.log.XxlJobFileAppender;
import king.bool.xxl.job.core.thread.JobThread;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author : 不二
 * @date : 2023/8/24-11:27
 * @desc : executor客户端的执行代码
 **/
@Slf4j
public class ExecutorBizImpl implements ExecutorBiz {

    @Override
    public ResultModel beat() {
        return ResultModel.SUCCESS;
    }

    @Override
    public ResultModel idleBeat(IdleBeatParam idleBeatParam) {

        // isRunningOrHasQueue
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = XxlJobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ResultModel(ResultModel.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ResultModel.SUCCESS;
    }

    @Override
    public ResultModel run(TriggerParam triggerParam) {

        log.info("----开始执行---run------");
        // load old：jobHandler + jobThread
        JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread!=null?jobThread.getHandler():null;
        String removeOldReason = null;

        // valid：jobHandler + jobThread
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        if (GlueTypeEnum.BEAN == glueTypeEnum) {

            // new jobhandler
            IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

            // valid old jobThread
            if (jobThread!=null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ResultModel(ResultModel.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {

            log.info("～～～～～～～为啥是GLUE_GROOVY???～～～～～～～");
            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                            && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                try {
                    // 这里报错
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    log.info("-------------------已获取到originJobHandler-------------------");
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return new ResultModel(ResultModel.FAIL_CODE, e.getMessage());
                }
            }
        } else if (glueTypeEnum!=null && glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return new ResultModel(ResultModel.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return new ResultModel(ResultModel.FAIL_CODE, "block strategy effect："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        ResultModel pushResult = jobThread.pushTriggerQueue(triggerParam);
        log.info("push data to queue结果是: ");

        return pushResult;
    }

    @Override
    public ResultModel kill(KillParam killParam) {
        // kill handlerThread, and create new one
        JobThread jobThread = XxlJobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            XxlJobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ResultModel.SUCCESS;
        }

        return new ResultModel(ResultModel.SUCCESS_CODE, "job thread already killed.");
    }

    @Override
    public ResultModel log(LogParam logParam) {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());

        LogResult logResult = XxlJobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return new ResultModel(ResultModel.SUCCESS_CODE, logResult);
    }

}
