package king.bool.xxl.job.core.biz.client;

import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.model.*;
import king.bool.xxl.job.core.util.XxlJobRemotingUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : 不二
 * @date : 2023/8/18-08:43
 * @desc : 执行器业务客户端, 主要负责和executor进行通信
 *         执行器实现类
 *         根据服务器地址, 发送请求到指定的服务器地址上去
 *         用在admin中, 用来给executor进行交互
 **/
@Slf4j
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
        log.info("～～～～～～开始发送请求进行心跳检测～～～～～～");
        // 这里没有请求对象, requestObj是空串
        return XxlJobRemotingUtil.postBody(addressUrl + "beat", accessToken, timeout, "", String.class);
    }

    @Override
    public ResultModel idleBeat(IdleBeatParam idleBeatParam) {
        return XxlJobRemotingUtil.postBody(addressUrl+"idleBeat", accessToken, timeout, idleBeatParam, String.class);
    }

    @Override
    public ResultModel run(TriggerParam triggerParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "run", accessToken, timeout, triggerParam, String.class);
    }

    @Override
    public ResultModel kill(KillParam killParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "kill", accessToken, timeout, killParam, String.class);
    }

    @Override
    public ResultModel log(LogParam logParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "log", accessToken, timeout, logParam, LogResult.class);
    }
}
