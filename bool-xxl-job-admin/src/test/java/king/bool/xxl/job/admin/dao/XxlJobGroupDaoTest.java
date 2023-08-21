package king.bool.xxl.job.admin.dao;

import king.bool.xxl.job.admin.core.model.XxlJobGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class XxlJobGroupDaoTest {

    @Autowired
    private XxlJobGroupDao xxlJobGroupDao;

    @Test
    void findAll() {
        final List<XxlJobGroup> all = xxlJobGroupDao.findAll();
        System.out.println(all);
    }
}
