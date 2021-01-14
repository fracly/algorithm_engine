package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.DelayTimeParameters;
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
public class DelayTimeTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public DelayTimeTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private DelayTimeParameters delayTimeParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        delayTimeParameters = JSONUtils.parseObject(taskProps.getTaskParams(), DelayTimeParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        delayTimeParameters.setDeployMode("client");
        delayTimeParameters.setMainJar(resourceInfo);
        delayTimeParameters.setDriverCores(1);
        delayTimeParameters.setDriverMemory("1G");
        delayTimeParameters.setExecutorMemory("2G");
        delayTimeParameters.setExecutorCores(2);
        delayTimeParameters.setNumExecutors(2);

        int dataSource = delayTimeParameters.getDatasource();
        int dataSource2 = delayTimeParameters.getDatasource2();
        int dataStorages = delayTimeParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        delayTimeParameters.setDatabases(database);
        delayTimeParameters.setSavebases(datasave);
        logger.info("左表名为：" + delayTimeParameters.getLeftTable());
        delayTimeParameters.setLeftTable(database + "." + delayTimeParameters.getLeftTable());
        delayTimeParameters.setRightTable(database2 + "." + delayTimeParameters.getRightTable());
        String savetable=delayTimeParameters.getSavebases()+ "." + delayTimeParameters.getNewTable();
        JSONArray createtable=delayTimeParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return delayTimeParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+delayTimeParameters.getDriverCores()+" " +
                "--driver-memory "+delayTimeParameters.getDriverMemory()+" " +
                "--num-executors "+delayTimeParameters.getNumExecutors()+" " +
                "--executor-cores "+delayTimeParameters.getExecutorCores()+" " +
                "--executor-memory "+delayTimeParameters.getExecutorMemory()+" --queue default";
        String Mode=delayTimeParameters.getDeployMode();
        String p=delayTimeParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+delayTimeParameters.getStart_time()+ "\""+" "+"\""+delayTimeParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  delayTimeParameters.getTimeType()+" "+delayTimeParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.DelayTime" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + delayTimeParameters.getMainJar().getRes() + " " +
                delayTimeParameters.getLeftTable()+ " " +
                StringUtils.join(delayTimeParameters.getVehpass(), ",")+" " +
                delayTimeParameters.getRightTable()+ " "+
                StringUtils.join(delayTimeParameters.getLane(), ",")+" " +
                param+
                delayTimeParameters.getSavebases()+"."+delayTimeParameters.getNewTable()+" "+delayTimeParameters.getTimeParam();

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
            this.delayTimeParameters = JSONObject.parseObject(taskParams, DelayTimeParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(delayTimeParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：延误时间的时间类型参数不能为空";
            return false;
        }
        if (delayTimeParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(delayTimeParameters.getStart_time() )&&StringUtils.isEmpty(delayTimeParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：延误时间的起止时间不能为空";
            return false;
        }
        if (delayTimeParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(delayTimeParameters.getTimeType() )&&StringUtils.isEmpty(delayTimeParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：延误时间的系统时间不能为空";
            return false;
        }
        if (delayTimeParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (delayTimeParameters.getLane().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
