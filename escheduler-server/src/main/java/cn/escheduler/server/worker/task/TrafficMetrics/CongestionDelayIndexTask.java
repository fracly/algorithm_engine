package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.CongestionDelayIndexParameters;
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
public class CongestionDelayIndexTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     *
     */
    public CongestionDelayIndexTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private CongestionDelayIndexParameters congestionDelayIndexParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        congestionDelayIndexParameters = JSONUtils.parseObject(taskProps.getTaskParams(), CongestionDelayIndexParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        congestionDelayIndexParameters.setDeployMode("client");
        congestionDelayIndexParameters.setMainJar(resourceInfo);
        congestionDelayIndexParameters.setDriverCores(1);
        congestionDelayIndexParameters.setDriverMemory("1G");
        congestionDelayIndexParameters.setExecutorMemory("2G");
        congestionDelayIndexParameters.setExecutorCores(2);
        congestionDelayIndexParameters.setNumExecutors(2);

        int dataSource = congestionDelayIndexParameters.getDatasource();
        int dataSource2 = congestionDelayIndexParameters.getDatasource2();
        int dataStorages = congestionDelayIndexParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        congestionDelayIndexParameters.setDatabases(database);
        congestionDelayIndexParameters.setSavebases(datasave);
        logger.info("左表名为：" + congestionDelayIndexParameters.getLeftTable());
        congestionDelayIndexParameters.setLeftTable(database + "." + congestionDelayIndexParameters.getLeftTable());
        congestionDelayIndexParameters.setRightTable(database2 + "." + congestionDelayIndexParameters.getRightTable());
        String savetable=congestionDelayIndexParameters.getSavebases()+ "." + congestionDelayIndexParameters.getNewTable();
        JSONArray createtable=congestionDelayIndexParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return congestionDelayIndexParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+congestionDelayIndexParameters.getDriverCores()+" " +
                "--driver-memory "+congestionDelayIndexParameters.getDriverMemory()+" " +
                "--num-executors "+congestionDelayIndexParameters.getNumExecutors()+" " +
                "--executor-cores "+congestionDelayIndexParameters.getExecutorCores()+" " +
                "--executor-memory "+congestionDelayIndexParameters.getExecutorMemory()+" --queue default";
        String Mode=congestionDelayIndexParameters.getDeployMode();
        String p=congestionDelayIndexParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+congestionDelayIndexParameters.getStart_time()+ "\""+" "+"\""+congestionDelayIndexParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  congestionDelayIndexParameters.getTimeType()+" "+congestionDelayIndexParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.CongestionDelayIndex" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + congestionDelayIndexParameters.getMainJar().getRes() + " " +
                congestionDelayIndexParameters.getLeftTable()+ " " +
                StringUtils.join(congestionDelayIndexParameters.getVehpass(), ",")+" " +
                congestionDelayIndexParameters.getRightTable()+ " "+
                StringUtils.join(congestionDelayIndexParameters.getLane(), ",")+" " +
                param+
                congestionDelayIndexParameters.getSavebases()+"."+congestionDelayIndexParameters.getNewTable()+" "+congestionDelayIndexParameters.getTimeParam();

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
            this.congestionDelayIndexParameters = JSONObject.parseObject(taskParams, CongestionDelayIndexParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(congestionDelayIndexParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：拥堵延时指数的时间类型参数不能为空";
            return false;
        }
        if (congestionDelayIndexParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(congestionDelayIndexParameters.getStart_time() )&&StringUtils.isEmpty(congestionDelayIndexParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：拥堵延时指数的起止时间不能为空";
            return false;
        }
        if (congestionDelayIndexParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(congestionDelayIndexParameters.getTimeType() )&&StringUtils.isEmpty(congestionDelayIndexParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：拥堵延时指数的系统时间不能为空";
            return false;
        }

        if (congestionDelayIndexParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (congestionDelayIndexParameters.getLane().size()==0){
            logger.error("lane is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
