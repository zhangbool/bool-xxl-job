package king.bool.xxl.job.core.enums;

/**
 * @author : 不二
 * @date : 2023/8/24-13:54
 * @desc :
 **/
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    // #todo: 这是什么写法
    public enum RegistType{ EXECUTOR, ADMIN }
}
