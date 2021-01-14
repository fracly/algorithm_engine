package cn.escheduler.common.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.escheduler.common.Constants.YARN_RESOURCEMANAGER_HA_RM_IDS;
import static cn.escheduler.common.utils.PropertyUtils.getString;

public class YarnUtils {


    private static final Logger logger = LoggerFactory.getLogger(YarnUtils.class);

    public static void killJob( List<String> appIdStr) {
        for (String appId:appIdStr){
            logger.info("取消spark任务,任务id：" + appId);
        }


        // 初始化 yarn的配置
        Configuration cf = new Configuration();

        boolean cross_platform = false;
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            cross_platform = true;
        }
        // 配置使用跨平台提交任务
        cf.set("HADOOP_HOME","/opt/cloudera/parcels/CDH/lib/hadoop");
        cf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);
        // 设置yarn资源，不然会使用localhost:8032
        cf.set("yarn.resourcemanager.address", getString(YARN_RESOURCEMANAGER_HA_RM_IDS)+":8032");

        // 创建yarn的客户端，此类中有杀死任务的方法
        YarnClient yarnClient = YarnClient.createYarnClient();

        // 初始化yarn的客户端
        yarnClient.init(cf);

        // yarn客户端启动
        yarnClient.start();

        try {
            for (String appId:appIdStr){
                // 根据应用id，杀死应用
                yarnClient.killApplication(getAppId(appId));
            }

        } catch (Exception e) {
            logger.error("取消spark任务失败", e);
        }

        // 关闭yarn客户端
        yarnClient.stop();
    }

    private static ApplicationId getAppId(String appIdStr) {
        return ConverterUtils.toApplicationId(appIdStr);
    }


}
