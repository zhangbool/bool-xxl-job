package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.core.model.Student;
import king.bool.xxl.job.core.biz.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/26-09:40
 * @desc :
 **/
@Slf4j
@Controller
public class FrontTestController {

    @RequestMapping("/getAllStudents")
    public String getAllStudents(Model model){

        Student student01 = new Student(1, "ivanl001", 10);
        Student student02 = new Student(2, "ivanl002", 20);
        Student student03 = new Student(3, "ivanl003", 30);
        List<Student> students = new ArrayList<>();
        students.add(student01);
        students.add(student02);
        students.add(student03);
        model.addAttribute("students", students);

        return "test";
    }


    @RequestMapping("/getAllStudentsData")
    @ResponseBody
    public Map<String, Object> getAllStudentsData(){

        Student student01 = new Student(1, "ivanl001", 10);
        Student student02 = new Student(2, "ivanl002", 20);
        Student student03 = new Student(3, "ivanl003", 30);
        List<Student> students = new ArrayList<>();
        students.add(student01);
        students.add(student02);
        students.add(student03);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", 11);		// 总记录数
//        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", students);  					// 分页列表
        return maps;
    }


}
