package king.bool.xxl.job.admin.dao;

import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.core.enums.RegistryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class XxlJobRegistryDaoTest {

    @Autowired
    private XxlJobRegistryDao xxlJobRegistryDao;

    @Test
    void findDead() {
        final List<Integer> dead = xxlJobRegistryDao.findDead(30, new Date());
        System.out.println(JacksonUtil.writeValueAsString(dead));

    }
}
