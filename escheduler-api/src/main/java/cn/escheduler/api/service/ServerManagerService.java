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
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.common.enums.DbType;
import cn.escheduler.common.job.db.BaseDataSource;
import cn.escheduler.common.job.db.HiveDataSource;
import cn.escheduler.common.job.db.KylinDataSource;
import cn.escheduler.common.job.db.MySQLDataSource;
import cn.escheduler.dao.mapper.DataSourceMapper;
import cn.escheduler.dao.mapper.ServerManagerMapper;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.ProcessData;
import cn.escheduler.dao.model.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * work group service
 */
@Service
public class ServerManagerService extends BaseService {

    private static String hodKey = "\\$\\{[\\s0-9a-zA-Z_.\\-\\s]{1,}\\}";

    private static String hodKeyIn = "in \\$\\{[\\s0-9a-zA-Z_.\\-\\s]{1,}\\}";

    private static final Logger logger = LoggerFactory.getLogger(ProcessDefinitionService.class);

    @Autowired
    private ServerManagerMapper serverManagerMapper;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    /**
     * create or update a worker group
     * @return
     */
    public Map<String, Object>  getServerList(User loginUser, String searchVal, Integer status, Integer pageNo, Integer pageSize,String type) {
        Map<String, Object> result = new HashMap<>(5);
        String userName = loginUser.getUserName();
        List<String> typeList=null;
        if(type!=null && !type.equals("")){
            typeList=Arrays.asList(type.replace(",", "','"));
        }
        Integer count = serverManagerMapper.countServerList(userName,searchVal,status,typeList);

        PageInfo pageInfo = new PageInfo<ProcessData>(pageNo, pageSize);
        List<Map> resourceList = serverManagerMapper.getServerList(userName,searchVal,status,pageInfo.getStart(),pageSize,typeList);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(resourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * create or update a worker group
     * @return
     */
    public Map<String, Object>  getServerList2( String searchVal, Integer status, Integer pageNo, Integer pageSize,String type) {
        Map<String, Object> result = new HashMap<>(5);
        List<String> typeList=null;
        if(type!=null && !type.equals("")){
            typeList=Arrays.asList(type.replace(",", "','"));
        }
        Integer count = serverManagerMapper.countServerList2(searchVal,status,typeList);
        PageInfo pageInfo = new PageInfo<ProcessData>(pageNo, pageSize);
        List<Map> resourceList = serverManagerMapper.getServerList2(searchVal,status,pageInfo.getStart(),pageSize,typeList);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(resourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * insert
     * @return
     */
    public Map<String, Object> insert(String serviceName, String serviceCode, String developMode,String type,String datasourceType,
                                      String datasourceName, String serviceSql, String status, String createUser,
                                      String des, Integer datasourceId, String serviceParams, String table,String inParam,String outParam) {

        Date createTime = new Date();

        Map<String, Object> result = new HashMap<>(5);

        //检验 reportCode 是否存在
        List<Map> code = serverManagerMapper.getServiceCode(0);
        for (Map map:code) {
            if(map.get("serviceCode").toString().equals(serviceCode)){
                putMsg(result, Status.CREATE_SERVICE_CODE_ERROR);
                return result;
            }
        }

        try{
            serverManagerMapper.insert(serviceName,serviceCode,developMode,type,datasourceType,
                    datasourceName,serviceSql,status,createUser,createTime,des,datasourceId,serviceParams,table,inParam,outParam);
            int idByCode = serverManagerMapper.getIdByCode(serviceCode);
            result.put("data",idByCode);
            putMsg(result, Status.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * update
     * @return
     */
    public Map<String, Object> update(int serviceId, String serviceName, String serviceCode,String type, String developMode, String datasourceType,
                                      String datasourceName, String serviceSql, String status, String des, Integer datasourceId,
                                      String serviceParams, String table,String inParam,String outParam) {


        Map<String, Object> result = new HashMap<>(5);

        //检验 reportCode 是否存在
        List<Map> code = serverManagerMapper.getServiceCode(serviceId);
        for (Map map:code) {
            if(map.get("serviceCode").toString().equals(serviceCode)){
                putMsg(result, Status.CREATE_SERVICE_CODE_ERROR);
                return result;
            }
        }

        try{
            serverManagerMapper.update(serviceId,serviceName,serviceCode,type,developMode,datasourceType,
                    datasourceName,serviceSql,status,des,datasourceId,serviceParams,table,inParam,outParam);
            putMsg(result, Status.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * update
     * @return
     */
    public Map<String, Object> getInfo(int serviceId) {

        Map<String, Object> result = new HashMap<>(5);
        try{
            Map info = serverManagerMapper.getInfo(serviceId);
            result.put(Constants.DATA_LIST,info);
            putMsg(result, Status.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * delete
     * @return
     */
    public Map<String, Object> delete(int serviceId) {

        Map<String, Object> result = new HashMap<>(5);
        try{
            serverManagerMapper.delete(serviceId);
            putMsg(result, Status.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }


    public Map<String, Object> analysisSql(String sql,int datasourceId,String mode) {
        int i = Integer.parseInt(mode);
        List list = null;
        Map<String, Object> result = new HashMap<>(5);


            //sql做小写字母转换
            String sql_tmp = ReportFromService.toLowerCaseSql(sql);

            //优先正则替换 in 方法
            Pattern rIn = Pattern.compile(hodKeyIn);
            Matcher matcherIn = rIn.matcher(sql_tmp);
            String sqlin=matcherIn.replaceAll(" in ('')");


            //解析sql 获取字段
            //做剩余未传参的参数进行替换
            Pattern r = Pattern.compile(hodKey);
            // 现在创建 matcher 对象
            Matcher matcher = r.matcher(sqlin);
            String sql1=matcher.replaceAll("''");

            sql="select * from ( "+sql1+" ) t";

            try {
                list = getColumnNameInfo(sql,datasourceId,null);
            } catch (SQLException e) {
                e.printStackTrace();
                putMsg(result, Status.SQL_CODE_ERROR);
                return result;
            }
            result.put(Constants.DATA_LIST,list);
            putMsg(result, Status.SUCCESS);
            return result;
    }

    public List getColumnNameInfo(String sql,int dataSourceId,String table) throws SQLException {

        DataSource dataSource = dataSourceMapper.queryById(dataSourceId);
        JSONObject connectionParams = JSONObject.parseObject(dataSource.getConnectionParams());
        String database = connectionParams.getString("database");
        String dstype = dataSource.getType().toString();
        BaseDataSource source;
        if(dstype.equals("MYSQL")){
            source=JSONObject.parseObject(dataSource.getConnectionParams(), MySQLDataSource.class);
        }else if(dstype.equals("HIVE")){
            source=JSONObject.parseObject(dataSource.getConnectionParams(), HiveDataSource.class);
        }else if(dstype.equals("KYLIN")){
            source=JSONObject.parseObject(dataSource.getConnectionParams(), KylinDataSource.class);
            try {
                Class.forName(Constants.COM_KYLIN_JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            return null;
        }


        Connection con = null;
        List list = new ArrayList();
        try {
            con = DriverManager.getConnection(source.getJdbcUrl(), source.getUser(), source.getPassword());

            if(sql!=null&&table==null){
                //执行SQL语句
                PreparedStatement pStatement = con.prepareStatement(sql);
                ResultSet resultSet = pStatement.executeQuery();
                //检索此 ResultSet对象的列的数量，类型和属性。
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                //返回此 ResultSet对象中的列数。
                int columnCount = resultSetMetaData.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    Map map = new HashMap();
                    String columnName = resultSetMetaData.getColumnName(i + 1);
                    String catalogName = resultSetMetaData.getCatalogName(i + 1);

                    String[] split;
                    if(dataSource.getType()==DbType.MYSQL ||dataSource.getType()==DbType.KYLIN){
                        map.put("name", columnName);
                        map.put("type", "");
                        map.put("comment", "");
                    }else if(dataSource.getType()==DbType.HIVE) {
                        split = columnName.split("\\.");
                        map.put("name", split[1]);
                        map.put("type", "");
                        map.put("comment", "");
                    }else {

                    }

                    list.add(map);
                }
            }else if(table!=null){
                Map map = new HashMap();
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet columns = metaData.getColumns(null, database, table, "%");
                while (columns.next()){
                    String name = columns.getString("COLUMN_NAME");
                    String type = columns.getString("TYPE_NAME");
                    String comment = columns.getString("REMARKS");

                    map.put("name", name);
                    map.put("type", type);
                    map.put("comment", comment);
                    list.add(map);
                }
            }

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.error("Mysql datasource try conn close conn error", e);
                    throw e;
                }
            }
        }
            return list;

    }

    public Map<String, Object> createParam(String id, String param) {

        Map<String, Object> result = new HashMap<>(5);
        String param_name;
        String param_type;
        String param_des;
        String type;
        String param_join;
        String param_exam;
        String param_variable;
        String isRequired;
        int service_id;

        try{

            serverManagerMapper.deleteParam(Integer.parseInt(id));

                JSONArray jsonArray=JSONArray.parseArray(param);
                for (int i=0;i<jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    param_name = jsonObject.getString("param_name");
                    param_type = jsonObject.getString("param_type");
                    param_des = jsonObject.getString("param_des");
                    type = jsonObject.getString("type");
                    param_join = jsonObject.getString("param_join");
                    param_exam = jsonObject.getString("param_exam");
                    service_id = Integer.parseInt(id);
                    if(type.equals("0")){
                        param_variable = jsonObject.getString("param_variable");
                    }else {
                        param_variable="";
                    }
                    isRequired = jsonObject.getString("is_required");
                    serverManagerMapper.insertParam(param_name,param_type,param_des,type,param_join,param_exam,service_id,param_variable,isRequired);
                }

            putMsg(result, Status.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * onLine report
     *
     * @param serviceId
     * @param status
     * @return
     */
    public Map<String, Object> onLine(int serviceId,int status) {
        Map<String, Object> result = new HashMap<>();
        serverManagerMapper.onLine(serviceId, status);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> getParam(int serviceId, String type) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> param = serverManagerMapper.getParam(serviceId,type);
        result.put(Constants.DATA_LIST,param);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
