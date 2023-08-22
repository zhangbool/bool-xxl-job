package king.bool.xxl.job.admin.core.conf;

import king.bool.xxl.job.admin.core.scheduler.XxlJobScheduler;
import king.bool.xxl.job.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author : 不二
 * @date : 2023/8/17-21:54
 * @desc :
 **/
// todo: 继承这两个目的是啥
// 这个地方为啥要注入呢， 注入的还是在getAdminConfig方法前提前创建对象吗
@Component
public class XxlJobAdminConfig implements InitializingBean, DisposableBean {

    private static XxlJobAdminConfig adminConfig = null;
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    // ---------------------- XxlJobScheduler ----------------------
    private XxlJobScheduler xxlJobScheduler;

    // InitializingBean的方法重写，在bean的属性初始化后都会执行该方法
    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        // 创建XxlJobScheduler对象
        xxlJobScheduler = new XxlJobScheduler();
        xxlJobScheduler.init();
    }

    // DisposableBean的方法重写, 释放资源的方式 ，只有一个扩展方法destroy()
    @Override
    public void destroy() throws Exception {
        xxlJobScheduler.destroy();
    }


    // ---------------------- XxlJobScheduler ----------------------
    // conf
    @Value("${xxl.job.i18n}")
    private String i18n;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

//    @Value("${spring.mail.from}")
//    private String emailFrom;

    @Value("${xxl.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${xxl.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

//    @Value("${xxl.job.logretentiondays}")
//    private int logretentiondays;

    // dao, service

    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobLogReportDao xxlJobLogReportDao;
//    @Resource
//    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
//    @Resource
//    private JobAlarmer jobAlarmer;


    public String getI18n() {
        // i18n是配置文件中的语言配置， 如果我们的多语言中没有该语言, 则默认返回 zh_CN
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        // 如果有，就返回配置语言即可
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

//    public String getEmailFrom() {
//        return emailFrom;
//    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

//    public int getLogretentiondays() {
//        if (logretentiondays < 7) {
//            return -1;  // Limit greater than or equal to 7, otherwise close
//        }
//        return logretentiondays;
//    }

    public XxlJobLogDao getXxlJobLogDao() {
        return xxlJobLogDao;
    }

    public XxlJobInfoDao getXxlJobInfoDao() {
        return xxlJobInfoDao;
    }

    public XxlJobRegistryDao getXxlJobRegistryDao() {
        return xxlJobRegistryDao;
    }

    public XxlJobGroupDao getXxlJobGroupDao() {
        return xxlJobGroupDao;
    }

    public XxlJobLogReportDao getXxlJobLogReportDao() {
        return xxlJobLogReportDao;
    }

//    public JavaMailSender getMailSender() {
//        return mailSender;
//    }

    public DataSource getDataSource() {
        return dataSource;
    }

//    public JobAlarmer getJobAlarmer() {
//        return jobAlarmer;
//    }
}
