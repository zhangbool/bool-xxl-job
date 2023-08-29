package king.bool.xxl.job.admin.core.trigger;

import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobInfo;
import king.bool.xxl.job.admin.core.model.XxlJobLog;
import king.bool.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import king.bool.xxl.job.admin.core.scheduler.XxlJobScheduler;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.core.util.ThrowableUtil;
import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;
import king.bool.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import king.bool.xxl.job.core.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author : 不二
 * @date : 2023/8/22-17:49
 * @desc :
 **/
@Slf4j
public class XxlJobTrigger {

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount
     * 			>=0: use this param
     * 			<0: use param from job info config
     * @param executorShardingParam
     * @param executorParam
     *          null: use job param
     *          not null: cover job param
     * @param addressList
     *          null: use executor addressList
     *          not null: cover
     */
    public static void trigger(int jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorShardingParam,
                               String executorParam,
                               String addressList) {

        log.info(">>>>>>>>>>>>>>> 开始调用触发器 >>>>>>>>>>>>>>> ");

        // 在触发的时候, 会传入jobId, 拿到job后
        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }

        // 手动传入参数, 则优先使用传入参数而非数据库中参数
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }

        int finalFailRetryCount = failRetryCount>=0?failRetryCount:jobInfo.getExecutorFailRetryCount();
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(jobInfo.getJobGroup());

        // cover addressList
        if (addressList!=null && addressList.trim().length()>0) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        // #todo: 这里是做什么的?????先放着
        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam!=null){
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length==2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.valueOf(shardingArr[0]);
                shardingParam[1] = Integer.valueOf(shardingArr[1]);
            }
        }

        // 这里的processTrigger是关键
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST==ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
                && group.getRegistryList()!=null && !group.getRegistryList().isEmpty()
                && shardingParam==null) {
            log.info(">>>>>>>>>>>>>>> 准备开始调用processTrigger-01 >>>>>>>>>>>>>>> ");

            for (int i = 0; i < group.getRegistryList().size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            log.info(">>>>>>>>>>>>>>> 准备开始调用processTrigger-02 >>>>>>>>>>>>>>> ");

            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }

    }

    private static boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group                     job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index                     sharding index
     * @param total                     sharding index
     */
    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total){

        log.info(">>>>>>>>>>>>>>> processTrigger >>>>>>>>>>>>>>> ");

        // param
        // 如果没有找到对应的阻塞策略, 则使用序列化执行
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum
                .match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy

        // 执行期路由策略
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum
                .match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy

        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST==executorRouteStrategyEnum)?String.valueOf(index).concat("/").concat(String.valueOf(total)):null;

        // 1, 保存日志
        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        log.info(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);


        // 3、init address
        String address = null;
        ResultModel routeAddressResult = null;
        if (group.getRegistryList()!=null && !group.getRegistryList().isEmpty()) {

            log.info(">>>>>>>>>>>>>>> init address 01 >>>>>>>>>>>>>>> ");

            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                log.info("路由参数是: {}--{}",
                        JacksonUtil.writeValueAsString(triggerParam),
                        JacksonUtil.writeValueAsString(group.getRegistryList()));

                // route就是获取实际的策略得出的ResultModel, ResultModel的content应该是String类型
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ResultModel.SUCCESS_CODE) {
                    log.info("获取到的routeAddressResult是: " + JacksonUtil.writeValueAsString(routeAddressResult));
                    // #todo: 这里我content不是string
                    // 表面上是Obj, 实际上是String类型, 直接toString应该是没啥问题的
                    address = routeAddressResult.getContentString();
                    log.info("---------------------> 路由成功, 最后的结果是: {} <---------------------",  address);
                }
            }
        } else {
            log.info(">>>>>>>>>>>>>>> init address 02 >>>>>>>>>>>>>>> ");
            routeAddressResult = new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 4、trigger remote executor
        // 根据路由得到的地址, 开始触发远程的executor
        // 这里开始调度远程!!!!!!!
        ResultModel triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ResultModel(ResultModel.FAIL_CODE, null);
        }

        // 5、collection trigger info
        // 获取触发信息
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                .append( (group.getAddressType() == 0)?I18nUtil.getString("jobgroup_field_addressType_0"):I18nUtil.getString("jobgroup_field_addressType_1") );
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("("+shardingParam+")");
        }
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_run") +"<<<<<<<<<<< </span><br>")
                .append((routeAddressResult!=null&&routeAddressResult.getMsg()!=null)?routeAddressResult.getMsg()+"<br><br>":"")
                .append(triggerResult.getMsg()!=null?triggerResult.getMsg():"");

        log.info("triggerMsgSb是: " + JacksonUtil.writeValueAsString(triggerMsgSb));

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        //jobLog.setTriggerTime();
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);

        log.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     * @param triggerParam
     * @param address
     * @return
     */
    public static ResultModel runExecutor(TriggerParam triggerParam, String address){
        ResultModel runResult = null;
        try {
            log.info(">>>>>>>>>>>>>>> runExecutor >>>>>>>>>>>>>>> ");
            // 这里ExecutorBiz是一个接口, executorBiz实际类是什么呢: ExecutorBizClient, 错了, 不是ExecutorBizClient, 而是:ExecutorBizImpl
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);

            // 根据不同的触发条件, 根据指定的地址, 发送请求到对应的服务器的不同接口上去
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ResultModel(ResultModel.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        return runResult;
    }

}
