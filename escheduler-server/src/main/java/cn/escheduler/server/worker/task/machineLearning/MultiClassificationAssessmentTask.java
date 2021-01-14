package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.MultiClassificationAssessmentParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import org.slf4j.Logger;

public class MultiClassificationAssessmentTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 多分类评估
     */
    public MultiClassificationAssessmentTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private MultiClassificationAssessmentParameters multiClassificationAssessmentParameters;

    @Override
    public void init() {

        logger.info("foracast task params {}", taskProps.getTaskParams());

        multiClassificationAssessmentParameters = JSONUtils.parseObject(taskProps.getTaskParams(), MultiClassificationAssessmentParameters.class);

        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        multiClassificationAssessmentParameters.setDeployMode("client");
        multiClassificationAssessmentParameters.setMainJar(resourceInfo);
        multiClassificationAssessmentParameters.setDriverCores(1);
        multiClassificationAssessmentParameters.setDriverMemory("1G");
        multiClassificationAssessmentParameters.setExecutorMemory("2G");
        multiClassificationAssessmentParameters.setExecutorCores(2);
        multiClassificationAssessmentParameters.setNumExecutors(2);

        int dataSource = multiClassificationAssessmentParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");

        multiClassificationAssessmentParameters.setDatabases(database);

        if (!multiClassificationAssessmentParameters.checkParameters()) {
            throw new RuntimeException("regressionModelAssessment task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return multiClassificationAssessmentParameters;
    }

    @Override
    protected String buildCommand()  {

        String para=" --driver-cores "+multiClassificationAssessmentParameters.getDriverCores()+" " +
                "--driver-memory "+multiClassificationAssessmentParameters.getDriverMemory()+" " +
                "--num-executors "+multiClassificationAssessmentParameters.getNumExecutors()+" " +
                "--executor-cores "+multiClassificationAssessmentParameters.getExecutorCores()+" " +
                "--executor-memory "+multiClassificationAssessmentParameters.getExecutorMemory()+" --queue default";
        String Mode=multiClassificationAssessmentParameters.getDeployMode();

        //参数 1.database + modelpath (input tbale) 2.LabelColumn 3.PredictionColumn
        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.MultiClassificationAssessment" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + multiClassificationAssessmentParameters.getMainJar().getRes()
                +" "+multiClassificationAssessmentParameters.getDatabases()+"."+multiClassificationAssessmentParameters.getTable() +" " +
                multiClassificationAssessmentParameters.getLabelColumn() +" "+
                multiClassificationAssessmentParameters.getPredictionColumn()+
                " "+multiClassificationAssessmentParameters.getDatabases()+"."+multiClassificationAssessmentParameters.getTable()+"_pg" ;

        logger.info("spark task command : {}", command);

        return command;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
