package king.bool.xxl.job.admin.core.thread;

import king.bool.xxl.job.admin.core.complete.XxlJobCompleter;
import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.model.XxlJobLog;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.core.biz.model.HandleCallbackParam;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author : 不二
 * @date : 2023/8/28-12:10
 * @desc :
 **/
@Slf4j
public class JobCompleteHelper {
    private static JobCompleteHelper instance = new JobCompleteHelper();
    public static JobCompleteHelper getInstance(){
        return instance;
    }

    // ---------------------- monitor ----------------------

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;
    public void start(){

        // for callback
        callbackThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        log.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                    }
                });


        // for monitor
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // wait for JobTriggerPoolHelper-init
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }

                // monitor
                while (!toStop) {
                    try {
                        // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                        Date losedTime = DateUtil.addMinutes(new Date(), -10);
                        List<Long> losedJobIds  = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findLostJobIds(losedTime);

                        if (losedJobIds!=null && losedJobIds.size()>0) {
                            for (Long logId: losedJobIds) {

                                XxlJobLog jobLog = new XxlJobLog();
                                jobLog.setId(logId);

                                jobLog.setHandleTime(new Date());
                                jobLog.setHandleCode(ResultModel.FAIL_CODE);
                                jobLog.setHandleMsg( I18nUtil.getString("joblog_lost_fail") );

                                XxlJobCompleter.updateHandleInfoAndFinish(jobLog);
                            }

                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }

                log.info(">>>>>>>>>>> xxl-job, JobLosedMonitorHelper stop");

            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobLosedMonitorHelper");
        monitorThread.start();
    }

    public void toStop(){
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public ResultModel callback(List<HandleCallbackParam> callbackParamList) {

        callbackThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (HandleCallbackParam handleCallbackParam: callbackParamList) {
                    ResultModel callbackResult = callback(handleCallbackParam);
                    log.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                            (callbackResult.getCode()== ResultModel.SUCCESS_CODE?"success":"fail"), handleCallbackParam, callbackResult);
                }
            }
        });

        return ResultModel.SUCCESS;
    }

    private ResultModel callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ResultModel(ResultModel.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ResultModel(ResultModel.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg()!=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        XxlJobCompleter.updateHandleInfoAndFinish(log);

        return ResultModel.SUCCESS;
    }
}
