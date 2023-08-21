package king.bool.xxl.job.admin.core.route;

import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-23:14
 * @desc :
 **/
@Slf4j
public abstract class ExecutorRouter {
    /**
     * route address
     *
     * @param addressList
     * @return ReturnT.content=address
     */
    public abstract ResultModel route(TriggerParam triggerParam, List<String> addressList);

}
