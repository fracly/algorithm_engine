package cn.escheduler.api.service;
;
import cn.escheduler.api.utils.HiveDataSourceUtil;
import cn.escheduler.dao.mapper.ModelDesignMapper;
import cn.escheduler.dao.model.ColRule;
import cn.escheduler.dao.model.TableInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


@Service
public class RunRuleService extends BaseService {


    @Autowired
    private ModelDesignMapper modelDesignMapper;

    private static final Logger logger = LoggerFactory.getLogger(ModelDesignService.class);

    public void runTblRule(String base, String tblName) throws SQLException {

        //获取 表规则信息
        TableInfo info= modelDesignMapper.getTableInfo(base, tblName);

        if(info!=null && !"".equals(info)) {

            List newList=new ArrayList<>();
            //表级别规则转换 json
            JSONArray tbljson= JSONArray.parseArray(info.getTabRule());

            if(tbljson!=null){
                // 遍历 tabrule 获取所有表规则
                for (int j = 0; j < tbljson.size(); j++) {
                    Map newMap = new HashMap();
                    //判断启动状态
                    if("ONLINE".equals(tbljson.getJSONObject(j).getString("isRun"))){
                        String type=tbljson.getJSONObject(j).getString("type");
                        String condi=tbljson.getJSONObject(j).getString("condi");
                        String param= tbljson.getJSONObject(j).getString("param");
                        newMap.put("isRun","ONLINE");
                        newMap.put("type",type);
                        newMap.put("condi",condi);
                        newMap.put("param",param);
                        //判断规则type
                        if("范围".equals(tbljson.getJSONObject(j).getString("type"))){
                            String sql="select count(1) as c from "+base+"."+tblName;
                            List<Map<String,String>> colRuleList = new ArrayList<>();
//                            HiveJDBCHelper jdbcHelper = HiveJDBCHelper.getInstance();
                            Object[] params = new Object[]{};
//                            jdbcHelper.executeQuery(sql, params, new HiveJDBCHelper.QueryCallback() {
//                                @Override
//                                public void process(ResultSet rs) throws Exception {
//                                    while (rs.next())
//                                    {
//                                        Map<String,String> map = new HashMap<String,String>();
//                                        map.put("count",rs.getString("c"));
//                                        colRuleList.add(map);
//                                    }
//                                }
//                            });

                            DataSource ds = HiveDataSourceUtil.getHiveDataSource();
                            Connection conn = ds.getConnection();
                            Statement stmt = null;
                            if(conn == null){
                                logger.error(" hive 获取连接失败");
                            }else{
                                try{
                                    stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql);
                                    int count=rs.getMetaData().getColumnCount();
                                    while(rs.next()){
                                        Map<String,String> map = new HashMap<String,String>();
                                        map.put("count",rs.getString("c"));
                                        colRuleList.add(map);
                                    }
                                }catch (SQLException e){
                                    e.printStackTrace();
                                }

                            }
                            stmt.close();
                            conn.close();

                            int count= Integer.parseInt(colRuleList.get(0).get("count"));
                            if (!compare(condi,param,count)){
                                newMap.put("tblWarn",count);
                                newMap.put("tabquality","正常");

                            }else {
                                newMap.put("tblWarn",count);
                                newMap.put("tabquality","异常");
                            }
                        }
                    }else if("XX".equals(tbljson.getJSONObject(j).getString("type"))){

                    }
                    newList.add(newMap);
                }
                JSONArray tabjson= JSONArray.parseArray(JSON.toJSONString(newList));

                modelDesignMapper.updateTblRule(base,tblName,tabjson,null,null,null);
            }
        }
    }

    public void runColRule(String base,String tblName) throws SQLException {
        //获取 表规则信息
        TableInfo info= modelDesignMapper.getColumnInfo(base, tblName);

        if(info!=null && !"".equals(info)) {

            List<ColRule> newList=new ArrayList<>();

            //表级别规则转换 json
            JSONArray clojson = JSONArray.parseArray(info.getColRule());

            if(clojson!=null) {
                // 遍历 clojson 获取所有字段规则
                for (int j = 0; j < clojson.size(); j++) {
                    ColRule colRule = new ColRule();
                    //判断启动状态
                    if("ONLINE".equals(clojson.getJSONObject(j).getString("isRun"))) {
                        String type = clojson.getJSONObject(j).getString("type");
                        String condi = clojson.getJSONObject(j).getString("condi");
                        String param = clojson.getJSONObject(j).getString("param");
                        String column = clojson.getJSONObject(j).getString("column");
                        colRule.setIsRun("ONLINE");
                        colRule.setType(type);
                        colRule.setCondi(condi);
                        colRule.setParam( param);
                        colRule.setColumn( column);
                        String sql="";
                        String sql1="";

                        //判断规则type
                        if("类型".equals(clojson.getJSONObject(j).getString("type"))){
                            if(condi.equals("空")){
                                condi="null";
                                 sql="select count(1) as c from "+base+"."+tblName+" where "+column+"= \'\'";
                                sql1="select * from "+base+"."+tblName+" where "+column+"= \'\' limit 10";
                            }else if (condi.equals("null")) {
                                 sql="select count(1) as c from "+base+"."+tblName+" where "+column+" is null";
                                sql1="select * from "+base+"."+tblName+" where "+column+" is null limit 10";
                            }else{

                            }
                        }
                        else {


                        }

                        if(!"".equals(sql)){

                            List<Map<String,String>> colRuleList = new ArrayList<>();
//                            HiveJDBCHelper jdbcHelper = HiveJDBCHelper.getInstance();
//                            Object[] params = new Object[]{};
//                            jdbcHelper.executeQuery(sql, params, new HiveJDBCHelper.QueryCallback() {
//                                @Override
//                                public void process(ResultSet rs) throws Exception {
//                                    while (rs.next())
//                                    {
//                                        Map<String,String> map = new HashMap<String,String>();
//                                        map.put("count",rs.getString("c"));
//                                        colRuleList.add(map);
//                                    }
//                                }
//                            });


                            DataSource ds = HiveDataSourceUtil.getHiveDataSource();
                            Connection conn = ds.getConnection();
                            Statement stmt = null;
                            if(conn == null){
                                logger.error(" hive 获取连接失败");
                            }else{
                                try{
                                    stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql);
                                    int count=rs.getMetaData().getColumnCount();
                                    while(rs.next()){
                                        Map<String,String> map = new HashMap<String,String>();
                                        map.put("count",rs.getString("c"));
                                        colRuleList.add(map);
                                    }
                                }catch (SQLException e){
                                    e.printStackTrace();
                                }

                            }
                            stmt.close();
                            conn.close();


                            List<Map<String,String>> list = new ArrayList<>();
//                            HiveJDBCHelper jdbcHelper2 = HiveJDBCHelper.getInstance();
//                            Object[] params2 = new Object[]{};
//                            jdbcHelper2.executeQuery(sql1, params2, new HiveJDBCHelper.QueryCallback() {
//                                @Override
//                                public void process(ResultSet rs) throws Exception {
//
//                                    int count=rs.getMetaData().getColumnCount();
//
//                                    while (rs.next())
//                                    {
//                                        Map map=new HashMap();
//                                        for (int i = 1; i <=count; i++) {
//                                            String cloName=rs.getMetaData().getColumnLabel(i);
//                                            String clo=cloName.split("\\.")[1];
//                                            String data = rs.getString(cloName);
//                                            map.put(clo,data);
//
//                                        }
//                                        list.add(map);
//                                    }
//                                }
//                            });


                            DataSource ds1 = HiveDataSourceUtil.getHiveDataSource();
                            Connection conn1 = ds1.getConnection();
                            Statement stmt1 = null;
                            if(conn1 == null){
                                logger.error(" hive 获取连接失败");
                            }else{
                                try{
                                    stmt1 = conn1.createStatement();
                                    ResultSet rs = stmt1.executeQuery(sql1);
                                    int count=rs.getMetaData().getColumnCount();
                                    while(rs.next()){
                                        Map map=new HashMap();
                                        for (int i = 1; i <=count; i++) {
                                            String cloName=rs.getMetaData().getColumnLabel(i);
                                            String clo=cloName.split("\\.")[1];
                                            String data = rs.getString(cloName);
                                            map.put(clo,data);

                                        }
                                        list.add(map);
                                    }
                                }catch (SQLException e){
                                    e.printStackTrace();
                                }

                            }
                            stmt1.close();
                            conn1.close();


                            if(colRuleList.size()>0){
                                int count= Integer.parseInt(colRuleList.get(0).get("count"));

                                if (!compare(condi,param,count)){
                                    colRule.setColWarn(count);
                                    colRule.setColQuality("正常");

                                }else {
                                    colRule.setColWarn(count);
                                    colRule.setColQuality("异常");
                                    colRule.setData(list);
                                }
                            }
                        }

                    }
                    newList.add(colRule);
                }
                JSONArray jsonRule= JSONArray.parseArray(JSON.toJSONString(newList, SerializerFeature.WriteMapNullValue));

                modelDesignMapper.updateTblRule(base,tblName,null,jsonRule,null,null);

            }

        }

    }


    public boolean compare(String condi,String param,int count){
    boolean boo=true;
    switch (condi){
        case "<" :
        case "null" :
            boo=Integer.parseInt(param)<count;
            break;
        case ">" :
            boo=Integer.parseInt(param)>count;
            break;
        case "<=" :
            boo=Integer.parseInt(param)<=count;
            break;
        case ">=" :
            boo=Integer.parseInt(param)>=count;
            break;
        case "=" :
            boo=Integer.parseInt(param)==count;
            break;
        case "!=" :
            boo=Integer.parseInt(param)!=count;
            break;
    }

    return boo;
    }

}
