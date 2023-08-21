package king.bool.xxl.job.admin.core.model;

import lombok.Data;

import java.util.Date;

/**
 * @author : 不二
 * @date : 2023/8/21-21:59
 * @desc :
 **/
@Data
public class XxlJobRegistry {
    private int id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;
}
