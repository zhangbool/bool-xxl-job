package king.bool.xxl.job.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : 不二
 * @date : 2023/8/17-11:01
 * @desc : job日志
 **/
@Controller
@RequestMapping("/joblog")
public class JobLogController {

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(required = false, defaultValue = "0") Integer jobId) {



        return "joblog/joblog.index";
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(int id, Model model){

        return "joblog/joblog.detail";
    }


}
