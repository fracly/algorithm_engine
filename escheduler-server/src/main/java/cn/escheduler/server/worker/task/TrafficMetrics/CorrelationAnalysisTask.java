package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.CorrelationAnalysisParameters;
import cn.escheduler.common.task.TrafficMetrics.OnroadUrbanParameters;
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

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/15
 * @version:
 */
public class CorrelationAnalysisTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     *
     */
    public CorrelationAnalysisTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CorrelationAnalysisParameters correlationAnalysisParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        correlationAnalysisParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CorrelationAnalysisParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        correlationAnalysisParameters.setDeployMode("client");
        correlationAnalysisParameters.setMainJar(resourceInfo);
        correlationAnalysisParameters.setDriverCores(1);
        correlationAnalysisParameters.setDriverMemory("1G");
        correlationAnalysisParameters.setExecutorMemory("2G");
        correlationAnalysisParameters.setExecutorCores(2);
        correlationAnalysisParameters.setNumExecutors(2);

        int dataSource = correlationAnalysisParameters.getDatasource();
        int dataStorages = correlationAnalysisParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        correlationAnalysisParameters.setDatabases(database);
        correlationAnalysisParameters.setSavebases(datasave);
        correlationAnalysisParameters.setTable(database + "." + correlationAnalysisParameters.getTable());
        String savetable=correlationAnalysisParameters.getSavebases()+ "." + correlationAnalysisParameters.getNewTable();
        JSONArray createtable=correlationAnalysisParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return correlationAnalysisParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+correlationAnalysisParameters.getDriverCores()+" " +
                "--driver-memory "+correlationAnalysisParameters.getDriverMemory()+" " +
                "--num-executors "+correlationAnalysisParameters.getNumExecutors()+" " +
                "--executor-cores "+correlationAnalysisParameters.getExecutorCores()+" " +
                "--executor-memory "+correlationAnalysisParameters.getExecutorMemory()+" --queue default";
        String Mode=correlationAnalysisParameters.getDeployMode();
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CorrelationAnalysis" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + correlationAnalysisParameters.getMainJar().getRes() + " " +
                correlationAnalysisParameters.getTable()+ " " +
                correlationAnalysisParameters.getTypeSelect()+ " "+
                StringUtils.join(correlationAnalysisParameters.getCols(), ",")+" " +
                correlationAnalysisParameters.getSavebases()+"."+correlationAnalysisParameters.getNewTable();

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
            this.correlationAnalysisParameters = JSONObject.parseObject(taskParams, CorrelationAnalysisParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(correlationAnalysisParameters.getTypeSelect())){
            logger.error("timeParam is null");
            errorMsg="参数有误：相关性分析的选择相关系数参数不能为空";
            return false;
        }

        if (correlationAnalysisParameters.getCols().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：相关性分析选择的列不能为空";
            return false;
        }
        return true;
    }
}
