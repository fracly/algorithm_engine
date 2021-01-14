package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.LinearRegressionParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import cn.escheduler.server.worker.task.enums.MainClassName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/25.
 */
public class LinearRegressionTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 线性回归
     */
    public LinearRegressionTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private LinearRegressionParameters linearRegressionParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        linearRegressionParameters = JSONUtils.parseObject(taskProps.getTaskParams(), LinearRegressionParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        linearRegressionParameters.setDeployMode("client");
        linearRegressionParameters.setMainJar(resourceInfo);
        linearRegressionParameters.setDriverCores(1);
        linearRegressionParameters.setDriverMemory("1G");
        linearRegressionParameters.setExecutorMemory("2G");
        linearRegressionParameters.setExecutorCores(2);
        linearRegressionParameters.setNumExecutors(2);
        linearRegressionParameters.setProgramType(ProgramType.SCALA);

        int dataSource = linearRegressionParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        logger.info("biaozhi33:" + linearRegressionParameters.getTable());
        linearRegressionParameters.setTable(database + "." + linearRegressionParameters.getTable());
        if (!linearRegressionParameters.checkParameters()) {
            throw new RuntimeException("linear regression task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return linearRegressionParameters;
    }

    @Override
    protected String buildCommand() {

        String para=" --driver-cores "+linearRegressionParameters.getDriverCores()+" " +
                "--driver-memory "+linearRegressionParameters.getDriverMemory()+" " +
                "--num-executors "+linearRegressionParameters.getNumExecutors()+" " +
                "--executor-cores "+linearRegressionParameters.getExecutorCores()+" "+
                "--executor-memory "+linearRegressionParameters.getExecutorMemory()+" --queue default";
        String Mode=linearRegressionParameters.getDeployMode();

        String modelPath = "";
        if(StringUtils.isNotEmpty(linearRegressionParameters.getModelName())){
            modelPath = "/escheduler/model/" + linearRegressionParameters.getModelName();
        }
        // 参数 1.CharacterColumns 2.LabelColumn 3.Table 4.modelPath
        String command = "spark-submit  " +
                "--class " + MainClassName.LINEAR_REGRESSION.getClassName() + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + linearRegressionParameters.getMainJar().getRes() + " " +
                StringUtils.join(linearRegressionParameters.getCharacterColumns(), ",") + " " +
                linearRegressionParameters.getLabelColumn() + " " +
                linearRegressionParameters.getTable() + " " +
                modelPath;

        logger.info("spark task command : {}", command);

        logger.info("biaozhi:" + command);
        return command;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
