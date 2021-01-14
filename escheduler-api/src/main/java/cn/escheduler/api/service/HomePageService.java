package cn.escheduler.api.service;

import cn.escheduler.api.configuration.ConfigurationManager;

import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.HiveDataSourceUtil;
import cn.escheduler.common.enums.DbType;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.mapper.HomePageMapper;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.TopPV;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


import static cn.escheduler.api.enums.Status.GET_DATASOURCE_TABLE_LIST_FAILURE;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


@Service
public class HomePageService extends BaseService{

    @Autowired
    private  HomePageMapper homePageMapper;

    @Autowired
    private  DataSourceService dataSourceService;

//    @Autowired
//    private EsUtil esUtil;


    private int a=0;

    private static final Logger logger = LoggerFactory.getLogger(HomePageService.class);

    /**
     *   获取 base 的业务分类
     * @param userId
     */
    public Map getInfo(@RequestParam(value = "userId") String userId) {
        //启动定时任务
        if(0==a){
            timer(a);
            a++;
        }

        Map map=new HashMap();
        try {
            String modelGroup= homePageMapper.getModelGroup(userId);
            String model= homePageMapper.getModel(userId);
            double hiveSize=getHiveSize();
            double beforHiveSize=homePageMapper.getBeforHiveSize();
            double newAddHiveSize=hiveSize-beforHiveSize;
            BigDecimal b =  new  BigDecimal(newAddHiveSize);
            double f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();


            map.put("modelGroup",modelGroup);
            map.put("model",model);
            map.put("hiveBase",getHiveBaseSize().size());
            map.put("hiveTable",getHiveTableSize("1").size());
            map.put("hiveSize",hiveSize);
            map.put("newAddHiveSize",f1);

            return  map;
        } catch (Exception e) {
            logger.error(GET_DATASOURCE_TABLE_LIST_FAILURE.getMsg(),e);
            return null;
        }

    }

