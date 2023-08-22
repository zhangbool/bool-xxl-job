package king.bool.xxl.job.core.biz.model;

/**
 * @author : 不二
 * @date : 2023/8/22-18:04
 * @desc :
 **/
public class LogParam {

    private static final long serialVersionUID = 42L;

    public LogParam() {
    }
    public LogParam(long logDateTim, long logId, int fromLineNum) {
        this.logDateTim = logDateTim;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    private long logDateTim;
    private long logId;
    private int fromLineNum;

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

}
