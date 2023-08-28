package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.core.exception.XxlJobException;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobInfo;
import king.bool.xxl.job.admin.core.model.XxlJobLog;
import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.dao.XxlJobGroupDao;
import king.bool.xxl.job.admin.dao.XxlJobInfoDao;
import king.bool.xxl.job.admin.dao.XxlJobLogDao;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/17-11:01
 * @desc : job日志
 **/
@Controller
@RequestMapping("/joblog")
@Slf4j
public class JobLogController {

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;

    // 这里如果是RequestMapping("/"), 则需要请求: /xxl-job-admin/joblog/
    // 如果没有("/"), 则需要请求:/xxl-job-admin/joblog
    // 是不一样的
    @RequestMapping
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(required = false, defaultValue = "0") Integer jobId) {
        // 执行器列表
        List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

        // filter group
        // 先从请求中拿出用户的角色, 然后根据角色进行过滤数据,
        // 如果是管理员角色, 全部
        // 如果是普通角色, 看下角色是否有相关JobGroup的权限
        // 但是这个是不是在sql里做更好, 这里好麻烦
        List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList==null || jobGroupList.size()==0) {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }

        // 把JobGroupList添加到模型中, 方便页面上用
        /*
        <#list JobGroupList as group>
            <option value="${group.id}" >${group.title}</option>
        </#list>
         */
        model.addAttribute("JobGroupList", jobGroupList);

        // 默认进入页面的时候jobId=0的
        // 任务
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }

            log.info("请求到jobInfo:" + JacksonUtil.writeValueAsString(jobInfo));
            model.addAttribute("jobInfo", jobInfo);

            // valid permission
            JobInfoController.validPermission(request, jobInfo.getJobGroup());
        }

        return "joblog/joblog.index";
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public ResultModel getJobsByGroup(int jobGroup){
        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ResultModel(ResultModel.SUCCESS_CODE, list);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int jobId, int logStatus, String filterTime) {

        // valid permission
        JobInfoController.validPermission(request, jobGroup);	// 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (filterTime!=null && filterTime.trim().length()>0) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }

        // page query
        List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表

        log.info("获取到的joglog数据是:" + JacksonUtil.writeValueAsString(maps));
        return maps;
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(int id, Model model){
        return "joblog/joblog.detail";
    }



}
