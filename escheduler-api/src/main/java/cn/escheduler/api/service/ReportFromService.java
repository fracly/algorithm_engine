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

import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.*;
import cn.escheduler.common.job.db.MySQLDataSource;
import cn.escheduler.dao.mapper.*;
import cn.escheduler.dao.model.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jdo.annotations.Transactional;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * process instance service
 */
@Service
public class ReportFromService extends BaseDAGService {


    private static final Logger logger = LoggerFactory.getLogger(ReportFromService.class);
    private static String hodKey = "\\$\\{[\\s0-9a-zA-Z_.\\-\\s]{1,}\\}";

    @Autowired
    ReportFromMapper reportFromMapper;

    @Autowired
    ResourcesService resourcesService;

    @Autowired
    private DataSourceMapper dataSourceMapper;


    /**
     * paging query process instance list, filtering according to project, process definition, time range, keyword, process status
     *
     * @param loginUser
     * @param reportName
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> queryReportFromList(User loginUser, String reportName, String status,
                                                   Integer pageNo, Integer pageSize, String roleId) {
        Map<String, Object> result = new HashMap<>(5);
        String createUser = loginUser.getUserName();
        PageInfo pageInfo = new PageInfo<ProcessInstance>(pageNo, pageSize);
        Integer count;
        List<ReportFrom> reportFroms;

        // 没有角色
        if (roleId == null || StringUtils.isEmpty(roleId)) {
            count = reportFromMapper.countReportFromList(createUser, reportName, status, pageInfo.getStart(), pageSize, roleId);
            reportFroms = reportFromMapper.queryReportFromList(
                    createUser, reportName, status, pageInfo.getStart(), pageSize, roleId);
            // 可能多个角色
        } else {
            String[] split = roleId.split(",");
            List<ReportFrom> list = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                reportFroms = reportFromMapper.queryReportFromList(createUser, reportName, status, pageInfo.getStart(), pageSize, split[0]);
                list.addAll(reportFroms);
            }
            //去重
            reportFroms = filterListById(list);
            count = reportFroms.size();

        }

        pageInfo.setTotalCount(count);
        pageInfo.setLists(reportFroms);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * paging query process instance list, filtering according to project, process definition, time range, keyword, process status
     *
     * @param groupId
     * @param reportName
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> getReportFromInfo(String groupId, String reportName, String status,
                                                   Integer pageNo, Integer pageSize, String roleId) {
        Map<String, Object> result = new HashMap<>(5);
        PageInfo pageInfo = new PageInfo<ProcessInstance>(pageNo, pageSize);
        Integer count;
        List<ReportFrom> reportFroms;
        List<ReportFrom> reportFromsC;

        // 没有角色
        if (roleId == null || StringUtils.isEmpty(roleId)) {
            count = reportFromMapper.countReportFromInfo(groupId, reportName, status, pageInfo.getStart(), pageSize, roleId);
            reportFroms = reportFromMapper.queryReportFromInfo(
                    groupId, reportName, status, pageInfo.getStart(), pageSize, roleId);
            // 可能多个角色
        } else {
            String[] split = roleId.split(",");
            List<ReportFrom> list = new ArrayList<>();
            List<ReportFrom> listC = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                reportFroms = reportFromMapper.queryReportFromInfo(groupId, reportName, status, pageInfo.getStart(), pageSize, split[0]);
                reportFromsC=reportFromMapper.queryReportFromInfo(groupId, reportName, status, 0, 100000, split[0]);
                list.addAll(reportFroms);
                listC.addAll(reportFromsC);
            }
            //去重
            reportFroms = filterListById(list);
            reportFromsC = filterListById(listC);
            count = reportFromsC.size();

        }

        pageInfo.setTotalCount(count);
        pageInfo.setLists(reportFroms);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * paging query process instance list, filtering according to project, process definition, time range, keyword, process status
     *
     * @param status
     * @return
     */
    public Map<String, Object> list(String status, String roleId) {
        Map<String, Object> result = new HashMap<>(5);
        PageInfo pageInfo = new PageInfo<ProcessInstance>(1, 10000);
        Integer count;
        List<ReportFrom> reportFroms;

        // 没有角色
        if (roleId == null || StringUtils.isEmpty(roleId)) {
            putMsg(result, Status.ROLE_ID_IS_NULL);
            return result;
            // 可能多个角色
        } else {
            String[] split = roleId.split(",");
            List<ReportFrom> list = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                reportFroms = reportFromMapper.list(status, split[0]);
                list.addAll(reportFroms);
            }
            //去重
            reportFroms = filterListById(list);
            count = reportFroms.size();

        }

