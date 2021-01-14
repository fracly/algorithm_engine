package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.CharacterInterceptParameters;
import cn.escheduler.common.task.preprocess.TimeFunctionParameters;
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

public class TimeFunctionTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public TimeFunctionTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TimeFunctionParameters timefunctionParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        timefunctionParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TimeFunctionParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        timefunctionParameters.setDeployMode("client");
        timefunctionParameters.setMainJar(resourceInfo);
        timefunctionParameters.setDriverCores(1);
        timefunctionParameters.setDriverMemory("1G");
        timefunctionParameters.setExecutorMemory("2G");
        timefunctionParameters.setExecutorCores(2);
        timefunctionParameters.setNumExecutors(2);

        int dataSource = timefunctionParameters.getDatasource();
        int dataStorages = timefunctionParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        timefunctionParameters.setDatabases(database);
        timefunctionParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + timefunctionParameters.getTable());
        timefunctionParameters.setTable(database + "." + timefunctionParameters.getTable());
        if (!timefunctionParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=timefunctionParameters.getSavebases()+ "." + timefunctionParameters.getNewTable();
        JSONArray createtable=timefunctionParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return timefunctionParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+timefunctionParameters.getDriverCores()+" " +
                "--driver-memory "+timefunctionParameters.getDriverMemory()+" " +
                "--num-executors "+timefunctionParameters.getNumExecutors()+" " +
                "--executor-cores "+timefunctionParameters.getExecutorCores()+" " +
                "--executor-memory "+timefunctionParameters.getExecutorMemory()+" --queue default";
        String Mode=timefunctionParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TimeFunction" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + timefunctionParameters.getMainJar().getRes() + " " +
                timefunctionParameters.getTable()+ " " +
                timefunctionParameters.getColumn()+ " " +
                StringUtils.join(timefunctionParameters.getFunctions(), ",")+ " " +
                timefunctionParameters.getSavebases()+"."+timefunctionParameters.getNewTable();

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
            this.timefunctionParameters = JSONObject.parseObject(taskParams, TimeFunctionParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(timefunctionParameters.getColumn()) || timefunctionParameters.getFunctions().size() == 0){
            logger.error("column is null or Start is null or length is null");
            errorMsg="参数有误：没有选择列或者没有选择时间函数";
            return false;
        }
        return true;
    }
}
