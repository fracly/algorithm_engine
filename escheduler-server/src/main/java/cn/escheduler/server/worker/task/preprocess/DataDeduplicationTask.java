package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.ColumnnameAlterParameters;
import cn.escheduler.common.task.preprocess.DataDeduplicationParameters;
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
 * @Date: 2020/11/3
 * @version:
 */
public class DataDeduplicationTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public DataDeduplicationTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DataDeduplicationParameters dataDeduplicationParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        dataDeduplicationParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DataDeduplicationParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        dataDeduplicationParameters.setDeployMode("client");
        dataDeduplicationParameters.setMainJar(resourceInfo);
        dataDeduplicationParameters.setDriverCores(1);
        dataDeduplicationParameters.setDriverMemory("1G");
        dataDeduplicationParameters.setExecutorMemory("2G");
        dataDeduplicationParameters.setExecutorCores(2);
        dataDeduplicationParameters.setNumExecutors(2);

        int dataSource = dataDeduplicationParameters.getDatasource();
        int dataStorages = dataDeduplicationParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        dataDeduplicationParameters.setDatabases(database);
        dataDeduplicationParameters.setSavebases(datasave);
        logger.info("表名为：" + dataDeduplicationParameters.getTable());
        dataDeduplicationParameters.setTable(database + "." + dataDeduplicationParameters.getTable());
        String savetable=dataDeduplicationParameters.getSavebases()+ "." + dataDeduplicationParameters.getNewTable();
        JSONArray createtable=dataDeduplicationParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return dataDeduplicationParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+dataDeduplicationParameters.getDriverCores()+" " +
                "--driver-memory "+dataDeduplicationParameters.getDriverMemory()+" " +
                "--num-executors "+dataDeduplicationParameters.getNumExecutors()+" " +
                "--executor-cores "+dataDeduplicationParameters.getExecutorCores()+" " +
                "--executor-memory "+dataDeduplicationParameters.getExecutorMemory()+" --queue default";
        String Mode=dataDeduplicationParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DataDeduplication" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + dataDeduplicationParameters.getMainJar().getRes() + " " +
                dataDeduplicationParameters.getTable()+ " " +
                StringUtils.join(dataDeduplicationParameters.getDuplicateColumns(), ",")+" " +
                dataDeduplicationParameters.getSavebases()+"."+dataDeduplicationParameters.getNewTable();

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
            this.dataDeduplicationParameters = JSONObject.parseObject(taskParams, DataDeduplicationParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (dataDeduplicationParameters.getDuplicateColumns().size()==0  ){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：数据去重的列为空";
            return false;
        }
        return true;
    }
}
