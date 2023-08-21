package king.bool.xxl.job.admin.service;

import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.core.biz.model.ResultModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : 不二
 * @date : 2023/8/17-17:56
 * @desc :
 **/
public interface LoginService {

    public static final String LOGIN_IDENTITY_KEY = "XXL_JOB_LOGIN_IDENTITY";

    public ResultModel login(HttpServletRequest request,
                             HttpServletResponse response,
                             String username,
                             String password,
                             boolean ifRemember);

    public ResultModel logout(HttpServletRequest request, HttpServletResponse response);

    public XxlJobUser ifLogin(HttpServletRequest request, HttpServletResponse response);


}
