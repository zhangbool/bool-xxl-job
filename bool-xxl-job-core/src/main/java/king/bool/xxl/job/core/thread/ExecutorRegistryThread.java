package king.bool.xxl.job.core.thread;

import king.bool.xxl.job.core.biz.AdminBiz;
import king.bool.xxl.job.core.biz.model.RegistryParam;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.enums.RegistryConfig;
import king.bool.xxl.job.core.executor.XxlJobExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author : 不二
 * @date : 2023/8/24-14:13
 * @desc :  执行器上的一个类
 *          执行器注册线程, 专门启动一个线程, 用于注册执行器
 **/
@Slf4j
public class ExecutorRegistryThread {

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();
    public static ExecutorRegistryThread getInstance(){
        return instance;
    }

    private Thread registryThread;
    private volatile boolean toStop = false;

    public void start(final String appname, final String address){

        // valid
        if (appname == null || appname.trim().length() == 0) {
            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appname is null.");
            return;
        }
        if (XxlJobExecutor.getAdminBizList() == null) {
            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
            return;
        }

        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // registry
                while (!toStop) {
                    try {
                        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
                        // 这里遍历主admin列表,
                        for (AdminBiz adminBiz: XxlJobExecutor.getAdminBizList()) {
                            log.info("遍历AdminBiz, 当前是: {}", adminBiz);
                            try {
                                // 根据配置的地址, 找到管理器进行执行器的app的注册, 写入到xxl_job_registry表中去
                                ResultModel registryResult = adminBiz.registry(registryParam);

                                if (registryResult != null && ResultModel.SUCCESS_CODE == registryResult.getCode()) {
                                    registryResult = ResultModel.SUCCESS;
                                    log.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                    break;
                                } else {
                                    log.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                }
                            } catch (Exception e) {
                                log.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    // 这个线程不会停止, 会一直循环, 每隔RegistryConfig.BEAT_TIMEOUT重新注册一次
                    try {
                        if (!toStop) {
                            TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                        }
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            log.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                        }
                    }
                }

                // 停止之后, 则需要删除对应的执行器
                // registry remove
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
                    for (AdminBiz adminBiz: XxlJobExecutor.getAdminBizList()) {
                        try {
                            ResultModel registryResult = adminBiz.registryRemove(registryParam);
                            if (registryResult!=null && ResultModel.SUCCESS_CODE == registryResult.getCode()) {
                                registryResult = ResultModel.SUCCESS;
                                log.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                break;
                            } else {
                                log.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            }
                        } catch (Exception e) {
                            if (!toStop) {
                                log.info(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}", registryParam, e);
                            }

                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info(">>>>>>>>>>> xxl-job, executor registry thread destroy.");

            }
        });
        registryThread.setDaemon(true);
        registryThread.setName("xxl-job, executor ExecutorRegistryThread");
        registryThread.start();
    }

    public void toStop() {
        toStop = true;

        // interrupt and wait
        if (registryThread != null) {
            registryThread.interrupt();
            try {
                registryThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

}
