package cn.escheduler.api.service;

import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.ClouderaManagerServiceMetrics;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Data;
import cn.escheduler.api.utils.Metric;
import com.cloudera.api.model.ApiTimeSeriesData;
import com.cloudera.api.model.ApiTimeSeriesResponseList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.escheduler.api.enums.Status.GET_CLOUDER_INFO_ERROR;

@Service
public class ClouderaService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ModelDesignService.class);

    /**
     *   获取 getHosts
     */
    public Map<String, Object> getHosts() {
        Map<String, Object> result = new HashMap<>(5);
        try {

            String query= ConfigurationManager.getProperty("memory_used");
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String now_time = dateFormat.format(date) + ":00";
            String before_time=getType("30m");
            List hosts = ClouderaManagerServiceMetrics.getHosts(query, before_time, now_time);

            result.put(Constants.DATA_LIST, hosts);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_CLOUDER_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }



    /**
     *   获取 getIO
     */
    public Map<String, Object> getHdfsIO(String param,String type) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            String query= ConfigurationManager.getProperty(param);
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String now_time = dateFormat.format(date) + ":00";
            String before_time=getType(type);

            ApiTimeSeriesResponseList response = ClouderaManagerServiceMetrics.getResponse(query, before_time, now_time);
            List<ApiTimeSeriesData> out = response.get(0).getTimeSeries().get(0).getData();
            List<ApiTimeSeriesData>  in = response.get(0).getTimeSeries().get(1).getData();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            List listOut =new ArrayList();
            List listIn =new ArrayList();
            Map map=new HashMap();

            for(ApiTimeSeriesData apiTimeSeriesData:out){
                Map m=new HashMap();
                double v = apiTimeSeriesData.getValue();
                BigDecimal bigDecimal = new BigDecimal(v).setScale(2,BigDecimal.ROUND_HALF_UP);
                String time = simpleDateFormat.format(apiTimeSeriesData.getTimestamp());
                m.put("value",bigDecimal);
                m.put("name",time);
                listOut.add(m);

            }

            for(ApiTimeSeriesData apiTimeSeriesData:in){
                Map m=new HashMap();
                double v = apiTimeSeriesData.getValue();
                BigDecimal bigDecimal = new BigDecimal(v).setScale(2,BigDecimal.ROUND_HALF_UP);
                String time = simpleDateFormat.format(apiTimeSeriesData.getTimestamp());
                m.put("value",bigDecimal);
                m.put("name",time);
                listIn.add(m);
            }
            map.put("i",listIn);
            map.put("o",listOut);

            result.put(Constants.DATA_LIST, map);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_CLOUDER_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    public Map<String, Object> getMem(String type) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            String query= ConfigurationManager.getProperty("memory");
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String now_time = dateFormat.format(date) + ":00";
            String before_time=getType(type);

            ApiTimeSeriesResponseList response = ClouderaManagerServiceMetrics.getResponse(query, before_time, now_time);
            List<ApiTimeSeriesData> dataYY = response.get(0).getTimeSeries().get(0).getData();
            List<ApiTimeSeriesData> dataKY = response.get(0).getTimeSeries().get(1).getData();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map map=new HashMap();
            List listYY =new ArrayList();
            List listKY =new ArrayList();
            for(ApiTimeSeriesData apiTimeSeriesData:dataYY){
                Map m=new HashMap();
                double v = Math.round(apiTimeSeriesData.getValue() / 1024*100)/100;
                Date timestamp = apiTimeSeriesData.getTimestamp();
                String time = simpleDateFormat.format(timestamp);
                m.put("value",v);
                m.put("name",time);
                listYY.add(m);
            }
            for(ApiTimeSeriesData apiTimeSeriesData:dataKY){
                Map m=new HashMap();
                double v = Math.round(apiTimeSeriesData.getValue() / 1024*100)/100;
                Date timestamp = apiTimeSeriesData.getTimestamp();
                String time = simpleDateFormat.format(timestamp);
                m.put("value",v);
                m.put("name",time);
                listKY.add(m);
            }
            map.put("YY",listYY);
            map.put("KY",listKY);

            result.put(Constants.DATA_LIST, map);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_CLOUDER_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    public Map<String, Object> getVc(String type) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            String query= ConfigurationManager.getProperty("vcores");
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String now_time = dateFormat.format(date) + ":00";
            String before_time=getType(type);

            ApiTimeSeriesResponseList response = ClouderaManagerServiceMetrics.getResponse(query, before_time, now_time);
            List<ApiTimeSeriesData> dataYY = response.get(0).getTimeSeries().get(0).getData();
            List<ApiTimeSeriesData> dataKY = response.get(0).getTimeSeries().get(1).getData();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map map=new HashMap();
            List listYY =new ArrayList();
            List listKY =new ArrayList();
            for(ApiTimeSeriesData apiTimeSeriesData:dataYY){
                Map m=new HashMap();
                double v = apiTimeSeriesData.getValue();
                Date timestamp = apiTimeSeriesData.getTimestamp();
                String time = simpleDateFormat.format(timestamp);
                m.put("value",v);
                m.put("name",time);
                listYY.add(m);
            }
            for(ApiTimeSeriesData apiTimeSeriesData:dataKY){
                Map m=new HashMap();
                double v = apiTimeSeriesData.getValue();
                Date timestamp = apiTimeSeriesData.getTimestamp();
                String time = simpleDateFormat.format(timestamp);
                m.put("value",v);
                m.put("name",time);
                listKY.add(m);
            }

            map.put("YY",listYY);
            map.put("KY",listKY);

            result.put(Constants.DATA_LIST, map);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_CLOUDER_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    public Map<String, Object> getCpu(String type) {

        Map<String, Object> result = new HashMap<>(5);
        try {
            List maxList=new ArrayList();
            List minList=new ArrayList();
            Map map=new HashMap();

            String before_time=getType(type);
            String query= ConfigurationManager.getProperty("cluster_cpu");
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String now_time = dateFormat.format(date) + ":00";



            ApiTimeSeriesResponseList response = ClouderaManagerServiceMetrics.getResponse(query, before_time, now_time);
            List<ApiTimeSeriesData> data = response.get(0).getTimeSeries().get(0).getData();

            for(ApiTimeSeriesData apiTimeSeriesData:data){

                Map maxMap=new HashMap();
                Map minMap=new HashMap();


                double max = apiTimeSeriesData.getAggregateStatistics().getMax();
                double min = apiTimeSeriesData.getAggregateStatistics().getMin();
                Date maxTime = apiTimeSeriesData.getAggregateStatistics().getMaxTime();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(maxTime);

                maxMap.put("name",time);
                maxMap.put("value",max);

                minMap.put("name",time);
                minMap.put("value",min);

                maxList.add(maxMap);
                minList.add(minMap);
            }
            map.put("max",maxList);
            map.put("min",minList);

            result.put(Constants.DATA_LIST, map);
            putMsg(result, Status.SUCCESS);
        } catch (Exception e) {
            logger.error(GET_CLOUDER_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;

    }




    public static String getType(String type){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Calendar beforeTime = Calendar.getInstance();

        switch (type){
            case "30m":
                beforeTime.add(Calendar.MINUTE, -30);
                System.out.println(beforeTime);
                break;
            case "6h":
                beforeTime.add(Calendar.HOUR_OF_DAY, -6);
                System.out.println(beforeTime);
                break;
            case "1d":
                beforeTime.add(Calendar.DAY_OF_MONTH, -1);
                System.out.println(beforeTime);
                break;
            case "7d":
                beforeTime.add(Calendar.DAY_OF_MONTH, -7);
                System.out.println(beforeTime);
                break;
            case "1mo":
                beforeTime.add(Calendar.MONTH, -1);
                System.out.println(beforeTime);
                break;
            case "3mo":
                beforeTime.add(Calendar.MONTH, -3);
                System.out.println(beforeTime);
                break;

        }

        Date beforeD = beforeTime.getTime();
        String before_time = dateFormat.format(beforeD) + ":00";

        return before_time;
    }



}
