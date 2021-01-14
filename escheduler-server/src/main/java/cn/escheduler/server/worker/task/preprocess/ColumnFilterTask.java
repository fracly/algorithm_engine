package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.ColumnFilterParameters;
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


public class ColumnFilterTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 过滤列
     */
    public ColumnFilterTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private ColumnFilterParameters columnfilterParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        columnfilterParameters = JSONUtils.parseObject(taskProps.getTaskParams(), ColumnFilterParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        columnfilterParameters.setDeployMode("client");
        columnfilterParameters.setMainJar(resourceInfo);
        columnfilterParameters.setDriverCores(1);
        columnfilterParameters.setDriverMemory("1G");
        columnfilterParameters.setExecutorMemory("2G");
        columnfilterParameters.setExecutorCores(2);
        columnfilterParameters.setNumExecutors(2);

        int dataSource = columnfilterParameters.getDatasource();
        int dataStorages = columnfilterParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        columnfilterParameters.setDatabases(database);
        columnfilterParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + columnfilterParameters.getTable());
        columnfilterParameters.setTable(database + "." + columnfilterParameters.getTable());
        if (!columnfilterParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=columnfilterParameters.getSavebases()+ "." + columnfilterParameters.getNewTable();
        JSONArray createtable=columnfilterParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return columnfilterParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+columnfilterParameters.getDriverCores()+" " +
                "--driver-memory "+columnfilterParameters.getDriverMemory()+" " +
                "--num-executors "+columnfilterParameters.getNumExecutors()+" " +
                "--executor-cores "+columnfilterParameters.getExecutorCores()+" " +
                "--executor-memory "+columnfilterParameters.getExecutorMemory()+" --queue default";
        String Mode=columnfilterParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.ColumnFilter" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + columnfilterParameters.getMainJar().getRes() + " " +
                columnfilterParameters.getTable()+ " " +
                StringUtils.join(columnfilterParameters.getCharacterColumns(), ",")+" " +
                columnfilterParameters.getSavebases()+"."+columnfilterParameters.getNewTable();

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
            this.columnfilterParameters = JSONObject.parseObject(taskParams, ColumnFilterParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (columnfilterParameters.getCharacterColumns() == null || columnfilterParameters.getCharacterColumns().size()==0){
            logger.error("CharacterColumns is null or not specify a table");
            errorMsg="参数有误：CharacterColumns is null or not specify a table";
            return false;
        }

        return true;
    }
}
