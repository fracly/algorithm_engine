package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源服务接口管理
 * @Date: 2020/4/8 15:20
 */
@Service
public class InterfaceManagerService extends BaseService{
    @Value("${ms.url}")
    private String url;

    @Value("${ms.username}")
    private String username;

    @Value("${ms.password}")
    private String password;

    /**
     * 获取调用接口token
     */
    public String getToken(){
        return HttpClientUtil.getToken(url,username,password);
    }

    /**
     * 查询所有接口服务的列表
     * @param pageNo 页码
     * @param pageSize 分页步长
     * @param userName 用户名
     * @param appName app名
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object>getAppList(int pageNo,int pageSize,String userName,String appName){
        try {
            appName = URLEncoder.encode(appName,"utf-8");
            userName = URLEncoder.encode(userName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&userName="+userName+"&appName="+appName;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/subscriber/app/list",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 查询用户信息
     * @param userName 用户名
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object>subscriberInfo(String userName){
        String param = "";
        try {
            userName = URLEncoder.encode(userName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/subscriber/list/"+userName,param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 查询接口列表
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object>apigroup(){
        String param = "";
        JSONObject res = HttpClientUtil.sendGet(url+"/apicollect/list/apigroup",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 查询用户接口服务的流控规则
     * @param resource 资源名称
     * @param limitApp 租户名称
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object>findRuleByResouceAndSubscriber(String resource,String limitApp){
        String param = "resource="+resource+"&limitApp="+limitApp;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/sentinel/findRuleByResouceAndSubscriber",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 编辑流控规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值,
     * grade 阀值类型(1), strategy (0), controlBehavior 流控方式(0)]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> update(String resource,String limitApp,int count,String grade,String strategy,String controlBehavior){
        String param = "resource="+resource+"&limitApp="+limitApp+"&count="+count+"&grade="
                +grade+"&strategy="+strategy+"&controlBehavior="+controlBehavior;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/sentinel/update",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 查询用户接口服务的权限
     * @Date 8:52 2020/4/9
     * @Param [limitApp 租户名称, appId 默认为1,resource 资源名称,groupName 分组名,version 版本]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> apiTree(String limitApp,int appId,String resource,String groupName,String version){
        String param = "";
        JSONObject res = HttpClientUtil.sendGet(url+"/apicollect/apiTree/"+limitApp+appId+resource+groupName+version,param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 更新用户接口服务的权限
     * @Date 8:52 2020/4/9
     * @Param [userName 租户名称, appId 默认为1,msName 资源名称,groupName 分组名,apiAuthorize 接口信息]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> updateAPIAuthorize(String userName, int appId, String msName, String groupName, ArrayList apiAuthorize){
        HashMap<String, Object> param = new HashMap<>();
        param.put("userName",userName);
        param.put("appId",appId);
        param.put("msName",msName);
        param.put("groupName",groupName);
        param.put("apiAuthorize",apiAuthorize);
        JSONObject res = HttpClientUtil.postJsonData(url+"/apicollect/updateAPIAuthorize",param);
        putMsg(res, Status.SUCCESS);
        return res;
    }

}
