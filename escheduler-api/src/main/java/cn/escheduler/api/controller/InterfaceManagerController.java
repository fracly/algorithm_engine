package cn.escheduler.api.controller;

import cn.escheduler.api.service.InterfaceManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_ZOOKEEPER_STATE_ERROR;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源服务接口管理
 * @Date: 2020/4/8 15:18
 */
@RestController
@RequestMapping("appservice")
public class InterfaceManagerController {
    private static final Logger logger = LoggerFactory.getLogger(InterfaceManagerController.class);
    @Autowired
    InterfaceManagerService interfaceManagerService;
    /**
     * 查询所有接口服务的列表
     * @param pageNo 页码
     * @param pageSize 分页步长
     * @param userName 用户名
     * @param appName app名
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/appList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getAppList(@RequestParam(value = "pageNo") int pageNo,
                                          @RequestParam(value = "pageSize") int pageSize,
                                          @RequestParam(value = "userName") String userName,
                                          @RequestParam(value = "appName") String appName){
        try{
            Map<String, Object> result = interfaceManagerService.getAppList(pageNo,pageSize,userName,appName);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 查询用户信息
     * @param userName 用户名
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/subscriberInfo")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> subscriberInfo(@RequestParam(value = "userName") String userName){
        try{
            Map<String, Object> result = interfaceManagerService.subscriberInfo(userName);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 查询接口列表
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/apigroup")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> apigroup(){
        try{
            Map<String, Object> result = interfaceManagerService.apigroup();
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * 查询用户接口服务的流控规则
     * @param resource 资源名称
     * @param limitApp 租户名称
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findRuleByResouceAndSubscriber")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findRuleByResouceAndSubscriber(@RequestParam(value = "resource") String resource,
                                                              @RequestParam(value = "limitApp") String limitApp){
        try{
            Map<String, Object> result = interfaceManagerService.findRuleByResouceAndSubscriber(resource,limitApp);
            return result;
        }catch (Exception e){
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(),e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 编辑流控规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值,
     * grade 阀值类型(1), strategy (0), controlBehavior 流控方式(0)]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> update(@RequestParam(value = "resource") String resource,
                                      @RequestParam(value = "limitApp") String limitApp,
                                      @RequestParam(value = "count") int count,
                                      @RequestParam(value = "grade") String grade,
                                      @RequestParam(value = "strategy") String strategy,
                                      @RequestParam(value = "controlBehavior") String controlBehavior) {
        try {
            Map<String, Object> result = interfaceManagerService.update(resource,limitApp,count,grade,strategy,controlBehavior);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询用户接口服务的权限
     * @Date 8:52 2020/4/9
     * @Param [limitApp 租户名称, appId 默认为1,resource 资源名称,groupName 分组名,version 版本]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/apiTree")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> apiTree(@RequestParam(value = "limitApp") String limitApp,
                                       @RequestParam(value = "grade") int grade,
                                       @RequestParam(value = "resource") String resource,
                                       @RequestParam(value = "groupName") String groupName,
                                       @RequestParam(value = "version") String version) {
        try {
            Map<String, Object> result = interfaceManagerService.apiTree(limitApp,grade,resource,groupName,version);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 更新用户接口服务的权限
     * @Date 8:52 2020/4/9
     * @Param [userName 租户名称, appId 默认为1,msName 资源名称,groupName 分组名,apiAuthorize 接口信息]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping("/updateAPIAuthorize")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> updateAPIAuthorize(@RequestParam(value = "userName") String userName,
                                                  @RequestParam(value = "appId") int appId,
                                                  @RequestParam(value = "msName") String msName,
                                                  @RequestParam(value = "groupName") String groupName,
                                                  @RequestParam(value = "apiAuthorize") ArrayList apiAuthorize) {
        try {
            Map<String, Object> result = interfaceManagerService.updateAPIAuthorize(userName,appId,msName,groupName,apiAuthorize);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }
}
