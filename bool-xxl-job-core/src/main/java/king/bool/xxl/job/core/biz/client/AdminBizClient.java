package king.bool.xxl.job.core.biz.client;

import king.bool.xxl.job.core.biz.AdminBiz;
import king.bool.xxl.job.core.biz.model.HandleCallbackParam;
import king.bool.xxl.job.core.biz.model.RegistryParam;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.util.XxlJobRemotingUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/24-14:06
 * @desc : 管理员业务客户端
 *         主要负责:
 *          executor中用
 **/
@Slf4j
public class AdminBizClient implements AdminBiz {

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;

    public AdminBizClient() {
    }

    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    @Override
    public ResultModel callback(List<HandleCallbackParam> callbackParamList) {
        return XxlJobRemotingUtil.postBody(addressUrl+"api/callback", accessToken, timeout, callbackParamList, String.class);
    }

    @Override
    public ResultModel registry(RegistryParam registryParam) {
        log.info("需要连接注册的地址是: {}, 参数是:{}", addressUrl, registryParam);
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registry", accessToken, timeout, registryParam, String.class);
    }

    @Override
    public ResultModel registryRemove(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove", accessToken, timeout, registryParam, String.class);
    }
}
