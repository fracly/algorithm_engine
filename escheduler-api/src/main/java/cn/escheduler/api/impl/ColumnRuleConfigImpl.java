//package cn.escheduler.api.impl;
//
//import cn.escheduler.api.jdbc.HiveJDBCHelper;
//import cn.escheduler.api.jdbc.JDBCHelper;
//import com.alibaba.fastjson.JSONObject;
//
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ColumnRuleConfigImpl implements IColumnRuleConfig {
//
//    /**
//     *  获取表的列配置规则
//     * @param base
//     * @param tblName
//     * @return
//     */
//    @Override
//    public List<Map<String,String>> getColumnRuleConf(String base, String tblName) {
//
//        List<Map<String,String>> colRuleList = new ArrayList<>();
//        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
//        String sql = "select * from ";
//        Object[] params = new Object[]{};
//        jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
//            @Override
//            public void process(ResultSet rs) throws Exception {
//
//                while (rs.next())
//                {
//                    Map<String,String> columnRuleMap = new HashMap<String,String>();
//                    String columnRule = rs.getString("columnRule");
//                    JSONObject jsonObject =  JSONObject.parseObject(columnRule);
//                    columnRuleMap.put("columnName",jsonObject.getString("columnName"));
//                    columnRuleMap.put("ruleName",jsonObject.getString("ruleName"));
//                    columnRuleMap.put("ruleCondition",jsonObject.getString("ruleCondition"));
//                    colRuleList.add(columnRuleMap);
//                }
//            }
//        });
//        return colRuleList;
//    }
//
//
//    /**
//     *  获取表的列配置规则
//     * @param base
//     * @param tblName
//     * @return
//     */
//    @Override
//    public int getResult(String base, String tblName) {
//
//        List<Map<String,String>> colRuleList = new ArrayList<>();
//        HiveJDBCHelper jdbcHelper = HiveJDBCHelper.getInstance();
//        String sql = "select count(1) from "+base+"."+tblName;
//        Object[] params = new Object[]{};
//        jdbcHelper.executeQuery(sql, params, new HiveJDBCHelper.QueryCallback() {
//            @Override
//            public void process(ResultSet rs) throws Exception {
//
//                System.out.println(rs+"==========================================================================");
//            }
//        });
//        return 0;
//    }
//
//}
