//package cn.escheduler.api.configuration;
//
//import cn.escheduler.api.utils.EsUtil;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//
///**
// *  ES配置类
// */
//@Lazy
//@Configuration
//public class ESConfig {
//
//    @Value("${es_uris}")
//    public String urls;
//
//    @Value("${es_cluster-name}")
//    public String clusterName;
//
//    @Bean
//    public EsUtil getEsUtil(){
//        return new EsUtil(urls,clusterName);
//    }
//}
