package king.bool.xxl.job.admin.core.route.strategy;

import king.bool.xxl.job.admin.core.route.ExecutorRouter;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/22-09:48
 * @desc :
 *       单个JOB对应的每个执行器，最久未使用的优先被选举
 *          a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 *          b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 **/
public class ExecutorRouteLRU extends ExecutorRouter {
    @Override
    public ResultModel route(TriggerParam triggerParam, List<String> addressList) {
        return null;
    }

}
