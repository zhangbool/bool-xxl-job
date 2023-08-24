package king.bool.xxl.job.core.biz.model;

import lombok.Data;

/**
 * @author : 不二
 * @date : 2023/8/24-13:52
 * @desc :
 **/
@Data
public class HandleCallbackParam {

    private static final long serialVersionUID = 42L;

    private long logId;
    private long logDateTim;

    private int handleCode;
    private String handleMsg;

    public HandleCallbackParam(){}

    public HandleCallbackParam(long logId, long logDateTim, int handleCode, String handleMsg) {
        this.logId = logId;
        this.logDateTim = logDateTim;
        this.handleCode = handleCode;
        this.handleMsg = handleMsg;
    }
}
