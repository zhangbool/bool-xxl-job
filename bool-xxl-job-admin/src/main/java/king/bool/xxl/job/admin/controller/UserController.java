package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.dao.XxlJobGroupDao;
import king.bool.xxl.job.admin.dao.XxlJobUserDao;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/17-11:35
 * @desc : 用户管理
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private XxlJobUserDao xxlJobUserDao;

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    public String index(Model model) {
        // 执行器列表
        // 获取有哪些执行器, 方便给用户赋予权限. 比如说有a/b/c三个执行期, 用户1只能操作a执行器这样
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);
        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username, int role) {
        // page list
        List<XxlJobUser> list = xxlJobUserDao.pageList(start, length, username, role);
        int list_count = xxlJobUserDao.pageListCount(start, length, username, role);

        // filter
        if (list!=null && list.size()>0) {
            for (XxlJobUser item: list) {
                // 密码脱敏
                item.setPassword(null);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }


    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel add(XxlJobUser xxlJobUser) {

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // md5 password
        xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));

        // check repeat
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("user_username_repeat") );
        }

        // write
        xxlJobUserDao.save(xxlJobUser);
        return ResultModel.SUCCESS;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel update(HttpServletRequest request, XxlJobUser xxlJobUser) {

        // avoid opt login seft
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getUsername().equals(xxlJobUser.getUsername())) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("user_update_loginuser_limit"));
        }

        // valid password
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
                return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
            }
            // md5 password
            xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));
        } else {
            xxlJobUser.setPassword(null);
        }

        // write
        xxlJobUserDao.update(xxlJobUser);
        return ResultModel.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(needAdminUser = true)
    public ResultModel remove(HttpServletRequest request, int id) {

        // avoid opt login seft
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getId() == id) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("user_update_loginuser_limit"));
        }

        xxlJobUserDao.delete(id);
        return ResultModel.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ResultModel updatePwd(HttpServletRequest request, String password){

        // valid password
        if (password==null || password.trim().length()==0){
            return new ResultModel(ResultModel.FAIL_CODE, "密码不可为空");
        }
        password = password.trim();
        if (!(password.length()>=4 && password.length()<=20)) {
            return new ResultModel(ResultModel.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        // md5 password
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // update pwd
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

        // do write
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(loginUser.getUsername());
        existUser.setPassword(md5Password);
        xxlJobUserDao.update(existUser);

        return ResultModel.SUCCESS;
    }




}
