package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.TimeDifferParameters;
import cn.escheduler.common.task.preprocess.TimeShiftParameters;
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

public class TimeDifferTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 时间差
     */
    public TimeDifferTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TimeDifferParameters timedifferParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        timedifferParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TimeDifferParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        timedifferParameters.setDeployMode("client");
        timedifferParameters.setMainJar(resourceInfo);
        timedifferParameters.setDriverCores(1);
        timedifferParameters.setDriverMemory("1G");
        timedifferParameters.setExecutorMemory("2G");
        timedifferParameters.setExecutorCores(2);
        timedifferParameters.setNumExecutors(2);

        int dataSource = timedifferParameters.getDatasource();
        int dataStorages = timedifferParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        timedifferParameters.setDatabases(database);
        timedifferParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + timedifferParameters.getTable());
        timedifferParameters.setTable(database + "." + timedifferParameters.getTable());
        if (!timedifferParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=timedifferParameters.getSavebases()+ "." + timedifferParameters.getNewTable();
        JSONArray createtable=timedifferParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return timedifferParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+timedifferParameters.getDriverCores()+" " +
                "--driver-memory "+timedifferParameters.getDriverMemory()+" " +
                "--num-executors "+timedifferParameters.getNumExecutors()+" " +
                "--executor-cores "+timedifferParameters.getExecutorCores()+" " +
                "--executor-memory "+timedifferParameters.getExecutorMemory()+" --queue default";
        String Mode=timedifferParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TimeDiffer" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + timedifferParameters.getMainJar().getRes() + " " +
                timedifferParameters.getTable()+ " " +
                timedifferParameters.getColumn() +" " +
                timedifferParameters.getColumn2() +" " +
                timedifferParameters.getUnit() +" " +
                timedifferParameters.getSavebases()+"."+timedifferParameters.getNewTable();

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
            this.timedifferParameters = JSONObject.parseObject(taskParams, TimeDifferParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(timedifferParameters.getColumn()) || StringUtils.isEmpty(timedifferParameters.getColumn2()) || StringUtils.isEmpty(timedifferParameters.getUnit()) ){
            logger.error("column is null or dawei is null ");
            errorMsg="参数有误：没有选择列或者被减列，或者没有选择单位";
            return false;
        }
        return true;
    }
}
