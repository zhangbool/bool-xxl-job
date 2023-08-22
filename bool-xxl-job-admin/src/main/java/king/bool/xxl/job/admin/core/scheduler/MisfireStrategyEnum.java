package king.bool.xxl.job.admin.core.scheduler;

import king.bool.xxl.job.admin.core.util.I18nUtil;

/**
 * @author : 不二
 * @date : 2023/8/22-10:48
 * @desc :
 **/
public enum MisfireStrategyEnum {

    /**
     * do nothing
     * 调度过期策略: 直接舍弃
     */
    DO_NOTHING(I18nUtil.getString("misfire_strategy_do_nothing")),

    /**
     * fire once now
     * 调度过期策略: 执行一次fire once now
     */
    FIRE_ONCE_NOW(I18nUtil.getString("misfire_strategy_fire_once_now"));

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem){
        for (MisfireStrategyEnum item: MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }
}
