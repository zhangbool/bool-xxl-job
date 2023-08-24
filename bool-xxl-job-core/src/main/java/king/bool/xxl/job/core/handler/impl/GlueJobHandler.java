package king.bool.xxl.job.core.handler.impl;

import king.bool.xxl.job.core.context.XxlJobHelper;
import king.bool.xxl.job.core.handler.IJobHandler;

/**
 * @author : 不二
 * @date : 2023/8/24-14:24
 * @desc :
 **/
public class GlueJobHandler extends IJobHandler {

    private long glueUpdatetime;
    private IJobHandler jobHandler;
    public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
        this.jobHandler = jobHandler;
        this.glueUpdatetime = glueUpdatetime;
    }
    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {
        XxlJobHelper.log("----------- glue.version:"+ glueUpdatetime +" -----------");
        jobHandler.execute();
    }

    @Override
    public void init() throws Exception {
        this.jobHandler.init();
    }

    @Override
    public void destroy() throws Exception {
        this.jobHandler.destroy();
    }
}
