package king.bool.xxl.job.core.config;

/**
 * @author : 不二
 * @date : 2023/8/21-22:03
 * @desc :
 **/
public class RegistryConfig {
    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    public enum RegistType{ EXECUTOR, ADMIN }

}
