package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.RidImbalanceParameters;
import cn.escheduler.common.task.TrafficMetrics.RidSaturationParameters;
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
public class RidSaturationTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public RidSaturationTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private RidSaturationParameters ridSaturationParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        ridSaturationParameters = JSONUtils.parseObject(taskProps.getTaskParams(), RidSaturationParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        ridSaturationParameters.setDeployMode("client");
        ridSaturationParameters.setMainJar(resourceInfo);
        ridSaturationParameters.setDriverCores(1);
        ridSaturationParameters.setDriverMemory("1G");
        ridSaturationParameters.setExecutorMemory("2G");
        ridSaturationParameters.setExecutorCores(2);
        ridSaturationParameters.setNumExecutors(2);

        int dataSource = ridSaturationParameters.getDatasource();
        int dataSource2 = ridSaturationParameters.getDatasource2();
        int dataStorages = ridSaturationParameters.getDataStorage();
        int dataSource3= ridSaturationParameters.getDatasource3();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource ds3 = processDao.findDataSourceById(dataSource3);
        DataSource dssave = processDao.findDataSourceById(dataStorages);

        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String database3 = JSONUtils.toMap(ds3.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        ridSaturationParameters.setDatabases(database);
        ridSaturationParameters.setSavebases(datasave);
        ridSaturationParameters.setLeftTable(database + "." + ridSaturationParameters.getLeftTable());
        ridSaturationParameters.setRightTable(database3 + "." + ridSaturationParameters.getRightTable());
        ridSaturationParameters.setMiddleTable(database2 + "." + ridSaturationParameters.getMiddleTable());
        logger.info("左表名为：" + ridSaturationParameters.getLeftTable());
        logger.info("中间表名为：" + ridSaturationParameters.getMiddleTable());
        logger.info("左表名为：" + ridSaturationParameters.getLeftTable());
        String savetable=ridSaturationParameters.getSavebases()+ "." + ridSaturationParameters.getNewTable();
        JSONArray createtable=ridSaturationParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return ridSaturationParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+ridSaturationParameters.getDriverCores()+" " +
                "--driver-memory "+ridSaturationParameters.getDriverMemory()+" " +
                "--num-executors "+ridSaturationParameters.getNumExecutors()+" " +
                "--executor-cores "+ridSaturationParameters.getExecutorCores()+" " +
                "--executor-memory "+ridSaturationParameters.getExecutorMemory()+" --queue default";
        String Mode=ridSaturationParameters.getDeployMode();
        String p=ridSaturationParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+ridSaturationParameters.getStart_time()+ "\""+" "+"\""+ridSaturationParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  ridSaturationParameters.getTimeType()+" "+ridSaturationParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.RidSaturation" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + ridSaturationParameters.getMainJar().getRes() + " " +
                ridSaturationParameters.getMiddleTable()+ " " +
                ridSaturationParameters.getLeftTable()+ " " +
                ridSaturationParameters.getRightTable()+ " "+
                StringUtils.join(ridSaturationParameters.getVehpass(), ",")+" " +
                StringUtils.join(ridSaturationParameters.getDev(), ",")+" " +
                ridSaturationParameters.getTimeParam()+" "+
                param+
                ridSaturationParameters.getSavebases()+"."+ridSaturationParameters.getNewTable();

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
            this.ridSaturationParameters = JSONObject.parseObject(taskParams, RidSaturationParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(ridSaturationParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：路段失衡指数的时间类型参数不能为空";
            return false;
        }
        if (ridSaturationParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(ridSaturationParameters.getStart_time() )&&StringUtils.isEmpty(ridSaturationParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：路段失衡指数的起止时间不能为空";
            return false;
        }
        if (ridSaturationParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(ridSaturationParameters.getTimeType() )&&StringUtils.isEmpty(ridSaturationParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：路段失衡指数的系统时间不能为空";
            return false;
        }
        if (ridSaturationParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (ridSaturationParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
