package king.bool.xxl.job.admin.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-15:04
 * @desc :
 **/
@Data
public class XxlJobGroup {

    private int id;
    private String appname;
    private String title;
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private Date updateTime;

    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }
}
