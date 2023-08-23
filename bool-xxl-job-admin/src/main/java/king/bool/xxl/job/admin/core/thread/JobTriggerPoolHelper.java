package king.bool.xxl.job.admin.core.thread;

import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.trigger.TriggerTypeEnum;
import king.bool.xxl.job.admin.core.trigger.XxlJobTrigger;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : 不二
 * @date : 2023/8/22-17:39
 * @desc :
 **/
@Slf4j
public class JobTriggerPoolHelper {

    // ---------------------- trigger pool ----------------------
    // fast/slow thread pool
    private ThreadPoolExecutor fastTriggerPool = null;
    private ThreadPoolExecutor slowTriggerPool = null;

    public void start() {
        // 这里创建一个线程池

        // 如果不活跃, 一个线程最多活跃1分钟然后kill掉
        // 核心线程10个, 如果有多余的任务, 先放进LinkedBlockingQueue里面, 可以存放2000个, 加起来最多是2010个
        // 如果还有更多的任务, 按照最大活跃线程活跃数量, 开启新的线程, 线程大于等于100, =triggerPoolSlowMax
        // 创建线程的方式参考: ThreadFactory里面的方法
        fastTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode());
                    }
                });

        // 如果不活跃, 一个线程最多活跃1分钟然后kill掉
        // 核心线程10个, 如果有多余的任务, 先放进LinkedBlockingQueue里面, 可以存放2000个, 加起来最多是2010个
        // 如果还有更多的任务, 按照最大活跃线程活跃数量, 开启新的线程, 线程大于等于100, =triggerPoolSlowMax
        // 创建线程的方式参考: ThreadFactory里面的方法
        slowTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode());
                    }
                });
    }

    public void stop() {
        //triggerPool.shutdown();
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
        log.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }


    // job timeout count
    // #todo: volatile的作用是啥来着???
    private volatile long minTim = System.currentTimeMillis()/60000;     // ms > min

    private volatile ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();

    /**
     * add trigger
     */
    public void addTrigger(final int jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {

        // choose thread pool
        ThreadPoolExecutor triggerPool_ = fastTriggerPool;

        // 每个job对应一个数据, 某个job请求过来, 取出这个job相关数据
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);

        // 如果在清空之前, 也就是在一分钟之内, 有超过10次超时, 则添加到慢任务的线程池中去
        // 如果一个job在1分钟內超时次数10次, 则认为是慢任务
        if (jobTimeoutCount!=null && jobTimeoutCount.get() > 10) {      // job-timeout 10 times in 1 min
            triggerPool_ = slowTriggerPool;
        }

        // trigger
        triggerPool_.execute(new Runnable() {

            @Override
            public void run() {
                // 注意: 这个是在子线程里面走的
                long start = System.currentTimeMillis();

                // 这里通过线程调用, 是因为如果耗时的话, 不使用线程池, 主线程会卡住
                try {
                    // do trigger
                    XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {

                    // check timeout-count-map
                    long minTim_now = System.currentTimeMillis()/60000;
                    // #todo: 这里是什么东西??? 为啥要判断两个不相等???
                    // 我大概知道是什么意思了, 每分钟清空一次
                    if (minTim != minTim_now) {
                        minTim = minTim_now;
                        // jobTimeoutCountMap是主线程创建的, 在子线程中操作,
                        // 所以会有多线程问题, 需要用ConcurrentHashMap
                        jobTimeoutCountMap.clear();
                    }

                    // 慢任务统计, 这里是在子线程中进行统计的
                    // incr timeout-count-map
                    long cost = System.currentTimeMillis()-start;
                    if (cost > 500) {       // ob-timeout threshold 500ms
                        AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                        if (timeoutCount != null) {
                            timeoutCount.incrementAndGet();
                        }
                    }
                }
            }
        });
    }



    // ---------------------- helper ----------------------
    // 这里是线程池
    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    public static void toStart() {
        helper.start();
    }
    public static void toStop() {
        helper.stop();
    }

    /**
     * @param jobId
     * @param triggerType
     * @param failRetryCount
     * 			>=0: use this param
     * 			<0: use param from job info config
     * @param executorShardingParam
     * @param executorParam
     *          null: use job param
     *          not null: cover job param
     */
    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        helper.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }

}
