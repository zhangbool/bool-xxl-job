package king.bool.xxl.job.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : 不二
 * @date : 2023/8/24-14:12
 * @desc :
 **/
public class ThrowableUtil {

    /**
     * parse error to string
     *
     * @param e
     * @return
     */
    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }

}
