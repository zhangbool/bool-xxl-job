//package king.bool.xxl.job.core.executor.impl;
//
//import king.bool.xxl.job.core.executor.XxlJobExecutor;
//import king.bool.xxl.job.core.handler.annotation.XxlJob;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author : 不二
// * @date : 2023/8/27-18:13
// * @desc :
// **/
//public class XxlJobSimpleExecutor extends XxlJobExecutor {
//
//    private List<Object> xxlJobBeanList = new ArrayList<>();
//    public List<Object> getXxlJobBeanList() {
//        return xxlJobBeanList;
//    }
//    public void setXxlJobBeanList(List<Object> xxlJobBeanList) {
//        this.xxlJobBeanList = xxlJobBeanList;
//    }
//
//
//    @Override
//    public void start() {
//
//        // init JobHandler Repository (for method)
//        initJobHandlerMethodRepository(xxlJobBeanList);
//
//        // super start
//        try {
//            super.start();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void destroy() {
//        super.destroy();
//    }
//
//
//    private void initJobHandlerMethodRepository(List<Object> xxlJobBeanList) {
//        if (xxlJobBeanList==null || xxlJobBeanList.size()==0) {
//            return;
//        }
//
//        // init job handler from method
//        for (Object bean: xxlJobBeanList) {
//            // method
//            Method[] methods = bean.getClass().getDeclaredMethods();
//            if (methods.length == 0) {
//                continue;
//            }
//            for (Method executeMethod : methods) {
//                XxlJob xxlJob = executeMethod.getAnnotation(XxlJob.class);
//                // registryc
//                registJobHandler(xxlJob, bean, executeMethod);
//            }
//
//        }
//
//    }
//
//}
