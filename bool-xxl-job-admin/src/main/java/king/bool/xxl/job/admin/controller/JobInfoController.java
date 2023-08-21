package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.core.exception.XxlJobException;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.dao.XxlJobGroupDao;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-14:42
 * @desc :
 **/
@Controller
@RequestMapping("/jobinfo")
@Slf4j
public class JobInfoController {

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
//        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
//        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
//        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
//        model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// 调度类型
//        model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略


        // 执行器列表
        List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

        // filter group
        List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList==null || jobGroupList.size()==0) {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }

        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);


        return "jobinfo/jobinfo.index";
    }


    public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all){
        List<XxlJobGroup> jobGroupList = new ArrayList<>();
        if (jobGroupList_all!=null && jobGroupList_all.size()>0) {
            // 把请求中的用户信息拿出来, 这个是每次验证过密码后进行写入到请求中的
            XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

            // 是管理员
            if (loginUser.getRole() == 1) {
                jobGroupList = jobGroupList_all;
            } else {
                List<String> groupIdStrs = new ArrayList<>();
                if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
                    groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
                }
                for (XxlJobGroup groupItem:jobGroupList_all) {
                    if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
                        jobGroupList.add(groupItem);
                    }
                }
            }
        }
        return jobGroupList;
    }

    public static void validPermission(HttpServletRequest request, int jobGroup) {
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (!loginUser.validPermission(jobGroup)) {
            throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
        }
    }

}
