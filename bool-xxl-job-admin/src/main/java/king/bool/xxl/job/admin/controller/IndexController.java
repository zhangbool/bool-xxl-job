package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.admin.service.XxlJobService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/17-09:37
 * @desc : index页面
 **/
@Slf4j
@Controller
public class IndexController {

    @Resource
    private XxlJobService xxlJobService;

    @Autowired
    private LoginService loginService;

    @RequestMapping("/")
    @PermissionLimit(needLogin=true)
    // 这里的Model用的是org.springframework.ui.Model!!!
    public String index(Model model) {
        log.info("index-index");
        Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
        model.addAllAttributes(dashboardMap);
        return "index";
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    // #todo: 这里有问题, 为啥直接使用Date不行? 元代码是可以的, 我这里不行, 感觉是哪个拦截器里处理了
    //  todo: 为啥呢, 这里注解是我这边自行加的,
    public ResultModel chartInfo(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date startDate,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date endDate) {
//    public ResultModel chartInfo(String startDate, String endDate) {
        log.info("startDate是: " + startDate + ", endDate: " + endDate);
         ResultModel chartInfo = xxlJobService.chartInfo(startDate, endDate);
        log.info("查询的图标信息是: {}", JacksonUtil.writeValueAsString(chartInfo));

         return chartInfo;
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

    @RequestMapping(value="logout", method=RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(needLogin = false)
    public ResultModel logout(HttpServletRequest request, HttpServletResponse response){
        return loginService.logout(request, response);
    }

    @RequestMapping("/help")
    public String help() {
        return "help/help";
    }

}
