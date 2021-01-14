//package cn.escheduler.api.impl;
//
//import cn.escheduler.api.jdbc.JDBCHelper;
//
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TableRuleConfigListImpl {
//
//    public  List<Map<String,String>> getTableRuleConfList()
//    {
//        List<Map<String,String>> ruleList = new ArrayList<>();
//        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
//        String sql = "select * from shedule.table_rule";
//        jdbcHelper.executeQuery(sql, null, new JDBCHelper.QueryCallback() {
//            @Override
//            public void process(ResultSet rs) throws Exception {
//                while (rs.next())
//                {
//                    Map<String,String> map = new HashMap<String,String>();
//                    map.put("ruleId",rs.getString("ruleId"));
//                    map.put("ruleName",rs.getString("ruleName"));
//                    map.put("ruleCondition",rs.getString("ruleCondition"));
//                    ruleList.add(map);
//                }
//            }
//        });
//    return  ruleList;
//    }
//
//}
