package king.bool.xxl.job.admin.test;

import org.junit.Test;
import org.springframework.util.DigestUtils;

/**
 * @author : 不二
 * @date : 2023/8/21-11:27
 * @desc :
 **/
public class JavaTest {
    @Test
    public void test01(){
        String passwordMd5 = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(passwordMd5);
    }

}
