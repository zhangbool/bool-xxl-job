package king.bool.xxl.job.admin.interceptor;
import king.bool.xxl.job.admin.core.util.FtlUtil;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


/**
 * @author : 不二
 * @date : 2023/8/18-09:39
 * @desc : push cookies to model as cookieMap //todo: 这里的cookie指的啥
 *         这里把I18nUtil返回
 **/
@Slf4j
@Component
public class CookieInterceptor implements AsyncHandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        // cookie
        // 设置页面是否
        if (modelAndView!=null && request.getCookies()!=null && request.getCookies().length>0) {
            HashMap<String, Cookie> cookieMap = new HashMap<String, Cookie>();
            for (Cookie ck : request.getCookies()) {
                cookieMap.put(ck.getName(), ck);
            }
            modelAndView.addObject("cookieMap", cookieMap);
        }

        // static method
        if (modelAndView != null) {
            modelAndView.addObject("I18nUtil", FtlUtil.generateStaticModel(I18nUtil.class.getName()));
        }
    }
}
