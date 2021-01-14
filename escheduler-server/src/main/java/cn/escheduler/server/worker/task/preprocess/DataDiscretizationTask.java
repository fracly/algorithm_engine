package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.DataDiscretizationParameters;
import cn.escheduler.common.task.preprocess.DataOffsetParameters;
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

public class DataDiscretizationTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public DataDiscretizationTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DataDiscretizationParameters datadiscretizationParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        datadiscretizationParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DataDiscretizationParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        datadiscretizationParameters.setDeployMode("client");
        datadiscretizationParameters.setMainJar(resourceInfo);
        datadiscretizationParameters.setDriverCores(1);
        datadiscretizationParameters.setDriverMemory("1G");
        datadiscretizationParameters.setExecutorMemory("2G");
        datadiscretizationParameters.setExecutorCores(2);
        datadiscretizationParameters.setNumExecutors(2);

        int dataSource = datadiscretizationParameters.getDatasource();
        int dataStorages = datadiscretizationParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        datadiscretizationParameters.setDatabases(database);
        datadiscretizationParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + datadiscretizationParameters.getTable());
        datadiscretizationParameters.setTable(database + "." + datadiscretizationParameters.getTable());
        if (!datadiscretizationParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=datadiscretizationParameters.getSavebases()+ "." + datadiscretizationParameters.getNewTable();
        JSONArray createtable=datadiscretizationParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return datadiscretizationParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+datadiscretizationParameters.getDriverCores()+" " +
                "--driver-memory "+datadiscretizationParameters.getDriverMemory()+" " +
                "--num-executors "+datadiscretizationParameters.getNumExecutors()+" " +
                "--executor-cores "+datadiscretizationParameters.getExecutorCores()+" " +
                "--executor-memory "+datadiscretizationParameters.getExecutorMemory()+" --queue default";
        String Mode=datadiscretizationParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DataDiscretization" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + datadiscretizationParameters.getMainJar().getRes() + " " +
                datadiscretizationParameters.getTable()+ " " +
                datadiscretizationParameters.getColumn()+ " " +
                datadiscretizationParameters.getWay()+ " " +
                datadiscretizationParameters.getAutonum()+ " " +
                StringUtils.join(datadiscretizationParameters.getNondown(), ",")+ " " +
                StringUtils.join(datadiscretizationParameters.getNonup(), ",")+ " " +
                StringUtils.join(datadiscretizationParameters.getNonnum(), ",")+ " " +
                datadiscretizationParameters.getSavebases()+"."+datadiscretizationParameters.getNewTable();

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
            this.datadiscretizationParameters = JSONObject.parseObject(taskParams, DataDiscretizationParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(datadiscretizationParameters.getColumn()) || StringUtils.isEmpty(datadiscretizationParameters.getWay())){
            logger.error("column is null or way is null  ");
            errorMsg="参数有误：没有选择列 或 离散化方式";
            return false;
        }
        if (StringUtils.isEmpty(datadiscretizationParameters.getAutonum())){
            logger.error("Autonum is null  ");
            errorMsg="参数有误：没有输入类别数";
            return false;
        }
        return true;
    }
}
