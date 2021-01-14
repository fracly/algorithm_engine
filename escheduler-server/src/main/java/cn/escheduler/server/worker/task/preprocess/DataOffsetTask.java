package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.CharacterMergingParameters;
import cn.escheduler.common.task.preprocess.DataOffsetParameters;
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

public class DataOffsetTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public DataOffsetTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DataOffsetParameters dataoffsetParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        dataoffsetParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DataOffsetParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        dataoffsetParameters.setDeployMode("client");
        dataoffsetParameters.setMainJar(resourceInfo);
        dataoffsetParameters.setDriverCores(1);
        dataoffsetParameters.setDriverMemory("1G");
        dataoffsetParameters.setExecutorMemory("2G");
        dataoffsetParameters.setExecutorCores(2);
        dataoffsetParameters.setNumExecutors(2);

        int dataSource = dataoffsetParameters.getDatasource();
        int dataStorages = dataoffsetParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        dataoffsetParameters.setDatabases(database);
        dataoffsetParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + dataoffsetParameters.getTable());
        dataoffsetParameters.setTable(database + "." + dataoffsetParameters.getTable());
        if (!dataoffsetParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=dataoffsetParameters.getSavebases()+ "." + dataoffsetParameters.getNewTable();
        JSONArray createtable=dataoffsetParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return dataoffsetParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+dataoffsetParameters.getDriverCores()+" " +
                "--driver-memory "+dataoffsetParameters.getDriverMemory()+" " +
                "--num-executors "+dataoffsetParameters.getNumExecutors()+" " +
                "--executor-cores "+dataoffsetParameters.getExecutorCores()+" " +
                "--executor-memory "+dataoffsetParameters.getExecutorMemory()+" --queue default";
        String Mode=dataoffsetParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DataOffset" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + dataoffsetParameters.getMainJar().getRes() + " " +
                dataoffsetParameters.getTable()+ " " +
                dataoffsetParameters.getGroupColumn()+ " " +
                dataoffsetParameters.getOrderColumn()+ " " +
                dataoffsetParameters.getFunc()+ " " +
                dataoffsetParameters.getValueColumn()+ " " +
                dataoffsetParameters.getOffset()+ " " +
                dataoffsetParameters.getSavebases()+"."+dataoffsetParameters.getNewTable();

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
            this.dataoffsetParameters = JSONObject.parseObject(taskParams, DataOffsetParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        /**
         *           groupColumn:"",
         *           //排序列
         *           orderColumn:"",
         *           //偏移函数
         *           func:"",
         *            //取值列
         *           valueColumn:"",
         *           //偏移量
         *           offset:"1",
         */
        if (StringUtils.isEmpty(dataoffsetParameters.getGroupColumn()) || StringUtils.isEmpty(dataoffsetParameters.getOrderColumn()) || StringUtils.isEmpty(dataoffsetParameters.getFunc()) ||  StringUtils.isEmpty(dataoffsetParameters.getValueColumn()) ||  StringUtils.isEmpty(dataoffsetParameters.getOffset())){
            logger.error("groupColumn is null or orderColumn is null or func is null or valueColumn is null or offset is null ");
            errorMsg="参数有误：没有选择排序列 或 排序函数 或 取值列 或 偏移量";
            return false;
        }
        return true;
    }
}
