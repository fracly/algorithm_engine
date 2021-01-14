//package cn.escheduler.api.impl;
//
//import cn.escheduler.api.jdbc.JDBCHelper;
//
//import java.sql.ResultSet;
//import java.util.HashMap;
//import java.util.Map;
//
//public class TableRuleConfigImpl implements ITableRuleConfig {
//
//    /**
//     *   获取表的规则配置信息
//     * @param base
//     * @param tableName
//     * @return
//     */
//    @Override
//    public Map<String, String> getTableRuleConf(String base, String tableName)
//    {
//        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
//        String sql = "";
//        Object[] params = new Object[]{};
//        Map<String,String> rtnMap = new HashMap<String,String>();
//        jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
//            @Override
//            public void process(ResultSet rs) throws Exception {
//                while (rs.next())
//                {
//                    rtnMap.put("","");
//                }
//            }
//        });
//        return rtnMap;
//    }
//}
