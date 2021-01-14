package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.DecusionTreeParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;




public class DecusionTreeTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 决策树
     */
    public DecusionTreeTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DecusionTreeParameters decusionTreeParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        decusionTreeParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DecusionTreeParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        decusionTreeParameters.setDeployMode("client");
        decusionTreeParameters.setMainJar(resourceInfo);
        decusionTreeParameters.setDriverCores(1);
        decusionTreeParameters.setDriverMemory("1G");
        decusionTreeParameters.setExecutorMemory("2G");
        decusionTreeParameters.setExecutorCores(2);
        decusionTreeParameters.setNumExecutors(2);

        int dataSource = decusionTreeParameters.getDatasource();

        DataSource ds = processDao.findDataSourceById(dataSource);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        decusionTreeParameters.setTable(database + "." + decusionTreeParameters.getTable());
        if (!decusionTreeParameters.checkParameters()) {
            throw new RuntimeException("linear regression task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return decusionTreeParameters;
    }

    @Override
    protected String buildCommand() {

        String modelPath = "";
        if(StringUtils.isNotEmpty(decusionTreeParameters.getModelName())){
            modelPath = ConfUtils.getUrl()+"/escheduler/model/" + decusionTreeParameters.getModelName();

        }

        String para=" --driver-cores "+decusionTreeParameters.getDriverCores()+" " +
                "--driver-memory "+decusionTreeParameters.getDriverMemory()+" " +
                "--num-executors "+decusionTreeParameters.getNumExecutors()+" " +
                "--executor-cores "+decusionTreeParameters.getExecutorCores()+" " +
                "--executor-memory "+decusionTreeParameters.getExecutorMemory()+" --queue default";
        String Mode=decusionTreeParameters.getDeployMode();


        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DecusionTree" +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + decusionTreeParameters.getMainJar().getRes() + " " +
                StringUtils.join(decusionTreeParameters.getCharacterColumns(), ",") + " " +
                decusionTreeParameters.getLabelColumn() + " " +
                modelPath + " " +
                decusionTreeParameters.getBranchMax()+" " +
                decusionTreeParameters.getDepthMax()+" "+
                decusionTreeParameters.getMethod()+" "+
                decusionTreeParameters.getTable() ;

        logger.info("spark task command : {}", command);

        return command;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
