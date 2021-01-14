package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HiveDataSourceUtil;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.mapper.EnumMapper;
import cn.escheduler.dao.mapper.SchemaBranchMapper;
import cn.escheduler.dao.mapper.SchemaMapper;
import cn.escheduler.dao.mapper.SchemaVersionMapper;
import cn.escheduler.dao.model.Schema;
import cn.escheduler.dao.model.SchemaBranch;
import cn.escheduler.dao.model.SchemaVersion;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * schema service
 */
@Service
public class SchemaService extends BaseService{
    public static final Logger logger = LoggerFactory.getLogger(SchemaService.class);

    @Autowired
    private SchemaMapper schemaMapper;

    @Autowired
    private SchemaBranchMapper schemaBranchMapper;

    @Autowired
    private SchemaVersionMapper schemaVersionMapper;

    @Autowired
    private EnumMapper enumMapper;

    /** request for select component **/
    public List<Map<String, Object>> getAllSchemaResouces(){
        List<Map<String, Object>> list = enumMapper.getAllSchemaResources();
        return list;
    }

    public List<Map<String, Object>> getSchemaBusinessTopic(String layer){
        List<Map<String, Object>> list = enumMapper.getSchemaBusinessTopic(layer);
        return list;
    }

    /** request for curd operation to schema **/
    public Map<String, Object> clean2init() throws SQLException {
        Map<String, Object> result = new HashMap<>(5);
        //clean
        schemaVersionMapper.deleteAll();
        schemaBranchMapper.deleteAll();

        //initialize
        List<Schema> schemaList = schemaMapper.getAllSchemas();
        for(Schema schema : schemaList) {
            SchemaBranch schemaBranch = initializeDefaultBranch(schema);
            int branchId = schemaBranchMapper.insert(schema, schemaBranch);
            SchemaVersion schemaVersion = initializeDefaultVersion(schema, branchId);

            //get column detail from hive
            Map<String, Object> schemaTextMap = composeColumnMap(schema, schemaVersion);
            schemaVersion.setSchemaText(JSON.toJSONString(schemaTextMap));
            schemaVersionMapper.insert(schemaVersion, schema, schemaBranch);
        }
        putMsg(result, Status.SUCCESS);
        return result;
    }

//    public int initialize(String base, String tableName) throws SQLException {
//        Schema schema = querySchema(base, tableName);
//        Integer branchId = schemaBranchMapper.queryByName(base, tableName, "master");
//        if(branchId == null || branchId == 0) {
//            SchemaBranch schemaBranch = initializeDefaultBranch(schema);
//            schemaBranchMapper.insert(schema, schemaBranch);
//            branchId = schemaBranchMapper.queryByName(base, tableName, "master");
//            if(branchId == 0) {
//                logger.error("创建分支失败！"); return 0;
//            }
//        }
//        SchemaBranch schemaBranch = schemaBranchMapper.queryById(branchId);
//        SchemaVersion schemaVersion = schemaVersionMapper.queryLastestVersionSchema(base, tableName, "master");
//        if( schemaVersion != null ) {
//            SchemaVersion nextVersion = incrementVersion(schema, branchId, schemaVersion);
//            Map<String, Object> schemaTextMap = composeColumnMap(schema, nextVersion);
//            nextVersion.setSchemaText(JSON.toJSONString(schemaTextMap));
//            return schemaVersionMapper.insert(nextVersion, schema, schemaBranch);
//        } else {
//            SchemaVersion newVersion = initializeDefaultVersion(schema, branchId);
//            Map<String, Object> schemaTextMap = composeColumnMap(schema, newVersion);
//            newVersion.setSchemaText(JSON.toJSONString(schemaTextMap));
//            return schemaVersionMapper.insert(newVersion, schema, schemaBranch);
//        }
//    }

//    public int addVersion(String base, String tableName) throws SQLException {
//        Schema schema = querySchema(base, tableName);
//        int branchId = schemaBranchMapper.queryByName(base, tableName, "master");
//        SchemaBranch branch = schemaBranchMapper.queryById(branchId);
//        SchemaVersion lastVersion = schemaVersionMapper.queryLastestVersionSchema(base, tableName, branch.getName());
//        SchemaVersion schemaVersion = incrementVersion(schema, branch.getId(), lastVersion);
//        Map<String, Object> schemaTextMap = composeColumnMap(schema, schemaVersion);
//        schemaVersion.setSchemaText(JSON.toJSONString(schemaTextMap));
//        int count  =schemaVersionMapper.insert(schemaVersion, schema, branch);
//        return count;
//    }

