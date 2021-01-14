package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;

import java.util.Map;

/**
 * @author lkx
 * @version 1.0
 * @date 2020/1/9
 */
@Service
public class SourceManagerService extends BaseService {

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
     * 查询指定的namespace的列表信息
     * @param namespaceId 命名空间ID
     */
    public Map<String, Object> getNamespace(String namespaceId){
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/namespace","namespaceId="+namespaceId,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }



    /**
     * 查询所有服务的列表
     * @param pageNo 页码
     * @param pageSize 分页步长
     * @param keyword 查询关键字(服务名称)
     * @param namespaceId 命名空间
     */
    public Map<String,Object> getServiceList(int pageNo,int pageSize,String keyword,String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&keyword="+keyword+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/serviceList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 查询集群列表
     * @param pageNo 页码
     * @param pageSize 分页步长
     * @param keyword 查询关键字(节点IP)
     * @param namespaceId 命名空间
     */
    public Map<String,Object> getClusterList(int pageNo,int pageSize,String keyword,String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&keyword="+keyword+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/clusterList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }


    /**
     * 获取指定服务的详细信息
     * @param serviceName 服务名称
     * @param groupName 组名称
     * @param namespaceId 命名空间
     */
    public Map<String,Object> getServiceDetail(String serviceName,String groupName,String namespaceId){
        String param = "serviceName="+serviceName+"&groupName="+groupName+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/serviceDetail",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 新建服务
     * @param serviceName 服务名称
     * @param groupName 组名称
     * @param protectThreshold 保护阀值
     * @param metadata 元数据
     * @param namespaceId 命名空间
     */
    public Map<String,Object> serviceCreate(String serviceName,String groupName,int protectThreshold,String metadata,String namespaceId){
        String param = "serviceName="+serviceName+"&groupName="+groupName+"&protectThreshold="
                +protectThreshold+"&metadata="+metadata+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/serviceCreate",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 获取指定服务的集群列表
     * @Param [pageNo 页码, pageSize 分页步长,serviceName 服务名称,
     * clusterName 集群名称 默认为DEFAULT,groupName 组名称,namespaceId 命名空间]
     */
    public Map<String,Object> instanceList(int pageNo,int pageSize,String serviceName,String clusterName,String groupName,String namespaceId){
        String param = "pageNo="+pageNo+"&pageSize="+pageSize+"&serviceName="+serviceName
                +"&clusterName="+clusterName+"&groupName="+groupName+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/instanceList",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 服务编辑
     * @Param [serviceName 服务名称,groupName 组名称, protectThreshold 保护阀值,metadata 元数据, namespaceId 命名空间]
     */
    public Map<String,Object> serviceEdit(String serviceName,String groupName,int protectThreshold,String metadata,String namespaceId){
        String param = "serviceName="+serviceName+"&groupName="+groupName+"&protectThreshold="+protectThreshold
                +"&metadata="+metadata+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/serviceEdit",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

    /**
     * 集群配置
     * @Param [serviceName 服务名称,clusterName 组名称,metadata 保护阀值,checkPort 检查端口
     * useInstancePort4Check true/false，type 协议类型tcp/http，namespaceId 命名空间]
     */
    public Map<String,Object> clusterEdit(String serviceName,String clusterName,String metadata,int checkPort,String useInstancePort4Check,String type,String namespaceId){
        String param = "serviceName="+serviceName+"&clusterName="+clusterName+"&metadata="+metadata+"&checkPort="+checkPort
                +"&useInstancePort4Check="+useInstancePort4Check+"&type="+type+"&namespaceId="+namespaceId;
        JSONObject res = HttpClientUtil.sendGet(url+"/admin/nacos/clusterEdit",param,getToken());
        putMsg(res, Status.SUCCESS);
        return res;
    }

}
