package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.DefaultFillingParameters;
import cn.escheduler.common.task.preprocess.SplitParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.utils.CreateTmpTable;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import java.sql.SQLException;

public class DefaultFillingTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 空值填补
     */
    public DefaultFillingTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DefaultFillingParameters defaultfillingParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        defaultfillingParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DefaultFillingParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        defaultfillingParameters.setDeployMode("client");
        defaultfillingParameters.setMainJar(resourceInfo);
        defaultfillingParameters.setDriverCores(1);
        defaultfillingParameters.setDriverMemory("1G");
        defaultfillingParameters.setExecutorMemory("2G");
        defaultfillingParameters.setExecutorCores(2);
        defaultfillingParameters.setNumExecutors(2);

        int dataSource = defaultfillingParameters.getDatasource();
        int dataStorages = defaultfillingParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        defaultfillingParameters.setDatabases(database);
        defaultfillingParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + defaultfillingParameters.getTable());
        defaultfillingParameters.setTable(database + "." + defaultfillingParameters.getTable());
        if (!defaultfillingParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=defaultfillingParameters.getSavebases()+ "." + defaultfillingParameters.getNewTable();
        JSONArray createtable=defaultfillingParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return defaultfillingParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+defaultfillingParameters.getDriverCores()+" " +
                "--driver-memory "+defaultfillingParameters.getDriverMemory()+" " +
                "--num-executors "+defaultfillingParameters.getNumExecutors()+" " +
                "--executor-cores "+defaultfillingParameters.getExecutorCores()+" " +
                "--executor-memory "+defaultfillingParameters.getExecutorMemory()+" --queue default";
        String Mode=defaultfillingParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.MissingValue" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + defaultfillingParameters.getMainJar().getRes() + " " +
                defaultfillingParameters.getTable()+ " " +
                defaultfillingParameters.getFillingColumn()+ " " +
                defaultfillingParameters.getFixedValue()+ " " +
                defaultfillingParameters.getFillStyle()+ " " +
                defaultfillingParameters.getSavebases()+"."+defaultfillingParameters.getNewTable();

        logger.info("spark task command : {}", command);

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
            this.defaultfillingParameters = JSONObject.parseObject(taskParams, DefaultFillingParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(defaultfillingParameters.getFillingColumn()) || StringUtils.isEmpty(defaultfillingParameters.getFillStyle())){
            logger.error("columnb is null  or fillStyle is null");
            errorMsg="参数有误：没有选择列 或者 没有选择 填充方式";
            return false;
        }
        return true;
    }
}
