package cn.escheduler.server.worker.task.preprocess;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.preprocess.AddColumnParameters;
import cn.escheduler.common.task.preprocess.SplitParameters;
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

public class SplitTask extends AbstractYarnTask {

    /**
     * @param taskProps
     * @param logger
     */
    public SplitTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private SplitParameters splitParameters;

    @Override
    public void init() {

        logger.info("foracast task params {}", taskProps.getTaskParams());

        splitParameters = JSONUtils.parseObject(taskProps.getTaskParams(), SplitParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        splitParameters.setDeployMode("client");
        splitParameters.setMainJar(resourceInfo);
        splitParameters.setDriverCores(1);
        splitParameters.setDriverMemory("1G");
        splitParameters.setExecutorMemory("2G");
        splitParameters.setExecutorCores(2);
        splitParameters.setNumExecutors(2);
        splitParameters.setProgramType(ProgramType.SCALA);

        int dataSource = splitParameters.getDatasource();
        int dataStorages = splitParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        splitParameters.setDatabases(database);
        splitParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + splitParameters.getTable());
        if (!splitParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetablex=splitParameters.getNewTable1();
        String savetabley=splitParameters.getNewTable2();
        JSONArray createtable=splitParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetablex,createtable);
            CreateTmpTable.createTmp(savetabley,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public AbstractParameters getParameters() {
        return splitParameters;
    }

    @Override
    protected String buildCommand()  {
        String para=" --driver-cores "+splitParameters.getDriverCores()+" " +
                "--driver-memory "+splitParameters.getDriverMemory()+" " +
                "--num-executors "+splitParameters.getNumExecutors()+" " +
                "--executor-cores "+splitParameters.getExecutorCores()+" "+
                "--executor-memory "+splitParameters.getExecutorMemory()+" --queue default";
        String Mode=splitParameters.getDeployMode();

        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.RandomSplit" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + splitParameters.getMainJar().getRes() +" "+
                splitParameters.getFraction()+" "+
                splitParameters.getDatabases()+"."+splitParameters.getTable()+" "+
                splitParameters.getSavebases()+"."+splitParameters.getNewTable1()+" "+
                splitParameters.getSavebases()+"."+splitParameters.getNewTable2();

        logger.info("spark task command : {}", command);

        logger.info("biaozhi:--" + command);
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
            this.splitParameters = JSONObject.parseObject(taskParams, SplitParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (splitParameters.getFraction() == null){
            logger.error("Fraction is null ");
            errorMsg="参数有误：没有选择拆分比例";
            return false;
        }
        return true;
    }
}
