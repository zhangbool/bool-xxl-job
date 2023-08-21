package king.bool.xxl.job.admin.test;


import king.bool.xxl.job.admin.core.model.XxlJobUser;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @author : 不二
 * @date : 2023/8/21-09:48
 * @desc :
 **/
public class ByteTest {

    @Test
    public void test01(){
        System.out.println("ivanl001 is the king of world!");
        String str = "Aa";
        final byte[] bytes = str.getBytes();
        System.out.println("-----");
        final String s = new String(bytes);
        System.out.println("=======");

        XxlJobUser xxlJobUser = new XxlJobUser();
        xxlJobUser.setId(0);
        xxlJobUser.setRole(0);
        xxlJobUser.setPassword("Aa");
        xxlJobUser.setUsername("Aa");
        xxlJobUser.setPermission("Aa");

        final String s1 = JacksonUtil.writeValueAsString(xxlJobUser);
        final byte[] bytes1 = s1.getBytes();
        final BigInteger bigInteger = new BigInteger(bytes1);
        String tokenHex = bigInteger.toString(16);
        System.out.println("+++++++++++++=");


    }

}
