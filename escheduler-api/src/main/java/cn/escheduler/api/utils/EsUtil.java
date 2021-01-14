//package cn.escheduler.api.utils;
//
//import com.alibaba.fastjson.JSON;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.elasticsearch.action.admin.indices.alias.Alias;
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
//import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
//import org.elasticsearch.action.bulk.*;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.unit.ByteSizeUnit;
//import org.elasticsearch.common.unit.ByteSizeValue;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author lkx
// * @version 1.0
// * @date 2020/2/24
// */
//public class EsUtil implements Serializable {
//    private static final Logger logger = LogManager.getLogger(EsUtil.class);
//    private RestHighLevelClient client=null;
//
//    /**
//     * 批量提交阀值
//     */
//    private Integer batchSize = 1000;
//    /**
//     * 累积提交的阀值 字节
//     */
//    private Integer byteSize = 1000;
//    /**
//     * 每10秒提交一次
//     */
//    private Integer interval = 10;
//    /**
//     * 并发数
//     */
//    private Integer concurrentSize = 2;
//
//    private BulkProcessor bulkProcessor=null;
//
//    public EsUtil(String esurl) {
//        if (StringUtils.isEmpty(esurl)) {
//            logger.error("ES的URL地址不能为空！");
//        } else {
//            String[] hosts = StringUtils.split(esurl, ",");
//            HttpHost[] httpHosts = new HttpHost[hosts.length];
//
//            for(int i = 0; i < hosts.length; ++i) {
//                String[] tmps = StringUtils.split(hosts[i], ":");
//                httpHosts[i] = new HttpHost(tmps[0], Integer.parseInt(tmps[1]), "http");
//            }
//
//            this.client = new RestHighLevelClient(RestClient.builder(httpHosts));
//        }
//    }
//
//    public EsUtil(String esUrl, String clusterName) {
//        this(esUrl);
//    }
//
//    public EsUtil(String esUrl, Integer batchSize, Integer byteSize, Integer interval, Integer concurrentSize) {
//        this(esUrl);
//        this.batchSize = batchSize;
//        this.byteSize = byteSize;
//        this.interval = interval;
//        this.concurrentSize = concurrentSize;
//    }
//
//    /**
//     * 默认监听
//     */
//    private BulkProcessor.Listener defaultBulkListener = new BulkProcessor.Listener() {
//        @Override
//        public void beforeBulk(long executionId, BulkRequest request) {
//
//        }
//
//        @Override
//        public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
//            BulkItemResponse[] brs = bulkResponse.getItems();
//            if (bulkResponse.hasFailures() && null != brs && brs.length > 0) {
//                for (int i = 0; i < brs.length; i++) {
//                    if (brs[i].isFailed()) {
//                        logger.error("ES调用发生错误，编号:{}, 错误代码为:{}", brs[i].getId(), brs[i].getFailureMessage());
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
//
//        }
//    };
//
//    /**
//     * 获取批量提交工具类
//     *
//     * @return
//     */
//    public BulkProcessor getBulkProcessor() {
//
//        if (bulkProcessor == null) {
//            BulkProcessor.Builder builder = BulkProcessor.builder(client::bulkAsync, defaultBulkListener);
//
//            builder.setBulkActions(batchSize);
//            builder.setBulkSize(new ByteSizeValue(byteSize, ByteSizeUnit.MB));
//            builder.setConcurrentRequests(concurrentSize);
//            builder.setFlushInterval(TimeValue.timeValueSeconds(interval));
//            builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));
//
//            bulkProcessor = builder.build();
//        }
//        return bulkProcessor;
//    }
//
//
//    public void setDefaultBulkListener(BulkProcessor.Listener defaultBulkListener) {
//        this.defaultBulkListener = defaultBulkListener;
//    }
//
//    public RestHighLevelClient getClient() {
//        return this.client;
//    }
//
//    /**
//     * 功能描述：关闭链接
//     */
//    public void close() {
//        try {
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 创建ES索引
//     *
//     * @param index 索引名称
//     */
//    public boolean createIndex (String index,String defaultMappingsJson,String mappingsJson) {
//        int existIndex = existIndex(index);
//        if (existIndex == 3) {
//            logger.error("判断索引是否存在时，发生IO异常");
//            return false;
//        } else {
//            if (existIndex == 1) {
//                logger.info("索引{}已经存在", index);
//            }
//
//            CreateIndexRequest createIndexRequest = (new CreateIndexRequest(index)).alias(new Alias("alias_" + index));
//            if (StringUtils.isNotEmpty(defaultMappingsJson)) {
//                createIndexRequest.mapping("_default_", defaultMappingsJson, XContentType.JSON);
//            }
//
//            if (StringUtils.isNotEmpty(mappingsJson)) {
//                createIndexRequest.mapping("doc", mappingsJson, XContentType.JSON);
//            }
//
//            CreateIndexResponse indexResponse = null;
//
//            try {
//                indexResponse = client.indices().create(createIndexRequest, new Header[0]);
//                if (indexResponse.isAcknowledged()) {
//                    logger.info("创建{}的索引及映射成功", index);
//                    return true;
//                } else {
//                    logger.error("{}索引的创建失败", index);
//                    return false;
//                }
//            } catch (IOException var9) {
//                var9.printStackTrace();
//                logger.error("创建索引时发生IO异常");
//                return false;
//            }
//        }
//    }
//
//    /**
//     * 删除索引
//     * @param indices 索引名称
//     */
//    public boolean deleteIndex (String... indices) {
//        DeleteIndexRequest request = new DeleteIndexRequest(indices);
//        if (existIndex(indices)==1){
//            try {
//                DeleteIndexResponse response = client.indices().delete(request, new Header[0]);
//                if(response.isAcknowledged()){
//                    logger.info(String.format("索引[%s]删除成功！",indices));
//                }else {
//                    logger.info(String.format("索引[%s]删除失败！",indices));
//                }
//                return response.isAcknowledged();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 插入或更新
//     * @param index 索引名称
//     * @param type 索引类型
//     * @param id   索引编号
//     * @param mapSource  数据
//     */
//    public void indexOrUpdate (String index, String type, String id,Map<String,Object> mapSource) {
//        indexOrUpdate(index,type,id, JSON.toJSONString(mapSource),XContentType.JSON);
//    }
//
//    /**
//     * 插入或更新
//     * @param index 索引名称
//     * @param type 索引类型
//     * @param id   索引编号
//     * @param jsonSource  json数据
//     */
//    public void indexOrUpdate (String index, String type, String id,String jsonSource) {
//        indexOrUpdate(index,type,id,jsonSource,XContentType.JSON);
//    }
//
//    /**
//     * 插入或更新
//     * @param index 索引名称
//     * @param type 索引类型
//     * @param id   索引编号
//     * @param source  数据
//     * @param contentType  内容类型
//     */
//    public void indexOrUpdate (String index, String type, String id,String source,XContentType contentType) {
//        IndexRequest indexRequest = new IndexRequest(index, type, id).source(source, contentType);
//        UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(source, contentType).upsert(indexRequest);
//        getBulkProcessor().add(updateRequest);//批量保存
//    }
//
//    public SearchRequest prepareSearch(SearchSourceBuilder searchSourceBuilder, String... index) {
//        SearchRequest searchRequest = new SearchRequest(index);
//        return searchRequest.source(searchSourceBuilder);
//    }
//
//    public SearchResponse search(SearchRequest request) {
//        try {
//            return getClient().search(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new SearchResponse();
//    }
//
//    /**
//     * 简单查询
//     * @param index 索引名称
//     * @param param 参数
//     */
//    public List<Map> search(String index, Map<String, Object> param){
//        return search(index, param, null);
//    }
//
//    /**
//     * 简单查询
//     * @param index 索引名称
//     * @param param 参数
//     * @param size 返回结果数量 默认值 100
//     */
//    public List<Map> search(String index, Map<String, Object> param, Integer size){
//        size = size!=null&size>0?size:100;
//        BoolQueryBuilder qb = QueryBuilders.boolQuery();
//        if(param!=null){
//            for(String key : param.keySet()){
//                qb.must(QueryBuilders.termsQuery(key, param.get(key)));
//            }
//        }
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchRequest SearchRequest = searchRequest.source(new SearchSourceBuilder().query(qb).size(size).from(0));
//        SearchResponse sr = null;
//        try {
//            sr = getClient().search(SearchRequest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //Long total = sr.getHits().getTotalHits();
//        SearchHit[] rows = sr.getHits().getHits();
//        List<Map> data = new ArrayList<Map>();
//        for(int i=0;i<rows.length;i++){
//            Map map = rows[i].getSourceAsMap();
//            data.add(map);
//        }
//        return data;
//    }
//
//    /**
//     * 简单查询
//     * @param index 索引名称
//     * @param param 参数
//     * @param page 页码 默认值 0
//     * @param size 返回结果数量 默认值 100
//     */
//    public List<Map> search(String index, Map<String, Object> param, Integer page,Integer size){
//        size = size!=null&size>0?size:100;
//        page = page!=null&page<=0?1:page;
//        BoolQueryBuilder qb = QueryBuilders.boolQuery();
//        if(param!=null){
//            for(String key : param.keySet()){
//                qb.must(QueryBuilders.termsQuery(key, param.get(key)));
//            }
//        }
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchRequest SearchRequest = searchRequest.source(new SearchSourceBuilder().query(qb).from((page-1)*size).size(size));
//        SearchResponse sr = null;
//        try {
//            sr = getClient().search(SearchRequest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //Long total = sr.getHits().getTotalHits();
//        SearchHit[] rows = sr.getHits().getHits();
//        List<Map> data = new ArrayList<Map>();
//        for(int i=0;i<rows.length;i++){
//            Map map = rows[i].getSourceAsMap();
//            data.add(map);
//        }
//        return data;
//    }
//
//    /**
//     * 简单查询
//     * @param index 索引名称
//     * @param params 参数
//     * @param page 页码 默认值 0
//     * @param size 返回结果数量 默认值 100
//     */
//    public List<Map> search(String index, List<Map<String, Object>> params, Integer page,Integer size){
//        size = size!=null&size>0?size:100;
//        page = page!=null&page<=0?1:page;
//        BoolQueryBuilder qb = QueryBuilders.boolQuery();
//        if(params!=null){
//            for(Map<String, Object> param :params){
//                if(param!=null){
//                    BoolQueryBuilder sqb = QueryBuilders.boolQuery();
//                    for(String key : param.keySet()){
//                        sqb.must(QueryBuilders.termsQuery(key, param.get(key)));
//                    }
//                    qb.should(sqb);
//                }
//            }
//        }
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchRequest SearchRequest = searchRequest.source(new SearchSourceBuilder().query(qb).from((page-1)*size).size(size));
//        SearchResponse sr = null;
//        try {
//            sr = getClient().search(SearchRequest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //Long total = sr.getHits().getTotalHits();
//        SearchHit[] rows = sr.getHits().getHits();
//        List<Map> data = new ArrayList<Map>();
//        for(int i=0;i<rows.length;i++){
//            Map map = rows[i].getSourceAsMap();
//            data.add(map);
//        }
//        return data;
//    }
//
//    /**
//     * 简单统计文档数量
//     * @param indices
//     * @return
//     */
//    public long docCount(String[] indices) {
//        try {
//            SearchRequest searchRequest = new SearchRequest(indices);
//            SearchRequest SearchRequest = searchRequest.source(new SearchSourceBuilder());
//            SearchResponse sr  = getClient().search(SearchRequest);
//            Long total = sr.getHits().getTotalHits();
//            return total;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0L;
//    }
//
//    /**
//     * 判断索引是否存在
//     * @param indices
//     * @return
//     */
//    private int existIndex (String... indices) {
//        try {
//            return client.indices().exists((GetIndexRequest)(new GetIndexRequest()).indices(indices), new Header[0]) ? 1 : 2;
//        } catch (IOException var3) {
//            var3.printStackTrace();
//            return 3;
//        }
//    }
//}
