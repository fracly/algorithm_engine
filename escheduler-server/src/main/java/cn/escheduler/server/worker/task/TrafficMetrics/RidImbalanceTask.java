package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.ParkingUrbanParameters;
import cn.escheduler.common.task.TrafficMetrics.RidImbalanceParameters;
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
 * @Date: 2020/12/22
 * @version:
 */
public class RidImbalanceTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public RidImbalanceTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private RidImbalanceParameters ridImbalanceParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        ridImbalanceParameters = JSONUtils.parseObject(taskProps.getTaskParams(), RidImbalanceParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        ridImbalanceParameters.setDeployMode("client");
        ridImbalanceParameters.setMainJar(resourceInfo);
        ridImbalanceParameters.setDriverCores(1);
        ridImbalanceParameters.setDriverMemory("1G");
        ridImbalanceParameters.setExecutorMemory("2G");
        ridImbalanceParameters.setExecutorCores(2);
        ridImbalanceParameters.setNumExecutors(2);

        int dataSource = ridImbalanceParameters.getDatasource();
        int dataSource2 = ridImbalanceParameters.getDatasource2();
        int dataStorages = ridImbalanceParameters.getDataStorage();
        int dataSource3= ridImbalanceParameters.getDatasource3();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource ds3 = processDao.findDataSourceById(dataSource3);
        DataSource dssave = processDao.findDataSourceById(dataStorages);

        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String database3 = JSONUtils.toMap(ds3.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        ridImbalanceParameters.setDatabases(database);
        ridImbalanceParameters.setSavebases(datasave);
        ridImbalanceParameters.setLeftTable(database + "." + ridImbalanceParameters.getLeftTable());
        ridImbalanceParameters.setRightTable(database3 + "." + ridImbalanceParameters.getRightTable());
        ridImbalanceParameters.setMiddleTable(database2 + "." + ridImbalanceParameters.getMiddleTable());
        logger.info("左表名为：" + ridImbalanceParameters.getLeftTable());
        logger.info("中间表名为：" + ridImbalanceParameters.getMiddleTable());
        logger.info("左表名为：" + ridImbalanceParameters.getLeftTable());
        String savetable=ridImbalanceParameters.getSavebases()+ "." + ridImbalanceParameters.getNewTable();
        JSONArray createtable=ridImbalanceParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return ridImbalanceParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+ridImbalanceParameters.getDriverCores()+" " +
                "--driver-memory "+ridImbalanceParameters.getDriverMemory()+" " +
                "--num-executors "+ridImbalanceParameters.getNumExecutors()+" " +
                "--executor-cores "+ridImbalanceParameters.getExecutorCores()+" " +
                "--executor-memory "+ridImbalanceParameters.getExecutorMemory()+" --queue default";
        String Mode=ridImbalanceParameters.getDeployMode();
        String p=ridImbalanceParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+ridImbalanceParameters.getStart_time()+ "\""+" "+"\""+ridImbalanceParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  ridImbalanceParameters.getTimeType()+" "+ridImbalanceParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.RidImbalance" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + ridImbalanceParameters.getMainJar().getRes() + " " +
                ridImbalanceParameters.getMiddleTable()+ " " +
                ridImbalanceParameters.getLeftTable()+ " " +
                ridImbalanceParameters.getRightTable()+ " "+
                StringUtils.join(ridImbalanceParameters.getVehpass(), ",")+" " +
                StringUtils.join(ridImbalanceParameters.getDev(), ",")+" " +
                ridImbalanceParameters.getTimeParam()+" "+
                param+
                ridImbalanceParameters.getSavebases()+"."+ridImbalanceParameters.getNewTable();

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
            this.ridImbalanceParameters = JSONObject.parseObject(taskParams, RidImbalanceParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(ridImbalanceParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：路段失衡指数的时间类型参数不能为空";
            return false;
        }
        if (ridImbalanceParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(ridImbalanceParameters.getStart_time() )&&StringUtils.isEmpty(ridImbalanceParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：路段失衡指数的起止时间不能为空";
            return false;
        }
        if (ridImbalanceParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(ridImbalanceParameters.getTimeType() )&&StringUtils.isEmpty(ridImbalanceParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：路段失衡指数的系统时间不能为空";
            return false;
        }
        if (ridImbalanceParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (ridImbalanceParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
