package cn.escheduler.server.utils;

import cn.escheduler.common.Constants;
import cn.escheduler.server.master.runner.MasterBaseTaskExecThread;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public  class ConfUtils {
    private static final Logger logger = LoggerFactory.getLogger(MasterBaseTaskExecThread.class);
    private static Configuration conf;
    static {
        try {
            conf = new PropertiesConfiguration(Constants.APPLICATION_MASTER_PROPERTIES_PATH);
        } catch (ConfigurationException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
    public static String getUrl(){

        return conf.getString(Constants.APPLICATION_MASTER_URL);
    }

}
