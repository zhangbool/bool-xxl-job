//package king.bool.xxl.job.admin.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import java.io.IOException;
//
///**
// * @author : 不二
// * @date : 2023/8/22-15:03
// * @desc :
// **/
//
//@Slf4j
//@Component
//@WebFilter(urlPatterns = "/*")
//// #todo: 这里好像并不能改变filter的顺序
//@Order(1000)
//public class TestFilter01 implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Filter.super.init(filterConfig);
//        log.info("---------TestFilter01---------init---------");
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        log.info("---------TestFilter01---------doFilter---------");
//        // FilterChain 参数是用来调用下一个过滤器或执行下一个流程
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    @Override
//    public void destroy() {
//        // Filter.super.destroy();
//        log.info("---------TestFilter01---------destroy---------");
//    }
//}
