package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.ColumnnameAlterParameters;
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

public class TimeShiftTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 过滤列
     */
    public TimeShiftTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TimeShiftParameters timeshiftParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        timeshiftParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TimeShiftParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        timeshiftParameters.setDeployMode("client");
        timeshiftParameters.setMainJar(resourceInfo);
        timeshiftParameters.setDriverCores(1);
        timeshiftParameters.setDriverMemory("1G");
        timeshiftParameters.setExecutorMemory("2G");
        timeshiftParameters.setExecutorCores(2);
        timeshiftParameters.setNumExecutors(2);

        int dataSource = timeshiftParameters.getDatasource();
        int dataStorages = timeshiftParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        timeshiftParameters.setDatabases(database);
        timeshiftParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + timeshiftParameters.getTable());
        timeshiftParameters.setTable(database + "." + timeshiftParameters.getTable());
        if (!timeshiftParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=timeshiftParameters.getSavebases()+ "." + timeshiftParameters.getNewTable();
        JSONArray createtable=timeshiftParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return timeshiftParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+timeshiftParameters.getDriverCores()+" " +
                "--driver-memory "+timeshiftParameters.getDriverMemory()+" " +
                "--num-executors "+timeshiftParameters.getNumExecutors()+" " +
                "--executor-cores "+timeshiftParameters.getExecutorCores()+" " +
                "--executor-memory "+timeshiftParameters.getExecutorMemory()+" --queue default";
        String Mode=timeshiftParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TimeShift" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + timeshiftParameters.getMainJar().getRes() + " " +
                timeshiftParameters.getTable()+ " " +
                timeshiftParameters.getTimeColumn() +" " +
                timeshiftParameters.getFormat() +" " +
                timeshiftParameters.getSavebases()+"."+timeshiftParameters.getNewTable();

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
            this.timeshiftParameters = JSONObject.parseObject(taskParams, TimeShiftParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(timeshiftParameters.getTimeColumn()) || StringUtils.isEmpty(timeshiftParameters.getFormat()) ){
            logger.error("TimeColumn is null or TimeFormat is null ");
            errorMsg="参数有误：没有选择时间列或者没有选择时间格式";
            return false;
        }
        return true;
    }
}
