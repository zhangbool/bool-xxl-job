package king.bool.xxl.job.admin.service;

import king.bool.xxl.job.admin.core.model.XxlJobInfo;
import king.bool.xxl.job.core.biz.model.ResultModel;

import java.util.Date;
import java.util.Map;

/**
 * @author : 不二
 * @date : 2023/8/22-10:54
 * @desc : 这是一个多个表集合的服务
 **/
public interface XxlJobService {
    /**
     * page list
     *
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param executorHandler
     * @param author
     * @return
     */
    public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    public ResultModel add(XxlJobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo
     * @return
     */
    public ResultModel update(XxlJobInfo jobInfo);

    /**
     * remove job
     * 	 *
     * @param id
     * @return
     */
    public ResultModel remove(int id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    public ResultModel start(int id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    public ResultModel stop(int id);

    /**
     * dashboard info
     *
     * @return
     */
    public Map<String,Object> dashboardInfo();

    /**
     * chart info
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public ResultModel chartInfo(Date startDate, Date endDate);

}
