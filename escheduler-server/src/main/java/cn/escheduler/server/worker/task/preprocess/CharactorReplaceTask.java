package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.AddColumnParameters;
import cn.escheduler.common.task.preprocess.CharactorReplaceParameters;
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

public class CharactorReplaceTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public CharactorReplaceTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CharactorReplaceParameters charactorreplaceParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        charactorreplaceParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CharactorReplaceParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        charactorreplaceParameters.setDeployMode("client");
        charactorreplaceParameters.setMainJar(resourceInfo);
        charactorreplaceParameters.setDriverCores(1);
        charactorreplaceParameters.setDriverMemory("1G");
        charactorreplaceParameters.setExecutorMemory("2G");
        charactorreplaceParameters.setExecutorCores(2);
        charactorreplaceParameters.setNumExecutors(2);

        int dataSource = charactorreplaceParameters.getDatasource();
        int dataStorages = charactorreplaceParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        charactorreplaceParameters.setDatabases(database);
        charactorreplaceParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + charactorreplaceParameters.getTable());
        charactorreplaceParameters.setTable(database + "." + charactorreplaceParameters.getTable());
        if (!charactorreplaceParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=charactorreplaceParameters.getSavebases()+ "." + charactorreplaceParameters.getNewTable();
        JSONArray createtable=charactorreplaceParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return charactorreplaceParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+charactorreplaceParameters.getDriverCores()+" " +
                "--driver-memory "+charactorreplaceParameters.getDriverMemory()+" " +
                "--num-executors "+charactorreplaceParameters.getNumExecutors()+" " +
                "--executor-cores "+charactorreplaceParameters.getExecutorCores()+" " +
                "--executor-memory "+charactorreplaceParameters.getExecutorMemory()+" --queue default";
        String Mode=charactorreplaceParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CharactorReplace" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + charactorreplaceParameters.getMainJar().getRes() + " " +
                charactorreplaceParameters.getTable()+ " " +
                charactorreplaceParameters.getColumn()+ " " +
                charactorreplaceParameters.getRownull()+ " " +
                charactorreplaceParameters.getStr_old()+ " " +
                charactorreplaceParameters.getStr_new()+ " " +
                charactorreplaceParameters.getSavebases()+"."+charactorreplaceParameters.getNewTable();

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
            this.charactorreplaceParameters = JSONObject.parseObject(taskParams, CharactorReplaceParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(charactorreplaceParameters.getColumn()) || StringUtils.isEmpty(charactorreplaceParameters.getRownull())){
            logger.error("Column is null or type is null");
            errorMsg="参数有误：没有选择列 或者没有选择类型";
            return false;
        }
        return true;
    }
}
