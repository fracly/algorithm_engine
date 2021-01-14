package cn.escheduler.server.worker.task.TrafficMetrics;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.BasePasstimeParameters;
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
public class BasePasstimeTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 去重列
     */
    public BasePasstimeTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }

    private static final String SPARK_COMMAND = "spark-submit";

    private BasePasstimeParameters basePasstimeParameters;

    @Override
    public void init() {

        logger.info("linear regression task params {}", taskProps.getTaskParams());


        basePasstimeParameters = JSONUtils.parseObject(taskProps.getTaskParams(), BasePasstimeParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        basePasstimeParameters.setDeployMode("client");
        basePasstimeParameters.setMainJar(resourceInfo);
        basePasstimeParameters.setDriverCores(1);
        basePasstimeParameters.setDriverMemory("1G");
        basePasstimeParameters.setExecutorMemory("2G");
        basePasstimeParameters.setExecutorCores(2);
        basePasstimeParameters.setNumExecutors(2);

        int dataSource = basePasstimeParameters.getDatasource();
        int dataSource2 = basePasstimeParameters.getDatasource2();
        int dataStorages = basePasstimeParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource ds2 = processDao.findDataSourceById(dataSource2);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String database2 = JSONUtils.toMap(ds2.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        basePasstimeParameters.setDatabases(database);
        basePasstimeParameters.setSavebases(datasave);
        logger.info("左表名为：" + basePasstimeParameters.getLeftTable());
        basePasstimeParameters.setLeftTable(database + "." + basePasstimeParameters.getLeftTable());
        basePasstimeParameters.setRightTable(database2 + "." + basePasstimeParameters.getRightTable());
        String savetable=basePasstimeParameters.getSavebases()+ "." + basePasstimeParameters.getNewTable();
        JSONArray createtable=basePasstimeParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return basePasstimeParameters;
    }

    @Override
    protected String buildCommand()   {

        String para=" --driver-cores "+basePasstimeParameters.getDriverCores()+" " +
                "--driver-memory "+basePasstimeParameters.getDriverMemory()+" " +
                "--num-executors "+basePasstimeParameters.getNumExecutors()+" " +
                "--executor-cores "+basePasstimeParameters.getExecutorCores()+" " +
                "--executor-memory "+basePasstimeParameters.getExecutorMemory()+" --queue default";
        String Mode=basePasstimeParameters.getDeployMode();
        String p=basePasstimeParameters.getTimeParam();
        String param="";
        logger.info(p);
        if(p.equals("1")){
            param  = "\""+basePasstimeParameters.getStart_time()+ "\""+" "+"\""+basePasstimeParameters.getEnd_time()+ "\""+" ";

        }else {
            param =  basePasstimeParameters.getTimeType()+" "+basePasstimeParameters.getValue()+" ";
        }
        logger.info(param);
        String command = "spark-submit  " +
                "--class " + "com.bcht.algorithms.engine.BasePasstime" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + basePasstimeParameters.getMainJar().getRes() + " " +
                basePasstimeParameters.getSign()+ " " +
                basePasstimeParameters.getLeftTable()+ " " +
                basePasstimeParameters.getRightTable()+ " "+
                StringUtils.join(basePasstimeParameters.getVehpass(), ",")+" " +
                StringUtils.join(basePasstimeParameters.getDev(), ",")+" " +
                basePasstimeParameters.getTimeParam()+" "+
                param+
                basePasstimeParameters.getSavebases()+"."+basePasstimeParameters.getNewTable();

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
            this.basePasstimeParameters = JSONObject.parseObject(taskParams, BasePasstimeParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (StringUtils.isEmpty(basePasstimeParameters.getTimeParam() )){
            logger.error("timeParam is null");
            errorMsg="参数有误：历史通行时长参数时间类型参数不能为空";
            return false;
        }
        if (basePasstimeParameters.getTimeParam().equals("1")&&StringUtils.isEmpty(basePasstimeParameters.getStart_time() )&&StringUtils.isEmpty(basePasstimeParameters.getEnd_time() )){
            logger.error("start_time or end_time is null");
            errorMsg="参数有误：历史通行时长参数的起止时间不能为空";
            return false;
        }
        if (basePasstimeParameters.getTimeParam().equals("2")&&StringUtils.isEmpty(basePasstimeParameters.getTimeType() )&&StringUtils.isEmpty(basePasstimeParameters.getValue() )){
            logger.error("time is null");
            errorMsg="参数有误：历史通行时长参数的系统时间不能为空";
            return false;
        }
        if (StringUtils.isEmpty(basePasstimeParameters.getSign())){
            logger.error("sign is null");
            errorMsg="参数有误：历史通行时长参数的指标不能为空";
            return false;
        }
        if (basePasstimeParameters.getVehpass().size()==0){
            logger.error("vehpass is null");
            errorMsg="参数有误：左表的列不能为空";
            return false;
        }
        if (basePasstimeParameters.getDev().size()==0){
            logger.error("dev is null");
            errorMsg="参数有误：右表的列不能为空";
            return false;
        }
        return true;
    }
}
