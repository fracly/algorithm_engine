package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.CharacterMergingParameters;
import cn.escheduler.common.task.preprocess.TimeDifferParameters;
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

public class CharactorMergingTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 字符串拼接
     */
    public CharactorMergingTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CharacterMergingParameters charactermergingParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        charactermergingParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CharacterMergingParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        charactermergingParameters.setDeployMode("client");
        charactermergingParameters.setMainJar(resourceInfo);
        charactermergingParameters.setDriverCores(1);
        charactermergingParameters.setDriverMemory("1G");
        charactermergingParameters.setExecutorMemory("2G");
        charactermergingParameters.setExecutorCores(2);
        charactermergingParameters.setNumExecutors(2);

        int dataSource = charactermergingParameters.getDatasource();
        int dataStorage = charactermergingParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorage);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        charactermergingParameters.setDatabases(database);
        charactermergingParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + charactermergingParameters.getTable());
        charactermergingParameters.setTable(database + "." + charactermergingParameters.getTable());
        if (!charactermergingParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=charactermergingParameters.getSavebases()+ "." + charactermergingParameters.getNewTable();
        JSONArray createtable=charactermergingParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return charactermergingParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+charactermergingParameters.getDriverCores()+" " +
                "--driver-memory "+charactermergingParameters.getDriverMemory()+" " +
                "--num-executors "+charactermergingParameters.getNumExecutors()+" " +
                "--executor-cores "+charactermergingParameters.getExecutorCores()+" " +
                "--executor-memory "+charactermergingParameters.getExecutorMemory()+" --queue default";
        String Mode=charactermergingParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CharacterMerging" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + charactermergingParameters.getMainJar().getRes() + " " +
                charactermergingParameters.getTable()+ " " +
                StringUtils.join(charactermergingParameters.getSelectColumnList(), ",")+ " " +
                charactermergingParameters.getSymbol() +" " +
                charactermergingParameters.getSavebases()+"."+charactermergingParameters.getNewTable();

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
            this.charactermergingParameters = JSONObject.parseObject(taskParams, CharacterMergingParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (charactermergingParameters.getSelectColumnList().size() ==0 || StringUtils.isEmpty(charactermergingParameters.getSymbol()) ){
            logger.error("column is null or symbol is null ");
            errorMsg="参数有误：没有选择拼接列或者拼接符号为空";
            return false;
        }
        return true;
    }
}
