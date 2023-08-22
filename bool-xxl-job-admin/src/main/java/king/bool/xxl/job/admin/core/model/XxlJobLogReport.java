package king.bool.xxl.job.admin.core.model;

import lombok.Data;

import java.util.Date;

/**
 * @author : 不二
 * @date : 2023/8/22-12:19
 * @desc :
 **/
@Data
public class XxlJobLogReport {
    private int id;
    private Date triggerDay;
    private int runningCount;
    private int sucCount;
    private int failCount;
}
