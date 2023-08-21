package king.bool.xxl.job.core.biz.client;

import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.model.ResultModel;

/**
 * @author : 不二
 * @date : 2023/8/18-08:43
 * @desc :
 **/
public class ExecutorBizClient implements ExecutorBiz {

    public ExecutorBizClient() {
    }

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
        return null;
    }

    /*@Override
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
    }*/
}
