/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.api.utils;


import cn.escheduler.api.dto.db.*;
import cn.escheduler.common.enums.DbType;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * datasource service
 */
@Service
public class DataSourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);

    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String TYPE = "type";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DATABASE = "database";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String OTHER = "other";

    /**
     * get connection
     *
     * @param dbType
     * @param parameter
     * @return
     */
    public Connection getConnection(DbType dbType, String parameter) {
        Connection connection = null;
        BaseDataSource datasource = null;
        try {
            switch (dbType) {
                case POSTGRESQL:
                    datasource = JSONObject.parseObject(parameter, PostgreDataSource.class);
                    Class.forName(Constants.ORG_POSTGRESQL_DRIVER);
                    break;
                case MYSQL:
                    datasource = JSONObject.parseObject(parameter, MySQLDataSource.class);
                    Class.forName(Constants.COM_MYSQL_JDBC_DRIVER);
                    break;
                case HIVE:
                    datasource = JSONObject.parseObject(parameter, HiveDataSource.class);
                    Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                    break;
                case SPARK:
                    datasource = JSONObject.parseObject(parameter, SparkDataSource.class);
                    Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                    break;
                case CLICKHOUSE:
                    datasource = JSONObject.parseObject(parameter, ClickHouseDataSource.class);
                    Class.forName(Constants.COM_CLICKHOUSE_JDBC_DRIVER);
                    break;
                case ORACLE:
                    datasource = JSONObject.parseObject(parameter, OracleDataSource.class);
                    Class.forName(Constants.COM_ORACLE_JDBC_DRIVER);
                    break;
                case SQLSERVER:
                    datasource = JSONObject.parseObject(parameter, SQLServerDataSource.class);
                    Class.forName(Constants.COM_SQLSERVER_JDBC_DRIVER);
                    break;
                case KAFKA:
                    datasource = JSONObject.parseObject(parameter, SQLServerDataSource.class);
                    break;
                default:
                    break;
            }
            if(datasource != null){
                //hive 暂不处理
                if(dbType == DbType.HIVE){

                }else{
                    connection = DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getUser(), datasource.getPassword());
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return connection;
    }



}
