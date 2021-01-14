package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.EntryLeavecityParameters;
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
public class EntryLeavecityTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public EntryLeavecityTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private EntryLeavecityParameters entryLeavecityParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        entryLeavecityParameters = JSONUtils.parseObject(taskProps.getTaskParams(), EntryLeavecityParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        entryLeavecityParameters.setDeployMode("client");
        entryLeavecityParameters.setMainJar(resourceInfo);
        entryLeavecityParameters.setDriverCores(1);
        entryLeavecityParameters.setDriverMemory("1G");
        entryLeavecityParameters.setExecutorMemory("2G");
        entryLeavecityParameters.setExecutorCores(2);
        entryLeavecityParameters.setNumExecutors(2);

        int dataSource = entryLeavecityParameters.getDatasource();
        int dataSource2 = entryLeavecityParameters.getDatasource2();
        int dataStorages = entryLeavecityParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        entryLeavecityParameters.setDatabases(database);
        entryLeavecityParameters.setSavebases(datasave);
        logger.info("左表名为：" + entryLeavecityParameters.getLeftTable());
        entryLeavecityParameters.setLeftTable(database + "." + entryLeavecityParameters.getLeftTable());
        entryLeavecityParameters.setRightTable(database2 + "." + entryLeavecityParameters.getRightTable());
        String savetable=entryLeavecityParameters.getSavebases()+ "." + entryLeavecityParameters.getNewTable();
        JSONArray createtable=entryLeavecityParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return entryLeavecityParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+entryLeavecityParameters.getDriverCores()+" " +
                "--driver-memory "+entryLeavecityParameters.getDriverMemory()+" " +
                "--num-executors "+entryLeavecityParameters.getNumExecutors()+" " +
                "--executor-cores "+entryLeavecityParameters.getExecutorCores()+" " +
                "--executor-memory "+entryLeavecityParameters.getExecutorMemory()+" --queue default";
        String Mode=entryLeavecityParameters.getDeployMode();
        String p=entryLeavecityParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+entryLeavecityParameters.getStart_time()+ "\""+" "+"\""+entryLeavecityParameters.getEnd_time()+ "\""+" ";

        }else {
            param =  entryLeavecityParameters.getTimeType()+" "+entryLeavecityParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.EntryLeavecity" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + entryLeavecityParameters.getMainJar().getRes() + " " +
                entryLeavecityParameters.getSign()+ " " +
                entryLeavecityParameters.getLeftTable()+ " " +
                entryLeavecityParameters.getRightTable()+ " "+
                StringUtils.join(entryLeavecityParameters.getVehpass(), ",")+" " +
                StringUtils.join(entryLeavecityParameters.getDev(), ",")+" " +
                entryLeavecityParameters.getTimeParam()+" "+
                param+
                entryLeavecityParameters.getSavebases()+"."+entryLeavecityParameters.getNewTable();

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
            this.entryLeavecityParameters = JSONObject.parseObject(taskParams, EntryLeavecityParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(entryLeavecityParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：交通流量的时间类型参数不能为空";
            return false;
        }
        if (entryLeavecityParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(entryLeavecityParameters.getStart_time() )&&StringUtils.isEmpty(entryLeavecityParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：交通流量的起止时间不能为空";
            return false;
        }
        if (entryLeavecityParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(entryLeavecityParameters.getTimeType() )&&StringUtils.isEmpty(entryLeavecityParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：交通流量的系统时间不能为空";
            return false;
        }
        if (StringUtils.isEmpty(entryLeavecityParameters.getSign())){
            logger.error("sign is null");
            errorMsg="参数有误：交通流量的指标不能为空";
            return false;
        }
        if (entryLeavecityParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (entryLeavecityParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
