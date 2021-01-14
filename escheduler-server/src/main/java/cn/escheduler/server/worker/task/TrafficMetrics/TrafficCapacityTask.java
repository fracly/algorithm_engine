package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.TrafficCapacityParameters;
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

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/29
 * @version:
 */
public class TrafficCapacityTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public TrafficCapacityTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TrafficCapacityParameters trafficCapacityParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        trafficCapacityParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TrafficCapacityParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        trafficCapacityParameters.setDeployMode("client");
        trafficCapacityParameters.setMainJar(resourceInfo);
        trafficCapacityParameters.setDriverCores(1);
        trafficCapacityParameters.setDriverMemory("1G");
        trafficCapacityParameters.setExecutorMemory("2G");
        trafficCapacityParameters.setExecutorCores(2);
        trafficCapacityParameters.setNumExecutors(2);

        int dataSource = trafficCapacityParameters.getDatasource();
        int dataSource2 = trafficCapacityParameters.getDatasource2();
        int dataStorages = trafficCapacityParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        trafficCapacityParameters.setDatabases(database);
        trafficCapacityParameters.setSavebases(datasave);
        logger.info("左表名为：" + trafficCapacityParameters.getLeftTable());
        trafficCapacityParameters.setLeftTable(database + "." + trafficCapacityParameters.getLeftTable());
        trafficCapacityParameters.setRightTable(database2 + "." + trafficCapacityParameters.getRightTable());
        String savetable=trafficCapacityParameters.getSavebases()+ "." + trafficCapacityParameters.getNewTable();
        JSONArray createtable=trafficCapacityParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return trafficCapacityParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+trafficCapacityParameters.getDriverCores()+" " +
                "--driver-memory "+trafficCapacityParameters.getDriverMemory()+" " +
                "--num-executors "+trafficCapacityParameters.getNumExecutors()+" " +
                "--executor-cores "+trafficCapacityParameters.getExecutorCores()+" " +
                "--executor-memory "+trafficCapacityParameters.getExecutorMemory()+" --queue default";
        String Mode=trafficCapacityParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TrafficCapacity" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + trafficCapacityParameters.getMainJar().getRes() + " " +
                trafficCapacityParameters.getLeftTable()+ " " +
                trafficCapacityParameters.getRightTable()+ " "+
                StringUtils.join(trafficCapacityParameters.getVehpass(), ",")+" " +
                StringUtils.join(trafficCapacityParameters.getDev(), ",")+" " +
                trafficCapacityParameters.getSavebases()+"."+trafficCapacityParameters.getNewTable();

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
            this.trafficCapacityParameters = JSONObject.parseObject(taskParams,TrafficCapacityParameters .class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (trafficCapacityParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (trafficCapacityParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
