package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/17-09:37
 * @desc : index页面
 **/
@Controller
@Slf4j
public class IndexController {

    @Autowired
    private LoginService loginService;

    @RequestMapping("/")
    @PermissionLimit(needLogin=true)
    // 这里的Model用的是org.springframework.ui.Model!!!
    public String index(Model model) {

        log.info("index-index");

        // Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
        Map<String, Object> dashboardMap = new HashMap<>();
        dashboardMap.put("name", "aaaaa");
        model.addAllAttributes(dashboardMap);
        return "index";
    }

    @RequestMapping("/toLogin")
    @PermissionLimit(needLogin=false)
    public ModelAndView toLogin(HttpServletRequest request,
                                HttpServletResponse response,
                                ModelAndView modelAndView) {
        log.info("toLogin-toLogin-toLogin-toLogin-toLogin");

        if (loginService.ifLogin(request, response) != null) {
            modelAndView.setView(new RedirectView("/",true,false));
            return modelAndView;
        }
        return new ModelAndView("login");
    }

    // 用户点击提交之后, 请求该接口获取返回数据
    // ResponseBody: 返回数据, 而非模型页面
    @RequestMapping(value="login", method= RequestMethod.POST)
    // 这里是返回数据, 而非页面或者页面模型
    @ResponseBody
    @PermissionLimit(needLogin = false)
    public ResultModel loginDo(HttpServletRequest request,
                               HttpServletResponse response,
                               String userName,
                               String password,
                               String ifRemember){
        boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember)) ? true : false;
        return loginService.login(request, response, userName, password, ifRem);
    }

}