    public Result<List<Schema>> querySchemas(Integer pageNo, Integer pageSize, String layer, String topic, String searchVal) {
        Result result = new Result();
        int offset = 0;
        if(pageNo > 1) { offset = (pageNo - 1) * 10;}

        List<Schema> schemas = schemaMapper.querySchemas(offset, pageSize, layer, topic, searchVal);
        result.setData(schemas);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("total", schemaMapper.getTotalSize(layer, topic, searchVal));
        result.setDataMap(dataMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Schema querySchema(String layer, String schemaName){
        return schemaMapper.queryByKey(layer, schemaName);
    }

    public List<SchemaBranch> getSchemaBranches(String layer, String schemaName) {
        List<SchemaBranch> list = schemaBranchMapper.queryByKey(layer, schemaName);
        return list;
    }

    public List<SchemaVersion> getSchemaVersions(String layer, String schemaName) {
        List<SchemaVersion> list = schemaVersionMapper.queryByKey(layer, schemaName);
        return list;
    }

    public SchemaVersion queryLastestVersionSchema(String layer, String schemaName, String branchName){
        SchemaVersion version = schemaVersionMapper.queryLastestVersionSchema(layer, schemaName, branchName);
        return version;
    }

    private SchemaBranch initializeDefaultBranch(Schema schema) {
        SchemaBranch schemaBranch = new SchemaBranch();
        schemaBranch.setName("master");
        schemaBranch.setSchemaName(schema.getTablename());
        schemaBranch.setBase(schema.getLayer());
        schemaBranch.setDesc("元数据的默认分支，master");
        schemaBranch.setCreateTime(new Date());
        return schemaBranch;
    }

    private SchemaVersion initializeDefaultVersion(Schema schema, int branchId) {
        SchemaVersion schemaVersion = new SchemaVersion();
        schemaVersion.setName("默认初始版本");
        schemaVersion.setVersion(1);
        schemaVersion.setDesc("初始化后，各元数据均有一个版本号为1的初始版本信息");
        schemaVersion.setCreateTime(new Date());
        schemaVersion.setUpdateTime(new Date());
        schemaVersion.setBranchId(branchId);
        schemaVersion.setState(1);
        schemaVersion.setBase(schema.getLayer());
        schemaVersion.setSchemaTableName(schema.getTablename());
        return schemaVersion;
    }

//    private SchemaVersion incrementVersion(Schema schema, int branchId, SchemaVersion schemaVersion) {
//        SchemaVersion newVersion = new SchemaVersion();
//        newVersion.setName("递增版本");
//        newVersion.setVersion(schemaVersion.getVersion() + 1);
//        newVersion.setDesc("修改字段信息后的自增版本");
//        newVersion.setCreateTime(new Date());
//        newVersion.setUpdateTime(new Date());
//        newVersion.setBranchId(branchId);
//        newVersion.setState(1);
//        newVersion.setBase(schema.getLayer());
//        newVersion.setSchemaTableName(schema.getTablename());
//        return newVersion;
//    }

    private Map<String, Object> composeColumnMap(Schema schema, SchemaVersion schemaVersion) throws SQLException {
//        HiveMetadataHelper helper = HiveMetadataHelper.getInstance();
//        List<FieldSchema> fieldsList = helper.getFieldsList(schema.getLayer(), schema.getTablename());
        Map<String, Object> schemaTextMap = new HashMap<>();
        schemaTextMap.put("name", schemaVersion.getName());
        schemaTextMap.put("package", "com.ahbcht.algorithm-engine");
//        if(fieldsList != null) {
//            List<Map<String, Object>> list = new ArrayList<>();
//            for(FieldSchema fieldSchema : fieldsList) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("name", fieldSchema.getName());
//                map.put("type", fieldSchema.getType());
//                map.put("comment", fieldSchema.getComment());
//                list.add(map);
//            }
//            schemaTextMap.put("columns", list);
//        }

        javax.sql.DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
            logger.error(" hive 获取连接失败");
        }else{

            try{
                stmt = conn.createStatement();
                ResultSet rs= stmt.executeQuery("desc " + schema.getLayer() + "." +schema.getTablename());
                List<Map<String, Object>> list = new ArrayList<>();
                while(rs.next()){
                    if(rs.getString(2)==null || rs.getString(1).equals("")){
                        break;
                    }
                    Map<String, Object> m = new HashMap<>();
                    String name = rs.getString(1);
                    String type1 = rs.getString(2);
                    String comment = rs.getString(3);
                    m.put("name",name);
                    m.put("type",type1);
                    m.put("comment",comment);
                    list.add(m);
                }
                schemaTextMap.put("columns", list);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        stmt.close();
        conn.close();
        return schemaTextMap;
    }

    public boolean initializeNewTableSchema(String database, String tableName) {
        Schema schema = schemaMapper.queryByKey(database, tableName);
        SchemaBranch schemaBranch = initializeDefaultBranch(schema);
        int branchId = schemaBranchMapper.insert(schema, schemaBranch);
        SchemaVersion schemaVersion = initializeDefaultVersion(schema, branchId);
        Map<String, Object> schemaTextMap = null;
        try {
            schemaTextMap = composeColumnMap(schema, schemaVersion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        schemaVersion.setSchemaText(JSON.toJSONString(schemaTextMap));
        int count = schemaVersionMapper.insert(schemaVersion, schema, schemaBranch);
        if(count > 0) {
            return true;
        }
        return false;
    }
}
