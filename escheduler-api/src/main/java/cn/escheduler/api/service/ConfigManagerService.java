package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源管理配置管理
 * @Date: 2020/4/9 9:46
 */
@Service
public class ConfigManagerService extends BaseService{
    @Value("${ms.url}")
    private String url;

    @Value("${ms.username}")
    private String username;

    @Value("${ms.password}")
    private String password;

    /**
     * 获取调用接口token
     */
    private String getToken(){
        return HttpClientUtil.getToken(url,username,password);
    }

    /**
     * @author ty
     * @Description 查询所有的配置列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, dataId 配置文件名 ,group 组名,appName 归属应用,
     * config_tags, tenant, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> configList(int pageNo, int pageSize, String dataId, String group, String appName, String config_tags, String tenant, String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&dataId="+dataId+"&group="
                +group+"&appName="+appName+"&config_tags="+config_tags+"&tenant="+tenant+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/configList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 查询指定配置的详细信息
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名, tenant 同命名空间 ,namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> configDetail(String dataId, String group, String tenant, String namespaceId){
        String param = "dataId="+dataId+"&group="+group+"&tenant="+tenant+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/configDetail",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 新建/编辑配置
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名,appName 归属应用,desc 描述,type 类型,content 配置内容,tenant 同命名空间 ,namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> configCreate(String dataId,String group,String appName,String desc,String type,String content, String tenant, String namespaceId){
        String param = "dataId="+dataId+"&group="+group+"&appName="+appName+"&desc="+desc+"&type="+type+"&content="+content
                +"&tenant="+tenant+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/configCreate",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 删除配置
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> configDel(String dataId,String group){
        String param = "dataId="+dataId+"&group="+group;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/configDel",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 历史配置版本
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, dataId 配置文件名 ,group 组名,tenant 命名空间, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> revisionList(int pageNo,int pageSize,String dataId,String group,String tenant,String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&dataId="+dataId+"&group="+group
                +"&tenant="+tenant+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/revisionList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * @author ty
     * @Description 监听查询
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长,ip ip地址, dataId 配置文件名 ,group 组名,tenant 命名空间, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> listenerList(int pageNo,int pageSize,String ip ,String dataId,String group,String tenant,String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&ip="+ip+"&dataId="+dataId+"&group="+group
                +"&tenant="+tenant+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/listenerList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }
}
