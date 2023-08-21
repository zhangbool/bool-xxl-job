package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : 不二
 * @date : 2023/8/21-14:45
 * @desc :
 **/
@Controller
@RequestMapping("/jobgroup")
@Slf4j
public class JobGroupController {
    @RequestMapping
    @PermissionLimit(needAdminUser = true)
    public String index(Model model) {
        return "jobgroup/jobgroup.index";
    }

}
