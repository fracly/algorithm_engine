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
package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.*;
import cn.escheduler.common.enums.DbType;
import cn.escheduler.common.job.db.*;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.mapper.DataSourceMapper;
import cn.escheduler.dao.mapper.DatasourceUserMapper;
import cn.escheduler.dao.mapper.ProjectMapper;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.Resource;
import cn.escheduler.dao.model.User;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * datasource service
 */
@Service
public class DataSourceService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);

    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String TYPE = "type";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DATABASE = "database";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String OTHER = "other";

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DatasourceUserMapper datasourceUserMapper;

    @Autowired
    private HomePageService homePageService;

    /**
     * create data source
     *
     * @param loginUser
     * @param name
     * @param desc
     * @param type
     * @param parameter
     * @return
     */
    public Map<String, Object> createDataSource(User loginUser, String name, String desc, DbType type, String parameter) {

        Map<String, Object> result = new HashMap<>(5);
        // check name can use or not
        if (checkName(name, result)) {
            return result;
        }
        Boolean isConnection = false;
        if (type == DbType.ELASTICSEARCH) {
            ElasticsearchDataSource elasticsearchDataSource = JSONUtils.parseObject(parameter, ElasticsearchDataSource.class);
            isConnection = checkEsConnection(elasticsearchDataSource.getHostname(), elasticsearchDataSource.getPort() + "");
        } else {
            isConnection = checkConnection(type, parameter);
        }

        if (!isConnection) {
            logger.info("connect failed, type:{}, parameter:{}", type, parameter);
            putMsg(result, Status.DATASOURCE_CONNECT_FAILED);
            return result;
        }

        BaseDataSource datasource = DataSourceFactory.getDatasource(type, parameter);
        if (datasource == null) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, parameter);
            return result;
        }

        // build datasource
        DataSource dataSource = new DataSource();
        Date now = new Date();

        dataSource.setName(name.trim());
        dataSource.setNote(desc);
        dataSource.setUserId(loginUser.getId());
        dataSource.setUserName(loginUser.getUserName());
        dataSource.setType(type);
        dataSource.setConnectionParams(parameter);
        dataSource.setCreateTime(now);
        dataSource.setUpdateTime(now);
        dataSourceMapper.insert(dataSource);

        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * updateProcessInstance datasource
     *
     * @param loginUser
     * @param name
     * @param desc
     * @param type
     * @param parameter
     * @return
     */
    public Map<String, Object> updateDataSource(int id, User loginUser, String name, String desc, DbType type, String parameter) {

        Map<String, Object> result = new HashMap<>();
        // determine whether the data source exists
        DataSource dataSource = dataSourceMapper.queryById(id);
        if (dataSource == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }

        //check name can use or not
        if (!name.trim().equals(dataSource.getName()) && checkName(name, result)) {
            return result;
        }

        Boolean isConnection = false;
        if (type == DbType.ELASTICSEARCH) {
            ElasticsearchDataSource elasticsearchDataSource = JSONUtils.parseObject(parameter, ElasticsearchDataSource.class);
            isConnection = checkEsConnection(elasticsearchDataSource.getHostname(), elasticsearchDataSource.getPort() + "");
        } else {
            isConnection = checkConnection(type, parameter);
        }
        if (!isConnection) {
            logger.info("connect failed, type:{}, parameter:{}", type, parameter);
            putMsg(result, Status.DATASOURCE_CONNECT_FAILED);
            return result;
        }
        Date now = new Date();

        dataSource.setName(name.trim());
        dataSource.setNote(desc);
        dataSource.setUserName(loginUser.getUserName());
        dataSource.setType(type);
        dataSource.setConnectionParams(parameter);
        dataSource.setUpdateTime(now);
        dataSourceMapper.update(dataSource);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    private boolean checkName(String name, Map<String, Object> result) {
        List<DataSource> queryDataSource = dataSourceMapper.queryDataSourceByName(name.trim());
        if (queryDataSource != null && queryDataSource.size() > 0) {
            putMsg(result, Status.DATASOURCE_EXIST);
            return true;
        }
        return false;
    }

    public DataSource queryDataSourceById(int id) {
        Map<String, Object> result = new HashMap<String, Object>(5);
        DataSource dataSource = dataSourceMapper.queryById(id);
        return dataSource;
    }

    /**
     * updateProcessInstance datasource
     */
    public Map<String, Object> queryDataSource(int id) {

        Map<String, Object> result = new HashMap<String, Object>(5);
        DataSource dataSource = dataSourceMapper.queryById(id);
        if (dataSource == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        // type
        String dataSourceType = dataSource.getType().toString();
        // name
        String dataSourceName = dataSource.getName();
        // desc
        String desc = dataSource.getNote();
        // parameter
        String parameter = dataSource.getConnectionParams();
        String host, port, username, password, database, other, address;
        Map<String, String> otherMap = new LinkedHashMap<String, String>();
        if (dataSourceType.equals("KAFKA") || dataSourceType.equals("ELASTICSEARCH")) {
            Map<String, String> map = JSONUtils.toMap(parameter);
            host = map.get("hostname");
            port = map.get("port");
            username = map.get("user");
            password = map.get("password");
            database = map.get("database");
        } else {
            BaseDataSource datasourceForm = DataSourceFactory.getDatasource(dataSource.getType(), parameter);
            database = datasourceForm.getDatabase();
            // jdbc connection params
            other = datasourceForm.getOther();
            address = datasourceForm.getAddress();
            String[] hostsPorts = getHostsAndPort(address);
            // ip host
            host = hostsPorts[0];
            // prot
            port = hostsPorts[1];
            String separator = "";

            switch (dataSource.getType()) {
                case HIVE:
                case SQLSERVER:
                    separator = ";";
                    break;
                case MYSQL:
                case POSTGRESQL:
                case CLICKHOUSE:
                case ORACLE:
                    separator = "&";
                    break;
                default:
                    separator = "&";
                    break;
            }
            if (other != null) {
                String[] configs = other.split(separator);
                for (String config : configs) {
                    otherMap.put(config.split("=")[0], config.split("=")[1]);
                }
            }
            username = datasourceForm.getUser();
            password = datasourceForm.getPassword();
        }

        Map<String, Object> map = new HashMap<>(10);
        map.put(NAME, dataSourceName);
        map.put(NOTE, desc);
        map.put(TYPE, dataSourceType);
        map.put(HOST, host);
        map.put(PORT, port);
        map.put(DATABASE, database);
        map.put(USER_NAME, username);
        map.put(PASSWORD, password);
        map.put(OTHER, otherMap);
        result.put(Constants.DATA_LIST, map);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * query datasource object
     */
    public DataSource queryDataSourceObject(int id) {
        return dataSourceMapper.queryById(id);
    }


    /**
     * query datasource list by keyword
     *
     * @param loginUser
     * @param searchVal
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> queryDataSourceListPaging(User loginUser, String searchVal, Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();

        Integer count = 0;
        if (("").equals(searchVal)) {
            count = getTotalCount(loginUser);
        } else {
            count = getTotalCount(loginUser, searchVal);
        }

        PageInfo pageInfo = new PageInfo<Resource>(pageNo, pageSize);
        pageInfo.setTotalCount(count);
        List<DataSource> datasourceList = getDataSources(loginUser, searchVal, pageSize, pageInfo);

        pageInfo.setLists(datasourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * get list paging
     *
     * @param loginUser
     * @param searchVal
     * @param pageSize
     * @param pageInfo
     * @return
     */
    private List<DataSource> getDataSources(User loginUser, String searchVal, Integer pageSize, PageInfo pageInfo) {
//        if (isAdmin(loginUser)) {
            return dataSourceMapper.queryAllDataSourcePaging(searchVal, pageInfo.getStart(), pageSize);
//        }
//        return dataSourceMapper.queryDataSourcePaging(loginUser.getId(), searchVal,
//                pageInfo.getStart(), pageSize);
    }

    /**
     * get datasource total num
     *
     * @param loginUser
     * @return
     */
    private Integer getTotalCount(User loginUser) {
//        if (isAdmin(loginUser)) {
            return dataSourceMapper.countAllDatasource();
//       }
//        return dataSourceMapper.countUserDatasource(loginUser.getId());
    }

    /**
     * 含有搜索词总条数
     *
     * @param loginUser
     * @return
     */
    private Integer getTotalCount(User loginUser, String searchVal) {
//        if (isAdmin(loginUser)) {
            return dataSourceMapper.countAllDatasourceBysearchVal(searchVal);
//        }
//        return dataSourceMapper.countUserDatasourceBysearchVal(loginUser.getId(), searchVal);
    }

    /**
     * query data resource list
     *
     * @param loginUser
     * @param type
     * @return
     */
    public Map<String, Object> queryDataSourceList(User loginUser, Integer type) {
        Map<String, Object> result = new HashMap<>(5);
        List<DataSource> datasourceList = dataSourceMapper.queryDataSourceByType(loginUser.getId(), type);

        result.put(Constants.DATA_LIST, datasourceList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * verify datasource exists
     *
     * @param loginUser
     * @param name
     * @return
     */
    public Result verifyDataSourceName(User loginUser, String name) {
        Result result = new Result();
        List<DataSource> dataSourceList = dataSourceMapper.queryDataSourceByName(name);
        if (dataSourceList != null && dataSourceList.size() > 0) {
            logger.error("datasource name:{} has exist, can't create again.", name);
            putMsg(result, Status.DATASOURCE_EXIST);
        } else {
            putMsg(result, Status.SUCCESS);
        }

        return result;
    }

    /**
     * get connection
     *
     * @param dbType
     * @param dataSource
     * @return
     */
    private Connection getConnection(DbType dbType, DataSource dataSource) {
        Connection connection = null;
        BaseDataSource params = null;
        try {
            switch (dbType) {
                case POSTGRESQL:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), PostgreDataSource.class);
                    Class.forName(Constants.ORG_POSTGRESQL_DRIVER);
                    break;
                case MYSQL:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), MySQLDataSource.class);
                    Class.forName(Constants.COM_MYSQL_JDBC_DRIVER);
                    break;
                case HIVE:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), HiveDataSource.class);
                    Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                    break;
                case SPARK:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), SparkDataSource.class);
                    Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                    break;
                case CLICKHOUSE:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), ClickHouseDataSource.class);
                    Class.forName(Constants.COM_CLICKHOUSE_JDBC_DRIVER);
                    break;
                case ORACLE:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), OracleDataSource.class);
                    Class.forName(Constants.COM_ORACLE_JDBC_DRIVER);
                    break;
                case SQLSERVER:
                    params = JSONObject.parseObject(dataSource.getConnectionParams(), SQLServerDataSource.class);
                    Class.forName(Constants.COM_SQLSERVER_JDBC_DRIVER);
                    break;
                default:
                    break;
            }
            if (params != null) {
                connection = DriverManager.getConnection(params.getJdbcUrl(), params.getUser(), params.getPassword());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return connection;
    }

    /**
     * query datasource table list
     *
     * @param type
     * @param datasource
     * @return
     */
    public List<Map<String, String>> getTableListById(DbType type, DataSource datasource) throws ExecutionException, InterruptedException, UnknownHostException, SQLException {
        List<Map<String, String>> tables = new ArrayList<>();
        switch (type) {
            case MYSQL: {
                Map<String, String> map = JSONUtils.toMap(datasource.getConnectionParams());
                MySQLDataSource mySQLDataSource = JSONObject.parseObject(datasource.getConnectionParams(), MySQLDataSource.class);
                Connection connection = DriverManager.getConnection(mySQLDataSource.getJdbcUrl(), mySQLDataSource.getUser(), mySQLDataSource.getPassword());
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet database = metaData.getTables(null, map.get("database").toString(), null, null);
                while (database.next()) {
                    Map m = new HashMap();
                    m.put("name", database.getString(3));
                    tables.add(m);
                }
                break;
            }
            case HIVE: {
                Map<String, String> map = JSONUtils.toMap(datasource.getConnectionParams());
                javax.sql.DataSource ds = HiveDataSourceUtil.getHiveDataSource();
                Connection conn = ds.getConnection();
                Statement stmt = null;
                if (conn == null) {
                    logger.error(" hive 获取连接失败");
                } else {

                    try {
                        stmt = conn.createStatement();
                        stmt.execute("use " + map.get("database"));
                        ResultSet res = stmt.executeQuery("show tables");

                        while (res.next()) {
                            String name = res.getString(1);
                            Map<String, String> m = new HashMap<>();
                            m.put("name", name);
                            tables.add(m);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                stmt.close();
                conn.close();
                break;
            }
            case KAFKA: {
                Map<String, String> map = JSONUtils.toMap(datasource.getConnectionParams());
                KafkaClient.checkKafkaCluster(map.get("hostname") + ":" + map.get("port"));
                Set<String> topics = KafkaClient.listTopics();
                for (String topic : topics) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("name", topic);
                    tables.add(map1);
                }
                break;
            }
            case KYLIN: {
                Map<String, String> map = JSONUtils.toMap(datasource.getConnectionParams());
                Connection connection = getConnection(type, datasource.getConnectionParams());
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet database = metaData.getTables(null, null, null, new String[]{"TABLE"});
                while (database.next()) {
                    Map m = new HashMap();
                    m.put("name", database.getString(3));
                    tables.add(m);
                }
                break;
            }
            case ELASTICSEARCH: {
                RestTemplate restTemplate = new RestTemplate();
                ElasticsearchDataSource elasticsearchDataSource = JSONUtils.parseObject(datasource.getConnectionParams(), ElasticsearchDataSource.class);
                String url = "http://" + elasticsearchDataSource.getHostname() + ":" + elasticsearchDataSource.getPort() + "/_cat/indices?format=json";
                try {
                    List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
                    for (Map<String, Object> item : result) {
                        Map m = new HashMap();
                        m.put("name", item.get("index"));
                        tables.add(m);
                    }
                } catch (Exception e) {
                    tables.clear();
                    System.out.println("Es连接测试报错：" + e);
                }
                break;
            }
            case ORACLE:
            case SQLSERVER:
            case CLICKHOUSE:
            case POSTGRESQL:
            case SPARK:
            default:
                break;
        }
        return tables;
    }

    /**
     * query datasource column list
     *
     * @param type
     * @param datasource
     * @param table
     * @return
     */
    public List<Map<String, String>> getColumnListById(DbType type, DataSource datasource, String table) throws SQLException {
        Map<String, String> map = JSONUtils.toMap(datasource.getConnectionParams());
        List<Map<String, String>> columnList = new ArrayList<>();
        switch (type) {
            case MYSQL: {
                MySQLDataSource mySQLDataSource = JSONObject.parseObject(datasource.getConnectionParams(), MySQLDataSource.class);
                Connection connection = DriverManager.getConnection(mySQLDataSource.getJdbcUrl(), mySQLDataSource.getUser(), mySQLDataSource.getPassword());
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet columns = metaData.getColumns(null, map.get("database"), table, "%");
                while (columns.next()) {
                    Map m = new HashMap();
                    String name = columns.getString("COLUMN_NAME");
                    String type1 = columns.getString("TYPE_NAME");
                    String comment = columns.getString("REMARKS");

                    m.put("name", name);
                    m.put("type", type1);
                    m.put("comment", comment);
                    columnList.add(m);
                }
                break;
            }
            case HIVE: {
                javax.sql.DataSource ds = HiveDataSourceUtil.getHiveDataSource();
                Connection conn = ds.getConnection();
                Statement stmt = null;
                if (conn == null) {
                    logger.error(" hive 获取连接失败");
                } else {

                    try {
                        stmt = conn.createStatement();

                        ResultSet rs = stmt.executeQuery("desc " + map.get("database") + "." + table);

                        while (rs.next()) {
                            if (rs.getString(2) == null || rs.getString(1).equals("")) {
                                break;
                            }
                            Map<String, String> m = new HashMap<>();
                            String name = rs.getString(1);
                            String type1 = rs.getString(2);
                            String comment = rs.getString(3);
                            m.put("name", name);
                            m.put("type", type1);
                            m.put("comment", comment);
                            columnList.add(m);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                stmt.close();
                conn.close();
                break;
            }
            case KYLIN: {
                Connection connection = getConnection(type, datasource.getConnectionParams());
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet columns = metaData.getColumns(null, null, table, "%");
                while (columns.next()) {
                    Map m = new HashMap();
                    String name = columns.getString("COLUMN_NAME");
                    String type1 = columns.getString("TYPE_NAME");
                    String comment = columns.getString("REMARKS");
                    m.put("name", name);
                    m.put("type", type1);
                    m.put("comment", comment);
                    columnList.add(m);
                }
                break;
            }
            case ELASTICSEARCH: {
                RestTemplate restTemplate = new RestTemplate();
                ElasticsearchDataSource elasticsearchDataSource = JSONUtils.parseObject(datasource.getConnectionParams(), ElasticsearchDataSource.class);
                String url = "http://" + elasticsearchDataSource.getHostname() + ":" + elasticsearchDataSource.getPort() + "/" + table + "/_mapping?pretty=true";
                try {
                    Map result = restTemplate.getForObject(url, Map.class);
                    Map<String, Object> maps = (Map<String, Object>) ((Map) ((Map) ((Map) result.get(table)).get("mappings")).get("doc")).get("properties");
                    for (String key : maps.keySet()) {
                        Map map1 = (Map) maps.get(key);
                        //字段有类型 切字段符合正常命名格式 且格式不为nested
                        if (map1.containsKey("type") && checkColumn(key) && !String.valueOf( map1.get("type")).equals("nested") ) {
                            Map m = new HashMap();
                            m.put("name", key);
                            m.put("type", map1.get("type"));
                            m.put("comment", map1.get("format"));
                            columnList.add(m);
                        }
                    }
                } catch (Exception e) {
                    columnList.clear();
                    System.out.println("Es连接测试报错：" + e);
                }
                break;
            }
            case KAFKA:
            case ORACLE:
            case SQLSERVER:
            case CLICKHOUSE:
            case POSTGRESQL:
            case SPARK:
            default:
                break;
        }
        if (type == DbType.HIVE) {

        } else if (type == DbType.MYSQL) {

        } else if (type == DbType.KYLIN) {

        }
        return columnList;
    }


    /**
     * 数据源资源校验 特殊字符得字段不予展示
     * @param column
     * @return
     */
    private static  boolean checkColumn(String column){
        boolean res = false;
        String regex="[a-zA-Z]{1,}+[a-zA-Z0-9_]{0,}"; //正则表达式
        boolean check=column.matches(regex);//判断
        if(check) {
            res = true;
        }
        return res;
    }

    public static void main(String[] args) {
        String[] column=new String[]{"aab_00","asd00","00asda","00@ada","@asdas","asfa_asda","asdasd_00","asdas%%%",""};
        for (String res : column){
            System.out.println(res+" 校验结果："+checkColumn(res));
        }
    }

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
                case KYLIN:
                    datasource = JSONObject.parseObject(parameter, KylinDataSource.class);
                    Class.forName(Constants.COM_KYLIN_JDBC_DRIVER);
                    break;
                default:
                    break;
            }
            if (datasource != null) {
                if (dbType == DbType.KAFKA) {

                } else if (dbType == DbType.ELASTICSEARCH) {

                } else if (dbType == DbType.HIVE) {
                    String testBase = datasource.getDatabase();

                    List<Map> hiveBaseSize = homePageService.getHiveBaseSize();

                    for (Map map : hiveBaseSize) {
                        String base = (String) map.get("base");
                        if (base.equals(testBase)) {
                            connection = DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getUser(), datasource.getPassword());
                        }
                    }

                } else {
                    connection = DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getUser(), datasource.getPassword());
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return connection;
    }


    /**
     * check connection
     *
     * @param type
     * @param parameter
     * @return
     */
    public boolean checkConnection(DbType type, String parameter) {
        Boolean isConnection = false;
//        if(type == DbType.ELASTICSEARCH) {
//            ElasticsearchDataSource datasource = JSONObject.parseObject(parameter, ElasticsearchDataSource.class);
//            RestHighLevelClient client = new RestHighLevelClient(
//                    RestClient.builder(new HttpHost(datasource.getHostname(), datasource.getPort(), "http")));
//            Header[] headers = {new BasicHeader("Connection-type", "application/json")};
//            try{
//                isConnection = client.ping(headers);
//            }catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
//        }else
        if (type == DbType.KAFKA) {
            KafkaDataSource datasource = JSONObject.parseObject(parameter, KafkaDataSource.class);
            String url = datasource.getHostname() + ":" + datasource.getPort();

            try {
                KafkaClient.checkKafkaCluster(url);
                Set<String> topics = KafkaClient.listTopics();
                isConnection = topics.contains(datasource.getDatabase());

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Connection con = getConnection(type, parameter);
            if (con != null) {
                isConnection = true;
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.error("close connection fail at DataSourceService::checkConnection()", e);
                }
            }
        }

        return isConnection;
    }


    public boolean checkEsConnection(String hostName, String port) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://";
        Boolean isConnection = false;
        url += (hostName) + ":" + port;
        Map<String, Object> map = new HashMap<>();
        try {
            String result = restTemplate.getForObject(url, String.class);
            isConnection = true;
        } catch (Exception e) {
            System.out.println("Es连接测试报错：" + e);
        }
        return isConnection;
    }

    /**
     * test connectionEs连接测试报错：org.springfr
     *
     * @param loginUser
     * @param id
     * @return
     */
    public boolean connectionTest(User loginUser, int id) {
        DataSource dataSource = dataSourceMapper.queryById(id);
        return checkConnection(dataSource.getType(), dataSource.getConnectionParams());
    }

    /**
     * build paramters
     *
     * @param name
     * @param desc
     * @param type
     * @param host
     * @param port
     * @param database
     * @param userName
     * @param password
     * @param other
     * @return
     */
    public String buildParameter(String name, String desc, DbType type, String host, String port, String database, String userName, String password, String other) {

        String address = buildAddress(type, host, port);
        String jdbcUrl = address + "/" + database;
        String separator = "";
        if (Constants.MYSQL.equals(type.name())
                || Constants.POSTGRESQL.equals(type.name())
                || Constants.CLICKHOUSE.equals(type.name())
                || Constants.ORACLE.equals(type.name())) {
            separator = "&";
        } else if (Constants.HIVE.equals(type.name())
                || Constants.SPARK.equals(type.name())
                || Constants.SQLSERVER.equals(type.name()) || Constants.KYLIN.equals(type.name())) {
            separator = ";";
        }

        Map<String, Object> parameterMap = new LinkedHashMap<String, Object>(6);
        parameterMap.put(Constants.USER, userName);
        parameterMap.put(Constants.PASSWORD, password);
        if (type == DbType.KAFKA || type == DbType.ELASTICSEARCH) {
            parameterMap.put(Constants.HOSTNAME, host);
            parameterMap.put(Constants.PORT, port);
            if (type == DbType.KAFKA) {
                parameterMap.put("database", database);
            } else {
                parameterMap.put(Constants.INDEX, database);
            }
            return JSONObject.toJSONString(parameterMap);
        }
        parameterMap.put(Constants.ADDRESS, address);
        parameterMap.put(Constants.DATABASE, database);
        parameterMap.put(Constants.JDBC_URL, jdbcUrl);

        if (other != null && !"".equals(other)) {
            Map map = JSONObject.parseObject(other, new TypeReference<LinkedHashMap<String, String>>() {
            });
            if (map.size() > 0) {
                Set<String> keys = map.keySet();
                StringBuilder otherSb = new StringBuilder();
                for (String key : keys) {
                    otherSb.append(String.format("%s=%s%s", key, map.get(key), separator));

                }
                otherSb.deleteCharAt(otherSb.length() - 1);
                parameterMap.put(Constants.OTHER, otherSb);
            }

        }

        if (logger.isDebugEnabled()) {
            logger.info("parameters map-----" + JSONObject.toJSONString(parameterMap));
        }
        parameterMap.put(Constants.HOSTNAME, host);
        parameterMap.put(Constants.PORT, port);
        return JSONObject.toJSONString(parameterMap);


    }

    private String buildAddress(DbType type, String host, String port) {
        StringBuilder sb = new StringBuilder();
        if (Constants.MYSQL.equals(type.name())) {
            sb.append(Constants.JDBC_MYSQL);
            sb.append(host).append(":").append(port);
        } else if (Constants.POSTGRESQL.equals(type.name())) {
            sb.append(Constants.JDBC_POSTGRESQL);
            sb.append(host).append(":").append(port);
        } else if (Constants.HIVE.equals(type.name()) || Constants.SPARK.equals(type.name())) {
            sb.append(Constants.JDBC_HIVE_2);
            String[] hostArray = host.split(",");
            if (hostArray.length > 0) {
                for (String zkHost : hostArray) {
                    sb.append(String.format("%s:%s,", zkHost, port));
                }
                sb.deleteCharAt(sb.length() - 1);
            }
        } else if (Constants.CLICKHOUSE.equals(type.name())) {
            sb.append(Constants.JDBC_CLICKHOUSE);
            sb.append(host).append(":").append(port);
        } else if (Constants.ORACLE.equals(type.name())) {
            sb.append(Constants.JDBC_ORACLE);
            sb.append(host).append(":").append(port);
        } else if (Constants.SQLSERVER.equals(type.name())) {
            sb.append(Constants.JDBC_SQLSERVER);
            sb.append(host).append(":").append(port);
        } else if (Constants.KAFKA.equals(type.name())) {
            sb.append(Constants.KAFKA_SERVER);
            sb.append(host).append(":").append(port);
        } else if (Constants.ELASTICSEARCH.equals(type.name())) {
            sb.append(Constants.ELASTICSEARCH_SERVER);
            sb.append(host).append(":").append(port);
        } else if (Constants.KYLIN.equals(type.name())) {
            sb.append(Constants.JDBC_KYLIN);
            sb.append(host).append(":").append(port);
        }
        return sb.toString();
    }

    /**
     * delete datasource
     *
     * @param loginUser
     * @param datasourceId
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Result delete(User loginUser, int datasourceId) {
        Result result = new Result();
        try {
            //query datasource by id
            DataSource dataSource = dataSourceMapper.queryById(datasourceId);
            if (dataSource == null) {
                logger.error("resource id {} not exist", datasourceId);
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
            if (loginUser.getId() != dataSource.getUserId()) {
                putMsg(result, Status.USER_NO_OPERATION_PERM);
                return result;
            }
            dataSourceMapper.deleteDataSourceById(datasourceId);
            datasourceUserMapper.deleteByDatasourceId(datasourceId);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error("delete datasource fail", e);
            throw new RuntimeException("delete datasource fail");
        }
        return result;
    }

    /**
     * unauthorized datasource
     *
     * @param loginUser
     * @param userId
     * @return
     */
    public Map<String, Object> unauthDatasource(User loginUser, Integer userId) {

        Map<String, Object> result = new HashMap<>();
        //only admin operate
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        /**
         * query all data sources except userId
         */
        List<DataSource> resultList = new ArrayList<>();
        List<DataSource> datasourceList = dataSourceMapper.queryDatasourceExceptUserId(userId);
        Set<DataSource> datasourceSet = null;
        if (datasourceList != null && datasourceList.size() > 0) {
            datasourceSet = new HashSet<>(datasourceList);

            List<DataSource> authedDataSourceList = dataSourceMapper.authedDatasource(userId);

            Set<DataSource> authedDataSourceSet = null;
            if (authedDataSourceList != null && authedDataSourceList.size() > 0) {
                authedDataSourceSet = new HashSet<>(authedDataSourceList);
                datasourceSet.removeAll(authedDataSourceSet);

            }
            resultList = new ArrayList<>(datasourceSet);
        }
        result.put(Constants.DATA_LIST, resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * authorized datasource
     *
     * @param loginUser
     * @param userId
     * @return
     */
    public Map<String, Object> authedDatasource(User loginUser, Integer userId) {
        Map<String, Object> result = new HashMap<>(5);

        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        List<DataSource> authedDatasourceList = dataSourceMapper.authedDatasource(userId);
        result.put(Constants.DATA_LIST, authedDatasourceList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get host and port by address
     *
     * @param address
     * @return
     */
    private String[] getHostsAndPort(String address) {
        String[] result = new String[2];
        String[] tmpArray = address.split("//");
        String hostsAndPorts = tmpArray[tmpArray.length - 1];
        StringBuilder hosts = new StringBuilder("");
        String[] hostPortArray = hostsAndPorts.split(",");
        String port = hostPortArray[0].split(":")[1];
        for (String hostPort : hostPortArray) {
            hosts.append(hostPort.split(":")[0]).append(",");
        }
        hosts.deleteCharAt(hosts.length() - 1);
        result[0] = hosts.toString();
        result[1] = port;
        return result;
    }
}
