package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.OnroadUrbanParameters;
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
 * @Date: 2020/12/08
 * @version:
 */
public class OnroadUrbanTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public OnroadUrbanTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private OnroadUrbanParameters onroadUrbanParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        onroadUrbanParameters = JSONUtils.parseObject(taskProps.getTaskParams(), OnroadUrbanParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        onroadUrbanParameters.setDeployMode("client");
        onroadUrbanParameters.setMainJar(resourceInfo);
        onroadUrbanParameters.setDriverCores(1);
        onroadUrbanParameters.setDriverMemory("1G");
        onroadUrbanParameters.setExecutorMemory("2G");
        onroadUrbanParameters.setExecutorCores(2);
        onroadUrbanParameters.setNumExecutors(2);

        int dataSource = onroadUrbanParameters.getDatasource();
        int dataSource2 = onroadUrbanParameters.getDatasource2();
        int dataStorages = onroadUrbanParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        onroadUrbanParameters.setDatabases(database);
        onroadUrbanParameters.setSavebases(datasave);
        logger.info("左表名为：" + onroadUrbanParameters.getLeftTable());
        onroadUrbanParameters.setLeftTable(database + "." + onroadUrbanParameters.getLeftTable());
        onroadUrbanParameters.setRightTable(database2 + "." + onroadUrbanParameters.getRightTable());
        String savetable=onroadUrbanParameters.getSavebases()+ "." + onroadUrbanParameters.getNewTable();
        JSONArray createtable=onroadUrbanParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return onroadUrbanParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+onroadUrbanParameters.getDriverCores()+" " +
                "--driver-memory "+onroadUrbanParameters.getDriverMemory()+" " +
                "--num-executors "+onroadUrbanParameters.getNumExecutors()+" " +
                "--executor-cores "+onroadUrbanParameters.getExecutorCores()+" " +
                "--executor-memory "+onroadUrbanParameters.getExecutorMemory()+" --queue default";
        String Mode=onroadUrbanParameters.getDeployMode();
        String p=onroadUrbanParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+onroadUrbanParameters.getStart_time()+ "\""+" "+"\""+onroadUrbanParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  onroadUrbanParameters.getTimeType()+" "+onroadUrbanParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.OnroadUrban" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + onroadUrbanParameters.getMainJar().getRes() + " " +
                onroadUrbanParameters.getLeftTable()+ " " +
                onroadUrbanParameters.getRightTable()+ " "+
                StringUtils.join(onroadUrbanParameters.getVehpass(), ",")+" " +
                StringUtils.join(onroadUrbanParameters.getDev(), ",")+" " +
                onroadUrbanParameters.getTimeParam()+" "+
                param+
                onroadUrbanParameters.getSavebases()+"."+onroadUrbanParameters.getNewTable();

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
            this.onroadUrbanParameters = JSONObject.parseObject(taskParams, OnroadUrbanParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(onroadUrbanParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：在途量的时间类型参数不能为空";
            return false;
        }
        if (onroadUrbanParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(onroadUrbanParameters.getStart_time() )&&StringUtils.isEmpty(onroadUrbanParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：在途量的起止时间不能为空";
            return false;
        }
        if (onroadUrbanParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(onroadUrbanParameters.getTimeType() )&&StringUtils.isEmpty(onroadUrbanParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：在途量的系统时间不能为空";
            return false;
        }
        if (onroadUrbanParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (onroadUrbanParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
