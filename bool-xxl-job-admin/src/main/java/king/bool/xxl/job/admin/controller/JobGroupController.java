package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobRegistry;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.dao.XxlJobGroupDao;
import king.bool.xxl.job.admin.dao.XxlJobInfoDao;
import king.bool.xxl.job.admin.dao.XxlJobRegistryDao;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.enums.RegistryConfig;
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
 * @date : 2023/8/21-14:45
 * @desc :
 **/
@Slf4j
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobGroupDao xxlJobGroupDao;

    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    @RequestMapping
    @PermissionLimit(needAdminUser = true)
    public String index(Model model) {
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String appname, String title) {

        // page query
        List<XxlJobGroup> list = xxlJobGroupDao.pageList(start, length, appname, title);
        int list_count = xxlJobGroupDao.pageListCount(start, length, appname, title);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @RequestMapping("/save")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel save(XxlJobGroup xxlJobGroup){

        // valid
        if (xxlJobGroup.getAppname()==null || xxlJobGroup.getAppname().trim().length()==0) {
            return new ResultModel(500, (I18nUtil.getString("system_please_input")+"AppName") );
        }
        if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
            return new ResultModel(500, I18nUtil.getString("jobgroup_field_appname_length") );
        }
        if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
            return new ResultModel(500, "AppName"+I18nUtil.getString("system_unvalid") );
        }
        if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
            return new ResultModel(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
        }
        if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
            return new ResultModel(500, I18nUtil.getString("jobgroup_field_title")+I18nUtil.getString("system_unvalid") );
        }

        if (xxlJobGroup.getAddressType()!=0) {
            if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
                return new ResultModel(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
            }
            // 这他妈判断是干啥的...
            if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
                return new ResultModel(500, I18nUtil.getString("jobgroup_field_registryList")+I18nUtil.getString("system_unvalid") );
            }

            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item: addresss) {
                if (item==null || item.trim().length()==0) {
                    return new ResultModel(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        log.info("获取执行组: " + JacksonUtil.writeValueAsString(xxlJobGroup));

        int ret = xxlJobGroupDao.save(xxlJobGroup);
        return (ret>0)? ResultModel.SUCCESS : ResultModel.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel update(XxlJobGroup xxlJobGroup){

        // valid
        if (xxlJobGroup.getAppname()==null || xxlJobGroup.getAppname().trim().length()==0) {
            return new ResultModel(500, (I18nUtil.getString("system_please_input")+"AppName") );
        }
        if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
            return new ResultModel(500, I18nUtil.getString("jobgroup_field_appname_length") );
        }
        if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
            return new ResultModel(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
        }


        if (xxlJobGroup.getAddressType() == 0) {
            // 0=自动注册
            // 如果是自动注册, 直接把注册表中的数据给拼接成对应的格式, 设置到JobGroup里面即可
            List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());

            String addressListStr = null;
            if (registryList!=null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item:registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length()-1);
            }
            xxlJobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            // 这里就是校验一下xxlJobGroup里面的AddressList是否合法
            if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
                return new ResultModel(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
            }
            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item: addresss) {
                if (item==null || item.trim().length()==0) {
                    return new ResultModel(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupDao.update(xxlJobGroup);
        return (ret > 0) ? ResultModel.SUCCESS : ResultModel.FAIL;
    }

    private List<String> findRegistryByAppName(String appnameParam){
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());

        log.info("获取到XxlJobRegistry列表是:" + JacksonUtil.writeValueAsString(list));
        if (list != null) {
            for (XxlJobRegistry item: list) {
                if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel remove(int id){

        // valid
        int count = xxlJobInfoDao.pageListCount(0, 10, id, -1,  null, null, null);
        if (count > 0) {
            return new ResultModel(500, I18nUtil.getString("jobgroup_del_limit_0") );
        }

        List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
        if (allList.size() == 1) {
            return new ResultModel(500, I18nUtil.getString("jobgroup_del_limit_1") );
        }

        // 执行器删除就是直接从表里面删了???
        int ret = xxlJobGroupDao.remove(id);
        return (ret > 0) ? ResultModel.SUCCESS : ResultModel.FAIL;
    }

    @RequestMapping("/loadById")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel loadById(int id){
        XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
        return jobGroup != null ? new ResultModel(ResultModel.SUCCESS_CODE, jobGroup) : new ResultModel(ResultModel.FAIL_CODE, null);
    }




}
