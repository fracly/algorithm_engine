package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.AccumulationCalculatorParameters;
import cn.escheduler.common.task.preprocess.CharacterMergingParameters;
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

public class AccumulationCalculatorTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public AccumulationCalculatorTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private AccumulationCalculatorParameters accumulationcalculatorParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        accumulationcalculatorParameters = JSONUtils.parseObject(taskProps.getTaskParams(), AccumulationCalculatorParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        accumulationcalculatorParameters.setDeployMode("client");
        accumulationcalculatorParameters.setMainJar(resourceInfo);
        accumulationcalculatorParameters.setDriverCores(1);
        accumulationcalculatorParameters.setDriverMemory("1G");
        accumulationcalculatorParameters.setExecutorMemory("2G");
        accumulationcalculatorParameters.setExecutorCores(2);
        accumulationcalculatorParameters.setNumExecutors(2);

        int dataSource = accumulationcalculatorParameters.getDatasource();
        int dataStorages = accumulationcalculatorParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        accumulationcalculatorParameters.setDatabases(database);
        accumulationcalculatorParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + accumulationcalculatorParameters.getTable());
        accumulationcalculatorParameters.setTable(database + "." + accumulationcalculatorParameters.getTable());
        if (!accumulationcalculatorParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=accumulationcalculatorParameters.getSavebases()+ "." + accumulationcalculatorParameters.getNewTable();
        JSONArray createtable=accumulationcalculatorParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return accumulationcalculatorParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+accumulationcalculatorParameters.getDriverCores()+" " +
                "--driver-memory "+accumulationcalculatorParameters.getDriverMemory()+" " +
                "--num-executors "+accumulationcalculatorParameters.getNumExecutors()+" " +
                "--executor-cores "+accumulationcalculatorParameters.getExecutorCores()+" " +
                "--executor-memory "+accumulationcalculatorParameters.getExecutorMemory()+" --queue default";
        String Mode=accumulationcalculatorParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.AccumulationCalculator" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + accumulationcalculatorParameters.getMainJar().getRes() + " " +
                accumulationcalculatorParameters.getTable()+ " " +
                StringUtils.join(accumulationcalculatorParameters.getCoefficientList(), ",")+ " " +
                StringUtils.join(accumulationcalculatorParameters.getList(), ",")+ " " +
                accumulationcalculatorParameters.getAbsolute()+ " " +
                accumulationcalculatorParameters.getSavebases()+"."+accumulationcalculatorParameters.getNewTable();

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
            this.accumulationcalculatorParameters = JSONObject.parseObject(taskParams, AccumulationCalculatorParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (accumulationcalculatorParameters.getList().size() ==0 || accumulationcalculatorParameters.getCoefficientList().size() ==0  ){
            logger.error("column is null or Coefficient is null ");
            errorMsg="参数有误：没有选择列或者系数为空";
            return false;
        }
        return true;
    }
}
