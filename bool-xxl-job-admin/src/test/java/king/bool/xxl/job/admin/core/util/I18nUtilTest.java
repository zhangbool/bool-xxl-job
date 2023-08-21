package king.bool.xxl.job.admin.core.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class I18nUtilTest {

    @Test
    void getMultString() {
        // log.info(I18nUtil.getString("admin_name"));

        log.info(I18nUtil.getMultString());
    }

}
