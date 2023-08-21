package king.bool.xxl.job.admin.core.model;

import lombok.Data;

import java.util.Date;

/**
 * @author : 不二
 * @date : 2023/8/21-17:12
 * @desc :
 **/
@Data
public class XxlJobLog {
    private long id;

    // job info
    private int jobGroup;
    private int jobId;

    // execute info
    private String executorAddress;
    private String executorHandler;
    private String executorParam;
    private String executorShardingParam;
    private int executorFailRetryCount;

    // trigger info
    private Date triggerTime;
    private int triggerCode;
    private String triggerMsg;

    // handle info
    private Date handleTime;
    private int handleCode;
    private String handleMsg;

    // alarm info
    private int alarmStatus;
}
