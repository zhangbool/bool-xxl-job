package king.bool.xxl.job.admin.dao;

import king.bool.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-17:08
 * @desc :
 **/
@Mapper
public interface XxlJobInfoDao {

    public List<XxlJobInfo> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("jobGroup") int jobGroup,
                                     @Param("triggerStatus") int triggerStatus,
                                     @Param("jobDesc") String jobDesc,
                                     @Param("executorHandler") String executorHandler,
                                     @Param("author") String author);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") int jobGroup,
                             @Param("triggerStatus") int triggerStatus,
                             @Param("jobDesc") String jobDesc,
                             @Param("executorHandler") String executorHandler,
                             @Param("author") String author);

    public int save(XxlJobInfo info);

    public XxlJobInfo loadById(@Param("id") int id);

    public int update(XxlJobInfo xxlJobInfo);

    public int delete(@Param("id") long id);

    public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    public int findAllCount();

    public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize );

    public int scheduleUpdate(XxlJobInfo xxlJobInfo);
}
