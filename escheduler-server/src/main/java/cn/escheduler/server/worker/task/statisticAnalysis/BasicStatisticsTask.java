package cn.escheduler.server.worker.task.statisticAnalysis;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.statisticAnalysis.BasicStatisticsParameters;
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

public class BasicStatisticsTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 过滤列
     */
    public BasicStatisticsTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private BasicStatisticsParameters basicStatisticsParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        basicStatisticsParameters = JSONUtils.parseObject(taskProps.getTaskParams(), BasicStatisticsParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        basicStatisticsParameters.setDeployMode("client");
        basicStatisticsParameters.setMainJar(resourceInfo);
        basicStatisticsParameters.setDriverCores(1);
        basicStatisticsParameters.setDriverMemory("1G");
        basicStatisticsParameters.setExecutorMemory("2G");
        basicStatisticsParameters.setExecutorCores(2);
        basicStatisticsParameters.setNumExecutors(2);

        int dataSource = basicStatisticsParameters.getDatasource();
        int dataStorages = basicStatisticsParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        basicStatisticsParameters.setDatabases(database);
        basicStatisticsParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + basicStatisticsParameters.getTable());
        basicStatisticsParameters.setTable(database + "." + basicStatisticsParameters.getTable());
        if (!basicStatisticsParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=basicStatisticsParameters.getSavebases()+ "." + basicStatisticsParameters.getNewTable();
        JSONArray createtable=basicStatisticsParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return basicStatisticsParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+basicStatisticsParameters.getDriverCores()+" " +
                "--driver-memory "+basicStatisticsParameters.getDriverMemory()+" " +
                "--num-executors "+basicStatisticsParameters.getNumExecutors()+" " +
                "--executor-cores "+basicStatisticsParameters.getExecutorCores()+" " +
                "--executor-memory "+basicStatisticsParameters.getExecutorMemory()+" --queue default";
        String Mode=basicStatisticsParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.BasicStatistics" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + basicStatisticsParameters.getMainJar().getRes() + " " +
                basicStatisticsParameters.getTable()+ " " +
                StringUtils.join(basicStatisticsParameters.getBasicColumns(), ",")+" " +
                basicStatisticsParameters.getSavebases()+"."+basicStatisticsParameters.getNewTable();

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
            this.basicStatisticsParameters = JSONObject.parseObject(taskParams, BasicStatisticsParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (basicStatisticsParameters.getBasicColumns().size()==0 ){
            logger.error("column is null ");
            errorMsg="参数有误：没有选择列";
            return false;
        }
        return true;
    }
}
