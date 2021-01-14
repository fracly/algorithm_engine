package cn.escheduler.api.utils;

/**
 * @author lkx
 * @version 1.0
 * @date 2020/1/9
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

public class HttpClientUtil {


    @Value("${ms.url}")
    private String url;

    @Value("${ms.username}")
    private String username;

    @Value("${ms.password}")
    private String password;



    /**
     * 发送post请求，json格式数据
     * @param url
     * @param params
     * @return
     */
    public static JSONObject postJsonData(String url, Map<String,Object> params){
        CloseableHttpClient httpclient = HttpClientUtil.createDefault();
        HttpPost httpPost = new HttpPost(url);
       // httpPost.addHeader("Authorization", "your token"); //认证token
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        //httpPost.addHeader("User-Agent", "imgfornote");
        httpPost.setHeader("Accept", "application/json");

        //拼接参数
        JSONObject jsonParams = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonParams.put(key, value);
        }

        //配置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1*60*1000)                //设置连接超时时间，单位毫秒
                // .setConnectionRequestTimeout(1000)         //设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
                .setSocketTimeout(3*60*1000).build();        //请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        httpPost.setConfig(requestConfig);

        CloseableHttpResponse response=null;
        try {
            httpPost.setEntity(new StringEntity(jsonParams.toString(), Charset.forName("UTF-8")));
            response = httpclient.execute(httpPost);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**请求发送成功，并得到响应**/
        JSONObject jsonObject=null;
        if(response != null){
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity httpEntity = response.getEntity();
                String result=null;
                try {
                    result = EntityUtils.toString(httpEntity);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 返回json格式：
                jsonObject = JSONObject.parseObject(result);
            }
        }

        return jsonObject;
    }

    /**
     * 发送post请求
     * @param url
     * @param params
     * @return
     */
    public static JSONObject postData(String url,Map<String,String> params){
        CloseableHttpClient httpclient = HttpClientUtil.createDefault();
        HttpPost httpPost = new HttpPost(url);

        //拼接参数
        List<NameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            NameValuePair pair = new BasicNameValuePair(key, value);
            list.add(pair);
        }

        CloseableHttpResponse response=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
            response = httpclient.execute(httpPost);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**请求发送成功，并得到响应**/
        JSONObject jsonObject=null;
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity httpEntity = response.getEntity();
            String result=null;
            try {
                result = EntityUtils.toString(httpEntity);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 返回json格式：
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static JSONObject sendGet(String url, String param,String token) {
        String result = "";
        BufferedReader in = null;
        JSONObject jsonResult = null;   //get请求返回结果
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            //connection.setRequestProperty("Access-Token", "*/*");
            connection.setRequestProperty("Access-Token",token);

            //connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        jsonResult = JSON.parseObject(result);
        return jsonResult;
    }


    public static JSONObject sendGet1(String url, String param,String token) {
        String result = "";
        BufferedReader in = null;
        JSONObject jsonResult = null;   //get请求返回结果
        try {
            String urlNameString;
            if(param.equals("")){
                urlNameString = url;
            }else {
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            //connection.setRequestProperty("Access-Token", "*/*");
            connection.setRequestProperty("authorization",token);

            //connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        jsonResult = JSON.parseObject(result);
        return jsonResult;
    }

    /**
     * get请求
     * @param url
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return
     */
    public static JSONObject httpGet(String url, String param){
        JSONObject jsonResult = null;   //get请求返回结果
        try {

            CloseableHttpClient httpclient = HttpClientUtil.createDefault();
            //发送get请求
            HttpGet request = new HttpGet(url);
            URI realUrl = new URI(url + "?" + param);
            request.setURI(realUrl);
            HttpResponse response = httpclient.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());

                /**把json字符串转换成json对象**/
                jsonResult = JSONObject.parseObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                System.out.println("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }


    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration.
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }


    public static  String getToken(String url,String username,String password){
        CloseableHttpClient httpclient = HttpClientUtil.createDefault();
        HttpPost httpPost = new HttpPost(url+"/admin/login");
        //拼接参数
        List<NameValuePair> list = new ArrayList<>();
        Map<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            NameValuePair pair = new BasicNameValuePair(key, value);
            list.add(pair);
        }
        CloseableHttpResponse response=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
            response = httpclient.execute(httpPost);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**请求发送成功，并得到响应**/
        JSONObject jsonObject=null;
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity httpEntity = response.getEntity();
            String result=null;
            try {
                result = EntityUtils.toString(httpEntity);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 返回json格式：
            jsonObject = JSON.parseObject(result);
        }
        return jsonObject.getJSONObject("data").getString("token");
    }

}
