package cn.escheduler.api.controller;

import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.service.SourceManagerService;
import cn.escheduler.api.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cn.escheduler.api.utils.Constants;

import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_ZOOKEEPER_STATE_ERROR;

/**
 * @author lkx
 * @version 1.0
 * @date 2020/1/9
 */

@RestController
@RequestMapping("apiserver")
public class SourceManagerController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SourceManagerController.class);


    @Autowired
    private SourceManagerService sourceManagerService;

    /**
     * 查询指定的namespace的列表信息
     * @param namespaceId
     * @return
     */
    @GetMapping(value = "/namespace")
    @ResponseStatus(HttpStatus.OK)
    public Result getNamespace(@RequestParam(value = "namespaceId") String namespaceId) {
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.getNamespace(namespaceId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return error(QUERY_ZOOKEEPER_STATE_ERROR.getCode(),
                    QUERY_ZOOKEEPER_STATE_ERROR.getMsg());
        }
    }

    /**
     * 查询指定的namespace的详情信息
     * @param namespaceId
     * @return
     */
    @GetMapping(value = "/namespaceDetail")
    @ResponseStatus(HttpStatus.OK)
    public Result getNamespaceDetail(@RequestParam(value = "namespaceId") String namespaceId) {
        try{
            //Map<String, Object> result = sourceManagerService.getNamespaceDetail(namespaceId);
            //return returnDataList(result);
            return null;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return error(QUERY_ZOOKEEPER_STATE_ERROR.getCode(),
                    QUERY_ZOOKEEPER_STATE_ERROR.getMsg());
        }
    }

    /**
     * 查询所有服务的列表
     * @param pageNo 页码
     * @param pageSize 分页步长
     * @param keyword 查询关键字(服务名称)
     * @param namespaceId 命名空间
     */
    @GetMapping(value = "/serviceList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getServiceList(@RequestParam(value = "pageNo") int pageNo,
                                @RequestParam(value = "pageSize") int pageSize,
                                @RequestParam(value = "keyword") String keyword,
                                @RequestParam(value = "namespace") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.getServiceList(pageNo,pageSize,keyword,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 获取指定服务的详细信息
     * @param serviceName 服务名称
     * @param groupName 组名称
     * @param namespaceId 命名空间
     */
    @GetMapping(value = "/serviceDetail")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getServiceDetail(@RequestParam(value = "serviceName") String serviceName,
                                              @RequestParam(value = "groupName") String groupName,
                                              @RequestParam(value = "namespaceId") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.getServiceDetail(serviceName,groupName,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 查询集群列表
     * @param pageNo 页码
     * @param pageSize 分页大小
     */
    @GetMapping(value = "/clusterList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getClusterList(@RequestParam(value = "pageNo") int pageNo,
                                              @RequestParam(value = "pageSize") int pageSize,
                                              @RequestParam(value = "keyword") String keyword,
                                              @RequestParam(value = "namespaceId") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.getClusterList(pageNo,pageSize,keyword,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 新建服务
     * @param serviceName 服务名称
     * @param groupName 组名称
     * @param protectThreshold 保护阀值
     * @param metadata 元数据
     * @param namespaceId 命名空间
     */
    @GetMapping(value = "/serviceCreate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> serviceCreate(@RequestParam(value = "serviceName") String serviceName,
                                             @RequestParam(value = "groupName") String groupName,
                                             @RequestParam(value = "protectThreshold") int protectThreshold,
                                             @RequestParam(value = "metadata") String metadata,
                                             @RequestParam(value = "namespaceId") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.serviceCreate(serviceName,groupName,protectThreshold,metadata,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 获取指定服务的集群列表
     * @Param [pageNo 页码, pageSize 分页步长,serviceName 服务名称,
     * clusterName 集群名称 默认为DEFAULT,groupName 组名称,namespaceId 命名空间]
     */
    @GetMapping(value = "/instanceList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> instanceList(@RequestParam(value = "pageNo") int pageNo,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "serviceName") String serviceName,
                                             @RequestParam(value = "clusterName") String clusterName,
                                             @RequestParam(value = "groupName") String groupName,
                                             @RequestParam(value = "namespace") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.instanceList(pageNo,pageSize,serviceName,clusterName,groupName,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 服务编辑
     * @Param [serviceName 服务名称,groupName 组名称, protectThreshold 保护阀值,metadata 元数据, namespaceId 命名空间]
     */
    @GetMapping(value = "/serviceEdit")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> serviceEdit(@RequestParam(value = "serviceName") String serviceName,
                                           @RequestParam(value = "groupName") String groupName,
                                           @RequestParam(value = "protectThreshold") int protectThreshold,
                                           @RequestParam(value = "metadata") String metadata,
                                           @RequestParam(value = "namespaceId") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.serviceEdit(serviceName,groupName,protectThreshold,metadata,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 集群配置
     * @Param [serviceName 服务名称,clusterName 组名称,metadata 保护阀值,checkPort 检查端口
     * useInstancePort4Check true/false，type 协议类型tcp/http，namespaceId 命名空间]
     */
    @GetMapping(value = "/clusterEdit")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> clusterEdit(@RequestParam(value = "serviceName") String serviceName,
                                           @RequestParam(value = "clusterName") String clusterName,
                                           @RequestParam(value = "metadata") String metadata,
                                           @RequestParam(value = "checkPort") int checkPort,
                                           @RequestParam(value = "useInstancePort4Check") String useInstancePort4Check,
                                           @RequestParam(value = "type") String type,
                                           @RequestParam(value = "namespaceId") String namespaceId){
        try{
            namespaceId = ConfigurationManager.getProperty(Constants.NAMESPACEID);
            Map<String, Object> result = sourceManagerService.clusterEdit(serviceName,clusterName,metadata,checkPort,useInstancePort4Check,type,namespaceId);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

}
