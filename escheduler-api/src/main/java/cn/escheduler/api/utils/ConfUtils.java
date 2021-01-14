//package cn.escheduler.api.utils;
//
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.Properties;
//
//public class ConfUtils {
//
//    public static String get(String str)   {
//        Properties properties;
//        try {
//            properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
//
//            return (new String(properties.getProperty(str).getBytes("iso-8859-1"), "gbk"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//}
