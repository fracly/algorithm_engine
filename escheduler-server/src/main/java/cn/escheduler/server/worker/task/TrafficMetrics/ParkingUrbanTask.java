package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.OnroadUrbanParameters;
import cn.escheduler.common.task.TrafficMetrics.ParkingUrbanParameters;
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
 * @Date: 2020/12/15
 * @version:
 */
public class ParkingUrbanTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public ParkingUrbanTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private ParkingUrbanParameters parkingUrbanParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        parkingUrbanParameters = JSONUtils.parseObject(taskProps.getTaskParams(), ParkingUrbanParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        parkingUrbanParameters.setDeployMode("client");
        parkingUrbanParameters.setMainJar(resourceInfo);
        parkingUrbanParameters.setDriverCores(1);
        parkingUrbanParameters.setDriverMemory("1G");
        parkingUrbanParameters.setExecutorMemory("2G");
        parkingUrbanParameters.setExecutorCores(2);
        parkingUrbanParameters.setNumExecutors(2);

        int dataSource = parkingUrbanParameters.getDatasource();
        int dataSource2 = parkingUrbanParameters.getDatasource2();
        int dataStorages = parkingUrbanParameters.getDataStorage();
        int dataSource3= parkingUrbanParameters.getDatasource3();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource ds3 = processDao.findDataSourceById(dataSource3);
        DataSource dssave = processDao.findDataSourceById(dataStorages);

        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String database3 = JSONUtils.toMap(ds3.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        parkingUrbanParameters.setDatabases(database);
        parkingUrbanParameters.setSavebases(datasave);
        parkingUrbanParameters.setLeftTable(database + "." + parkingUrbanParameters.getLeftTable());
        parkingUrbanParameters.setRightTable(database3 + "." + parkingUrbanParameters.getRightTable());
        parkingUrbanParameters.setMiddleTable(database2 + "." + parkingUrbanParameters.getMiddleTable());
        logger.info("左表名为：" + parkingUrbanParameters.getLeftTable());
        logger.info("中间表名为：" + parkingUrbanParameters.getMiddleTable());
        logger.info("左表名为：" + parkingUrbanParameters.getLeftTable());
        String savetable=parkingUrbanParameters.getSavebases()+ "." + parkingUrbanParameters.getNewTable();
        JSONArray createtable=parkingUrbanParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return parkingUrbanParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+parkingUrbanParameters.getDriverCores()+" " +
                "--driver-memory "+parkingUrbanParameters.getDriverMemory()+" " +
                "--num-executors "+parkingUrbanParameters.getNumExecutors()+" " +
                "--executor-cores "+parkingUrbanParameters.getExecutorCores()+" " +
                "--executor-memory "+parkingUrbanParameters.getExecutorMemory()+" --queue default";
        String Mode=parkingUrbanParameters.getDeployMode();
        String p=parkingUrbanParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+parkingUrbanParameters.getStart_time()+ "\""+" "+"\""+parkingUrbanParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  parkingUrbanParameters.getTimeType()+" "+parkingUrbanParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.ParkingUrban" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + parkingUrbanParameters.getMainJar().getRes() + " " +
                parkingUrbanParameters.getMiddleTable()+ " " +
                parkingUrbanParameters.getLeftTable()+ " " +
                parkingUrbanParameters.getRightTable()+ " "+
                StringUtils.join(parkingUrbanParameters.getVehpass(), ",")+" " +
                StringUtils.join(parkingUrbanParameters.getDev(), ",")+" " +
                parkingUrbanParameters.getTimeParam()+" "+
                param+
                parkingUrbanParameters.getSavebases()+"."+parkingUrbanParameters.getNewTable();

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
            this.parkingUrbanParameters = JSONObject.parseObject(taskParams, ParkingUrbanParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(parkingUrbanParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：城区驻车量的时间类型参数不能为空";
            return false;
        }
        if (parkingUrbanParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(parkingUrbanParameters.getStart_time() )&&StringUtils.isEmpty(parkingUrbanParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：城区驻车量的起止时间不能为空";
            return false;
        }
        if (parkingUrbanParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(parkingUrbanParameters.getTimeType() )&&StringUtils.isEmpty(parkingUrbanParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：城区驻车量的系统时间不能为空";
            return false;
        }
        if (parkingUrbanParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (parkingUrbanParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
