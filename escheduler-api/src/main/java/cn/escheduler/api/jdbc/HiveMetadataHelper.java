//package cn.escheduler.api.jdbc;
//
//import cn.escheduler.api.configuration.ConfigurationManager;
//import cn.escheduler.api.utils.Constants;
//import org.apache.hadoop.hive.conf.HiveConf;
//import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
//import org.apache.hadoop.hive.metastore.api.*;
//import org.apache.thrift.TException;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class HiveMetadataHelper {
//
//    private static HiveMetaStoreClient hiveMetaStoreClient;
//
//    private static HiveMetadataHelper instance = null;
//
//    /**
//     *  获取单例
//     * @return 单例
//     */
//    public static HiveMetadataHelper getInstance()
//    {
//        if (instance==null)
//        {
//            synchronized (HiveMetadataHelper.class)
//            {
//                if (instance==null)
//                {
//                    instance = new HiveMetadataHelper();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private HiveMetadataHelper()
//    {
//        HiveConf hiveConf = new HiveConf();
//        hiveConf.set("hive.metastore.uris", ConfigurationManager.getProperty(Constants.HIVE_METASTORE_URIS));
//        hiveConf.addResource("hive-site.xml");
//        try {
//            this.hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
//            this.hiveMetaStoreClient.setMetaConf("hive.metastore.client.capability.check","false");
//        } catch (TException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *  获取数据库信息
//     * @param dbName
//     * @return
//     */
//    public Database getDatabaseInfo(String dbName)
//    {
//        try {
//            return hiveMetaStoreClient.getDatabase(dbName);
//        } catch (TException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     *  获取hive表信息
//    * @param dbName 数据库名称
//     * @param tableName 表名称
//     * @return
//     */
//    public Table getTableInfo(String dbName,String tableName)
//    {
//        try {
//            return hiveMetaStoreClient.getTable(dbName,tableName);
//        } catch (TException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获取数据库，所有的表
//     * @param dbName 数据库名称
//     */
//    public List<String> getDbAllTables(String dbName) {
//        List<String> tableNames = new ArrayList<>();
//        try {
//            tableNames = hiveMetaStoreClient.getTables(dbName, "*"); // *表示获取所有的表
//        } catch (MetaException e) {
//            e.printStackTrace();
//        }
//        return tableNames;
//    }
//
//
//    /**
//     *  获取列信息
//     * @param dbName  数据库名称
//     * @param tableName 表名称
//     * @return
//     */
//    public List<FieldSchema> getFieldsList(String dbName, String tableName)
//    {
//        Table table = getTableInfo(dbName,tableName);
//        if(table != null) {
//            return table.getSd().getCols();
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     *  关闭客户端
//     */
//    public void closeClient()
//    {
//        this.hiveMetaStoreClient.close();
//    }
//
//
//
//    private static void silentDropDatabase(String dbName) throws MetaException, TException {
//        try {
//            for (String tableName : hiveMetaStoreClient.getTables(dbName, "*")) {
//                hiveMetaStoreClient.dropTable(dbName, tableName);
//            }
//            hiveMetaStoreClient.dropDatabase(dbName);
//        } catch (NoSuchObjectException e) {
//        } catch (InvalidOperationException e) {
//        }
//    }
//
//
//
//    public int createTable(String dbName, String tblName, Map<String,String> columnsMap) throws TException {
//        String typeName = "Person";
//        hiveMetaStoreClient.dropTable(dbName, tblName);
//        silentDropDatabase(dbName);
//
//        Database db = new Database();
//        db.setName(dbName);
//        hiveMetaStoreClient.createDatabase(db);
//        hiveMetaStoreClient.dropType(typeName);
//        Type typ1 = new Type();
//        typ1.setName(typeName);
//        typ1.setFields(new ArrayList<FieldSchema>(2));
///*        typ1.getFields().add(
//                new FieldSchema("name", Constants.STRING_TYPE_NAME, ""));
//        typ1.getFields().add(
//                new FieldSchema("income", Constants.INT_TYPE_NAME, ""));
//        client.createType(typ1);*/
//        Table tbl = new Table();
//        tbl.setDbName(dbName);
//        tbl.setTableName(tblName);
//        StorageDescriptor sd = new StorageDescriptor();
//        tbl.setSd(sd);
//        sd.setCols(typ1.getFields());
//        sd.setCompressed(false);
//        sd.setNumBuckets(1);
//        sd.setParameters(new HashMap<String, String>());
//        sd.getParameters().put("test_param_1", "Use this for comments etc");
//        sd.setBucketCols(new ArrayList<String>(2));
//        sd.getBucketCols().add("name");
//        sd.setSerdeInfo(new SerDeInfo());
//        sd.getSerdeInfo().setName(tbl.getTableName());
//        sd.getSerdeInfo().setParameters(new HashMap<String, String>());
//        sd.getSerdeInfo().getParameters().put(
//                org.apache.hadoop.hive.serde.Constants.SERIALIZATION_FORMAT, "1");
//        sd.getSerdeInfo().setSerializationLib(
//                org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe.class.getName());
//        tbl.setPartitionKeys(new ArrayList<FieldSchema>());
//        hiveMetaStoreClient.createTable(tbl);
//        return  0;
//    }
//
//
//}
