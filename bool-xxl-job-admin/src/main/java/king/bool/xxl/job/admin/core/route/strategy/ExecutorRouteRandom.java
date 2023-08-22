package king.bool.xxl.job.admin.core.route.strategy;

import king.bool.xxl.job.admin.core.route.ExecutorRouter;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Random;

/**
 * @author : 不二
 * @date : 2023/8/22-09:28
 * @desc : 列表随机机制
 **/
public class ExecutorRouteRandom extends ExecutorRouter {

    private static Random localRandom = new Random();

    @Override
    public ResultModel route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(localRandom.nextInt(addressList.size()));
        return new ResultModel(ResultModel.SUCCESS_CODE, address);
    }

}
