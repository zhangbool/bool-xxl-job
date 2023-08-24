package king.bool.xxl.job.core.biz.model;

import lombok.Data;

/**
 * @author : 不二
 * @date : 2023/8/24-11:41
 * @desc :
 **/
@Data
public class LogResult {

    private static final long serialVersionUID = 42L;

    public LogResult() {
    }
    public LogResult(int fromLineNum, int toLineNum, String logContent, boolean isEnd) {
        this.fromLineNum = fromLineNum;
        this.toLineNum = toLineNum;
        this.logContent = logContent;
        this.isEnd = isEnd;
    }

    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;

}
