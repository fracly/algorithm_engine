package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.TravelSpeedParameters;
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
 * @Date: 2020/11/25
 * @version:
 */
public class TravelSpeedTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     *
     */
    public TravelSpeedTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TravelSpeedParameters  travelSpeedParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        travelSpeedParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TravelSpeedParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        travelSpeedParameters.setDeployMode("client");
        travelSpeedParameters.setMainJar(resourceInfo);
        travelSpeedParameters.setDriverCores(1);
        travelSpeedParameters.setDriverMemory("1G");
        travelSpeedParameters.setExecutorMemory("2G");
        travelSpeedParameters.setExecutorCores(2);
        travelSpeedParameters.setNumExecutors(2);

        int dataSource = travelSpeedParameters.getDatasource();
        int dataSource2 = travelSpeedParameters.getDatasource2();
        int dataStorages = travelSpeedParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        travelSpeedParameters.setDatabases(database);
        travelSpeedParameters.setSavebases(datasave);
        logger.info("左表名为：" + travelSpeedParameters.getLeftTable());
        travelSpeedParameters.setLeftTable(database + "." + travelSpeedParameters.getLeftTable());
        travelSpeedParameters.setRightTable(database2 + "." + travelSpeedParameters.getRightTable());
        String savetable=travelSpeedParameters.getSavebases()+ "." + travelSpeedParameters.getNewTable();
        JSONArray createtable=travelSpeedParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return travelSpeedParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+travelSpeedParameters.getDriverCores()+" " +
                "--driver-memory "+travelSpeedParameters.getDriverMemory()+" " +
                "--num-executors "+travelSpeedParameters.getNumExecutors()+" " +
                "--executor-cores "+travelSpeedParameters.getExecutorCores()+" " +
                "--executor-memory "+travelSpeedParameters.getExecutorMemory()+" --queue default";
        String Mode=travelSpeedParameters.getDeployMode();
        String p=travelSpeedParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+travelSpeedParameters.getStart_time()+ "\""+" "+"\""+travelSpeedParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  travelSpeedParameters.getTimeType()+" "+travelSpeedParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TravelSpeed" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + travelSpeedParameters.getMainJar().getRes() + " " +
                travelSpeedParameters.getLeftTable()+ " " +
                StringUtils.join(travelSpeedParameters.getVehpass(), ",")+" " +
                travelSpeedParameters.getRightTable()+ " "+
                StringUtils.join(travelSpeedParameters.getLane(), ",")+" " +
                param+
                travelSpeedParameters.getSavebases()+"."+travelSpeedParameters.getNewTable()+" "+travelSpeedParameters.getTimeParam();

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
            this.travelSpeedParameters = JSONObject.parseObject(taskParams, TravelSpeedParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(travelSpeedParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：行程速度的时间类型参数不能为空";
            return false;
        }
        if (travelSpeedParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(travelSpeedParameters.getStart_time() )&&StringUtils.isEmpty(travelSpeedParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：行程速度的起止时间不能为空";
            return false;
        }
        if (travelSpeedParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(travelSpeedParameters.getTimeType() )&&StringUtils.isEmpty(travelSpeedParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：行程速度的系统时间不能为空";
            return false;
        }

        if (travelSpeedParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (travelSpeedParameters.getLane().size()==0){
            logger.error("lane is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
