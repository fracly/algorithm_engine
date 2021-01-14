package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源管理流量管理
 * @Date: 2020/4/9 8:20
 */
@Service
public class FlowManagerService extends BaseService {
    @Value("${ms.url}")
    private String url;

    @Value("${ms.username}")
    private String username;

    @Value("${ms.password}")
    private String password;

    /**
     * 获取调用接口token
     */
    private String getToken() {
        return HttpClientUtil.getToken(url, username, password);
    }


    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询所有的网关规则列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, limitApp 租户名称, resource 资源名称]
     */
    public Map<String, Object> findGatewayFlowRules(int pageNo, int pageSize, String limitApp, String resource) {
        String param = "pageNo=" + pageNo + "&pageSize=" + pageSize + "&limitApp=" + limitApp + "&resource=" + resource;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/sentinel/findGatewayFlowRules", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询指定的网关访问量
     * @Date 8:52 2020/4/9
     * @Param [url 资源名称, subscriberName 租户名称]
     */
    public Map<String, Object> findAccessInfo(String url, String subscriberName) {
        String param = "url=" + url + "&subscriberName" + subscriberName;
        JSONObject res = HttpClientUtil.sendGet(this.url + "/admin/sentinel/findAccessInfo", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询所有资源名称
     * @Date 8:52 2020/4/9
     */
    public Map<String, Object> findAuthMicroServiceName() {
        String param = "";
        JSONObject res = HttpClientUtil.sendGet(url + "/apicollect/findAuthMicroServiceName", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @param resource 资源名称
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 根据资源名称查询用户名
     * @Date 8:52 2020/4/9
     */
    public Map<String, Object> findSubscriberNameByResource(String resource) {
        String param = "resource=" + resource;
        JSONObject res = HttpClientUtil.sendGet(url + "/apicollect/findSubscriberNameByResource", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 新建规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值,
     * grade 阀值类型(1), strategy (0), controlBehavior 流控方式(0)]
     */
    public Map<String, Object> create(String resource, String limitApp, int count, String grade, String strategy, String controlBehavior) {
        String param = "resource=" + resource + "&limitApp=" + limitApp + "&count=" + count + "&grade="
                + grade + "&strategy=" + strategy + "&controlBehavior=" + controlBehavior;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/sentinel/create", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 编辑规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值,
     * grade 阀值类型(1), strategy (0), controlBehavior 流控方式(0)]
     */
    public Map<String, Object> update(String resource, String limitApp, int count, String grade, String strategy, String controlBehavior) {
        String param = "resource=" + resource + "&limitApp=" + limitApp + "&count=" + count + "&grade="
                + grade + "&strategy=" + strategy + "&controlBehavior=" + controlBehavior;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/sentinel/update", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 删除规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值]
     */
    public Map<String, Object> del(String resource, String limitApp, int count) {
        String param = "resource=" + resource + "&limitApp=" + limitApp + "&count=" + count;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/sentinel/del", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询所有的流量规则列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,
     * microServiceName 微服务名称, interfaceType 接口类型, apiPath API路径]
     */
    public Map<String, Object> listInvokeRule(int pageNo, int pageSize, String subscriberName, String microServiceName, String interfaceType, String apiPath) {
        String param = "pageNo=" + pageNo + "&pageSize=" + pageSize + "&subscriberName=" + subscriberName + "&microServiceName="
                + microServiceName + "&interfaceType=" + interfaceType + "&apiPath=" + apiPath;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/listInvokeRule", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @param microServiceName 服务名称
     * @param subscriberName   租户名称
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 根据资源名称和租户名查找接口类型
     * @Date 8:52 2020/4/9
     */
    public Map<String, Object> findInterfaceTypeListByMSNameAndSubscriber(String microServiceName, String subscriberName) {
        String param = "microServiceName=" + microServiceName + "&subscriberName=" + subscriberName;
        JSONObject res = HttpClientUtil.sendGet(url + "/apicollect/findInterfaceTypeListByMSNameAndSubscriber", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @param microServiceName 服务名称
     * @param subscriberName   租户名称
     * @param interfaceType    接口类型
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 根据资源名称、租户名、接口类型查找API路径
     * @Date 8:52 2020/4/9
     */
    public Map<String, Object> findAPIPathByMSNameSubscriberNameAndInterfaceType(String microServiceName, String subscriberName, String interfaceType) {
        String param = "microServiceName=" + microServiceName + "&subscriberName=" + subscriberName + "&interfaceType=" + interfaceType;
        JSONObject res = HttpClientUtil.sendGet(url + "/apicollect/findAPIPathByMSNameSubscriberNameAndInterfaceType", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 创建流量规则
     * @Date 8:52 2020/4/9
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     * interfaceType 接口类型, apiPath API路径，invokeTime 调用限数]
     */
    public Map<String, Object> isExistInvokeRule(String subscriberName, String microServiceName, String interfaceType, String apiPath, int invokeTime) {
        String param = "subscriberName=" + subscriberName + "&microServiceName=" + microServiceName + "&interfaceType=" + interfaceType + "&apiPath="
                + apiPath + "&invokeTime=" + invokeTime;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/createInvokeRule", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 编辑流量规则
     * @Date 8:52 2020/4/9
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     * apiPath API路径，invokeTime 调用限数]
     */
    public Map<String, Object> editInvokeRule(String subscriberName, String microServiceName, String apiPath, int invokeTime) {
        try {
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "subscriberName=" + subscriberName + "&microServiceName=" + microServiceName + "&apiPath="
                + apiPath + "&invokeTime=" + invokeTime;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/editInvokeRule", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 删除流量规则
     * @Date 8:52 2020/4/9
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,apiPath API路径]
     */
    public Map<String, Object> deleteInvokeRule(String subscriberName, String microServiceName, String apiPath) {
        try {
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "subscriberName=" + subscriberName + "&microServiceName=" + microServiceName + "&apiPath=" + apiPath;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/deleteInvokeRule", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询流量规则是否存在
     * @Date 8:52 2020/4/9
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     * interfaceType 接口类型, apiPath API路径，invokeTime 调用限数]
     */
    public Map<String, Object> createInvokeRule(String subscriberName, String microServiceName, String interfaceType, String apiPath, int invokeTime) {
        try {
            interfaceType = URLEncoder.encode(interfaceType, "utf-8");
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "subscriberName=" + subscriberName + "&microServiceName=" + microServiceName + "&interfaceType=" + interfaceType + "&apiPath="
                + apiPath + "&invokeTime=" + invokeTime;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/isExistInvokeRule", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询所有的调用次数列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     */
    public Map<String, Object> listSubscriberInvokeStatistic(int pageNo, int pageSize, String subscriberName, String microServiceName, String interfaceType, String apiPath, String startDate, String endDate) {
        try {
            interfaceType = URLEncoder.encode(interfaceType, "utf-8");
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "pageNo=" + pageNo + "&pageSize=" + pageSize + "&subscriberName=" + subscriberName + "&microServiceName="
                + microServiceName + "&interfaceType=" + interfaceType + "&apiPath=" + apiPath + "&startDate=" + startDate + "&endDate=" + endDate;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/listSubscriberInvokeStatistic", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 导出调用次数
     * @Date 8:52 2020/4/9
     * @Param [subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     */
    public void invokeStatisticExport(HttpServletResponse response, String subscriberName, String microServiceName, String interfaceType, String apiPath, String startDate, String endDate) {
        try {
            interfaceType = URLEncoder.encode(interfaceType, "utf-8");
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "subscriberName=" + subscriberName + "&microServiceName=" + microServiceName + "&interfaceType="
                + interfaceType + "&apiPath=" + apiPath + "&startDate=" + startDate + "&endDate=" + endDate;
        try {
            String urlNameString = url + "/export/invokeStatisticExport" + "?" + param;
            URL url = new URL(urlNameString);
            URLConnection con = url.openConnection();
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("Access-Token", getToken());
            con.connect();
            response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''" + URLEncoder.encode("调用统计信息.xls", "UTF-8"));
            InputStream inputStream = con.getInputStream();
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while (len != -1) {
                response.getOutputStream().write(buffer, 0, len);
                len = inputStream.read(buffer);
            }
            inputStream.close();
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
    }


    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author ty
     * @Description 查询所有的调用日志列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     */
    public Map<String, Object> listStatisticLog(int pageNo, int pageSize, String subscriberName, String microServiceName, String interfaceType, String apiPath, String startDate, String endDate) {
        try {
            interfaceType = URLEncoder.encode(interfaceType, "utf-8");
            apiPath = URLEncoder.encode(apiPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "pageNo=" + pageNo + "&pageSize=" + pageSize + "&subscriberName=" + subscriberName + "&microServiceName="
                + microServiceName + "&interfaceType=" + interfaceType + "&apiPath=" + apiPath + "&startDate=" + startDate + "&endDate=" + endDate;
        JSONObject res = HttpClientUtil.sendGet(url + "/admin/invoke/listStatisticLog", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }
}
