package king.bool.xxl.job.core.enums;

/**
 * @author : 不二
 * @date : 2023/8/24-13:54
 * @desc : 这里是注册配置, BEAT_TIMEOUT是每30s会去像主admin进行注册一次
 *         如果超过3次注册失败, 则认为该executor节点已经dead
 **/
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    // #todo: 这是什么写法
    // 就是一个最简单的枚举类
    public enum RegistType{ EXECUTOR, ADMIN }
}
