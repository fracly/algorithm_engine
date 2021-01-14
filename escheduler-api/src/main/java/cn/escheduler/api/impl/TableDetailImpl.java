//package cn.escheduler.api.impl;
//
//import cn.escheduler.api.jdbc.HiveMetadataHelper;
//import cn.escheduler.api.jdbc.JDBCHelper;
//import cn.escheduler.dao.mapper.ModelDesignMapper;
//import cn.escheduler.dao.model.TableInfo;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.hadoop.hive.metastore.api.FieldSchema;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.sql.ResultSet;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//public class TableDetailImpl implements ITableDetail {
//
//    @Autowired
//    private ModelDesignMapper modelDesignMapper;
//
//
//    @Override
//    public List<Map<String, String>> getTableDetailInfo(String base, String tblName) {
//        // 1.获取hive元数据信息
//        List<Map<String,String>> list = new LinkedList<Map<String,String>>();
//        HiveMetadataHelper helper = HiveMetadataHelper.getInstance();
//        List<FieldSchema> fieldsList = helper.getFieldsList(base, tblName);
//        for (int i = 0; i < fieldsList.size(); i++) {
//            Map map = new HashMap<String,String>();
//            FieldSchema fieldSchema = fieldsList.get(i);
//            String name = fieldSchema.getName();
//            String type = fieldSchema.getType();
//            String comment = fieldSchema.getComment();
//            map.put("name",name);
//            map.put("type",type);
//            map.put("comment",comment);
//            list.add(map);
//        }
//        // 2.获取mysql中主表的列信息
//
////        TableInfo info=modelDesignMapper.getTableInfo(base,tblName);
////        System.out.println("================================================="+info.getColquality());
////        System.out.println(info.getColrule());
//
//
//
//
//
//
////        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
////        String sql = "select * from  escheduler.t_escheduler_model_design where base = ? and tablename= ? " ;
////       Object[] params = new Object[]{base,tblName};
////        jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
////            @Override
////            public void process(ResultSet rs) throws Exception {
////                String colQualities =  rs.getString("colquality");
////                String colWarns = rs.getString("colwarn");
////                String colRulers = rs.getString("colrule");
////                if (colQualities!=null&&colWarns!=null&&colRulers!=null)
////                {
////                    JSONObject colQualityJson = JSONObject.parseObject(colQualities);
////                    JSONObject colWarnJson = JSONObject.parseObject(colWarns);
////                    JSONObject colRulerJson = JSONObject.parseObject(colRulers);
////                    for (int i = 0; i < list.size(); i++) {
////                        Map<String, String> map = list.get(i);
////                        String name = map.get("name");
////                        String colRule = colRulerJson.getString(name);
////                        String  colQuality = colQualityJson.getString(name);
////                        String colWarn = colWarnJson.getString(name);
////                        map.put("colRule",colRule);
////                        map.put("colQuality",colQuality);
////                        map.put("colWarn",colWarn);
////                    }
////                }
////            }
////        });
//        return list;
//    }
//}
