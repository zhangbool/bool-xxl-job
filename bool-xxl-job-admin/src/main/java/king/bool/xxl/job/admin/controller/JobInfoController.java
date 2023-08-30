package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.core.exception.XxlJobException;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobInfo;
import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import king.bool.xxl.job.admin.core.scheduler.MisfireStrategyEnum;
import king.bool.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import king.bool.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import king.bool.xxl.job.admin.core.trigger.TriggerTypeEnum;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.dao.XxlJobGroupDao;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.admin.service.XxlJobService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import king.bool.xxl.job.core.glue.GlueTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								    // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
        model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				    // 调度类型
        model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略

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

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

        return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }


    @RequestMapping("/add")
    @ResponseBody
    public ResultModel add(XxlJobInfo jobInfo) {
        log.info("接受到的模型是: {}", JacksonUtil.writeValueAsString(jobInfo));

        return xxlJobService.add(jobInfo);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResultModel update(XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ResultModel remove(int id) {
        return xxlJobService.remove(id);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ResultModel pause(int id) {
        return xxlJobService.stop(id);
    }

    @RequestMapping("/start")
    @ResponseBody
    public ResultModel start(int id) {
        return xxlJobService.start(id);
    }


    // 这里是触发器, 如果触发, 则可能会有:
    //     执行任务触发器, 这个时候会和集群执行器进行交互
    @RequestMapping("/trigger")
    @ResponseBody
    //@PermissionLimit(limit = false)
    public ResultModel triggerJob(int id, String executorParam, String addressList) {

        log.info("开始调用trigger函数: {}, {}, {}", id, executorParam, addressList);

        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        // #todo: 这里先不处理
        // 这里是执行job,包括: 执行一次, 查询日志, 注册节点, 下次执行时间等....
        // 这里是手动触发, 这里可以传入参数, addressList地址如果传入, 会进行判断, 如果没有, 则使用默认的数据库中记录的job地址
        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ResultModel.SUCCESS;
    }

    @RequestMapping("/nextTriggerTime")
    @ResponseBody
    public ResultModel nextTriggerTime(String scheduleType, String scheduleConf) {

        XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
        paramXxlJobInfo.setScheduleType(scheduleType);
        paramXxlJobInfo.setScheduleConf(scheduleConf);

        List<String> result = new ArrayList<>();

        // #todo: 这里先不处理
        /*try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {

                lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResultModel(ResultModel.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
        }*/

        return new ResultModel(ResultModel.SUCCESS_CODE, result);

    }

}
