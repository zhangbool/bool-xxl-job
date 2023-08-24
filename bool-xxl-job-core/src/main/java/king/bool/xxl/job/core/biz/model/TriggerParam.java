package king.bool.xxl.job.core.biz.model;

import lombok.Data;

/**
 * @author : 不二
 * @date : 2023/8/21-23:15
 * @desc :
 **/
@Data
public class TriggerParam {

    private static final long serialVersionUID = 42L;

    private int jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private long logId;
    private long logDateTime;

    private String glueType;
    private String glueSource;
    private long glueUpdatetime;

    private int broadcastIndex;
    private int broadcastTotal;
}
