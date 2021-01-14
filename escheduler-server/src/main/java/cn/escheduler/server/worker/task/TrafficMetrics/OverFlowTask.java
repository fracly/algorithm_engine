package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.OverFlowParameters;
import cn.escheduler.common.task.TrafficMetrics.QueueLengthParameters;
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
public class OverFlowTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public OverFlowTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private OverFlowParameters queueLengthParametersters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        queueLengthParametersters = JSONUtils.parseObject(taskProps.getTaskParams(), OverFlowParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        queueLengthParametersters.setDeployMode("client");
        queueLengthParametersters.setMainJar(resourceInfo);
        queueLengthParametersters.setDriverCores(1);
        queueLengthParametersters.setDriverMemory("1G");
        queueLengthParametersters.setExecutorMemory("2G");
        queueLengthParametersters.setExecutorCores(2);
        queueLengthParametersters.setNumExecutors(2);

        int dataSource = queueLengthParametersters.getDatasource();
        int dataSource2 = queueLengthParametersters.getDatasource2();
        int dataStorages = queueLengthParametersters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        queueLengthParametersters.setDatabases(database);
        queueLengthParametersters.setSavebases(datasave);
        logger.info("左表名为：" + queueLengthParametersters.getLeftTable());
        queueLengthParametersters.setLeftTable(database + "." + queueLengthParametersters.getLeftTable());
        queueLengthParametersters.setRightTable(database2 + "." + queueLengthParametersters.getRightTable());
        String savetable=queueLengthParametersters.getSavebases()+ "." + queueLengthParametersters.getNewTable();
        JSONArray createtable=queueLengthParametersters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return queueLengthParametersters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+queueLengthParametersters.getDriverCores()+" " +
                "--driver-memory "+queueLengthParametersters.getDriverMemory()+" " +
                "--num-executors "+queueLengthParametersters.getNumExecutors()+" " +
                "--executor-cores "+queueLengthParametersters.getExecutorCores()+" " +
                "--executor-memory "+queueLengthParametersters.getExecutorMemory()+" --queue default";
        String Mode=queueLengthParametersters.getDeployMode();
        String p=queueLengthParametersters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+queueLengthParametersters.getStart_time()+ "\""+" "+"\""+queueLengthParametersters.getEnd_time()+ "\""+" ";
        }else {
            param =  queueLengthParametersters.getTimeType()+" "+queueLengthParametersters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.OverFlow" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + queueLengthParametersters.getMainJar().getRes() + " " +
                queueLengthParametersters.getLeftTable()+ " " +
                StringUtils.join(queueLengthParametersters.getVehpass(), ",")+" " +
                queueLengthParametersters.getRightTable()+ " "+
                StringUtils.join(queueLengthParametersters.getLane(), ",")+" " +
                param+
                queueLengthParametersters.getSavebases()+"."+queueLengthParametersters.getNewTable()+" "+queueLengthParametersters.getTimeParam();

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
            this.queueLengthParametersters = JSONObject.parseObject(taskParams, OverFlowParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(queueLengthParametersters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：溢流的时间类型参数不能为空";
            return false;
        }
        if (queueLengthParametersters.getTimeParam().equals("1")&&StringUtils.isEmpty(queueLengthParametersters.getStart_time() )&&StringUtils.isEmpty(queueLengthParametersters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：溢流的起止时间不能为空";
            return false;
        }
        if (queueLengthParametersters.getTimeParam().equals("2")&&StringUtils.isEmpty(queueLengthParametersters.getTimeType() )&&StringUtils.isEmpty(queueLengthParametersters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：溢流的系统时间不能为空";
            return false;
        }
        if (queueLengthParametersters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (queueLengthParametersters.getLane().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
