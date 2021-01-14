package cn.escheduler.server.worker.task.io;

import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.OutputParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hive.service.cli.HiveSQLException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

public class OutputTask extends AbstractTask {
    /**
     * @param taskProps
     * @param logger
     */

    /**
     *  input parameters
     */
    private OutputParameters  outputParameters;

    /**
     *  process database access
     */
    private ProcessDao processDao;


    public OutputTask(TaskProps props, Logger logger) {
        super(props, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
        logger.info("input task params {}", taskProps.getTaskParams());
        this.outputParameters = JSONObject.parseObject(props.getTaskParams(), OutputParameters.class);

        if (!outputParameters.checkParameters()) {
            throw new RuntimeException("input task params is not valid");
        }
        this.processDao = DaoFactory.getDaoInstance(ProcessDao.class);

    }

    @Override
    public void handle() throws Exception {

        // set the name of the current thread
        String threadLoggerInfoName = String.format("TaskLogInfo-%s", taskProps.getTaskAppId());
        Thread.currentThread().setName(threadLoggerInfoName);
        logger.info(outputParameters.toString());


        try {
            // determine whether there is a data source
            if (outputParameters.getDatasource() == 0 || StringUtils.isEmpty(outputParameters.getTable())){
                logger.error("datasource is null or not specify a table");
                exitStatusCode = -1;
            }else {
                DataSource dataSource = processDao.findDataSourceById(outputParameters.getDatasource());
                Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
                String database=map.get("database");
                String table = outputParameters.getTable();

                outputParameters.setDatabases(database);


                DataSource tpyeDatasource =processDao.findDataSourceById(outputParameters.getTypeDatasource());
                Map<String, String> typemap = JSONUtils.toMap(tpyeDatasource.getConnectionParams());
                String typedatabase=typemap.get("database");
                String typeTable=outputParameters.getLastTable().replace("-","_");

                if(map.get("address").contains("hive")){

                    Class.forName(cn.escheduler.api.utils.Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                    Connection connection = DriverManager.getConnection(map.get("jdbcUrl"), map.get("user"), "");
                    Statement stmt = connection.createStatement();
                    stmt.execute("drop table "+database+"."+table);
                    stmt.execute("create table "+database+"."+table+" as select * from "+typedatabase+"."+typeTable);
                    exitStatusCode = 0;

                }else if(map.get("address").contains("mysql")){

                }else {
                    logger.error("只支持 hive mysql 数据源 ，请检查数据源信息:{}",map.get("address"));
                }

            }

        }catch(Exception e){
            logger.error(e.getMessage(), e);
            exitStatusCode = -1;
        }

    }

    @Override
    public AbstractParameters getParameters() {
        return this.outputParameters;
    }

    @Override
    public boolean checkParams() {
        String taskParams = taskProps.getTaskParams();
        if("{}".equals(taskParams)){
            this.errorMsg="组件所有参数为空，请初始化组件参数";
            return false;
        }
        try {
            this.outputParameters = JSONObject.parseObject(taskParams, OutputParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (outputParameters.getDatasource() == 0 || StringUtils.isEmpty(outputParameters.getTable())){
            logger.error("datasource is null or not specify a table");
            errorMsg="参数有误：datasource is null or not specify a table";
            return false;
        }
        return true;
    }
}
