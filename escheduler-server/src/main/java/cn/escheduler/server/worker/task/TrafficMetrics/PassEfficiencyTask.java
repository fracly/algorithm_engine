package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.OnroadUrbanParameters;
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
 * @Date: 2020/12/08
 * @version:
 */
public class PassEfficiencyTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public PassEfficiencyTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private PassEfficiencyParameters passEfficiencyParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        passEfficiencyParameters = JSONUtils.parseObject(taskProps.getTaskParams(), PassEfficiencyParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        passEfficiencyParameters.setDeployMode("client");
        passEfficiencyParameters.setMainJar(resourceInfo);
        passEfficiencyParameters.setDriverCores(1);
        passEfficiencyParameters.setDriverMemory("1G");
        passEfficiencyParameters.setExecutorMemory("2G");
        passEfficiencyParameters.setExecutorCores(2);
        passEfficiencyParameters.setNumExecutors(2);

        int dataSource = passEfficiencyParameters.getDatasource();
        int dataSource2 = passEfficiencyParameters.getDatasource2();
        int dataStorages = passEfficiencyParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        passEfficiencyParameters.setDatabases(database);
        passEfficiencyParameters.setSavebases(datasave);
        logger.info("左表名为：" + passEfficiencyParameters.getLeftTable());
        passEfficiencyParameters.setLeftTable(database + "." + passEfficiencyParameters.getLeftTable());
        passEfficiencyParameters.setRightTable(database2 + "." + passEfficiencyParameters.getRightTable());
        String savetable=passEfficiencyParameters.getSavebases()+ "." + passEfficiencyParameters.getNewTable();
        JSONArray createtable=passEfficiencyParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return passEfficiencyParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+passEfficiencyParameters.getDriverCores()+" " +
                "--driver-memory "+passEfficiencyParameters.getDriverMemory()+" " +
                "--num-executors "+passEfficiencyParameters.getNumExecutors()+" " +
                "--executor-cores "+passEfficiencyParameters.getExecutorCores()+" " +
                "--executor-memory "+passEfficiencyParameters.getExecutorMemory()+" --queue default";
        String Mode=passEfficiencyParameters.getDeployMode();
        String p=passEfficiencyParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+passEfficiencyParameters.getStart_time()+ "\""+" "+"\""+passEfficiencyParameters.getEnd_time()+ "\""+" ";
        }else {
            param =  passEfficiencyParameters.getTimeType()+" "+passEfficiencyParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.PassEfficiency" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + passEfficiencyParameters.getMainJar().getRes() + " " +
                passEfficiencyParameters.getLeftTable()+ " " +
                passEfficiencyParameters.getRightTable()+ " "+
                StringUtils.join(passEfficiencyParameters.getVehpass(), ",")+" " +
                StringUtils.join(passEfficiencyParameters.getDev(), ",")+" " +
                passEfficiencyParameters.getTimeParam()+" "+
                param+
                passEfficiencyParameters.getSavebases()+"."+passEfficiencyParameters.getNewTable();

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
            this.passEfficiencyParameters = JSONObject.parseObject(taskParams, PassEfficiencyParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(passEfficiencyParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：通行效率指数的时间类型参数不能为空";
            return false;
        }
        if (passEfficiencyParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(passEfficiencyParameters.getStart_time() )&&StringUtils.isEmpty(passEfficiencyParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：通行效率指数的起止时间不能为空";
            return false;
        }
        if (passEfficiencyParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(passEfficiencyParameters.getTimeType() )&&StringUtils.isEmpty(passEfficiencyParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：通行效率指数的系统时间不能为空";
            return false;
        }
        if (passEfficiencyParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (passEfficiencyParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
