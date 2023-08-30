package king.bool.xxl.job.core.biz;


import king.bool.xxl.job.core.biz.model.*;

/**
 * @author : 不二
 * @date : 2023/8/24-13:56
 * @desc : 业务逻辑代码, 有两个实现类:
 *
 *          ExecutorBizImpl:   admin中用
 *          ExecutorBizClient: 客户端中用
 *
 *
 **/
public interface ExecutorBiz {

    /**
     * beat 这个是心跳程序吗
     * @return
     */
    public ResultModel beat();

    /**
     * idle beat
     *
     * @param idleBeatParam
     * @return
     */
    public ResultModel idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ResultModel run(TriggerParam triggerParam);

    /**
     * kill
     * @param killParam
     * @return
     */
    public ResultModel kill(KillParam killParam);

    /**
     * log
     * @param logParam
     * @return
     */
    public ResultModel log(LogParam logParam);

}
