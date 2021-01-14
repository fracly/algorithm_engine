package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.RegressionModelAssessmentParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import org.slf4j.Logger;


public class RegressionModelAssessmentTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 回归模型评估
     */
    public RegressionModelAssessmentTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private RegressionModelAssessmentParameters regressionModelAssessmentParameters;

    @Override
    public void init() {

        logger.info("foracast task params {}", taskProps.getTaskParams());

        regressionModelAssessmentParameters = JSONUtils.parseObject(taskProps.getTaskParams(), RegressionModelAssessmentParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        regressionModelAssessmentParameters.setDeployMode("client");
        regressionModelAssessmentParameters.setMainJar(resourceInfo);
        regressionModelAssessmentParameters.setDriverCores(1);
        regressionModelAssessmentParameters.setDriverMemory("1G");
        regressionModelAssessmentParameters.setExecutorMemory("2G");
        regressionModelAssessmentParameters.setExecutorCores(2);
        regressionModelAssessmentParameters.setNumExecutors(2);

        int dataSource = regressionModelAssessmentParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");

        regressionModelAssessmentParameters.setDatabases(database);

        if (!regressionModelAssessmentParameters.checkParameters()) {
            throw new RuntimeException("regressionModelAssessment task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return regressionModelAssessmentParameters;
    }

    @Override
    protected String buildCommand() throws Exception {

        String para=" --driver-cores "+regressionModelAssessmentParameters.getDriverCores()+" " +
                "--driver-memory "+regressionModelAssessmentParameters.getDriverMemory()+" " +
                "--num-executors "+regressionModelAssessmentParameters.getNumExecutors()+" " +
                "--executor-cores "+regressionModelAssessmentParameters.getExecutorCores()+" " +
                "--executor-memory "+regressionModelAssessmentParameters.getExecutorMemory()+" --queue default";
        String Mode=regressionModelAssessmentParameters.getDeployMode();

        //参数 1.database + modelpath (input tbale) 2.LabelColumn 3.PredictionColumn
        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.RegressionModelAssessment" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + regressionModelAssessmentParameters.getMainJar().getRes() +" "+
                regressionModelAssessmentParameters.getDatabases()+"."+regressionModelAssessmentParameters.getTable() +" " +
                regressionModelAssessmentParameters.getLabelColumn() +" "+
                regressionModelAssessmentParameters.getPredictionColumn()+ " "+
                regressionModelAssessmentParameters.getDatabases()+"."+regressionModelAssessmentParameters.getTable()+"_pg" ;

        logger.info("spark task command : {}", command);

        return command;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
