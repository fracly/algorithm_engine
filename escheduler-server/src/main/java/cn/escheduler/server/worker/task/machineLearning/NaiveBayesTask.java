package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.NaiveBayesParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

public class NaiveBayesTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     */
    public NaiveBayesTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private NaiveBayesParameters naiveBayesParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        naiveBayesParameters = JSONUtils.parseObject(taskProps.getTaskParams(), NaiveBayesParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        naiveBayesParameters.setDeployMode("client");
        naiveBayesParameters.setMainJar(resourceInfo);
        naiveBayesParameters.setDriverCores(1);
        naiveBayesParameters.setDriverMemory("1G");
        naiveBayesParameters.setExecutorMemory("2G");
        naiveBayesParameters.setExecutorCores(2);
        naiveBayesParameters.setNumExecutors(2);
        naiveBayesParameters.setProgramType(ProgramType.SCALA);

        int dataSource = naiveBayesParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        logger.info("biaozhi33:" + naiveBayesParameters.getTable());
        naiveBayesParameters.setTable(database + "." + naiveBayesParameters.getTable());
        if (!naiveBayesParameters.checkParameters()) {
            throw new RuntimeException("linear regression task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return naiveBayesParameters;
    }

    @Override
    protected String buildCommand() {

        String para = " --driver-cores " + naiveBayesParameters.getDriverCores() + " " +
                "--driver-memory " + naiveBayesParameters.getDriverMemory() + " " +
                "--num-executors " + naiveBayesParameters.getNumExecutors() + " " +
                "--executor-cores " + naiveBayesParameters.getExecutorCores() + " " +
                "--executor-memory " + naiveBayesParameters.getExecutorMemory() + " --queue default";
        String Mode = naiveBayesParameters.getDeployMode();

        String modelPath = "";
        if (StringUtils.isNotEmpty(naiveBayesParameters.getModelName())) {
            modelPath = "/escheduler/model/" + naiveBayesParameters.getModelName();
        }
        // 参数 1.CharacterColumns 2.LabelColumn 3.Table 4.modelPath
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.NaiveBayes" + " " +
                " --master  yarn --deploy-mode " + Mode + " " +
                para + " " +
                ConfUtils.getUrl() + naiveBayesParameters.getMainJar().getRes() + " " +
                StringUtils.join(naiveBayesParameters.getCharacterColumns(), ",") + " " +
                naiveBayesParameters.getLabelColumn() + " " +
                naiveBayesParameters.getTable() + " " +
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
            this.naiveBayesParameters = JSONObject.parseObject(taskParams, NaiveBayesParameters.class);
        } catch (Exception e) {
            this.errorMsg = "组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }

        if (naiveBayesParameters.getCharacterColumns().size() == 0) {
            logger.error("vehpass is null");
            errorMsg = "参数有误：朴素贝叶斯的特征列不能为空";
            return false;
        }
        if (StringUtils.isEmpty(naiveBayesParameters.getLabelColumn())) {
            logger.error("dev is null");
            errorMsg = "参数有误：朴素贝叶斯的标签列不能为空";
            return false;
        }
        return true;
    }
}
