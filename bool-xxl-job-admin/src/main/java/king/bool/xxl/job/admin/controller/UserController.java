package king.bool.xxl.job.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : 不二
 * @date : 2023/8/17-11:35
 * @desc : 用户管理
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping
    public String index(Model model) {

        // 执行器列表
//        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
//        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }




}
