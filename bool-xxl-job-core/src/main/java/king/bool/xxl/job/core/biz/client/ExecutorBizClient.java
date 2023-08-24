package king.bool.xxl.job.core.biz.client;

import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.model.*;
import king.bool.xxl.job.core.util.XxlJobRemotingUtil;

/**
 * @author : 不二
 * @date : 2023/8/18-08:43
 * @desc : 执行器实现类
 **/
public class ExecutorBizClient implements ExecutorBiz {

    // 空参构造器
    public ExecutorBizClient() {
    }

    // 有参构造器
    public ExecutorBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;


    @Override
    public ResultModel beat() {
        // 这里没有请求对象, requestObj是空串
        return XxlJobRemotingUtil.postBody(addressUrl + "beat", accessToken, timeout, "", String.class);
    }

    @Override
    public ResultModel idleBeat(IdleBeatParam idleBeatParam) {
        return null;
    }

    @Override
    public ResultModel run(TriggerParam triggerParam) {
        return null;
    }

    @Override
    public ResultModel kill(KillParam killParam) {
        return null;
    }

    @Override
    public ResultModel log(LogParam logParam) {
        return null;
    }
}
