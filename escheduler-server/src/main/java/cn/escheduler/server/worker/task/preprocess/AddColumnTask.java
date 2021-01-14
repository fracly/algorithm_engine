package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.AddColumnParameters;
import cn.escheduler.common.task.preprocess.TimeFunctionParameters;
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

public class AddColumnTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     * 累加器
     */
    public AddColumnTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private AddColumnParameters addcolumnParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        addcolumnParameters = JSONUtils.parseObject(taskProps.getTaskParams(), AddColumnParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        addcolumnParameters.setDeployMode("client");
        addcolumnParameters.setMainJar(resourceInfo);
        addcolumnParameters.setDriverCores(1);
        addcolumnParameters.setDriverMemory("1G");
        addcolumnParameters.setExecutorMemory("2G");
        addcolumnParameters.setExecutorCores(2);
        addcolumnParameters.setNumExecutors(2);

        int dataSource = addcolumnParameters.getDatasource();
        int dataStorages = addcolumnParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        addcolumnParameters.setDatabases(database);
        addcolumnParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + addcolumnParameters.getTable());
        addcolumnParameters.setTable(database + "." + addcolumnParameters.getTable());
        if (!addcolumnParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=addcolumnParameters.getSavebases()+ "." + addcolumnParameters.getNewTable();
        JSONArray createtable=addcolumnParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return addcolumnParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+addcolumnParameters.getDriverCores()+" " +
                "--driver-memory "+addcolumnParameters.getDriverMemory()+" " +
                "--num-executors "+addcolumnParameters.getNumExecutors()+" " +
                "--executor-cores "+addcolumnParameters.getExecutorCores()+" " +
                "--executor-memory "+addcolumnParameters.getExecutorMemory()+" --queue default";
        String Mode=addcolumnParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.AddColumn" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + addcolumnParameters.getMainJar().getRes() + " " +
                addcolumnParameters.getTable()+ " " +
                addcolumnParameters.getTypeselect()+ " " +
                addcolumnParameters.getValue()+ " " +
                (addcolumnParameters.getOrderColumn()==null?"null":addcolumnParameters.getOrderColumn())+ " " +
                addcolumnParameters.getSavebases()+"."+addcolumnParameters.getNewTable();

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
            this.addcolumnParameters = JSONObject.parseObject(taskParams, AddColumnParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(addcolumnParameters.getTypeselect())){
            logger.error("Typeselect is null ");
            errorMsg="参数有误：没有选择类型";
            return false;
        }
        return true;
    }
}
