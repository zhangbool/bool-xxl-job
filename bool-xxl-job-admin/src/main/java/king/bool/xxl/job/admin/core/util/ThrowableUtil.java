package king.bool.xxl.job.admin.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : 不二
 * @date : 2023/8/22-18:00
 * @desc :
 **/
public class ThrowableUtil {
    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }
}
