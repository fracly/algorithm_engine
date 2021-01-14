package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.TrafficFlowParameters;
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
public class TrafficFlowTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public TrafficFlowTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private TrafficFlowParameters trafficFlowParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        trafficFlowParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TrafficFlowParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        trafficFlowParameters.setDeployMode("client");
        trafficFlowParameters.setMainJar(resourceInfo);
        trafficFlowParameters.setDriverCores(1);
        trafficFlowParameters.setDriverMemory("1G");
        trafficFlowParameters.setExecutorMemory("2G");
        trafficFlowParameters.setExecutorCores(2);
        trafficFlowParameters.setNumExecutors(2);

        int dataSource = trafficFlowParameters.getDatasource();
        int dataSource2 = trafficFlowParameters.getDatasource2();
        int dataStorages = trafficFlowParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        trafficFlowParameters.setDatabases(database);
        trafficFlowParameters.setSavebases(datasave);
        logger.info("左表名为：" + trafficFlowParameters.getLeftTable());
        trafficFlowParameters.setLeftTable(database + "." + trafficFlowParameters.getLeftTable());
        trafficFlowParameters.setRightTable(database2 + "." + trafficFlowParameters.getRightTable());
        String savetable=trafficFlowParameters.getSavebases()+ "." + trafficFlowParameters.getNewTable();
        JSONArray createtable=trafficFlowParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return trafficFlowParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+trafficFlowParameters.getDriverCores()+" " +
                "--driver-memory "+trafficFlowParameters.getDriverMemory()+" " +
                "--num-executors "+trafficFlowParameters.getNumExecutors()+" " +
                "--executor-cores "+trafficFlowParameters.getExecutorCores()+" " +
                "--executor-memory "+trafficFlowParameters.getExecutorMemory()+" --queue default";
        String Mode=trafficFlowParameters.getDeployMode();
        String p=trafficFlowParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = trafficFlowParameters.getStart_time()+" "+trafficFlowParameters.getEnd_time()+" ";
        }else {
            param =  trafficFlowParameters.getTimeType()+" "+trafficFlowParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.TrafficFlow" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + trafficFlowParameters.getMainJar().getRes() + " " +
                trafficFlowParameters.getLeftTable()+ " " +trafficFlowParameters.getHphm()+ " " +
                trafficFlowParameters.getHpzl()+ " " +trafficFlowParameters.getJgsj()+ " " +trafficFlowParameters.getSbbh()+ " " +
                trafficFlowParameters.getRightTable()+ " " +trafficFlowParameters.getDevc_no()+ " "+trafficFlowParameters.getLoadnet_inter_id()+" "+
                trafficFlowParameters.getLoadnet_rid()+" " +trafficFlowParameters.getLane_id()+ " "+ trafficFlowParameters.getStart_cross_id()+ " "+
                trafficFlowParameters.getEnd_cross_id()+ " "+ trafficFlowParameters.getAngle()+ " "+ param+
                trafficFlowParameters.getSavebases()+"."+trafficFlowParameters.getNewTable()+" "+trafficFlowParameters.getSign()+" "+
                trafficFlowParameters.getTimeParam();

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
            this.trafficFlowParameters = JSONObject.parseObject(taskParams, TrafficFlowParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(trafficFlowParameters.getTimeParam() )){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：交通流量的时间类型参数不能为空";
            return false;
        }
        if (trafficFlowParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(trafficFlowParameters.getStart_time() )&&StringUtils.isEmpty(trafficFlowParameters.getEnd_time() )){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：交通流量的起止时间不能为空";
            return false;
        }
        if (trafficFlowParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(trafficFlowParameters.getTimeType() )&&StringUtils.isEmpty(trafficFlowParameters.getValue() )){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：交通流量的系统时间不能为空";
            return false;
        }
        if (StringUtils.isEmpty(trafficFlowParameters.getSign())){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：交通流量的指标不能为空";
            return false;
        }
        if (StringUtils.isEmpty(trafficFlowParameters.getHphm())&&StringUtils.isEmpty(trafficFlowParameters.getHpzl())&&
                StringUtils.isEmpty(trafficFlowParameters.getJgsj())&&StringUtils.isEmpty(trafficFlowParameters.getSbbh())){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (StringUtils.isEmpty(trafficFlowParameters.getLane_id())&&StringUtils.isEmpty(trafficFlowParameters.getLoadnet_rid())
        &&StringUtils.isEmpty(trafficFlowParameters.getDevc_no())&&StringUtils.isEmpty(trafficFlowParameters.getLoadnet_inter_id())
        &&StringUtils.isEmpty(trafficFlowParameters.getStart_cross_id())&&StringUtils.isEmpty(trafficFlowParameters.getEnd_cross_id())&&StringUtils.isEmpty(trafficFlowParameters.getAngle())){
            logger.error("duplicateColumns is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
