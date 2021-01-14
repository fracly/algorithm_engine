package cn.escheduler.server.worker.task.io;

import cn.escheduler.api.service.DataSourceService;
import cn.escheduler.api.utils.DataSourceUtil;
import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.MysqlInputParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;

import java.util.Map;


public class MysqlInputTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     */
    public MysqlInputTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private MysqlInputParameters mysqlInputParameters;

    @Override
    public void init() {

        logger.info("mysqlinput task params {}", taskProps.getTaskParams());

        mysqlInputParameters = JSONUtils.parseObject(taskProps.getTaskParams(), MysqlInputParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        mysqlInputParameters.setDeployMode("client");
        mysqlInputParameters.setMainJar(resourceInfo);
        mysqlInputParameters.setDriverCores(1);
        mysqlInputParameters.setDriverMemory("1G");
        mysqlInputParameters.setExecutorMemory("2G");
        mysqlInputParameters.setExecutorCores(2);
        mysqlInputParameters.setNumExecutors(2);
        mysqlInputParameters.setProgramType(ProgramType.SCALA);

        int dataSource = mysqlInputParameters.getDatasource();
        int dataStorages = mysqlInputParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);

        String host, port, username, password, database, other, address;
        Map<String, String> map = JSONUtils.toMap(ds.getConnectionParams());
        address = map.get("address");
        username = map.get("user");
        password = map.get("password");
        database = map.get("database");

        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        mysqlInputParameters.setDatabases(database);
        mysqlInputParameters.setSavebases(datasave);
        mysqlInputParameters.setAddress(address);
        mysqlInputParameters.setUser(username);
        mysqlInputParameters.setPassword(password);




        logger.info("biaozhi33:" + mysqlInputParameters.getTable());
        if (!mysqlInputParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }


    }

    @Override
    public AbstractParameters getParameters() {
        return mysqlInputParameters;
    }

    @Override
    protected String buildCommand()  {
        String para=" --driver-cores "+mysqlInputParameters.getDriverCores()+" " +
                "--driver-memory "+mysqlInputParameters.getDriverMemory()+" " +
                "--num-executors "+mysqlInputParameters.getNumExecutors()+" " +
                "--executor-cores "+mysqlInputParameters.getExecutorCores()+" "+
                "--executor-memory "+mysqlInputParameters.getExecutorMemory()+" --queue default";
        String Mode=mysqlInputParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.MysqlInPut" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + mysqlInputParameters.getMainJar().getRes() +" "+
                mysqlInputParameters.getAddress()+" "+
                mysqlInputParameters.getUser()+" "+
                mysqlInputParameters.getPassword()+" "+
                mysqlInputParameters.getTable()+" "+
                mysqlInputParameters.getDatabases()+" "+
                mysqlInputParameters.getSavebases()+"."+mysqlInputParameters.getNewTable()+" "+
                mysqlInputParameters.getCond().replaceAll(" ","--");


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
            this.mysqlInputParameters = JSONObject.parseObject(taskParams, MysqlInputParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }

        return true;
    }
}
