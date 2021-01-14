package cn.escheduler.api.utils;

import cn.escheduler.api.configuration.ConfigurationManager;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HiveDataSourceUtil {
    private static DruidDataSource hiveDataSource = new DruidDataSource();
    public static Connection conn = null;


    public static DruidDataSource getHiveDataSource() {
        if(hiveDataSource.isInited()){
            return hiveDataSource;
        }

        try {

            //基本属性 url、user、password
            hiveDataSource.setUrl(ConfigurationManager.getProperty(Constants.HIVE_CONNECTION_URL));
//            hiveDataSource.setUsername(dsProp.getProperty("hive_jdbc_username"));
//            hiveDataSource.setPassword(dsProp.getProperty("hive_jdbc_password"));

//            //配置初始化大小、最小、最大
            hiveDataSource.setInitialSize(20);
            hiveDataSource.setMinIdle(20);
            hiveDataSource.setMaxActive(200);
//
//            //配置获取连接等待超时的时间
            hiveDataSource.setMaxWait(6000);

            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            hiveDataSource.setTimeBetweenEvictionRunsMillis(30000);

            //配置一个连接在池中最小生存的时间，单位是毫秒
            hiveDataSource.setMinEvictableIdleTimeMillis(300000);

//            hiveDataSource.setValidationQuery("select * from xxxx");
            hiveDataSource.setTestWhileIdle(false);
//            hiveDataSource.setTestOnBorrow(false);
//            hiveDataSource.setTestOnReturn(false);

            //打开PSCache，并且指定每个连接上PSCache的大小
            hiveDataSource.setPoolPreparedStatements(true);
            hiveDataSource.setMaxPoolPreparedStatementPerConnectionSize(40);

            //配置监控统计拦截的filters
//            hiveDataSource.setFilters("stat");

            hiveDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
            closeHiveDataSource();
        }
        return hiveDataSource;
    }

    /**
     *@Description:关闭Hive连接池
     */
    public static void closeHiveDataSource(){
        if(hiveDataSource != null){
            hiveDataSource.close();
        }
    }

    /**
     *
     *@Description:获取Hive连接
     *@return
     */
    public static Connection getHiveConn(){
        try {
            hiveDataSource = getHiveDataSource();
            conn = hiveDataSource.getConnection();
        } catch (SQLException e) {

        }
        return conn;
    }

    /**
     *@Description:关闭Hive数据连接
     */
    public static void closeConn(){
        try {
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {

        }
    }

    public static void close(Connection connection, Statement stmt, ResultSet rs) {
        if(rs != null) {
            try{
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(connection, stmt);
    }

    public static void close(Connection connection, Statement stmt) {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(connection);
    }

    public static void close(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
