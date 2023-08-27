package king.bool.xxl.job.executor.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : 不二
 * @date : 2023/8/25-09:24
 * @desc :
 **/
@Controller
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    String index() {
        return "xxl job executor running.";
    }

}
