package cn.escheduler.server.worker.task.io;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.MysqlOutputParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;

import java.util.Map;


public class MysqlOutputTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     */
    public MysqlOutputTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private MysqlOutputParameters mysqlOutputParameters;

    @Override
    public void init() {

        logger.info("mysqlinput task params {}", taskProps.getTaskParams());

        mysqlOutputParameters = JSONUtils.parseObject(taskProps.getTaskParams(), MysqlOutputParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        mysqlOutputParameters.setDeployMode("client");
        mysqlOutputParameters.setMainJar(resourceInfo);
        mysqlOutputParameters.setDriverCores(1);
        mysqlOutputParameters.setDriverMemory("1G");
        mysqlOutputParameters.setExecutorMemory("2G");
        mysqlOutputParameters.setExecutorCores(2);
        mysqlOutputParameters.setNumExecutors(2);
        mysqlOutputParameters.setProgramType(ProgramType.SCALA);

        int dataSource = mysqlOutputParameters.getDatasource();
        int dataStorages = mysqlOutputParameters.getDataStorage();

        DataSource hiveDs = processDao.findDataSourceById(dataSource);
        DataSource mysqlDs = processDao.findDataSourceById(dataStorages);

        String  username, password, mysqlDatabase, address;
        Map<String, String> map = JSONUtils.toMap(mysqlDs.getConnectionParams());
        address = map.get("address");
        username = map.get("user");
        password = map.get("password");
        mysqlDatabase = map.get("database");

        String datasave = JSONUtils.toMap(hiveDs.getConnectionParams()).get("database");
        mysqlOutputParameters.setDatabases(datasave);
        mysqlOutputParameters.setSavebases(mysqlDatabase);
        mysqlOutputParameters.setAddress(address);
        mysqlOutputParameters.setUser(username);
        mysqlOutputParameters.setPassword(password);




        logger.info("biaozhi33:" + mysqlOutputParameters.getTable());
        if (!mysqlOutputParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }


    }

    @Override
    public AbstractParameters getParameters() {
        return mysqlOutputParameters;
    }

    @Override
    protected String buildCommand()  {
        String para=" --driver-cores "+mysqlOutputParameters.getDriverCores()+" " +
                "--driver-memory "+mysqlOutputParameters.getDriverMemory()+" " +
                "--num-executors "+mysqlOutputParameters.getNumExecutors()+" " +
                "--executor-cores "+mysqlOutputParameters.getExecutorCores()+" "+
                "--executor-memory "+mysqlOutputParameters.getExecutorMemory()+" --queue default";
        String Mode=mysqlOutputParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.MysqlOutPut" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + mysqlOutputParameters.getMainJar().getRes() +" "+
                mysqlOutputParameters.getAddress()+" "+
                mysqlOutputParameters.getUser()+" "+
                mysqlOutputParameters.getPassword()+" "+
                mysqlOutputParameters.getTable()+" "+
                mysqlOutputParameters.getDatabases()+" "+
                mysqlOutputParameters.getNewTable()+" "+
                mysqlOutputParameters.getSavebases()+" "+
                mysqlOutputParameters.getSave();


        logger.info("spark task command : {}", command);

        logger.info("biaozhi:--" + command);
        return command;
    }

    @Override
    public boolean checkParams() {
        String taskParams = taskProps.getTaskParams();
        if("{}".equals(taskParams)){
            this.errorMsg="组件所有参数为空，请初始化组件参数";
            return false;
        }
        try {
            this.mysqlOutputParameters = JSONObject.parseObject(taskParams, MysqlOutputParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }

        return true;
    }
}
