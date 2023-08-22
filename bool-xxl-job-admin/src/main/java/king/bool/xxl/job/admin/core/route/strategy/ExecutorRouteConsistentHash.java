package king.bool.xxl.job.admin.core.route.strategy;

import king.bool.xxl.job.admin.core.route.ExecutorRouter;
import king.bool.xxl.job.admin.core.util.JacksonUtil;
import king.bool.xxl.job.core.biz.model.ResultModel;
import king.bool.xxl.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author : 不二
 * @date : 2023/8/22-09:29
 * @desc :  #todo: 一致性hash算法暂时还未搞懂,我们这里先使用简单hash先实现一下
 *                后续学习参考文档: https://www.xuxueli.com/blog/?blog=./notebook/6-%E7%AE%97%E6%B3%95/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95.md
 *                               https://zhuanlan.zhihu.com/p/439268771
 *
 *
 *          一致性hash策略:
 *          分组下机器地址相同，不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；
 *          a、virtual node：解决不均衡问题
 *          b、hash method replace hashCode：String的hashCode可能重复，需要进一步扩大hashCode的取值范围
 **/
@Slf4j
public class ExecutorRouteConsistentHash extends ExecutorRouter {

    private static int VIRTUAL_NODE_NUM = 100;

    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值)
     * @param key
     * @return
     */
    private static long hash(String key) {

        // md5 byte
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes;
        keyBytes = key.getBytes(StandardCharsets.UTF_8);

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }

    public String hashJob(int jobId, List<String> addressList) {

        // ------A1------A2-------A3------
        // -----------J1------------------
        TreeMap<Long, String> addressRing = new TreeMap<Long, String>();
        for (String address: addressList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String key = "SHARD-" + address + "-NODE-" + i;
                log.info("key是: " + key);

                long addressHash = hash(key);
                addressRing.put(addressHash, address);
            }
        }

        long jobHash = hash(String.valueOf(jobId));
        // 返回大于等于的值, 这里就是过滤掉小小于jobHash的数据
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }
        return addressRing.firstEntry().getValue();
    }

    @Override
    public ResultModel route(TriggerParam triggerParam, List<String> addressList) {
//        String address = hashJob(triggerParam.getJobId(), addressList);
//        return new ResultModel(ResultModel.SUCCESS_CODE, address);


        // 这个是一个简单的hash算法
        // 这里会有一个问题, 如果有节点的增加或者删除, 可能会造成数据命中无效删除节点数据
        int index = triggerParam.getJobId() % addressList.size();
        return new ResultModel(ResultModel.SUCCESS_CODE, addressList.get(index));

    }


    public static void main(String[] args) {
        ExecutorRouter executorRouter = new ExecutorRouteConsistentHash();
        TriggerParam param = new TriggerParam();
        param.setJobId(2);
        List<String> addressList = new ArrayList<>();
        addressList.add("192.101.101.001");
        addressList.add("192.101.101.002");
        addressList.add("192.101.101.003");
        addressList.add("192.101.101.004");

        final ResultModel route = executorRouter.route(param, addressList);
        System.out.println("结果是: " + JacksonUtil.writeValueAsString(route));


    }

}
