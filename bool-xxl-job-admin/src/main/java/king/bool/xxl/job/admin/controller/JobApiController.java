package king.bool.xxl.job.admin.controller;

import king.bool.xxl.job.admin.controller.annotation.PermissionLimit;
import king.bool.xxl.job.admin.core.conf.XxlJobAdminConfig;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.core.biz.AdminBiz;
import king.bool.xxl.job.core.biz.model.HandleCallbackParam;
import king.bool.xxl.job.core.biz.model.RegistryParam;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.util.GsonTool;
import king.bool.xxl.job.core.util.XxlJobRemotingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-14:48
 * @desc :
 **/
@Slf4j
@Controller
@RequestMapping("/api")
public class JobApiController {

    @Resource
    private AdminBiz adminBiz;

    /**
     * api
     * 这里不就和executor的内置服务器差不多吗
     * 内置服务器直接根据请求路径判断
     * 这里根据请求url判断
     * 然后根据不同的请求做出不同的逻辑
     *
     * @param uri
     * @param data
     * @return
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(needLogin = false)
    public ResultModel api(HttpServletRequest request,
                           @PathVariable("uri") String uri,
                           @RequestBody(required = false) String data) {

        log.info("接受到api请求:{}-{}", uri, data);

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ResultModel(ResultModel.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri==null || uri.trim().length()==0) {
            return new ResultModel(ResultModel.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ResultModel(ResultModel.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        if ("callback".equals(uri)) {
            // 这里就是一个数据格式的转换, 从string转json
            List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
            return adminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            log.info("registry模型转换结果是: {}", JacksonUtil.writeValueAsString(registryParam));
            return adminBiz.registry(registryParam);
            // return ResultModel.SUCCESS.setOKResult("ivanl001 is the king of world!");
        } else if ("registryRemove".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registryRemove(registryParam);
        } else {
            return new ResultModel(ResultModel.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
        }

    }


}
