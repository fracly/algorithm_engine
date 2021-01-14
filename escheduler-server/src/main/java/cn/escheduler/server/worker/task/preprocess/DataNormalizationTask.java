package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.DataDeduplicationParameters;
import cn.escheduler.common.task.preprocess.DataNormalizationParameters;
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
 * @description: 归一化组件
 * @author: jgn
 * @Date: 2020/11/5
 * @version:
 */
public class DataNormalizationTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public DataNormalizationTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DataNormalizationParameters dataNormalizationParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        dataNormalizationParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DataNormalizationParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        dataNormalizationParameters.setDeployMode("client");
        dataNormalizationParameters.setMainJar(resourceInfo);
        dataNormalizationParameters.setDriverCores(1);
        dataNormalizationParameters.setDriverMemory("1G");
        dataNormalizationParameters.setExecutorMemory("2G");
        dataNormalizationParameters.setExecutorCores(2);
        dataNormalizationParameters.setNumExecutors(2);

        int dataSource = dataNormalizationParameters.getDatasource();
        int dataStorages = dataNormalizationParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        dataNormalizationParameters.setDatabases(database);
        dataNormalizationParameters.setSavebases(datasave);
        logger.info("表名为：" + dataNormalizationParameters.getTable());
        dataNormalizationParameters.setTable(database + "." + dataNormalizationParameters.getTable());
        String savetable=dataNormalizationParameters.getSavebases()+ "." + dataNormalizationParameters.getNewTable();
        JSONArray createtable=dataNormalizationParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return dataNormalizationParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+dataNormalizationParameters.getDriverCores()+" " +
                "--driver-memory "+dataNormalizationParameters.getDriverMemory()+" " +
                "--num-executors "+dataNormalizationParameters.getNumExecutors()+" " +
                "--executor-cores "+dataNormalizationParameters.getExecutorCores()+" " +
                "--executor-memory "+dataNormalizationParameters.getExecutorMemory()+" --queue default";
        String Mode=dataNormalizationParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DataNormalization" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + dataNormalizationParameters.getMainJar().getRes() + " " +
                dataNormalizationParameters.getTable()+ " " +
                StringUtils.join(dataNormalizationParameters.getNormalizationColumns(), ",")+" " +
                dataNormalizationParameters.getIsSaveOriginalField()+ " " +
                dataNormalizationParameters.getSavebases()+"."+dataNormalizationParameters.getNewTable();

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
            this.dataNormalizationParameters = JSONObject.parseObject(taskParams, DataNormalizationParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (dataNormalizationParameters.getNormalizationColumns().size()==0  ){
            logger.error("normalizationColumns is null");
            errorMsg="参数有误：归一化的列为空";
            return false;
        }
        return true;
    }
}
