package king.bool.xxl.job.admin.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author : 不二
 * @date : 2023/8/22-16:05
 * @desc : AOP拦截的是类的元数据(包、类、方法名、参数等)
 *         AOP针对具体的代码，能够实现更加复杂的业务逻辑。
 *         过滤器，拦截器拦截的是URL
 *
 * 过滤器，拦截器拦截的是URL。AOP拦截的是类的元数据(包、类、方法名、参数等)。
 *
 * 过滤器并没有定义业务用于执行逻辑前、后等，仅仅是请求到达就执行。
 * 拦截器有三个方法，相对于过滤器更加细致，有被拦截逻辑执行前、后等。
 * AOP针对具体的代码，能够实现更加复杂的业务逻辑。
 *
 * 三者功能类似，但各有优势，从过滤器--》拦截器--》切面，拦截规则越来越细致。
 * 执行顺序依次是过滤器、拦截器、切面。
 **/
@Slf4j
// #todo: 这里不对, 暂时先不纠结, 先放这里, 后面再看aop到底咋用
//@Aspect
//@Component
public class AopAspect {

    /*
    *： 匹配任意数量的字符
    +：匹配制定数量的类及其子类
    ..：一般用于匹配任意数量的子包或参数
    * com.azazie.report.job.jobService.IMReportTask
     */

    @Pointcut("execution(public * *(..))")
    public void myPointcut() {
    }

    @Around("myPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        Method method = getCurrentMethod(point);
        log.info("当前方法名是: " + method);

        return null;
    }

    private Method getCurrentMethod(ProceedingJoinPoint point) {
        try {
            Signature sig = point.getSignature();
            MethodSignature msig = (MethodSignature) sig;
            Object target = point.getTarget();
            return target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
