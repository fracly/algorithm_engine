package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.TravelTimeParameters;
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
public class TravelTimeTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public TravelTimeTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TravelTimeParameters travelTimeParameters ;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        travelTimeParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TravelTimeParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        travelTimeParameters.setDeployMode("client");
        travelTimeParameters.setMainJar(resourceInfo);
        travelTimeParameters.setDriverCores(1);
        travelTimeParameters.setDriverMemory("1G");
        travelTimeParameters.setExecutorMemory("2G");
        travelTimeParameters.setExecutorCores(2);
        travelTimeParameters.setNumExecutors(2);

        int dataSource = travelTimeParameters.getDatasource();
        int dataSource2 = travelTimeParameters.getDatasource2();
        int dataStorages = travelTimeParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        travelTimeParameters.setDatabases(database);
        travelTimeParameters.setSavebases(datasave);
        logger.info("左表名为：" + travelTimeParameters.getLeftTable());
        travelTimeParameters.setLeftTable(database + "." + travelTimeParameters.getLeftTable());
        travelTimeParameters.setRightTable(database2 + "." + travelTimeParameters.getRightTable());
        String savetable=travelTimeParameters.getSavebases()+ "." + travelTimeParameters.getNewTable();
        JSONArray createtable=travelTimeParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return travelTimeParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+travelTimeParameters.getDriverCores()+" " +
                "--driver-memory "+travelTimeParameters.getDriverMemory()+" " +
                "--num-executors "+travelTimeParameters.getNumExecutors()+" " +
                "--executor-cores "+travelTimeParameters.getExecutorCores()+" " +
                "--executor-memory "+travelTimeParameters.getExecutorMemory()+" --queue default";
        String Mode=travelTimeParameters.getDeployMode();
        String p=travelTimeParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = travelTimeParameters.getStart_time()+" "+travelTimeParameters.getEnd_time()+" ";
        }else {
            param =  travelTimeParameters.getTimeType()+" "+travelTimeParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TravelTime" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + travelTimeParameters.getMainJar().getRes() + " " +
                travelTimeParameters.getLeftTable()+ " " +
                StringUtils.join(travelTimeParameters.getVehpass(), ",")+" " +
                travelTimeParameters.getRightTable()+ " "+
                StringUtils.join(travelTimeParameters.getLane(), ",")+" " +
                param+
                travelTimeParameters.getSavebases()+"."+travelTimeParameters.getNewTable()+" "+travelTimeParameters.getTimeParam();

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
            this.travelTimeParameters = JSONObject.parseObject(taskParams, TravelTimeParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(travelTimeParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：行程时间的时间类型参数不能为空";
            return false;
        }
        if (travelTimeParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(travelTimeParameters.getStart_time() )&&StringUtils.isEmpty(travelTimeParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：行程时间的起止时间不能为空";
            return false;
        }
        if (travelTimeParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(travelTimeParameters.getTimeType() )&&StringUtils.isEmpty(travelTimeParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：行程时间的系统时间不能为空";
            return false;
        }

        if (travelTimeParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (travelTimeParameters.getLane().size()==0){
            logger.error("lane is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
