package king.bool.xxl.job.admin.service.imp;

import king.bool.xxl.job.admin.core.thread.JobCompleteHelper;
import king.bool.xxl.job.admin.core.thread.JobRegistryHelper;
import king.bool.xxl.job.core.biz.AdminBiz;
import king.bool.xxl.job.core.biz.model.HandleCallbackParam;
import king.bool.xxl.job.core.biz.model.RegistryParam;
import king.bool.xxl.job.core.biz.model.ResultModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/28-12:06
 * @desc : 给admin用的一个业务实现
 *         里面包含:
 *              executor注册到admin, admin调用该类进行业务处理
 **/
@Service
public class AdminBizImpl implements AdminBiz {

    @Override
    public ResultModel callback(List<HandleCallbackParam> callbackParamList) {
        return JobCompleteHelper.getInstance().callback(callbackParamList);
    }

    @Override
    public ResultModel registry(RegistryParam registryParam) {
        return JobRegistryHelper.getInstance().registry(registryParam);
    }

    @Override
    public ResultModel registryRemove(RegistryParam registryParam) {
        return JobRegistryHelper.getInstance().registryRemove(registryParam);
    }

}
