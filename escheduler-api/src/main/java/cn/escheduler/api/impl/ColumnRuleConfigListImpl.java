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
//public class ColumnRuleConfigListImpl {
//
//    public List<Map<String,String>> getColumnRuleConfList()
//    {
//        List<Map<String,String>> colRuleList = new ArrayList<>();
//        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
//        String sql = "select * from shedule.Column_rule";
//        Object[] params = new Object[]{};
//        jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
//            @Override
//            public void process(ResultSet rs) throws Exception {
//                while (rs.next())
//                {
//                    Map<String,String> map = new HashMap<String,String>();
//                 //   map.put("colName",rs.getString("colName"));
//                    map.put("ruleId",rs.getString("ruleId"));
//                    map.put("ruleName",rs.getString("ruleName"));
//                    map.put("ruleCondition",rs.getString("ruleCondition"));
//                    colRuleList.add(map);
//                }
//            }
//        });
//        return  colRuleList;
//    }
//
//}
