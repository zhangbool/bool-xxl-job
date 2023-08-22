package king.bool.xxl.job.admin.core.route.strategy;

import king.bool.xxl.job.admin.core.route.ExecutorRouter;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/22-08:33
 * @desc : 列表第一个
 **/
public class ExecutorRouteFirst extends ExecutorRouter {
    @Override
    public ResultModel route(TriggerParam triggerParam, List<String> addressList) {
        return new ResultModel(ResultModel.SUCCESS_CODE, addressList.get(0));
    }
}
