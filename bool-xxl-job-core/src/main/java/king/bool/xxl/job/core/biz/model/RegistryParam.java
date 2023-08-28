package king.bool.xxl.job.core.biz.model;

import lombok.Data;

/**
 * @author : 不二
 * @date : 2023/8/24-13:57
 * @desc :
 **/
@Data
public class RegistryParam {

    private static final long serialVersionUID = 42L;

    private String registryGroup;
    private String registryKey;
    private String registryValue;

    public RegistryParam(){}

    public RegistryParam(String registryGroup, String registryKey, String registryValue) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
    }

}
