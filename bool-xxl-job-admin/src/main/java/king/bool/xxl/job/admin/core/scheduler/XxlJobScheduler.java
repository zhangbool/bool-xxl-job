package king.bool.xxl.job.admin.core.scheduler;

import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.thread.JobRegistryHelper;
import king.bool.xxl.job.admin.core.thread.JobScheduleHelper;
import king.bool.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.client.ExecutorBizClient;
import king.bool.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : 不二
 * @date : 2023/8/17-21:57
 * @desc :
 **/
@Slf4j
public class XxlJobScheduler {

    public void init() throws Exception {

        // init i18n
        initI18n();

        // admin trigger pool start
        JobTriggerPoolHelper.toStart();

        // admin registry monitor run
        JobRegistryHelper.getInstance().start();

        // admin fail-monitor run
//        JobFailMonitorHelper.getInstance().start();

        // admin lose-monitor run ( depend on JobTriggerPoolHelper )
//        JobCompleteHelper.getInstance().start();

        // admin log report start
//        JobLogReportHelper.getInstance().start();


        // #todo:01: 这个类只是启动和关闭, 有啥用
        // start-schedule  ( depend on JobTriggerPoolHelper )
        JobScheduleHelper.getInstance().start();

        log.info(">>>>>>>>> init xxl-job admin success.");
    }


    public void destroy() throws Exception {

        // stop-schedule
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
//        JobLogReportHelper.getInstance().toStop();

        // admin lose-monitor stop
//        JobCompleteHelper.getInstance().toStop();

        // admin fail-monitor stop
//        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
//        JobRegistryHelper.getInstance().toStop();

        // admin trigger pool stop
        JobTriggerPoolHelper.toStop();

    }

    // ---------------------- I18n ----------------------
    private void initI18n(){
        // 这个就是迭代枚举元素
        for (ExecutorBlockStrategyEnum item: ExecutorBlockStrategyEnum.values()) {
            // log.info("结果是: " + "jobconf_block_".concat(item.name()));
            // 这里name拿的就是SERIAL_EXECUTION， Serial execution是title
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }


    // ---------------------- executor-client ----------------------
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    public static ExecutorBiz getExecutorBiz(String address) {
        // valid
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // 如果之前已经用过, 放入了map里面, 就直接从里面获取
        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // 如果从内存中没有获取到, 说明是第一次使用
        // set-cache
        // #todo: token是干啥的
        executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());
        // 创建好对象, 放入内存map中
        executorBizRepository.put(address, executorBiz);

        // #todo: 这里是null
        log.info("获取executorBiz: {}", JacksonUtil.writeValueAsString(executorBiz));
        return executorBiz;
    }

}
