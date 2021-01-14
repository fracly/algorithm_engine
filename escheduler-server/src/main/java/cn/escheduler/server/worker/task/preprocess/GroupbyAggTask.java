package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.GroupbyAggParameters;
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


public class GroupbyAggTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 分组聚合
     */
    public GroupbyAggTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private GroupbyAggParameters groupbyaggParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        groupbyaggParameters = JSONUtils.parseObject(taskProps.getTaskParams(), GroupbyAggParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        groupbyaggParameters.setDeployMode("client");
        groupbyaggParameters.setMainJar(resourceInfo);
        groupbyaggParameters.setDriverCores(1);
        groupbyaggParameters.setDriverMemory("1G");
        groupbyaggParameters.setExecutorMemory("2G");
        groupbyaggParameters.setExecutorCores(2);
        groupbyaggParameters.setNumExecutors(2);

        int dataSource = groupbyaggParameters.getDatasource();
        int dataStorage = groupbyaggParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorage);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        groupbyaggParameters.setDatabases(database);
        groupbyaggParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + groupbyaggParameters.getTable());
        groupbyaggParameters.setTable(database + "." + groupbyaggParameters.getTable());
        if (!groupbyaggParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=groupbyaggParameters.getSavebases()+ "." + groupbyaggParameters.getNewTable();
        JSONArray createtable=groupbyaggParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return groupbyaggParameters;
    }

    @Override
    protected String buildCommand()  {

        String para=" --driver-cores "+groupbyaggParameters.getDriverCores()+" " +
                "--driver-memory "+groupbyaggParameters.getDriverMemory()+" " +
                "--num-executors "+groupbyaggParameters.getNumExecutors()+" " +
                "--executor-cores "+groupbyaggParameters.getExecutorCores()+" " +
                "--executor-memory "+groupbyaggParameters.getExecutorMemory()+" --queue default";
        String Mode=groupbyaggParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.Groupbyagg" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + groupbyaggParameters.getMainJar().getRes() + " " +
                groupbyaggParameters.getTable()+ " " +
                StringUtils.join(groupbyaggParameters.getGroupColumns(), ",")+ " " +
                StringUtils.join(groupbyaggParameters.getCountColumns(), ",") +" " +
                groupbyaggParameters.getMethod()+" " +
                groupbyaggParameters.getSavebases()+"."+groupbyaggParameters.getNewTable();

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
            this.groupbyaggParameters = JSONObject.parseObject(taskParams, GroupbyAggParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (groupbyaggParameters.getMethod() == null || StringUtils.isEmpty(groupbyaggParameters.getMethod())){
            logger.error("Method is null or not specify a table");
            errorMsg="参数有误：Method is null or not specify a table";
            return false;
        }
        if (groupbyaggParameters.getCountColumns() == null || groupbyaggParameters.getCountColumns().size()==0){
            logger.error("CountColumns is null or not specify a table");
            errorMsg="参数有误：CountColumns is null or not specify a table";
            return false;
        }
        if (groupbyaggParameters.getGroupColumns() == null || groupbyaggParameters.getGroupColumns().size()==0){
            logger.error("GroupColumns is null or not specify a table");
            errorMsg="参数有误：GroupColumns is null or not specify a table";
            return false;
        }
        return true;
    }
}
