package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.LogisticRegressionParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import cn.escheduler.server.worker.task.enums.MainClassName;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

public class LogisticRegressionTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     */
    public LogisticRegressionTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private LogisticRegressionParameters logisticRegressionParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        logisticRegressionParameters = JSONUtils.parseObject(taskProps.getTaskParams(), LogisticRegressionParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        logisticRegressionParameters.setDeployMode("client");
        logisticRegressionParameters.setMainJar(resourceInfo);
        logisticRegressionParameters.setDriverCores(1);
        logisticRegressionParameters.setDriverMemory("1G");
        logisticRegressionParameters.setExecutorMemory("2G");
        logisticRegressionParameters.setExecutorCores(2);
        logisticRegressionParameters.setNumExecutors(2);
        logisticRegressionParameters.setProgramType(ProgramType.SCALA);

        int dataSource = logisticRegressionParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        logger.info("biaozhi33:" + logisticRegressionParameters.getTable());
        logisticRegressionParameters.setTable(database + "." + logisticRegressionParameters.getTable());
        if (!logisticRegressionParameters.checkParameters()) {
            throw new RuntimeException("linear regression task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return logisticRegressionParameters;
    }

    @Override
    protected String buildCommand() {

        String para = " --driver-cores " + logisticRegressionParameters.getDriverCores() + " " +
                "--driver-memory " + logisticRegressionParameters.getDriverMemory() + " " +
                "--num-executors " + logisticRegressionParameters.getNumExecutors() + " " +
                "--executor-cores " + logisticRegressionParameters.getExecutorCores() + " " +
                "--executor-memory " + logisticRegressionParameters.getExecutorMemory() + " --queue default";
        String Mode = logisticRegressionParameters.getDeployMode();

        String modelPath = "";
        if (StringUtils.isNotEmpty(logisticRegressionParameters.getModelName())) {
            modelPath = "/escheduler/model/" + logisticRegressionParameters.getModelName();
        }
        // 参数 1.CharacterColumns 2.LabelColumn 3.Table 4.modelPath
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.LogisticRegression" + " " +
                " --master  yarn --deploy-mode " + Mode + " " +
                para + " " +
                ConfUtils.getUrl() + logisticRegressionParameters.getMainJar().getRes() + " " +
                StringUtils.join(logisticRegressionParameters.getCharacterColumns(), ",") + " " +
                logisticRegressionParameters.getLabelColumn() + " " +
                logisticRegressionParameters.getTable() + " " +
                modelPath;

        logger.info("spark task command : {}", command);

        logger.info("biaozhi:" + command);
        return command;
    }

    @Override
    public boolean checkParams() {
        String taskParams = taskProps.getTaskParams();
        if ("{}".equals(taskParams)) {
            this.errorMsg = "组件所有参数为空，请初始化组件参数";
            return false;
        }
        try {
            this.logisticRegressionParameters = JSONObject.parseObject(taskParams, LogisticRegressionParameters.class);
        } catch (Exception e) {
            this.errorMsg = "组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }

        if (logisticRegressionParameters.getCharacterColumns().size() == 0) {
            logger.error("vehpass is null");
            errorMsg = "参数有误：逻辑回归的特征列不能为空";
            return false;
        }
        if (StringUtils.isEmpty(logisticRegressionParameters.getLabelColumn())) {
            logger.error("dev is null");
            errorMsg = "参数有误：逻辑回归的标签列不能为空";
            return false;
        }
        return true;
    }
}
