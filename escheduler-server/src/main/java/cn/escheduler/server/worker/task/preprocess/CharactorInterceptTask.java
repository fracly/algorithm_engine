package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.CharacterInterceptParameters;
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

public class CharactorInterceptTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 字符串截取
     */
    public CharactorInterceptTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CharacterInterceptParameters characterinterceptParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        characterinterceptParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CharacterInterceptParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        characterinterceptParameters.setDeployMode("client");
        characterinterceptParameters.setMainJar(resourceInfo);
        characterinterceptParameters.setDriverCores(1);
        characterinterceptParameters.setDriverMemory("1G");
        characterinterceptParameters.setExecutorMemory("2G");
        characterinterceptParameters.setExecutorCores(2);
        characterinterceptParameters.setNumExecutors(2);

        int dataSource = characterinterceptParameters.getDatasource();
        int dataStorages = characterinterceptParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        characterinterceptParameters.setDatabases(database);
        characterinterceptParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + characterinterceptParameters.getTable());
        characterinterceptParameters.setTable(database + "." + characterinterceptParameters.getTable());
        if (!characterinterceptParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=characterinterceptParameters.getSavebases()+ "." + characterinterceptParameters.getNewTable();
        JSONArray createtable=characterinterceptParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return characterinterceptParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+characterinterceptParameters.getDriverCores()+" " +
                "--driver-memory "+characterinterceptParameters.getDriverMemory()+" " +
                "--num-executors "+characterinterceptParameters.getNumExecutors()+" " +
                "--executor-cores "+characterinterceptParameters.getExecutorCores()+" " +
                "--executor-memory "+characterinterceptParameters.getExecutorMemory()+" --queue default";
        String Mode=characterinterceptParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CharacterIntercept" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + characterinterceptParameters.getMainJar().getRes() + " " +
                characterinterceptParameters.getTable()+ " " +
                characterinterceptParameters.getColumn()+ " " +
                characterinterceptParameters.getStart()+ " " +
                characterinterceptParameters.getLength()+ " " +
                characterinterceptParameters.getSavebases()+"."+characterinterceptParameters.getNewTable();

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
            this.characterinterceptParameters = JSONObject.parseObject(taskParams, CharacterInterceptParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(characterinterceptParameters.getColumn()) || StringUtils.isEmpty(characterinterceptParameters.getStart()) || StringUtils.isEmpty(characterinterceptParameters.getLength()) ){
            logger.error("column is null or Start is null or length is null");
            errorMsg="参数有误：没有选择拼接列或者没有选择长度或者没有选择起始位置";
            return false;
        }
        return true;
    }
}
