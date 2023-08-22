package king.bool.xxl.job.admin.core.route.strategy;

import king.bool.xxl.job.admin.core.route.ExecutorRouter;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : 不二
 * @date : 2023/8/22-08:44
 * @desc : 列表轮训机制
 *         #todo: 这里还没有搞懂
 *
 *
 **/
public class ExecutorRouteRound extends ExecutorRouter {

    // #todo: 这里用ConcurrentMap的目的是啥, 好像也不用分布式 不用多线程???
    private static ConcurrentMap<Integer, AtomicInteger> routeCountEachJob = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    private static int count(int jobId) {

        // 每个jobId有个自己的对应循环,
        // 比如说有两个jobId, job1, job2
        // map中就会存放两条数据: job1 = 0, job2 = 0
        // 然后每调用一次, 就会对对应对jobId的value+1
        // 比如说: job1调用, 则: job1 = 1
        //        job2调用, 则: job2 = 1
        // 然后通过这个数字除以对应执行期的数量, 从而使得每个执行器进行round遍历

        // 为啥要清空, 清空的不就重新来了吗
        // 清空的目的可能是重新打乱
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            // 这里有效时间是1d
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        AtomicInteger count = routeCountEachJob.get(jobId);
        if (count == null || count.get() > 1000000) {
            // 初始化时主动Random一次，缓解首次压力
            // 缓解啥压力???
            // 哦, 我大概知道了, 如果不random的话, 对于n个job, 肯定都会在第一台上执行, 对于头部服务器来说可能会有压力
            count = new AtomicInteger(new Random().nextInt(100));
        } else {
            // count++
            count.addAndGet(1);
        }
        routeCountEachJob.put(jobId, count);
        return count.get();
    }

    @Override
    public ResultModel route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(count(triggerParam.getJobId())%addressList.size());
        return new ResultModel(ResultModel.SUCCESS_CODE, address);
    }
}
