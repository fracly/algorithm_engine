package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.AccumulationCalculatorParameters;
import cn.escheduler.common.task.preprocess.TableJoinParameters;
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

public class TableJoinTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 表join
     */
    public TableJoinTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TableJoinParameters tablejoinParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        tablejoinParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TableJoinParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        tablejoinParameters.setDeployMode("client");
        tablejoinParameters.setMainJar(resourceInfo);
        tablejoinParameters.setDriverCores(1);
        tablejoinParameters.setDriverMemory("1G");
        tablejoinParameters.setExecutorMemory("2G");
        tablejoinParameters.setExecutorCores(2);
        tablejoinParameters.setNumExecutors(2);

        int dataSource = tablejoinParameters.getDatasource();
        int dataSource2 = tablejoinParameters.getDatasource2();
        int dataStorage = tablejoinParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorage);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        tablejoinParameters.setDatabases(database);
        tablejoinParameters.setSavebases(datasave);
        tablejoinParameters.setLeftTable(database + "." + tablejoinParameters.getLeftTable());
        tablejoinParameters.setRightTable(database2 + "." + tablejoinParameters.getRightTable());
        if (!tablejoinParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=tablejoinParameters.getSavebases()+ "." + tablejoinParameters.getNewTable();
        JSONArray createtable=tablejoinParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return tablejoinParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+tablejoinParameters.getDriverCores()+" " +
                "--driver-memory "+tablejoinParameters.getDriverMemory()+" " +
                "--num-executors "+tablejoinParameters.getNumExecutors()+" " +
                "--executor-cores "+tablejoinParameters.getExecutorCores()+" " +
                "--executor-memory "+tablejoinParameters.getExecutorMemory()+" --queue default";
        String Mode=tablejoinParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TableJoin" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + tablejoinParameters.getMainJar().getRes() + " " +
                tablejoinParameters.getLeftTable()+ " " +
                tablejoinParameters.getRightTable()+ " " +
                tablejoinParameters.getConnectionType()+ " " +
                StringUtils.join(tablejoinParameters.getLeftJoinColumn(), ",")+ " " +
                StringUtils.join(tablejoinParameters.getRightJoinColumn(), ",")+ " " +
                StringUtils.join(tablejoinParameters.getSymbolList(), ",")+ " " +
                StringUtils.join(tablejoinParameters.getLeftColumnList(), ",")+ " " +
                StringUtils.join(tablejoinParameters.getRightColumnList(), ",")+ " " +
                tablejoinParameters.getSavebases()+"."+tablejoinParameters.getNewTable();

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
            this.tablejoinParameters = JSONObject.parseObject(taskParams, TableJoinParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        /**
         *           rightColumnList: [],
         *           leftColumnList: [],
         *           leftJoinColumn: [],
         *           symbolList: [],
         *           rightJoinColumn: []
         *           connectionType
         */
        if (tablejoinParameters.getRightColumnList().size() ==0 || tablejoinParameters.getLeftColumnList().size() ==0  ){
            logger.error("rightColumnList is null or leftColumnList is null ");
            errorMsg="参数有误：没有选择左输出列或者没有选择右输出列";
            return false;
        }
        if (tablejoinParameters.getLeftJoinColumn().size() ==0 || tablejoinParameters.getRightJoinColumn().size() ==0 || tablejoinParameters.getSymbolList().size() ==0 ){
            logger.error("leftJoinColumn is null or rightJoinColumn is null ");
            errorMsg="参数有误：没有选择左关联列或者没有选择右关联列或者关联符号为空";
            return false;
        }
        if (StringUtils.isEmpty(tablejoinParameters.getConnectionType()) ){
            logger.error("connectionType is null");
            errorMsg="参数有误：没有选择连接类型";
            return false;
        }
        return true;
    }
}
