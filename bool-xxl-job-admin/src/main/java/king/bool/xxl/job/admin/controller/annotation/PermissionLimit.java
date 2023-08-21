package king.bool.xxl.job.admin.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 不二
 * @date : 2023/8/17-16:20
 * @desc : 权限拦截
 **/
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionLimit {

    // 是否开启权限限制, 默认开启
    boolean needLogin() default true;

    // 是否是管理员, 默认否
    boolean needAdminUser() default false;

}
