package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.DataFilterParameters;
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


public class DataFilterTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 数据过滤
     */
    public DataFilterTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }

    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DataFilterParameters datafilterParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        datafilterParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DataFilterParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        datafilterParameters.setDeployMode("client");
        datafilterParameters.setMainJar(resourceInfo);
        datafilterParameters.setDriverCores(1);
        datafilterParameters.setDriverMemory("1G");
        datafilterParameters.setExecutorMemory("2G");
        datafilterParameters.setExecutorCores(2);
        datafilterParameters.setNumExecutors(2);

        int dataSource = datafilterParameters.getDatasource();
        int dataStorages = datafilterParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        datafilterParameters.setDatabases(database);
        datafilterParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + datafilterParameters.getTable());
        datafilterParameters.setTable(database + "." + datafilterParameters.getTable());
        if (!datafilterParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=datafilterParameters.getSavebases()+ "." + datafilterParameters.getNewTable();
        JSONArray createtable=datafilterParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return datafilterParameters;
    }

    @Override
    protected String buildCommand()  {
        String para=" --driver-cores "+datafilterParameters.getDriverCores()+" " +
                "--driver-memory "+datafilterParameters.getDriverMemory()+" " +
                "--num-executors "+datafilterParameters.getNumExecutors()+" " +
                "--executor-cores "+datafilterParameters.getExecutorCores()+" " +
                "--executor-memory "+datafilterParameters.getExecutorMemory()+" --queue default";
        String Mode=datafilterParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DataFilter" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + datafilterParameters.getMainJar().getRes() + " " +
                datafilterParameters.getTable()+ " "  +
                datafilterParameters.getLabelColumn()  +" " +
                datafilterParameters.getCondition() +" " +
                datafilterParameters.getNum() +" " +
                datafilterParameters.getSavebases()+"."+datafilterParameters.getNewTable();

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
            this.datafilterParameters = JSONObject.parseObject(taskParams, DataFilterParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (datafilterParameters.getCondition() == null || StringUtils.isEmpty(datafilterParameters.getCondition())){
            logger.error("Condition is null or not specify a table");
            errorMsg="参数有误：Condition is null or not specify a table";
            return false;
        }
        if (datafilterParameters.getLabelColumn() == null || StringUtils.isEmpty(datafilterParameters.getLabelColumn())){
            logger.error("LabelColumn is null or not specify a table");
            errorMsg="参数有误：LabelColumn is null or not specify a table";
            return false;
        }
        if (datafilterParameters.getNum() == null || StringUtils.isEmpty(datafilterParameters.getNum())){
            logger.error("Num is null or not specify a table");
            errorMsg="参数有误：Num is null or not specify a table";
            return false;
        }

        return true;
    }

}
