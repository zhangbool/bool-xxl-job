package king.bool.xxl.job.admin.controller.interceptor;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : 不二
 * @date : 2023/8/17-17:02
 * @desc : 权限拦截
 **/
// todo: AsyncHandlerInterceptor这个原理是啥
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

    @Resource
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // return false;
        // 这里只拦截方法
        if (!(handler instanceof HandlerMethod)) {
            return true;	// proceed with the next interceptor
        }

        // if we need login
        boolean needLogin = true;
        boolean needAdminUser = false;

        // 看一下被拦截的方法是否有注解, 如果有注解， 就按照注解方式看下是否需要登陆和管理员权限
        HandlerMethod method = (HandlerMethod)handler;
        // 获取方法的注解
        PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
        if (permission != null) {
            needLogin = permission.needLogin();
            needAdminUser = permission.needAdminUser();
        }

        // 不管有没有注解，下面这里都会进行校验哈
        if (needLogin) {
            // 请求的cookie中是否有相关用户数据, 如果有重新验证密码, 如果正确, 则通过, 否则返回null
            // 是不是每个请求都需要验证用户密码??????
            XxlJobUser loginUser = loginService.ifLogin(request, response);
            if (loginUser == null) {
                // 重定向
                response.setStatus(302);
                response.setHeader("location", request.getContextPath()+"/toLogin");
                return false;
            }
            // 如果用户已经登陆, 则判断是否权限足够
            if (needAdminUser && loginUser.getRole()!=1) {
                throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
            }
            request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);
        }

        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }
}
