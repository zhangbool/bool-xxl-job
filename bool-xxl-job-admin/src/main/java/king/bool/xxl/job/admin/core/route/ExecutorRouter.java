package king.bool.xxl.job.admin.core.route;

import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : 不二
 * @date : 2023/8/21-23:14
 * @desc : 抽象类
 *
 *         https://tobebetterjavaer.com/oo/abstract-vs-interface.html
 *         抽象类作为很多子类的父类，它是一种模板式设计。
 *         而接口是一种行为规范，它是一种辐射式设计。
 *
 *         什么是模板式设计？
 *         最简单例子，大家都用过 ppt 里面的模板，如果用模板 A 设计了 ppt B 和 ppt C，ppt B 和 ppt C 公共的部分就是模板 A 了，
 *         如果它们的公共部分需要改动，则只需要改动模板 A 就可以了，不需要重新对 ppt B 和 ppt C 进行改动。
 *         而辐射式设计，比如某个电梯都装了某种报警器，一旦要更新报警器，就必须全部更新。
 *
 *         也就是说对于抽象类，如果需要添加新的方法，可以直接在抽象类中添加具体的实现，子类可以不进行变更；
 *         而对于接口则不行，如果接口进行了变更，则所有实现这个接口的类都必须进行相应的改动。
 *
 **/
@Slf4j
public abstract class ExecutorRouter {
    /**
     * route address
     *
     * @param addressList
     * @return ReturnT.content=address
     */
    public abstract ResultModel route(TriggerParam triggerParam, List<String> addressList);

}
