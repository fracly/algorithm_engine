package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author TY
 * @version : V1.0.0
 * @description 预定义自定义接口服务
 * @Date: 2020-05-09 16:05
 */
@Service
public class InterfaceService extends BaseService {

    private String url = "http://10.20.10.162:1985";

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

    public Map<String, Object> tableList() {
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/tableList", "", getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> getColumnListByTable(String table) {
        String param = "table=" + table;
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/columnListByTable", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> getTableApp_traffic_sbbh_cllx_day() {
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/getTableApp_traffic_sbbh_cllx_day", "", getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> getTable(String userName, String table, String selectParams, String whereParams, String comments) {
        try {
            userName = URLEncoder.encode(userName, "utf-8");
            selectParams = URLEncoder.encode(selectParams, "utf-8");
            whereParams = URLEncoder.encode(whereParams, "utf-8");
            comments = URLEncoder.encode(comments, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "userName=" + userName + "&table=" + table + "&selectParams=" + selectParams + "&whereParams=" + whereParams + "&comments=" + comments;
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/getTable", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> saveUserInterface(String userName, String table, String selectParams, String whereParams, String comments) {
        try {
            userName = URLEncoder.encode(userName, "utf-8");
            selectParams = URLEncoder.encode(selectParams, "utf-8");
            whereParams = URLEncoder.encode(whereParams, "utf-8");
            comments = URLEncoder.encode(comments, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "userName=" + userName + "&table=" + table + "&selectParams=" + selectParams + "&whereParams=" + whereParams + "&comments=" + comments;
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/saveUserInterface", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> getUserTable(String userName) {
        String param = "userName=" + userName;
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/getUserTable", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    public Map<String, Object> deleteUserTable(String userName, String comments) {
        try {
            userName = URLEncoder.encode(userName, "utf-8");
            comments = URLEncoder.encode(comments, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "userName=" + userName + "&comments=" + comments;
        JSONObject res = HttpClientUtil.sendGet1(url + "/api/bd-ms-dwa/bigdata/dwa/datasources/deleteUserTable", param, getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }
}
