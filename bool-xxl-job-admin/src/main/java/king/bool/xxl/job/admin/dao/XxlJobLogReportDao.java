package king.bool.xxl.job.admin.dao;

import king.bool.xxl.job.admin.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/22-12:20
 * @desc :
 **/
@Mapper
public interface XxlJobLogReportDao {
    public int save(XxlJobLogReport xxlJobLogReport);
    public int update(XxlJobLogReport xxlJobLogReport);
    public List<XxlJobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
                                                @Param("triggerDayTo") Date triggerDayTo);
    public XxlJobLogReport queryLogReportTotal();
}
