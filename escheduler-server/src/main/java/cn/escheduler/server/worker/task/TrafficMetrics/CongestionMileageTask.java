package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.CongestionMileageParameters;
import cn.escheduler.common.task.TrafficMetrics.PassEfficiencyParameters;
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
public class CongestionMileageTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public CongestionMileageTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CongestionMileageParameters congestionMileageParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        congestionMileageParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CongestionMileageParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        congestionMileageParameters.setDeployMode("client");
        congestionMileageParameters.setMainJar(resourceInfo);
        congestionMileageParameters.setDriverCores(1);
        congestionMileageParameters.setDriverMemory("1G");
        congestionMileageParameters.setExecutorMemory("2G");
        congestionMileageParameters.setExecutorCores(2);
        congestionMileageParameters.setNumExecutors(2);

        int dataSource = congestionMileageParameters.getDatasource();
        int dataSource2 = congestionMileageParameters.getDatasource2();
        int dataStorages = congestionMileageParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        congestionMileageParameters.setDatabases(database);
        congestionMileageParameters.setSavebases(datasave);
        logger.info("左表名为：" + congestionMileageParameters.getLeftTable());
        congestionMileageParameters.setLeftTable(database + "." + congestionMileageParameters.getLeftTable());
        congestionMileageParameters.setRightTable(database2 + "." + congestionMileageParameters.getRightTable());
        String savetable=congestionMileageParameters.getSavebases()+ "." + congestionMileageParameters.getNewTable();
        JSONArray createtable=congestionMileageParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return congestionMileageParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+congestionMileageParameters.getDriverCores()+" " +
                "--driver-memory "+congestionMileageParameters.getDriverMemory()+" " +
                "--num-executors "+congestionMileageParameters.getNumExecutors()+" " +
                "--executor-cores "+congestionMileageParameters.getExecutorCores()+" " +
                "--executor-memory "+congestionMileageParameters.getExecutorMemory()+" --queue default";
        String Mode=congestionMileageParameters.getDeployMode();
        String p=congestionMileageParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+congestionMileageParameters.getStart_time()+ "\""+" "+"\""+congestionMileageParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  congestionMileageParameters.getTimeType()+" "+congestionMileageParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CongestionMileage" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + congestionMileageParameters.getMainJar().getRes() + " " +
                congestionMileageParameters.getLeftTable()+ " " +
                congestionMileageParameters.getRightTable()+ " "+
                StringUtils.join(congestionMileageParameters.getVehpass(), ",")+" " +
                StringUtils.join(congestionMileageParameters.getDev(), ",")+" " +
                congestionMileageParameters.getTimeParam()+" "+
                param+
                congestionMileageParameters.getSavebases()+"."+congestionMileageParameters.getNewTable();

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
            this.congestionMileageParameters = JSONObject.parseObject(taskParams, CongestionMileageParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(congestionMileageParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：道路路况里程占比的时间类型参数不能为空";
            return false;
        }
        if (congestionMileageParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(congestionMileageParameters.getStart_time() )&&StringUtils.isEmpty(congestionMileageParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：道路路况里程占比的起止时间不能为空";
            return false;
        }
        if (congestionMileageParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(congestionMileageParameters.getTimeType() )&&StringUtils.isEmpty(congestionMileageParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：道路路况里程占比的系统时间不能为空";
            return false;
        }
        if (congestionMileageParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (congestionMileageParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
