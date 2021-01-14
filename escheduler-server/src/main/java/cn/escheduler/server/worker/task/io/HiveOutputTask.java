package cn.escheduler.server.worker.task.io;

import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.HiveOutputParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

public class HiveOutputTask extends AbstractTask {
    /**
     * @param taskProps
     * @param logger
     */

    /**
     *  input parameters
     */
    private HiveOutputParameters sqloutputParameters;

    /**
     *  process database access
     */
    private ProcessDao processDao;

    public HiveOutputTask(TaskProps props, Logger logger) {
        super(props, logger);
        logger.info("sqlOutPut task params {}", taskProps.getTaskParams());
        this.sqloutputParameters = JSONObject.parseObject(props.getTaskParams(), HiveOutputParameters.class);

        if (!sqloutputParameters.checkParameters()) {
            throw new RuntimeException("input task params is not valid");
        }
        this.processDao = DaoFactory.getDaoInstance(ProcessDao.class);

    }

    @Override
    public void handle() throws Exception {

        // set the name of the current thread
        String threadLoggerInfoName = String.format("TaskLogInfo-%s", taskProps.getTaskAppId());
        Thread.currentThread().setName(threadLoggerInfoName);
        logger.info(sqloutputParameters.toString());


        try {
            // determine whether there is a data source
            if (sqloutputParameters.getDatasource() == 0 || StringUtils.isEmpty(sqloutputParameters.getTable())){
                logger.error("datasource is null or not specify a table");
                exitStatusCode = -1;
            }else {
                DataSource dataSource = processDao.findDataSourceById(sqloutputParameters.getDatasource());
                Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
                String database=map.get("database");
                String tableName = sqloutputParameters.getTable();
                String lastTable = sqloutputParameters.getLastTable();
                sqloutputParameters.setDatabases(database);


                DataSource tpyeDatasource =processDao.findDataSourceById(sqloutputParameters.getTypeDatasource());
                Map<String, String> typemap = JSONUtils.toMap(tpyeDatasource.getConnectionParams());
                String typedatabase=typemap.get("database");
                String part=sqloutputParameters.getColumn();

                Class.forName(cn.escheduler.api.utils.Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                Connection connection = DriverManager.getConnection(map.get("jdbcUrl"), map.get("user"), "");
                Statement stmt = connection.createStatement();
                stmt.execute("set hive.exec.dynamic.partition=true");
                stmt.execute("set hive.exec.dynamic.partition.mode=nonstrict");

                String save=sqloutputParameters.getSave();

                if(save.equals("W")){
                    if(part!=null && !part.equals("")){
                        logger.info("sql :  insert overwrite table "+database+"."+tableName+" partition("+part+") select * from "+ typedatabase+"."+lastTable );
                        stmt.execute("insert overwrite table "+database+"."+tableName+" partition("+part+") select * from "+ typedatabase+"."+lastTable);
                        exitStatusCode = 0;
                    }else {
                        logger.info("sql :   insert overwrite table  "+database+"."+tableName+" select * from "+ typedatabase+"."+lastTable);
                        stmt.execute("insert overwrite table  "+database+"."+tableName+" select * from "+ typedatabase+"."+lastTable);
                        exitStatusCode = 0;
                    }
                }else {
                    if(part!=null && !part.equals("")){
                        logger.info("sql :  insert into table  "+database+"."+tableName+" partition("+part+") select * from "+ typedatabase+"."+lastTable);
                        stmt.execute("insert into table  "+database+"."+tableName+" partition("+part+") select * from "+ typedatabase+"."+lastTable);
                        exitStatusCode = 0;
                    }else {
                        logger.info("sql : insert into table  "+database+"."+tableName+" select * from "+ typedatabase+"."+lastTable);
                        stmt.execute("insert into table  "+database+"."+tableName+" select * from "+ typedatabase+"."+lastTable);
                        exitStatusCode = 0;
                    }
                }


            }

        }catch(Exception e){
            logger.error(e.getMessage(), e);
            exitStatusCode = -1;
        }

    }

    @Override
    public AbstractParameters getParameters() {
        return this.sqloutputParameters;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
