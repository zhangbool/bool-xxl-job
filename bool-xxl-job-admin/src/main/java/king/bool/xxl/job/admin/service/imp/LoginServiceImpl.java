package king.bool.xxl.job.admin.service.imp;

import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.CookieUtil;
import king.bool.xxl.job.admin.core.util.I18nUtil;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.admin.dao.XxlJobUserDao;
import king.bool.xxl.job.admin.service.LoginService;
import king.bool.xxl.job.core.biz.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author : 不二
 * @date : 2023/8/17-18:14
 * @desc :
 **/
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Resource
    private XxlJobUserDao xxlJobUserDao;

    private String makeToken(XxlJobUser xxlJobUser){

        String tokenJson = JacksonUtil.writeValueAsString(xxlJobUser);
        log.info("tokenJson: " + tokenJson);
        final BigInteger bigInteger = new BigInteger(tokenJson.getBytes());
        // log.info("bigInteger是：" + bigInteger);

        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        // log.info("tokenHex: " + tokenHex);

        return tokenHex;
    }

    @Override
    public ResultModel login(HttpServletRequest request,
                             HttpServletResponse response,
                             String username,
                             String password,
                             boolean ifRemember) {
        ResultModel resultModel = new ResultModel();

        // param
        if (username==null || username.trim().length()==0 || password==null || password.trim().length()==0) {
            return resultModel.setErrorResult(I18nUtil.getString("login_param_empty"));
        }

        // valid passowrd
        XxlJobUser xxlJobUser = xxlJobUserDao.loadByUserName(username);
        if (xxlJobUser == null) {
            return resultModel.setErrorResult(I18nUtil.getString("login_param_unvalid"));
        }

        // 对密码进行md5加密
        String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!passwordMd5.equals(xxlJobUser.getPassword())) {
            return resultModel.setErrorResult(I18nUtil.getString("login_param_unvalid"));
        }

        String loginToken = makeToken(xxlJobUser);

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        return resultModel.setOKResult("bool-job login success");
    }

    @Override
    public ResultModel logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
        return ResultModel.SUCCESS;
    }


    private XxlJobUser parseToken(String tokenHex){
        XxlJobUser xxlJobUser = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
            xxlJobUser = JacksonUtil.readValue(tokenJson, XxlJobUser.class);
        }
        return xxlJobUser;
    }

    /**
     * 先从cookie里面获取用户信息
     * 1, 如果获取不到，返回null
     * 2, 如果获取到, 进行解析token到原先到用户模型
     * 3, 如果解析错误， 则直接logout
     * 4, 如果解析正确, 重新判断用户密码是否正确
     * 5, 正确, 则返回用户数据, 错误则返回null
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public XxlJobUser ifLogin(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            XxlJobUser cookieUser = null;
            try {
                cookieUser = parseToken(cookieToken);
            } catch (Exception e) {
                logout(request, response);
            }
            if (cookieUser != null) {
                XxlJobUser dbUser = xxlJobUserDao.loadByUserName(cookieUser.getUsername());
                log.info("----------------------------验证用户密码-------------------------------");
                if (dbUser != null) {
                    if (cookieUser.getPassword().equals(dbUser.getPassword())) {
                        log.info("----------------------------用户密码验证通过-------------------------------");
                        return dbUser;
                    }
                }
            }
        }
        return null;
    }
}