        pageInfo.setTotalCount(count);
        pageInfo.setLists(reportFroms);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    private static List<ReportFrom> filterListById(List<ReportFrom> list) {
        if (null == list || list.size() <= 0) {
            return list;
        } else {
            for (int i = 0; i < list.size(); i++) {
                ReportFrom map1 = list.get(i);
                for (int j = i + 1; j < list.size(); j++) {
                    if (map1.getId().equals(list.get(j).getId())) {
                        list.remove(j);
                        j--;
                    }
                }
            }
            return list;
        }

    }

    /**
     * onLine report
     *
     * @param reportId
     * @param status
     * @return
     */
    public Map<String, Object> onLine(int reportId, int status) {

        Map<String, Object> result = new HashMap<>();

        reportFromMapper.onLine(reportId, status);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * delete report
     *
     * @param reportId
     * @return
     */
    public Map<String, Object> delete(int reportId) {

        Map<String, Object> result = new HashMap<>();

        reportFromMapper.delete(reportId);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * getGroup report
     *
     * @return
     */
    public Map<String, Object> getGroup() {

        Map<String, Object> result = new HashMap<>(5);
        List<Map> group = reportFromMapper.getGroup();
        result.put(Constants.DATA_LIST, group);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    public Map<String, Object> getGroupList(String name, Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>(5);
        PageInfo pageInfo = new PageInfo<ProcessInstance>(pageNo, pageSize);

        List<Map> groupList;
        groupList = reportFromMapper.queryGroupList(name, pageInfo.getStart(), pageSize);
        List<Map> group = reportFromMapper.getGroup();
        Integer count = group.size();
        pageInfo.setTotalCount(count);
        pageInfo.setLists(groupList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * createGrup report
     *
     * @return
     */
    public Map<String, Object> createGroup(String userName, String groupName, String status, String desc) {

        Date now = new Date();
        Map<String, Object> result = new HashMap<>(5);
        try {
            // verify group name exist
            List<Map> groupList = reportFromMapper.queryGroupList(groupName, 1, 1000);
            if (groupList.size() > 0) {
                logger.error("group name {} has exist, can't recreate", groupName);
                putMsg(result, Status.GROUP_FUNCTION_EXISTS);
                return result;
            }
            reportFromMapper.createGroup(userName, groupName, status, desc, now);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * createGrup report
     *
     * @return
     */
    public Map<String, Object> updateGroup(String groupName, Integer id, String desc) {

        Map<String, Object> result = new HashMap<>(5);
        try {
            reportFromMapper.updateGroup(groupName, id, desc);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * delete report
     *
     * @param groupId
     * @return
     */
    public Map<String, Object> deleteGroup(int groupId) {

        Map<String, Object> result = new HashMap<>();

        reportFromMapper.deleteGroup(groupId);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * getRoles report
     *
     * @return
     */
    public Map<String, Object> getRoles() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = ConfigurationManager.getProperty(Constants.ROLEURL);
        String user = ConfigurationManager.getProperty(Constants.ROLEUSERNAME);
        String password = ConfigurationManager.getProperty(Constants.ROLEPASSWORD);
        //2.建立连接
        Connection connections = DriverManager.getConnection(url, user, password);
        PreparedStatement pStatement = connections.prepareStatement("select id,name from roles where appid='dfd6c263-f313-4367-82a5-c0cb3b797850'");
        //4.执行SQL语句
        ResultSet resultSet = pStatement.executeQuery();
        List list = new ArrayList();
        //5.处理结果遍历要查询的数据
        while (resultSet.next()) {
            Map map = new HashMap();
            //遍历行数
            map.put("id", resultSet.getObject(1));
            map.put("name", resultSet.getObject(2));
            list.add(map);
        }
        //6.关闭连接
        resultSet.close();
        pStatement.close();
        connections.close();
        Map<String, Object> result = new HashMap<>(5);
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * getDict
     *
     * @return
     */
    public Map<String, Object> getDict() {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> group = reportFromMapper.getDict();
        result.put(Constants.DATA_LIST, group);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * getDictInfo
     *
     * @return
     */
    public Map<String, Object> getDictInfo(int id) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> group = reportFromMapper.getDictInfo(id);
        result.put(Constants.DATA_LIST, group);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * getReportInfo
     *
     * @return
     */
    public Map<String, Object> getReportInfo(int id) {
        Map<String, Object> result = new HashMap<>(5);
        Map info = reportFromMapper.getReportInfo(id);
        List<Map> paramById = reportFromMapper.getParamById(id);
        List<Map> list = new ArrayList();
        for (int i = 0; i < paramById.size(); i++) {
            Map map = paramById.get(i);
            map.put("isShow", "true");
            list.add(map);
        }

        JSONArray json = (JSONArray) JSONArray.toJSON(list);
        info.put("param", json);
        result.put(Constants.DATA_LIST, info);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * createReport
     *
     * @param userName
     * @param dataSoureType
     * @param dataSourceId
     * @param dataSourceName
     * @param sql
     * @param status
     * @param groupId
     * @param roleId
     * @param roleName
     * @return
     */

    public Map<String, Object> createReport(String userName, String reportId, String reportName, String reportCode, String reportType,
                                            String dataSoureType, int dataSourceId, String dataSourceName, String sql, String status, int groupId, String roleId,
                                            String roleName, String param, String path, String origin_file_name) {
        Date now = new Date();
        Map<String, Object> result = new HashMap<>(5);
//        //sql做小写字母转换
//        String sql_tmp = toLowerCaseSql(sql);
//        //解析sql 获取字段
//        //做剩余未传参的参数进行替换
//        Pattern r = Pattern.compile(hodKey);
//        // 现在创建 matcher 对象
//        Matcher matcher = r.matcher(sql_tmp);
//        String sql1=matcher.replaceAll("''");
//        List list = null;
//
//        try {
//            list = getColumnName(sql1,dataSourceId);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            putMsg(result, Status.SQL_CODE_ERROR);
//            return result;
//        }
//        if(list.size()==0){
//            putMsg(result, Status.SQL_CODE_ERROR_NULL);
//            return result;
//        }
//        String [] CloumList = (String[]) list.toArray(new String[list.size()]);
//        String headMid = toJSONString(OfficeUtil.readExcel(path));
//        String head = toJSONString(OfficeUtil.parseHead(OfficeUtil.readExcel(path), CloumList));
        try {
            if (!reportId.equals("")) {
                //检验 reportCode 是否存在
                List<Map> code = reportFromMapper.getReportCode(Integer.parseInt(reportId));
                for (Map map : code) {
                    if (map.get("reportCode").toString().equals(reportCode)) {
                        putMsg(result, Status.CREATE_REPORT_CODE_ERROR);
                        return result;
                    }
                }

                reportFromMapper.updateReport(Integer.parseInt(reportId), userName, now, reportName, reportCode, reportType,
                        path, dataSoureType, dataSourceId, dataSourceName,
                        sql, status, groupId, roleId, roleName, origin_file_name);
                result.put(Constants.DATA_LIST, reportId);

                reportFromMapper.deleteParam(Integer.parseInt(reportId));
                if (param != null) {
                    saveParam(param, Integer.parseInt(reportId));
                }

                putMsg(result, Status.SUCCESS);
            } else {
                //检验 reportCode 是否存在
                List<Map> code = reportFromMapper.getReportCode(0);
                for (Map map : code) {
                    if (map.get("reportCode").toString().equals(reportCode)) {
                        putMsg(result, Status.CREATE_REPORT_CODE_ERROR);
                        return result;
                    }
                }
                reportFromMapper.createReport(userName, now, reportName, reportCode, reportType,
                        path, dataSoureType, dataSourceId, dataSourceName,
                        sql, status, groupId, roleId, roleName, origin_file_name);
                int idByCode = reportFromMapper.getIdByCode(reportCode);
                result.put(Constants.DATA_LIST, idByCode);
                if (reportId != null && !reportId.equals("")) {
                    reportFromMapper.deleteParam(Integer.parseInt(reportId));
                }

                if (param != null) {
                    saveParam(param, idByCode);
                }
                putMsg(result, Status.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    public Map<String, Object> parseSqlAndExcle(int id) {

        Map reportInfo = reportFromMapper.getReportInfo(id);
        String sql = reportInfo.get("sql").toString();
        int dataSourceId = (int) reportInfo.get("dataSourceId");
        String path = reportInfo.get("path").toString();
        Map<String, Object> result = new HashMap<>(5);
        //sql做小写字母转换
        String sql_tmp = toLowerCaseSql(sql);
        //解析sql 获取字段
        //做剩余未传参的参数进行替换
        Pattern r = Pattern.compile(hodKey);
        // 现在创建 matcher 对象
        Matcher matcher = r.matcher(sql_tmp);
        String sql1 = matcher.replaceAll("''");
        List list = null;

        try {
            list = getColumnName(sql1, dataSourceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list.size() == 0) {
            putMsg(result, Status.SQL_CODE_ERROR_NULL);
            return result;
        }
        String[] CloumList = (String[]) list.toArray(new String[list.size()]);
        List ListheadMid = OfficeUtil.readExcel(path);
        reportEngine reportEngine = new reportEngine(ListheadMid, CloumList);
        Map<String, Object> stringObjectMap = reportEngine.parseHead();
        //优化excel报表解析代码
        if(reportEngine.status==-1){
            putMsg(result, Status.REPORTFAILED,reportEngine.ErrorMsg);
        }else{
            reportFromMapper.updateParas(id,toJSONString(ListheadMid),toJSONString(stringObjectMap));
            putMsg(result, Status.SUCCESS);
        }
        return result;

    }

    public void saveParam(String param, int reportId) {
        JSONArray paramArray = JSONArray.parseArray(param);
        for (int i = 0; i < paramArray.size(); i++) {
            String name = paramArray.getJSONObject(i).getString("name");
            String desc = paramArray.getJSONObject(i).getString("desc");
            String type = paramArray.getJSONObject(i).getString("type");
            String value = paramArray.getJSONObject(i).getString("value");
            reportFromMapper.insertParam(name, desc, type, value, reportId);
        }
    }

    private String getCloums(String sql){
        sql = "select * from ("+sql+") t_Nuu_up where 1=-1";
        return sql;
    }

    public List getColumnName(String sql, int dataSourceId) throws SQLException {
        DataSource dataSource = dataSourceMapper.queryById(dataSourceId);
        MySQLDataSource mySQLDataSource = JSONObject.parseObject(dataSource.getConnectionParams(), MySQLDataSource.class);
        String url = mySQLDataSource.getJdbcUrl();
        String user = mySQLDataSource.getUser();
        String password = mySQLDataSource.getPassword();
        //建立连接
        Connection connections = null;
        List list = new ArrayList();
        String cloums = getCloums(sql);
        try {
            connections = DriverManager.getConnection(url, user, password);
            PreparedStatement pStatement = connections.prepareStatement(cloums);
            ResultSet resultSet = pStatement.executeQuery();
            //检索此 ResultSet对象的列的数量，类型和属性。
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //返回此 ResultSet对象中的列数。
            int columnCount = resultSetMetaData.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);
                list.add(columnName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connections != null) {
                try {
                    connections.close();
                } catch (SQLException e) {
                    logger.error("Mysql datasource try conn close conn error", e);
                    throw e;
                }
            }
        }

        return list;

    }

    /*转换sql 去除宏变量后 转换大小写*/
    public static String toLowerCaseSql(String sql) {
        //处理剩下的为传参的宏变量 匹配sql关键字
        List<String> list = new ArrayList<String>();
        Pattern r = Pattern.compile(hodKey);
        // 现在创建 matcher 对象
        Matcher matcher = r.matcher(sql);
        //找到所有宏变量
        while (matcher.find()) {
            String group = matcher.group();
            list.add(group);
        }
        //sql做字母小写转换 防止关键字组合过多
        sql = sql.toLowerCase();
        for (String group : list) {
            String groupTemp = group.toLowerCase();
            sql = sql.replaceAll(escapeQueryChars(groupTemp), escapeQueryChars(group));
        }

        return sql;
    }

    public static String escapeQueryChars(String s) {
        if (!StringUtils.isNotEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')'
                    || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"'
                    || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                    || c == '|' || c == '&' || c == ';' || c == '/' || c == '.'
                    || c == '$' || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * addUseCountByCode
     *
     * @param reportCode
     * @return
     */

    public Map<String, Object> addUseCountByCode(String reportCode) {
        Map<String, Object> result = new HashMap<>();
        //检验 reportCode 是否存在
        List<Map> code = reportFromMapper.getReportCode(0);
        for (Map map : code) {
            if (map.get("reportCode").toString().equals(reportCode)) {
                int count = reportFromMapper.getUseCountByCode(reportCode);
                reportFromMapper.addUseCountByCode(reportCode,count+1);
                putMsg(result, Status.SUCCESS);
                return result;
            }else{
                putMsg(result, Status.REPORT_CODE_ERROR);
            }
        }
        return  result;
    }

}
