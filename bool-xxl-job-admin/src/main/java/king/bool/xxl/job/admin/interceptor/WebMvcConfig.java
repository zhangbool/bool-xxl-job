package king.bool.xxl.job.admin.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author : 不二
 * @date : 2023/8/18-09:55
 * @desc :
 *
 * 过滤器，拦截器拦截的是URL。AOP拦截的是类的元数据(包、类、方法名、参数等)。
 *
 * 过滤器并没有定义业务用于执行逻辑前、后等，仅仅是请求到达就执行。
 * 拦截器有三个方法，相对于过滤器更加细致，有被拦截逻辑执行前、后等。
 * AOP针对具体的代码，能够实现更加复杂的业务逻辑。
 *
 * 三者功能类似，但各有优势，从过滤器--》拦截器--》切面，拦截规则越来越细致。
 * 执行顺序依次是过滤器、拦截器、切面。
 *
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
    }
}
