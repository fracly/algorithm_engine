package cn.escheduler.api.service;

import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.HiveDataSourceUtil;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.mapper.DataSourceMapper;
import cn.escheduler.dao.mapper.ModelDesignMapper;
import cn.escheduler.dao.model.*;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jcodings.util.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static cn.escheduler.api.enums.Status.GET_DATASOURCE_TABLE_LIST_FAILURE;

@Service
public class ModelDesignService extends BaseService{

    @Autowired
    private ModelDesignMapper modelDesignMapper;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    private static final Logger logger = LoggerFactory.getLogger(ModelDesignService.class);

    /**
     *   获取 base 的业务分类
     * @param base
     */
    public Map<String, Object> getLayer(@RequestParam(value = "base") String base) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            List<Map> list= modelDesignMapper.queryLayerByDatabase(base);
            result.put(Constants.DATA_LIST, list);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_DATASOURCE_TABLE_LIST_FAILURE.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * update Layer
     *
     * @param base
     * @param layer
     * @param newlayer
     * @param id
     * @return
     */
    public Map<String, Object> updateLayer(String base,String layer,String newlayer,String id) {
        Map<String, Object> result = new HashMap<>(5);
        modelDesignMapper.updateLayer(base,layer,newlayer,id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * update Layer2
     *
     * @param base
     * @param layer
     * @param newlayer
     * @return
     */
    public Map<String, Object> updateLayer2( String base, String layer, String newlayer) {
        Map<String, Object> result = new HashMap<>(5);
        modelDesignMapper.updateLayer2(base,layer,newlayer);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * delete Layer
     *
     * @param base
     * @param layer
     * @return
     */
    public Map<String, Object> deleteLayer(String base, String layer) {
        Map<String, Object> result = new HashMap<>(5);

        int i = modelDesignMapper.countModelDesignList(base, layer, null);
        if(i>0){
            putMsg(result, Status.DELETE_LAYRE_NOT_NULL);
            return result;
        }
        modelDesignMapper.deleteLayer(base,layer);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *   新增 base 的业务分类
     * @param base
     * @param layer
     */
    public Map<String, Object> insert( String  base,String layer,int id){
        Map<String, Object> result = new HashMap<>(5);
        modelDesignMapper.insert(base,layer,id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *   获取表的规则配置列表
     * @return
     */
    public  Map<String, Object> getTableRuleConfList(){
        Map<String, Object> result = new HashMap<>(5);
        List<Map> list = modelDesignMapper.getTableRuleConfList();
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *   获取表的规则配置列表规则条件
     * @param type  库
     * @return
     */
    public Map<String, Object> getTableRuleConfListCondition(String type) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> list = modelDesignMapper.getTableRuleConfListCondition(type);
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *   获取列的规则配置列表规则条件
     * @param type  库
     * @return
     */
    public Map<String, Object> getColumnRuleConfListCondition(String type) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> list = modelDesignMapper.getColumnRuleConfListCondition(type);
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *   获取表的规则配置列表规则条件
     * @param condi
     * @return
     */
    public Map<String, Object> getTableRuleConfListConditionParam(String type, String condi) {

        Map<String, Object> result = new HashMap<>(5);
        List<Map> lm=new ArrayList<>();
        List<Map> tableRuleConfListConditionParam = modelDesignMapper.getTableRuleConfListConditionParam(type, condi);
        if(tableRuleConfListConditionParam.get(0) == null) {
            result.put(Constants.DATA_LIST, lm);
            putMsg(result, Status.SUCCESS);
        } else {
            String tabler= (String) tableRuleConfListConditionParam.get(0).get("param");
            if(tabler!=null && !"".equals(tabler)){
                result.put(Constants.DATA_LIST, tableRuleConfListConditionParam);
                putMsg(result, Status.SUCCESS);
            }else {
                result.put(Constants.DATA_LIST, lm);
                putMsg(result, Status.SUCCESS);
            }
        }
        return result;
    }

    /**
     *   获取列的规则配置列表规则条件
     * @param condi
     * @return
     */
    public Map<String, Object> getColumnRuleConfListConditionParam(String type, String condi) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> lm=new ArrayList<>();
        List<Map> columnRuleConfListConditionParam = modelDesignMapper.getColumnRuleConfListConditionParam(type, condi);
        String tabler= (String) columnRuleConfListConditionParam.get(0).get("param");
        if(tabler!=null && !"".equals(tabler)){
            result.put(Constants.DATA_LIST, columnRuleConfListConditionParam);
            putMsg(result, Status.SUCCESS);
        }else {
            result.put(Constants.DATA_LIST, lm);
            putMsg(result, Status.SUCCESS);
        }
        return result;
    }


    /**
     *   获取列规则配置列表
     * @return
     */
    public  Map<String, Object> getColumnRuleConfList() {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> columnRuleConfList = modelDesignMapper.getColumnRuleConfList();
        result.put(Constants.DATA_LIST, columnRuleConfList);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * create table
     *
     * @return
     */

    public Result createTable( String base, String layer, String applicationName, String tableStatement, String tableComment, String tablename, int dataSourceId, int userId, String userName,
                               String tableTypes, String mysqlcolumnList, String escolumnList ,String createMethod) throws  ClassNotFoundException, IOException {
        Result result = new Result();
        if (tableTypes.indexOf("hive") >= 0){
            DataSource ds = null;
            Connection connection = null;
            Statement stmt = null;
            ResultSet dbTables = null;
            try {
                ds = HiveDataSourceUtil.getHiveDataSource();
                connection = ds.getConnection();
                stmt = connection.createStatement();

                if(connection == null) {
                    putMsg(result, Status.CUSTOM_FAILED, "获取链接失败");
                    return result;
                }
                stmt.execute("use " + base);
                dbTables = stmt.executeQuery("show tables");
                while(dbTables.next()) {
                    String tableName = dbTables.getString(1);
                    if(tableName.equals(tablename)) {
                        putMsg(result, Status.CUSTOM_FAILED, "hive表已经存在");
                        return result;
                    }
                }
                stmt.execute("use " + base);
                stmt.execute(tableStatement);
                // 再次检查一遍是否成功
                stmt.execute("use " + base);
                ResultSet rs2 = stmt.executeQuery("show tables");
                Map<String,String> tableMap = new HashMap<>();
                while(rs2.next()) {
                    String tableName = rs2.getString(1);
                    tableMap.put(tableName,tableName);
                }
                dbTables.close();
                stmt.close();
                connection.close();
                //确认没建表成功就直接返回
                if(!tableMap.containsKey(tablename)){
                    putMsg(result, Status.CUSTOM_FAILED, "hive建表失败");
                    return result;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                putMsg(result, Status.CUSTOM_FAILED, "hive建表失败 语句解析出错");
                return result;
            } finally {
                try {
                    dbTables.close();
                    stmt.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (tableTypes.indexOf("mysql") >= 0){
            try {
                String sql;
                //建表语句
                if ("1".equals(createMethod)) {
                    sql = tableStatement;

                    //字段拼接sql mysql字段
                } else {
                    JSONArray objects = JSONObject.parseArray(mysqlcolumnList);
                    StringBuffer sb = new StringBuffer();
                    sb.append("CREATE TABLE ");
                    sb.append(tablename);
                    sb.append(" ( ");
                    for (int i = 0; i < objects.size(); i++) {
                        Map<String, String> a = (Map<String, String>) objects.get(i);
                        String name = a.get("name");
                        String type = a.get("type").equals("varchar") ? "varchar(50)" : a.get("type");
                        String comment = a.get("comment");
                        sb.append(name + " " + type + " COMMENT '" + comment + "' ");
                        if (i != objects.size() - 1) {
                            sb.append(" ,");
                        }
                    }
                    sb.append(" ) ");
                    sb.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + tableComment + "' ");
                    sql = sb.toString();
                }
                Connection connection = DriverManager.getConnection(ConfigurationManager.getProperty("jdbcurl"), ConfigurationManager.getProperty("jdbcuser"), ConfigurationManager.getProperty("jdbcpassword"));
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
                preparedStatement.close();
                connection.close();
            }catch (Exception e){
                //此处建表失败回滚 建的hive表（假如有的话）
                if (tableTypes.indexOf("hive")>=0){
                    rollbackHiveTable(base,tablename);
                }
                //返回错误信息
                e.printStackTrace();
                putMsg(result, Status.FAILED, "mysql表建表失败");
                return result;
            }

        }
        if (tableTypes.indexOf("es") >= 0){
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", ConfigurationManager.getProperty("es_cluster-name")).build();
            TransportClient transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(new InetSocketAddress(ConfigurationManager.getProperty("esip"), Integer.parseInt(ConfigurationManager.getProperty("esport")))));
            System.out.println("开始连接");
            transportClient.admin().indices().prepareCreate(tablename).get();
            XContentBuilder builder= XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("doc")
                    .startObject("properties");
            //sql语句暂未实现
            if ("1".equals(createMethod)){


                //es字段拼接建立索引
            }else {
                JSONArray objects = JSONObject.parseArray(escolumnList);
                StringBuffer sb = new StringBuffer();
                sb.append("CREATE TABLE ");
                sb.append(tablename);
                sb.append(" ( ");
                for (int i=0;i<objects.size();i++){
                    Map<String,String> a= (Map<String,String>)objects.get(i);
                    String name = a.get("name");
                    String type = a.get("type");
                    String comment = a.get("comment");
                    builder .startObject(name)
                            .field("type", type)
//                            .field("index", false)
                            .endObject();
                }
                        builder.endObject()
                        .endObject()
                        .endObject();

            }
            try {
                // 创建映射
                PutMappingRequest mapping= Requests.putMappingRequest(tablename)
                        .type("doc").source(builder);
                transportClient.admin().indices().putMapping(mapping).get();
                transportClient.close();
                 System.out.println("关闭连接");
            } catch (Exception e) {
                //此处建表失败回滚 建的hive表和mysql表（假如有的话）
                if (tableTypes.indexOf("hive")>=0){
                      rollbackHiveTable(base,tablename);
                }
                if(tableTypes.indexOf("mysql")>=0){
                      rollbackMysqlTable(tablename);
                }
                e.printStackTrace();
                putMsg(result, Status.CUSTOM_FAILED, "es建表失败");
                return result;

            }
        }
//        向mysql表里面插入建表的记录，没有插入成功则需要回滚之前建的mhive、mysql、es表
//        String type = dataSourceMapper.queryById(dataSourceId).getType().toString();
        int count = modelDesignMapper.insertTableDescribe(base, layer, applicationName, tableComment, tablename, dataSourceId, tableTypes, new Date(), userId, userName);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "建表成功");
            return result;
        } else {
            //回滚三种表
            if (tableTypes.indexOf("hive")>=0){
                rollbackHiveTable(base,tablename);
            }
            if(tableTypes.indexOf("mysql")>=0){
                rollbackMysqlTable(tablename);
            }
            if(tableTypes.indexOf("es")>=0){
                rollbackEsTable(tablename);
            }
            putMsg(result, Status.CUSTOM_FAILED, "建表失败");
            return result;
        }
    }

    //回滚hive表
    private void rollbackHiveTable(String base,String tableName){
        DataSource ds = null;
        Connection connection = null;
        Statement stmt = null;
        try {
            ds = HiveDataSourceUtil.getHiveDataSource();
            connection = ds.getConnection();
            stmt = connection.createStatement();
            stmt.execute("use " + base);
            stmt.executeQuery("drop table if exists  " + tableName);
            stmt.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

     //mysql回滚
     private void rollbackMysqlTable(String tableName){
        try {
            Connection connection = DriverManager.getConnection(ConfigurationManager.getProperty("jdbcurl"), ConfigurationManager.getProperty("jdbcuser"), ConfigurationManager.getProperty("jdbcpassword"));
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS " + tableName);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
       }

    //es回滚
    private void rollbackEsTable(String tableName){
        Settings settings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", ConfigurationManager.getProperty("es_cluster-name")).build();
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(new InetSocketAddress(ConfigurationManager.getProperty("esip"), Integer.parseInt(ConfigurationManager.getProperty("esport")))));
        System.out.println("开始连接");
        transportClient.admin().indices().prepareDelete(tableName).execute().actionGet();
        transportClient.close();
    }

    /**
     *   delete table
     * @return
     */
    public Map<String, Object> deleteTable( String base, String tablename) throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
            logger.error(" hive 获取连接失败");
        }else {
            stmt = conn.createStatement();
            try {
                boolean execute = stmt.execute("drop table " + base + "." + tablename);
            }catch (SQLException e){
                e.printStackTrace();
            }

            stmt.close();
            conn.close();
        }
        // 更新mysql表
        modelDesignMapper.deleteTable(base, tablename);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    // getColumnInfo
    public Map<String, Object> getColumnInfo(String base,String tblName,String tableType) throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        TableInfo info= modelDesignMapper.getColumnInfo(base, tblName);
        List<Map<String,String>> list = new LinkedList<Map<String,String>>();

        //判断表类型是mysql还是es或者hive
        if ("hive".equalsIgnoreCase(tableType)){
            DataSource ds = HiveDataSourceUtil.getHiveDataSource();
            Connection conn = ds.getConnection();
            Statement stmt = null;
            if(conn == null){
                logger.error(" hive 获取连接失败");
            }else{
                stmt = conn.createStatement();
                try {
                    ResultSet rs = stmt.executeQuery("desc "+base+"."+tblName);
                    int id = 1;
                    while (rs.next()){
                        if(rs.getString(2)==null || rs.getString(1).equals("")){
                            break;
                        }
                        Map map = new HashMap<String,String>();
                        String name = rs.getString(1);
                        String type = rs.getString(2);
                        String comment = rs.getString(3);
                        map.put("name",name);
                        map.put("type",type);
                        map.put("comment",comment);
                        map.put("id", id);
                        map.put("editable", false);
                        id ++;

                        if(info!=null) {
                            JSONArray coljson= JSONArray.parseArray(info.getColRule());
                            if(coljson!=null){
                                for (int j = 0; j < coljson.size(); j++) {
                                    String column=coljson.getJSONObject(j).getString("column");
                                    if(column!=null && !"".equals(column)) {
                                        if(column.equals(name)){
                                            map.put("colQuality", coljson.getJSONObject(j).getString("colQuality"));
                                            map.put("colRule",coljson.getJSONObject(j).getString("type"));
                                            map.put("colRuleCondi",coljson.getJSONObject(j).getString("condi"));
                                            map.put("colRuleParam",coljson.getJSONObject(j).getString("param"));
                                            map.put("colWarn", coljson.getJSONObject(j).getString("colWarn"));
                                            map.put("isRun", coljson.getJSONObject(j).getString("isRun"));
                                        }
                                    }
                                }
                            }
                        }
                        list.add(map);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    stmt.close();
                    conn.close();
                }
            }
        }else if ("mysql".equalsIgnoreCase(tableType)){
            try {
                String sql="SELECT Column_name,Data_type,column_Comment from information_schema.columns  WHERE  table_name = '"+tblName+"'";
                Connection connection = DriverManager.getConnection(ConfigurationManager.getProperty("jdbcurl"), ConfigurationManager.getProperty("jdbcuser"), ConfigurationManager.getProperty("jdbcpassword"));
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                int id = 1;
                while (rs.next()){
                    if(rs.getString(2)==null || rs.getString(1).equals("")){
                        break;
                    }
                    Map map = new HashMap<String,String>();
                    String name = rs.getString(1);
                    String type = rs.getString(2);
                    String comment = rs.getString(3);
                    map.put("name",name);
                    map.put("type",type);
                    map.put("comment",comment);
                    map.put("id", id);
                    map.put("editable", false);
                    id ++;
                    list.add(map);
                }

                stmt.close();
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if ("es".equalsIgnoreCase(tableType)){
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", ConfigurationManager.getProperty("es_cluster-name")).build();
            TransportClient transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(new InetSocketAddress(ConfigurationManager.getProperty("esip"), Integer.parseInt(ConfigurationManager.getProperty("esport")))));
            System.out.println("开始连接");
            GetMappingsRequest getMappings = new GetMappingsRequest().indices(tblName);
            ActionFuture<GetMappingsResponse> mappings = transportClient.admin().indices().getMappings(getMappings);
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings1 = mappings.actionGet().getMappings();
            HashMap<String,Map<String,String>> propertiesMap = (HashMap<String,Map<String,String>>)mappings1.get(tblName).get("doc").getSourceAsMap().get("properties");
            int id = 1;
            for (Map.Entry map:propertiesMap.entrySet()){
                String key = (String) map.getKey();
                Map<String,String> value = (Map<String,String>)map.getValue();
                String type = value.get("type");
                Map mapZd = new HashMap<String,String>();
                mapZd.put("name",key);
                mapZd.put("type",type);
                mapZd.put("comment","");
                mapZd.put("id", id);
                mapZd.put("editable", false);
                id ++;
                list.add(mapZd);
            }
            transportClient.close();
        }
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public List<Map> getColumnRule(String base, String tableName) {
        TableInfo info= modelDesignMapper.getColumnInfo(base, tableName);
        String colRuleJson = info.getColRule();
        List<Map> mapList = JSON.parseArray(colRuleJson, Map.class);
        return mapList;
    }


    public Map<String, Object> getModelDesignList(String layer, String topic, int pageNo, int pageSize, String searchVal) {
        Map<String, Object> result = new HashMap<>(5);
        PageInfo pageInfo = new PageInfo<DesignModel>(pageNo, pageSize);
        Integer count = modelDesignMapper.countModelDesignList(layer, topic, searchVal);
        List<DesignModel> modelDesignList = modelDesignMapper.getModelList(layer, topic, pageInfo.getStart(), pageSize, searchVal);

        for(int i = 0; i< modelDesignList.size(); i ++){
            DesignModel model = modelDesignList.get(i);
            String tabRule = model.getTabRule();
            List<Map<String,String>> tblWarnList = new ArrayList<>();
            Map<String,String> map=new HashMap<>();
            JSONArray tbljson= JSONArray.parseArray(tabRule);
            if(tbljson!=null){
                //多个表级预警信息 合并
                for(int j=0;j<tbljson.size();j++){
                    String type=tbljson.getJSONObject(j).getString("type");
                    String tabquality =tbljson.getJSONObject(j).getString("tabquality");
                    String tblWarn=tbljson.getJSONObject(j).getString("tblWarn");
                    if("异常".equals(tabquality)){
                        modelDesignList.get(i).setTblQuality("异常");
                        map.put("tblWarn",tblWarn);
                        map.put("type",type);
                        tblWarnList.add(map);
                    }
                }
            }
            modelDesignList.get(i).setTblWarn(tblWarnList);
        }
        pageInfo.setTotalCount(count);
        pageInfo.setLists(modelDesignList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> getModelList(String base, String topic, int pageNo, int pageSize, String searchVal) {
        Map<String, Object> result = new HashMap<>(5);
        PageInfo pageInfo = new PageInfo<DesignModel>(pageNo, pageSize);
        Integer count = modelDesignMapper.countModel(base, topic, searchVal);
        List<DesignModel> modelDesignList = modelDesignMapper.getTableList(base, topic, pageInfo.getStart(), pageSize, searchVal);

        for(int i = 0; i< modelDesignList.size(); i ++){
            DesignModel model = modelDesignList.get(i);
            String tabRule = model.getTabRule();
            List<Map<String,String>> tblWarnList = new ArrayList<>();
            Map<String,String> map=new HashMap<>();
            JSONArray tbljson= JSONArray.parseArray(tabRule);
            if(tbljson!=null){
                //多个表级预警信息 合并
                for(int j=0;j<tbljson.size();j++){
                    String type=tbljson.getJSONObject(j).getString("type");
                    String tabquality =tbljson.getJSONObject(j).getString("tabquality");
                    String tblWarn=tbljson.getJSONObject(j).getString("tblWarn");
                    if("异常".equals(tabquality)){
                        modelDesignList.get(i).setTblQuality("异常");
                        map.put("tblWarn",tblWarn);
                        map.put("type",type);
                        tblWarnList.add(map);
                    }
                }
            }
            modelDesignList.get(i).setTblWarn(tblWarnList);
        }
        pageInfo.setTotalCount(count);
        pageInfo.setLists(modelDesignList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> updateModelDesignList(String base, String tblName,String layer,String area,String tabledescribe) {
        Map<String, Object> result = new HashMap<>(5);
        Date now=new Date();
        modelDesignMapper.updateModelDesignList(base,tblName,layer,area,now,tabledescribe);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> updateTblRule(String base, String tblName,List tabrule,List colrule) {
        Map<String, Object> result = new HashMap<>(5);
         modelDesignMapper.updateTblRule(base,tblName,tabrule,colrule,null,null);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Deprecated
    public Map<String, Object> addTblRule(String base, String tblName,List tabrule,List colrule) {
        Map<String, Object> result = new HashMap<>(5);
        TableInfo info= modelDesignMapper.getTableInfo(base, tblName);

        List tbljson=tabrule;
        if(info!=null&&info.getTabRule()!=null && !"".equals(info.getTabRule())){
            String table=info.getTabRule();
             tbljson= JSONObject.parseArray(table);
            tbljson.add(tabrule.get(0));
        }
        modelDesignMapper.updateTblRule(base,tblName,tbljson,colrule,null,null);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result addTableRule(String base, String tableName, List<Map> rules) {
        Result result = new Result();
        int count = modelDesignMapper.updateTblRule(base, tableName, rules, null, null, null);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "表规则操作成功");
            return result;
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "添表规则操作失败");
            return result;
        }
    }

    public Result cudColumnRule(String base, String tableName, List<Map> rules) {
        Result result = new Result();
        int count = modelDesignMapper.updateTblRule(base, tableName, null, rules, null, null);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "字段规则操作成功");
            return result;
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "字段规则操作失败");
            return result;
        }
    }

    public Map<String, Object> jump(int dataSourceId, String table) {
        Map<String, Object> result = new HashMap<>(5);
        List<Map> jump = modelDesignMapper.jump();

        List<Map> list=new ArrayList<>();


        for (int j = 0; j < jump.size(); j++) {

            String id = jump.get(j).get("id").toString();
            String name = jump.get(j).get("name").toString();
            JSONObject process_definition_json = JSONObject.parseObject(jump.get(j).get("process_definition_json").toString());
            JSONArray tasks=process_definition_json.getJSONArray("tasks");

            for (int i = 0; i <tasks.size(); i++) {
                Map m = new HashMap<String,String>();
                Map map = (Map) tasks.get(i);
                String type = map.get("type").toString();
                Map params= (Map) map.get("params");
                int datasource=0;
                if(params.get("datasource")!=null && !params.get("datasource").equals("")){
                    datasource= Integer.parseInt(params.get("datasource").toString());
                }
                String table2= (String) params.get("table");

                if (type.equals("OUTPUT")){
                    if(datasource==dataSourceId && table2.equals(table)){
                        m.put("id",id);
                        m.put("name",name);
                        list.add(m);
                    }
                }
                if (type.equals("SQL_OUTPUT")){
                    if(datasource==dataSourceId && table2.equals(table)){
                        m.put("id",id);
                        m.put("name",name);
                        list.add(m);
                    }
                }
            }
        }
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> preview(String base, String tblName) throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        String sql="select * from "+base+"."+tblName+" limit 10 ";
        logger.info("SQL :"+sql);
        List<Map<String,String>> list = new ArrayList<>();
        List<Map<String,String>> linkList = new LinkedList<Map<String,String>>();

        DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
            logger.error(" hive 获取连接失败");
        }else{

            try{
                stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("desc " + base + "." + tblName);
                while (rs.next()){
                    if(rs.getString(2)==null || rs.getString(1).equals("")){
                        break;
                    }
                    Map map = new HashMap<String,String>();
                    String name = rs.getString(1);
                    String type = rs.getString(2);
                    String comment = rs.getString(3);
                    map.put("name",name);
                    map.put("type",type);
                    map.put("comment",comment);
                    linkList.add(map);

                }

                ResultSet res = stmt.executeQuery(sql);
                int count=res.getMetaData().getColumnCount();
                while(res.next()){
                    Map map=new HashMap();
                    for (int i = 1; i <=count; i++) {
                        Map<String, String> stringStringMap = linkList.get(i - 1);
                        String cloName=res.getMetaData().getColumnLabel(i);
                        String clo=cloName.split("\\.")[1];
                        String name = stringStringMap.get("comment");
                        if(!"".equals(name)){
                            clo=name;
                        }
                        String data = res.getString(cloName);
                        map.put(clo,data);
                    }
                    list.add(map);
                }
                res.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        stmt.close();
        conn.close();
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * 修改表结构 -- 增加列
     *
     * @param base
     * @param table
     * @param column
     * @param type
     * @param comment
     * @return 0代表创建成功，1代表创建失败
     */
    public Map<String, Object> addColumn(String base, String table, String column, String type, String comment) throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        // 建立hive表
        int i=1;
        DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
            logger.error(" hive 获取连接失败");
        }else{
            System.out.println("conn");
            stmt = conn.createStatement();
            try {
                i = stmt.executeUpdate("ALTER TABLE " + base + "." + table + " ADD COLUMNS ( " + column + " " + type + " comment '" + comment + "')");
            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        stmt.close();
        conn.close();
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 修改表结构 -- 修改列
     *
     * @param base
     * @param table
     * @param oldColumn
     * @param newColumn
     * @param type
     * @param comment
     * @return 0代表修改成功，1代表修改失败
     */
    public Map<String, Object> updateColumn(String base, String table, String oldColumn, String newColumn, String type, String comment) throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        // 建立hive表
        DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
            logger.error(" hive 获取连接失败");
        }else{
            stmt = conn.createStatement();
            try {
                String sql = "ALTER TABLE " + base + "." + table + " change " + oldColumn + " " + newColumn + " " + type + " comment '" + comment + "'";
                System.out.println(sql);
                stmt.executeUpdate(sql);
                putMsg(result, Status.CUSTOM_SUCESSS, "更新字段成功");
                return result;
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                stmt.close();
                conn.close();
            }
        }
        putMsg(result, Status.CUSTOM_FAILED, "更新字段失败");
        return result;
    }
}
