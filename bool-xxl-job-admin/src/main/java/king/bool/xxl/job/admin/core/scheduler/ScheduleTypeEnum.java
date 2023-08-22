package king.bool.xxl.job.admin.core.scheduler;

import king.bool.xxl.job.admin.core.util.I18nUtil;

/**
 * @author : 不二
 * @date : 2023/8/22-10:43
 * @desc :
 **/
public enum ScheduleTypeEnum {

    // 无调度
    NONE(I18nUtil.getString("schedule_type_none")),

    /**
     * schedule by cron
     * 通过定时cron来进行调度
     */
    CRON(I18nUtil.getString("schedule_type_cron")),

    /**
     * schedule by fixed rate (in seconds)
     * 通过固定时间间隔进行调度
     */
    FIX_RATE(I18nUtil.getString("schedule_type_fix_rate")),

    /**
     * schedule by fix delay (in seconds)， after the last time
     */
    /*FIX_DELAY(I18nUtil.getString("schedule_type_fix_delay"))*/;

    private String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    // #todo: 这里的defaultItem是不是有点多余, defaultItem不是应该必然是ScheduleTypeEnum.values()里面的其中一个吗
    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem){
        for (ScheduleTypeEnum item: ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
