package cn.escheduler.server.worker.task.statisticAnalysis;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.CharacterInterceptParameters;
import cn.escheduler.common.task.statisticAnalysis.KernelDensityEstimationParameters;
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

public class KernelDensityEstimationTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 过滤列
     */
    public KernelDensityEstimationTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private KernelDensityEstimationParameters kerneldensityestimationParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        kerneldensityestimationParameters = JSONUtils.parseObject(taskProps.getTaskParams(), KernelDensityEstimationParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        kerneldensityestimationParameters.setDeployMode("client");
        kerneldensityestimationParameters.setMainJar(resourceInfo);
        kerneldensityestimationParameters.setDriverCores(1);
        kerneldensityestimationParameters.setDriverMemory("1G");
        kerneldensityestimationParameters.setExecutorMemory("2G");
        kerneldensityestimationParameters.setExecutorCores(2);
        kerneldensityestimationParameters.setNumExecutors(2);

        int dataSource = kerneldensityestimationParameters.getDatasource();
        int dataStorages = kerneldensityestimationParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        kerneldensityestimationParameters.setDatabases(database);
        kerneldensityestimationParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + kerneldensityestimationParameters.getTable());
        kerneldensityestimationParameters.setTable(database + "." + kerneldensityestimationParameters.getTable());
        if (!kerneldensityestimationParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=kerneldensityestimationParameters.getSavebases()+ "." + kerneldensityestimationParameters.getNewTable();
        JSONArray createtable=kerneldensityestimationParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return kerneldensityestimationParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+kerneldensityestimationParameters.getDriverCores()+" " +
                "--driver-memory "+kerneldensityestimationParameters.getDriverMemory()+" " +
                "--num-executors "+kerneldensityestimationParameters.getNumExecutors()+" " +
                "--executor-cores "+kerneldensityestimationParameters.getExecutorCores()+" " +
                "--executor-memory "+kerneldensityestimationParameters.getExecutorMemory()+" --queue default";
        String Mode=kerneldensityestimationParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.KernelDensityEstimation" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + kerneldensityestimationParameters.getMainJar().getRes() + " " +
                kerneldensityestimationParameters.getTable()+ " " +
                kerneldensityestimationParameters.getColumn() +" " +
                kerneldensityestimationParameters.getSavebases()+"."+kerneldensityestimationParameters.getNewTable();

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
            this.kerneldensityestimationParameters = JSONObject.parseObject(taskParams, KernelDensityEstimationParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(kerneldensityestimationParameters.getColumn())){
            logger.error("column is null ");
            errorMsg="参数有误：没有选择列";
            return false;
        }
        return true;
    }
}
