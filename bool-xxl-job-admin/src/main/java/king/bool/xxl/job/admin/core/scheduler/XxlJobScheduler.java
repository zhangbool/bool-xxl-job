package king.bool.xxl.job.admin.core.scheduler;

import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.util.I18nUtil;
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
//        JobTriggerPoolHelper.toStart();
//
//        // admin registry monitor run
//        JobRegistryHelper.getInstance().start();
//
//        // admin fail-monitor run
//        JobFailMonitorHelper.getInstance().start();
//
//        // admin lose-monitor run ( depend on JobTriggerPoolHelper )
//        JobCompleteHelper.getInstance().start();
//
//        // admin log report start
//        JobLogReportHelper.getInstance().start();
//
//        // start-schedule  ( depend on JobTriggerPoolHelper )
//        JobScheduleHelper.getInstance().start();

        log.info(">>>>>>>>> init xxl-job admin success.");
    }


    public void destroy() throws Exception {

        // stop-schedule
//        JobScheduleHelper.getInstance().toStop();
//
//        // admin log report stop
//        JobLogReportHelper.getInstance().toStop();
//
//        // admin lose-monitor stop
//        JobCompleteHelper.getInstance().toStop();
//
//        // admin fail-monitor stop
//        JobFailMonitorHelper.getInstance().toStop();
//
//        // admin registry stop
//        JobRegistryHelper.getInstance().toStop();
//
//        // admin trigger pool stop
//        JobTriggerPoolHelper.toStop();

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
    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        // valid
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
