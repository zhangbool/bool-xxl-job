package king.bool.xxl.job.core.handler.impl;

import king.bool.xxl.job.core.handler.IJobHandler;

import java.lang.reflect.Method;

/**
 * @author : 不二
 * @date : 2023/8/24-14:22
 * @desc : 方法的执行器, 启动的时候, 先根据注解, 把对应的XxlJob注解的方法获取出来, 封装成MethodJobHandler,
 *          存入(注册)到内存中的ConcurrentMap中, 方便后续取出来进行处理
 **/
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 0) {
            method.invoke(target, new Object[paramTypes.length]);       // method-param can not be primitive-types
        } else {
            method.invoke(target);
        }
    }

    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
