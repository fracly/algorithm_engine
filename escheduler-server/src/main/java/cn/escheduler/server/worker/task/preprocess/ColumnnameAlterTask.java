package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.InputParameters;
import cn.escheduler.common.task.preprocess.ColumnnameAlterParameters;
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

public class ColumnnameAlterTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 过滤列
     */
    public ColumnnameAlterTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private ColumnnameAlterParameters columnnamealterParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        columnnamealterParameters = JSONUtils.parseObject(taskProps.getTaskParams(), ColumnnameAlterParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        columnnamealterParameters.setDeployMode("client");
        columnnamealterParameters.setMainJar(resourceInfo);
        columnnamealterParameters.setDriverCores(1);
        columnnamealterParameters.setDriverMemory("1G");
        columnnamealterParameters.setExecutorMemory("2G");
        columnnamealterParameters.setExecutorCores(2);
        columnnamealterParameters.setNumExecutors(2);

        int dataSource = columnnamealterParameters.getDatasource();
        int dataStorages = columnnamealterParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        columnnamealterParameters.setDatabases(database);
        columnnamealterParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + columnnamealterParameters.getTable());
        columnnamealterParameters.setTable(database + "." + columnnamealterParameters.getTable());
        if (!columnnamealterParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=columnnamealterParameters.getSavebases()+ "." + columnnamealterParameters.getNewTable();
        JSONArray createtable=columnnamealterParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return columnnamealterParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+columnnamealterParameters.getDriverCores()+" " +
                "--driver-memory "+columnnamealterParameters.getDriverMemory()+" " +
                "--num-executors "+columnnamealterParameters.getNumExecutors()+" " +
                "--executor-cores "+columnnamealterParameters.getExecutorCores()+" " +
                "--executor-memory "+columnnamealterParameters.getExecutorMemory()+" --queue default";
        String Mode=columnnamealterParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.ColumnnameAlter" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + columnnamealterParameters.getMainJar().getRes() + " " +
                columnnamealterParameters.getTable()+ " " +
                StringUtils.join(columnnamealterParameters.getBeforeColumnList(), ",")+" " +
                StringUtils.join(columnnamealterParameters.getAfterColumnList(), ",")+" " +
                columnnamealterParameters.getSavebases()+"."+columnnamealterParameters.getNewTable();

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
            this.columnnamealterParameters = JSONObject.parseObject(taskParams, ColumnnameAlterParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (columnnamealterParameters.getBeforeColumnList().size()==0 || columnnamealterParameters.getAfterColumnList().size() == 0 ){
            logger.error("BeforeColumnList is null or AfterColumnList is null ");
            errorMsg="参数有误：修改的字段为空";
            return false;
        }
        return true;
    }
}
