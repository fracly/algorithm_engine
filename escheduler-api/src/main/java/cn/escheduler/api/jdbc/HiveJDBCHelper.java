//package cn.escheduler.api.jdbc;
//
//import cn.escheduler.api.configuration.ConfigurationManager;
//import cn.escheduler.api.utils.Constants;
//
//import java.sql.*;
//import java.util.LinkedList;
//
//public class HiveJDBCHelper {
//
//    /**
//     * hiveJDBC 辅助组件
//     *
//     */
//    static
//    {
//        String driver = ConfigurationManager.getProperty(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
//        try {
//            Class.forName(driver);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static HiveJDBCHelper instance = null;
//
//
//    /**
//     *  获取单例
//     * @return 单例
//     */
//    public static HiveJDBCHelper getInstance()
//    {
//        if (instance==null)
//        {
//            synchronized (HiveJDBCHelper.class)
//            {
//                if (instance==null)
//                {
//                    instance = new HiveJDBCHelper();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//    private LinkedList<Connection> dataSource = new LinkedList<>();
//
//    private HiveJDBCHelper() {
//
//        // 获取数据库连接池的大小，通过配置文件配置
//        int dataConnectionSize = ConfigurationManager.getInteger(Constants.HIVE_CONNECTION_SIZE);
//        // 创建指定数量的数据库连接池，并放入数据库
//        for (int i = 0; i < dataConnectionSize; i++) {
//                String url = ConfigurationManager.getProperty(Constants.HIVE_CONNECTION_URL);
//                String user = ConfigurationManager.getProperty(Constants.HIVE_CONNECTION_USER);
//            try {
//                Connection connection = DriverManager.getConnection(url,user,"");
//                dataSource.push(connection);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     *  获取数据库连接池
//     * @return
//     */
//    public synchronized Connection getConnection()
//    {
//        while (dataSource.size()==0)
//        {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return dataSource.poll();
//    }
//
//    /**
//     * 执行更新
//     * @param sql
//     * @return
//     */
//    public int executeUpdate(String sql)
//    {
//      return  executeUpdate(sql,null);
//    }
//
//    /**
//     * 执行更新
//     * @param sql
//     * @param params
//     * @return
//     */
//    public int executeUpdate(String sql,Object[] params)
//    {
//        int rtn = -1;
//        Connection conn = null;
//        PreparedStatement pstmrt = null;
//
//        conn = getConnection();
//        try {
//            conn.setAutoCommit(false);
//            pstmrt = conn.prepareStatement(sql);
//            if (params!=null&&params.length>0)
//            {
//                for (int i = 0; i < params.length;i++)
//                {
//                    pstmrt.setObject(i+1,params[i]);
//                }
//            }
//            rtn = pstmrt.executeUpdate();
//            conn.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (conn!=null)
//            {
//                dataSource.push(conn);
//            }
//        }
//        return rtn;
//    }
//
//
//    /**
//     * 执行查询SQL语句
//     * @param sql
//     * @param params
//     * @param callback
//     */
//    public void executeQuery(String sql, Object[] params,
//                             QueryCallback callback) {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = getConnection();
//            pstmt = conn.prepareStatement(sql);
//
//            if(params != null && params.length > 0) {
//                for(int i = 0; i < params.length; i++) {
//                    pstmt.setObject(i + 1, params[i]);
//                }
//            }
//            rs = pstmt.executeQuery();
//            callback.process(rs);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(conn != null) {
//                dataSource.push(conn);
//            }
//        }
//    }
//
//
//    /**
//     * 静态内部类：查询回调接口
//     * @author Administrator
//     *
//     */
//    public static interface QueryCallback {
//
//        /**
//         * 处理查询结果
//         * @param rs
//         * @throws Exception
//         */
//        void process(ResultSet rs) throws Exception;
//
//    }
//
//
//}