    public void timer(int a) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        Date time = calendar.getTime();

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    clearHiveTmpTable();
                    updateBeforHiveSize();
                    getHiveTableSize("0");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行

    }

    public void clearHiveTmpTable() {
        DataSource dataSource = dataSourceService.queryDataSourceObject(16);
        Connection connection = null;
        Statement stmt = null;
        Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
        String database = map.get("database");
        ResultSet columnRS = null;
        try {
            List<Map<String, String>> tableList = dataSourceService.getTableListById(DbType.HIVE, dataSource);
            javax.sql.DataSource hiveDataSource = HiveDataSourceUtil.getHiveDataSource();
            connection = hiveDataSource.getConnection();
            stmt = connection.createStatement();
            for (Map<String, String> tableMap : tableList) {
                String table = tableMap.get("name");
                columnRS = stmt.executeQuery("show table extended in " + database + " like " + table);

                while (columnRS.next()) {
                    String value = columnRS.getString(1);
                    if (value.startsWith("lastUpdateTime")) {
                        value = value.split(":")[1];
                        double floor = Math.floor((System.currentTimeMillis() - Long.valueOf(value)) / (24 * 3600 * 1000.0));
                        System.out.println(floor);
                        if (floor > 100) {
                            stmt.execute("drop table " + table);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("读取数据失败，请查看报错信息！" + e.getMessage());
        } catch (InterruptedException | ExecutionException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            HiveDataSourceUtil.close(connection, stmt, columnRS);
        }
    }

    public static double getHiveSize() throws IOException {
        String dirPath = "/user/hive/";
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", ConfigurationManager.getProperty(Constants.MASTER));
        FileSystem fileSystem = FileSystem.get(conf);
        Path path = new Path(dirPath);

        // 获取文件列表
        FileStatus[] files = fileSystem.listStatus(path);
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Cannot access " + dirPath + ": No such file or directory.");
        }
        long totalSize=0;
        for (int i = 0; i < files.length; i++) {
            String pathStr = files[i].getPath().toString();
            String base=pathStr.replaceAll(ConfigurationManager.getProperty(Constants.MASTER)+dirPath,"").replaceAll(".db","");
            FileSystem fs = files[i].getPath().getFileSystem(conf);
            totalSize += fs.getContentSummary(files[i].getPath()).getLength();
            fs.close();
        }
        double f=(double)totalSize/(1024*1024*1024);
        BigDecimal b =  new  BigDecimal(f);
        double f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        return  f1;
    }

    public List<Map> getHiveBaseSize() throws IOException {
        String dirPath = "/user/hive/warehouse/";
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", ConfigurationManager.getProperty(Constants.MASTER));
        FileSystem fileSystem = FileSystem.get(conf);
        Path path = new Path(dirPath);

        // 获取文件列表
        FileStatus[] files = fileSystem.listStatus(path);
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Cannot access " + dirPath + ": No such file or directory.");
        }

        List<Map> list =new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Map map=new HashMap();
            String pathStr = files[i].getPath().toString();
            if(pathStr.contains(".db")){
                String base=pathStr.replaceAll(ConfigurationManager.getProperty(Constants.MASTER)+dirPath,"").replaceAll(".db","");
                FileSystem fs = files[i].getPath().getFileSystem(conf);
                long totalSize = fs.getContentSummary(files[i].getPath()).getLength();
                fs.close();
                double f=(double)totalSize/(1024*1024*1024);
                BigDecimal b   =   new   BigDecimal(f);
                double   f1   =   b.setScale(3,   BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("base",base);
                map.put("size",f1);
                list.add(map);
            }
        }
        return  list;
    }


    public Map getHiveTableSize(String flag) throws IOException {
        String dirPath = "/user/hive/warehouse/";
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", ConfigurationManager.getProperty(Constants.MASTER));
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        FileSystem fileSystem = FileSystem.get(conf);
        Path path = new Path(dirPath);

        // 获取文件列表
        FileStatus[] files = fileSystem.listStatus(path);
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Cannot access " + dirPath + ": No such file or directory.");
        }
        Map map = new HashMap();

        //遍历所有 hive base
        for (int i = 0; i < files.length; i++) {
            String dirPath2 = files[i].getPath().toString();
            Path path2 = new Path(dirPath2);
            FileStatus[] files2 = fileSystem.listStatus(path2);

            //遍历所有 hive table
            for(int j=0;j<files2.length;j++){
                String pathStr = files2[j].getPath().toString();
                FileSystem fs = files2[j].getPath().getFileSystem(conf);
                long totalSize = fs.getContentSummary(files2[j].getPath()).getLength();
                String table=pathStr.replaceAll(ConfigurationManager.getProperty(Constants.MASTER)+dirPath,"").replaceAll(".db","");

                if(!table.contains(".")){
                    String hiveTableName=table.replaceAll("/",".");
                    long hiveTableSize=totalSize;
                    if(flag=="0"){
                        homePageMapper.insertBeforHiveTableSize(hiveTableSize,hiveTableName);
                        homePageMapper.updateBeforHiveTableSize(hiveTableSize,hiveTableName);
                    }

                    map.put(hiveTableName,totalSize);
                }

            }

        }
        fileSystem.close();
        return  map;

    }


    public List getTopHiveTableSize() throws IOException {
        Map<String, Long> hiveTableSize = getHiveTableSize("1");
//        List<Map.Entry<String, Integer>> mobileList = hiveTableSize.entrySet().stream().sorted((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o2.getValue() - o1.getValue())
//        .collect(Collectors.toList())
//                        .subList(0, 10);

        LinkedHashMap<String, Long> collect = hiveTableSize.entrySet().stream().sorted(Collections.reverseOrder(comparingByValue())).collect(
                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
        List<Map.Entry<String, Long>> mobileList = collect.entrySet().stream().collect(Collectors.toList());

        List<Map> list=new ArrayList();
        for(Map.Entry<String, Long> mapE : mobileList){
            Map m=new HashMap();
            String name=mapE.getKey();
            Long size= Long.valueOf(mapE.getValue());
            double msize=(double)size/(1024*1024);
            BigDecimal b1   =   new   BigDecimal(msize);
            double   f1   =   b1.setScale(3,   BigDecimal.ROUND_HALF_UP).doubleValue();

            m.put("table",name);
            m.put("size",f1);
            list.add(m);


        }


        return list;
    }

    public List getTopHiveTableAddSize() throws IOException {

        Map<String, Long> m=new HashMap();

        Map<String, Long> hiveTableSize = getHiveTableSize("1");

        //获取 所有新增表大小
        for(Map.Entry<String,Long> map : hiveTableSize.entrySet()){
            String name=map.getKey();
            Long size= map.getValue();
            Long beforSize=homePageMapper.getBeforHiveTableSize(name);
            if(beforSize==null){
                beforSize= Long.valueOf(0);
            }
            Long addSize=size-beforSize;

            m.put(name,addSize);
        }

        //对所有新增量 进行排序 倒叙 前十
//        List<Map.Entry<String,Integer>>  addList=m.entrySet().stream().sorted((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o2.getValue() - o1.getValue()).collect(Collectors.toList())
//                .subList(0, 10);
        LinkedHashMap<String, Long> collect = m.entrySet().stream().sorted(Collections.reverseOrder(comparingByValue())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        List<Map.Entry<String, Long>> addList = collect.entrySet().stream().collect(Collectors.toList());

        List<Map> list=new ArrayList();

        // 提供 list 接口格式
        for(Map.Entry<String, Long> mapE : addList){
            Map map=new HashMap();
            String table=mapE.getKey();
            Long addSize= mapE.getValue();
            double addSizeD=(double)addSize/(1024*1024);
            BigDecimal b1   =   new   BigDecimal(addSizeD);
            double   f1   =   b1.setScale(3,   BigDecimal.ROUND_HALF_UP).doubleValue();

            map.put("table",table);
            map.put("addSize",f1);

            list.add(map);
        }

        return list;
    }

    public int updateBeforHiveSize() throws IOException {
        double hiveSize= HomePageService.getHiveSize();
        return homePageMapper.updateBeforHiveSize(hiveSize);

    }



//    public List getTopNApi(int topN,String startTime,String endTime) {
//        List<Map> res=new ArrayList<>();
//        try {
//            if(topN<0){
//                topN =10;
//            }
//            SearchRequest searchRequest = new SearchRequest("statistic").types("log");
//            BoolQueryBuilder bq = QueryBuilders.boolQuery();
//
//            if(StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)){
//                bq.must(QueryBuilders.rangeQuery("invokeTime").gte(startTime).lte(endTime));
//            }
//            AggregationBuilder agg = AggregationBuilders.terms("count").field("interfaceType").size(topN);
//
//            SearchRequest request = searchRequest.source(new SearchSourceBuilder()
//                    .query(bq).aggregation(agg).size(0)
//            );
//            SearchResponse response =  esUtil.getClient().search(request);
//
//            Terms dwbhTerms = response.getAggregations().get("count");
//            for (Terms.Bucket entry : dwbhTerms.getBuckets()) {
//                String key = entry.getKeyAsString();
//                Map temp = new HashMap();
//                temp.put("name",key);
//                temp.put("value",entry.getDocCount());
//                res.add(temp);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    public List<TopPV> getTopPV() {
        List<TopPV> topPV = homePageMapper.getTopPV();

        return topPV;
    }
    public List<TopPV> getTopZD() {
        List<TopPV> topPV = homePageMapper.getTopZD();

        return topPV;
    }

}
