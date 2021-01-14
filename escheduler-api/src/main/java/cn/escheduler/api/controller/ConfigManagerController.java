package cn.escheduler.api.controller;

import cn.escheduler.api.service.ConfigManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_ZOOKEEPER_STATE_ERROR;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源管理配置管理
 * @Date: 2020/4/9 9:43
 */
@RestController
@RequestMapping("configservice")
public class ConfigManagerController {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManagerController.class);

    @Autowired
    private ConfigManagerService configManagerService;
    /**
     * @author ty
     * @Description 查询所有的配置列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, dataId 配置文件名 ,group 组名,appName 归属应用,
     * config_tags, tenant, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/configList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> configList(@RequestParam(value = "pageNo") int pageNo,
                                          @RequestParam(value = "pageSize") int pageSize,
                                          @RequestParam(value = "dataId") String dataId,
                                          @RequestParam(value = "group") String group,
                                          @RequestParam(value = "appName") String appName,
                                          @RequestParam(value = "config_tags") String config_tags,
                                          @RequestParam(value = "tenant") String tenant,
                                          @RequestParam(value = "namespaceId") String namespaceId) {
        try {
            Map<String, Object> result = configManagerService.configList(pageNo,pageSize,dataId,group,appName,config_tags,tenant,namespaceId);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询指定配置的详细信息
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名, tenant 同命名空间 ,namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/configDetail")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> configDetail(@RequestParam(value = "dataId") String dataId,
                                            @RequestParam(value = "group") String group,
                                            @RequestParam(value = "tenant") String tenant,
                                            @RequestParam(value = "namespaceId") String namespaceId) {
        try {
            Map<String, Object> result = configManagerService.configDetail(dataId,group,tenant,namespaceId);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 新建/编辑配置
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名,appName 归属应用,desc 描述,type 类型,content 配置内容,tenant 同命名空间 ,namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/configCreate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> configCreate(@RequestParam(value = "dataId") String dataId,
                                            @RequestParam(value = "group") String group,
                                            @RequestParam(value = "appName") String appName,
                                            @RequestParam(value = "desc") String desc,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "content") String content,
                                            @RequestParam(value = "tenant") String tenant,
                                            @RequestParam(value = "namespaceId") String namespaceId) {
        try {
            Map<String, Object> result = configManagerService.configCreate(dataId,group,appName,desc,type,content,tenant,namespaceId);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 删除配置
     * @Date 8:52 2020/4/9
     * @Param [dataId, group 组名]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/configDel")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> configDel(@RequestParam(value = "dataId") String dataId,
                                            @RequestParam(value = "group") String group) {
        try {
            Map<String, Object> result = configManagerService.configDel(dataId,group);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 历史配置版本
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, dataId 配置文件名 ,group 组名,tenant 命名空间, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/revisionList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> revisionList(@RequestParam(value = "pageNo") int pageNo,
                                            @RequestParam(value = "pageSize") int pageSize,
                                            @RequestParam(value = "dataId") String dataId,
                                            @RequestParam(value = "group") String group,
                                            @RequestParam(value = "tenant") String tenant,
                                            @RequestParam(value = "namespaceId") String namespaceId) {
        try {
            Map<String, Object> result = configManagerService.revisionList(pageNo,pageSize,dataId,group,tenant,namespaceId);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 监听查询
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长,ip ip地址, dataId 配置文件名 ,group 组名,tenant 命名空间, namespaceId 命名空间]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/listenerList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> listenerList(@RequestParam(value = "pageNo") int pageNo,
                                            @RequestParam(value = "pageSize") int pageSize,
                                            @RequestParam(value = "ip") String ip,
                                            @RequestParam(value = "dataId") String dataId,
                                            @RequestParam(value = "group") String group,
                                            @RequestParam(value = "tenant") String tenant,
                                            @RequestParam(value = "namespaceId") String namespaceId) {
        try {
            Map<String, Object> result = configManagerService.listenerList(pageNo,pageSize,ip,dataId,group,tenant,namespaceId);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }
}
