package king.bool.xxl.job.core.biz.model;

/**
 * @author : 不二
 * @date : 2023/8/22-18:03
 * @desc :
 **/
public class IdleBeatParam {
    private static final long serialVersionUID = 42L;

    public IdleBeatParam() {
    }
    public IdleBeatParam(int jobId) {
        this.jobId = jobId;
    }

    private int jobId;


    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
