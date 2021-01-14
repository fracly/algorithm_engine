package cn.escheduler.server.utils;

import cn.escheduler.api.utils.HiveDataSourceUtil;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class CreateTmpTable {

    private static final Logger logger = LoggerFactory.getLogger(CreateTmpTable.class);

    public static void createTmp(String table, JSONArray tbljson) throws SQLException {
        logger.info("table: "+table+"   tbljson： "+tbljson);


        String  sb="create table ";
        sb=sb+table+"(";
        for (int i=0;i<tbljson.size();i++) {
            String name = (String) ((Map) tbljson.get(i)).get("name");
            String type = (String) ((Map) tbljson.get(i)).get("type");
            String comment = " comment '" + (String) ((Map) tbljson.get(i)).get("comment") + "'";
            sb = sb + name + " " + type + " " + comment;
            if (i < tbljson.size() - 1) {
                sb = sb + " ,";
            } else {
                sb = sb + " )";
            }
        }


        javax.sql.DataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if(conn == null){
        }else{
            try{
                stmt = conn.createStatement();
                stmt.execute(sb);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        stmt.close();
        conn.close();

    }


}
