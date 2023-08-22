package king.bool.xxl.job.admin.filter;

import king.bool.xxl.job.admin.core.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author : 不二
 * @date : 2023/8/22-15:03
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

@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
// #todo: 这里好像并不能改变filter的顺序
@Order(1000000)
public class TestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter.super.init(filterConfig);
        log.info("---------TestFilter---------init---------");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("---------TestFilter---------doFilter---------");
        // FilterChain 参数是用来调用下一个过滤器或执行下一个流程
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Filter.super.destroy();
        log.info("---------destroy---------destroy---------");
    }
}
