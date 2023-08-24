package king.bool.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 * @author : 不二
 * @date : 2023/8/24-14:20
 * @desc :
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * jobhandler name
     */
    String value();

    /**
     * init handler, invoked when JobThread init
     */
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    String destroy() default "";
}
