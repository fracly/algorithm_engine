package cn.escheduler.server.worker.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.ForecastParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.utils.ConfUtils;
import cn.escheduler.server.utils.CreateTmpTable;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskConstants;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.sql.SQLException;


public class ForecastTask extends AbstractYarnTask {
    /**
     * @param taskProps
     * @param logger
     * 预测
     */
    public ForecastTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }


    private static final String SPARK_COMMAND = "spark-submit";

    private static final String FORECAST_MAIN_CLASS = "com.bcht.page.RunForecast";

    private static final String FORECAST_MAIN_JAR = "/escheduler/jars/bigdata_algoritms.jar";

    private ForecastParameters forecastParameters;

    @Override
    public void init() {

        logger.info("foracast task params {}", taskProps.getTaskParams());

        forecastParameters = JSONUtils.parseObject(taskProps.getTaskParams(), ForecastParameters.class);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setRes(TaskConstants.ALGORITHM_JAR);


        forecastParameters.setDeployMode("client");
        forecastParameters.setMainJar(resourceInfo);
        forecastParameters.setDriverCores(1);
        forecastParameters.setDriverMemory("1G");
        forecastParameters.setExecutorMemory("2G");
        forecastParameters.setExecutorCores(2);
        forecastParameters.setNumExecutors(2);

        int dataSource = forecastParameters.getDatasource();
        int dataStorages = forecastParameters.getDataStorage();

        DataSource ds = processDao.findDataSourceById(dataSource);
        DataSource dssave = processDao.findDataSourceById(dataStorages);
        String database = JSONUtils.toMap(ds.getConnectionParams()).get("database");
        String datasave = JSONUtils.toMap(dssave.getConnectionParams()).get("database");
        forecastParameters.setDatabases(database);
        forecastParameters.setSavebases(datasave);
        logger.info("biaozhi33:" + forecastParameters.getTable());
        forecastParameters.setTable(database + "." + forecastParameters.getTable());
        if (!forecastParameters.checkParameters()) {
            throw new RuntimeException("data filter task params is not valid");
        }
        String savetable=forecastParameters.getSavebases()+ "." + forecastParameters.getModelName().replace("-","_")+ " ";
        JSONArray createtable=forecastParameters.getColumnList();
        try {
            CreateTmpTable.createTmp(savetable,createtable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return forecastParameters;
    }

    @Override
    protected String buildCommand() throws Exception {

        String modelPath = "/escheduler/model/" + forecastParameters.getModelName();

        String para=" --driver-cores "+forecastParameters.getDriverCores()+" " +
                "--driver-memory "+forecastParameters.getDriverMemory()+" " +
                "--num-executors "+forecastParameters.getNumExecutors()+" " +
                "--executor-cores "+forecastParameters.getExecutorCores()+" " ;
//                "--executor-memory "+forecastParameters.getExecutorMemory()+" --queue default";
        String Mode=forecastParameters.getDeployMode();


        //参数 1.CharacterColumns 2.modelPath 3.input2table 4.input2 database + modelpath (output table)
        String command = "spark-submit  " +
                "--class " +"com.bcht.algorithms.engine.Forecast" + " " +
                " --master  yarn --deploy-mode "+ Mode+ " "+
                para +" "+
                ConfUtils.getUrl() + forecastParameters.getMainJar().getRes() +" "+
                StringUtils.join(forecastParameters.getCharacterColumns(), ",") + " "+
                ConfUtils.getUrl() +modelPath+ " " +
                forecastParameters.getTable() + " "+
                forecastParameters.getSavebases()+"."+
                forecastParameters.getNewTable()+ " " +
                forecastParameters.getLastTaskType() ;

        logger.info("spark task command : {}", command);

        return command;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
