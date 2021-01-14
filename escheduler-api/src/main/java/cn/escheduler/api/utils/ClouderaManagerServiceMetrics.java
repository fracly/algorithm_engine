package cn.escheduler.api.utils;
import cn.escheduler.api.configuration.ConfigurationManager;
import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.*;
import com.cloudera.api.v11.TimeSeriesResourceV11;
import com.cloudera.api.v30.RootResourceV30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClouderaManagerServiceMetrics {

    private static final Logger logger = LoggerFactory.getLogger(ClouderaManagerServiceMetrics.class);

    static RootResourceV30 apiRoot;

    static {
        apiRoot = new ClouderaManagerClientBuilder().withHost(ConfigurationManager.getProperty(Constants.CLUSTER_IP))
                .withPort(7180)
                .withUsernamePassword(ConfigurationManager.getProperty(Constants.CLUSTER_USER_NAME),ConfigurationManager.getProperty(Constants.CLUSTER_USER_PASSWORD)).build().getRootV30();

    }

    public static ApiTimeSeriesResponseList getResponse(String query, String startTime, String endTime) {
        TimeSeriesResourceV11 timeSeriesResourceV11 = apiRoot.getTimeSeriesResource();
//        String[] params = new String[]{query, startTime, endTime};
        ApiTimeSeriesResponseList response = timeSeriesResourceV11.queryTimeSeries(query, startTime, endTime);
        return response;
    }


    private static List<Metric> formatApiTimeSeriesResponse(List<ApiTimeSeriesResponse> apiTimeSeriesResponseList) {
        List<Metric> metrics = new ArrayList<>();
        for (ApiTimeSeriesResponse apiTimeSeriesResponse : apiTimeSeriesResponseList) {
            List<Data> dataList = new ArrayList<>();
            List<ApiTimeSeries> apiTimeSeriesList = apiTimeSeriesResponse.getTimeSeries();
            for (ApiTimeSeries apiTimeSeries : apiTimeSeriesList) {
                Metric metric = new Metric();
                metric.setMetricName(apiTimeSeries.getMetadata().getMetricName());
                metric.setEntityName(apiTimeSeries.getMetadata().getEntityName());
                metric.setStartTime(apiTimeSeries.getMetadata().getStartTime().toString());
                metric.setEndTime(apiTimeSeries.getMetadata().getEndTime().toString());
                for (ApiTimeSeriesData apiTimeSeriesData : apiTimeSeries.getData()) {
                    Data data = new Data();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    data.setTimestamp(simpleDateFormat.format(apiTimeSeriesData.getTimestamp()));
                    data.setType(apiTimeSeriesData.getType());
                    data.setValue(apiTimeSeriesData.getValue());
                    dataList.add(data);
                }
                metric.setData(dataList);
                metrics.add(metric);
            }
        }
        return metrics;
    }

    public static List<Metric> getServiceMetrics(String query, String startTime, String endTime) {
        TimeSeriesResourceV11 timeSeriesResourceV11 = apiRoot.getTimeSeriesResource();
//        String[] params = new String[]{query, startTime, endTime};
        ApiTimeSeriesResponseList response = timeSeriesResourceV11.queryTimeSeries(query, startTime, endTime);
        List<ApiTimeSeriesResponse> apiTimeSeriesResponseList = response.getResponses();
        List<Metric> metrics = formatApiTimeSeriesResponse(apiTimeSeriesResponseList);
        return metrics;
    }

    public static List getDetailData(String query,String before_time, String now_time){
        List<Metric> serviceMetrics = ClouderaManagerServiceMetrics.getServiceMetrics(query, before_time, now_time);
        List<Data> data = serviceMetrics.get(0).getData();
        List list=new ArrayList();

        for(int i=1;i<=data.size()/2;i++){
            Map map=new HashMap();
            map.put("i",(double) Math.round(data.get(i-1).getValue()/1024*100)/100);
            map.put("o",(double) Math.round(data.get(data.size()/2+i-1).getValue()/1024*100)/100);
            map.put("time",data.get(i-1).getTimestamp());
            list.add(map);
        }
        return list;
    }


    public static List getHosts(String query, String before_time,String now_time){
        TimeSeriesResourceV11 timeSeriesResourceV11 = apiRoot.getTimeSeriesResource();
        ApiHostList apiHosts = apiRoot.getHostsResource().readHosts(DataView.FULL);
//        String[] params = new String[]{query, startTime, endTime};
        ApiTimeSeriesResponseList response = timeSeriesResourceV11.queryTimeSeries(query, before_time, now_time);
        List<ApiTimeSeries> timeSeries = response.get(0).getTimeSeries();
        Map map=new HashMap();

        List<Map> hosts=new ArrayList<>();
        for(ApiHost apiHost: apiHosts){
            Map host=new HashMap();
            String ipAddress = apiHost.getIpAddress();
            String hostname = apiHost.getHostname();
            String healthSummary = apiHost.getHealthSummary().toString();
            long numCores = apiHost.getNumCores();
            long totalPhysMemBytes = apiHost.getTotalPhysMemBytes();
            double totalPhysMemGB = new BigDecimal(totalPhysMemBytes / (double) (1024 * 1024 * 1024)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();


            List<ApiRoleRef> roleRefs = apiHost.getRoleRefs();
            host.put("ipAddress",ipAddress);
            host.put("hostname",hostname);
            host.put("healthSummary",healthSummary);
            host.put("numCores",numCores);
            host.put("totalPhysMem",totalPhysMemGB);
            host.put("roleRefs",roleRefs);

            double usedPhysMemGB = 0;

            for(ApiTimeSeries apiTimeSeries:timeSeries){
                String entityName = apiTimeSeries.getMetadata().getEntityName();
                if(entityName.equals(hostname)){
                    if(apiTimeSeries.getData().size()>0){
                    double value = apiTimeSeries.getData().get(apiTimeSeries.getData().size() - 1).getValue();
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    String scoreStr = nf.format(value);
                    double v = new BigDecimal(Long.parseLong(scoreStr)/ (double) (1024 * 1024 * 1024)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    usedPhysMemGB=v;
                    }
                }
            }
            host.put("usedPhysMemGB",usedPhysMemGB);

            hosts.add(host);

        }
        return hosts;
    }
}

