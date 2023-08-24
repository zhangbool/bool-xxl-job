package king.bool.xxl.job.core.biz;


import king.bool.xxl.job.core.biz.model.*;

/**
 * Created by xuxueli on 17/3/1.
 */
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
