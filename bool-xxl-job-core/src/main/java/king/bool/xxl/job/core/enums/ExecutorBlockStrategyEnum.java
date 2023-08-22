package king.bool.xxl.job.core.enums;

// 每个枚举都是通过 Class 在内部实现的，且所有的枚举值都是 public static final 的。
public enum ExecutorBlockStrategyEnum {

    // 序列化执行
    SERIAL_EXECUTION("Serial execution"),

    // #todo: 丢弃后续调度???
    /*CONCURRENT_EXECUTION("并行"),*/
    DISCARD_LATER("Discard Later"),

    // #todo: 覆盖之前调度???
    COVER_EARLY("Cover Early");

    private String title;

    private ExecutorBlockStrategyEnum (String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        // 如果能找到name， 就用name这个枚举
        if (name != null) {
            // 这个就是迭代枚举元素
            for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        // 找不到name， 就用默认的这个
        return defaultItem;
    }
}
