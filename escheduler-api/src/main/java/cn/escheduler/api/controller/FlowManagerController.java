package cn.escheduler.api.controller;

import cn.escheduler.api.service.FlowManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_ZOOKEEPER_STATE_ERROR;

/**
 * @author TY
 * @version : V1.0.0
 * @description AI平台资源管理流量管理
 * @Date: 2020/4/9 8:17
 */
@RestController
@RequestMapping("flowservice")
public class FlowManagerController {
    private static final Logger logger = LoggerFactory.getLogger(FlowManagerController.class);

    @Autowired
    private FlowManagerService flowManagerService;

    /**
     * @author ty
     * @Description 查询所有的网关规则列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, limitApp 租户名称, resource 资源名称]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findGatewayFlowRules")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findGatewayFlowRules(@RequestParam(value = "pageNo") int pageNo,
                                                    @RequestParam(value = "pageSize") int pageSize,
                                                    @RequestParam(value = "limitApp") String limitApp,
                                                    @RequestParam(value = "resource") String resource) {
        try {
            Map<String, Object> result = flowManagerService.findGatewayFlowRules(pageNo, pageSize, limitApp, resource);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询指定的网关访问量
     * @Date 8:52 2020/4/9
     * @Param [url 资源名称, subscriberName 租户名称]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findAccessInfo")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findAccessInfo(@RequestParam(value = "url") String url,
                                              @RequestParam(value = "subscriberName") String subscriberName) {
        try {
            Map<String, Object> result = flowManagerService.findAccessInfo(url, subscriberName);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询所有资源名称
     * @Date 8:52 2020/4/9
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findAuthMicroServiceName")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findAccessInfo() {
        try {
            Map<String, Object> result = flowManagerService.findAuthMicroServiceName();
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }
    /**
     * @author ty
     * @Description 根据资源名称查询用户名
     * @Date 8:52 2020/4/9
     * @param resource 资源名称
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findSubscriberNameByResource")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findSubscriberNameByResource(@RequestParam(value = "resource") String resource) {
        try {
            Map<String, Object> result = flowManagerService.findSubscriberNameByResource(resource);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 新建规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值,
     * grade 阀值类型(1), strategy (0), controlBehavior 流控方式(0)]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/create")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> create(@RequestParam(value = "resource") String resource,
                                      @RequestParam(value = "limitApp") String limitApp,
                                      @RequestParam(value = "count") int count,
                                      @RequestParam(value = "grade") String grade,
                                      @RequestParam(value = "strategy") String strategy,
                                      @RequestParam(value = "controlBehavior") String controlBehavior) {
        try {
            Map<String, Object> result = flowManagerService.create(resource,limitApp,count,grade,strategy,controlBehavior);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 编辑规则
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
            Map<String, Object> result = flowManagerService.update(resource,limitApp,count,grade,strategy,controlBehavior);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 删除规则
     * @Date 8:52 2020/4/9
     * @Param [resource 资源名称, limitApp 租户名称, count 阈值]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/del")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> del(@RequestParam(value = "resource") String resource,
                                   @RequestParam(value = "limitApp") String limitApp,
                                   @RequestParam(value = "count") int count) {
        try {
            Map<String, Object> result = flowManagerService.del(resource,limitApp,count);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询所有的流量规则列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,
     * microServiceName 微服务名称, interfaceType 接口类型, apiPath API路径]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/listInvokeRule")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> listInvokeRule(@RequestParam(value = "pageNo") int pageNo,
                                              @RequestParam(value = "pageSize") int pageSize,
                                              @RequestParam(value = "subscriberName") String subscriberName,
                                              @RequestParam(value = "microServiceName") String microServiceName,
                                              @RequestParam(value = "interfaceType") String interfaceType,
                                              @RequestParam(value = "apiPath") String apiPath) {
        try {
            Map<String, Object> result = flowManagerService.listInvokeRule(pageNo,pageSize,subscriberName,microServiceName,interfaceType,apiPath);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 根据资源名称和租户名查找接口类型
     * @Date 8:52 2020/4/9
     * @param microServiceName 服务名称
     * @param subscriberName 租户名称
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findInterfaceTypeListByMSNameAndSubscriber")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findInterfaceTypeListByMSNameAndSubscriber(@RequestParam(value = "microServiceName") String microServiceName,
                                                                          @RequestParam(value = "subscriberName") String subscriberName) {
        try {
            Map<String, Object> result = flowManagerService.findInterfaceTypeListByMSNameAndSubscriber(microServiceName,subscriberName);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 根据资源名称、租户名、接口类型查找API路径
     * @Date 8:52 2020/4/9
     * @param microServiceName 服务名称
     * @param subscriberName 租户名称
     * @param interfaceType 接口类型
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/findAPIPathByMSNameSubscriberNameAndInterfaceType")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findAPIPathByMSNameSubscriberNameAndInterfaceType(@RequestParam(value = "microServiceName") String microServiceName,
                                                                                 @RequestParam(value = "subscriberName") String subscriberName,
                                                                                 @RequestParam(value = "interfaceType") String interfaceType) {
        try {
            Map<String, Object> result = flowManagerService.findAPIPathByMSNameSubscriberNameAndInterfaceType(microServiceName,subscriberName,interfaceType);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询流量规则是否存在
     * @Date 8:52 2020/4/9
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     *  interfaceType 接口类型, apiPath API路径，invokeTime 调用限数]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/isExistInvokeRule")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> isExistInvokeRule(@RequestParam(value = "microServiceName") String microServiceName,
                                                 @RequestParam(value = "subscriberName") String subscriberName,
                                                 @RequestParam(value = "interfaceType") String interfaceType,
                                                 @RequestParam(value = "apiPath") String apiPath,
                                                 @RequestParam(value = "invokeTime") int invokeTime) {
        try {
            Map<String, Object> result = flowManagerService.isExistInvokeRule(microServiceName,subscriberName,interfaceType,apiPath,invokeTime);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 创建流量规则
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     *  interfaceType 接口类型, apiPath API路径，invokeTime 调用限数]
     * @Date 8:52 2020/4/9
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/createInvokeRule")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> createInvokeRule(@RequestParam(value = "microServiceName") String microServiceName,
                                                @RequestParam(value = "subscriberName") String subscriberName,
                                                @RequestParam(value = "interfaceType") String interfaceType,
                                                @RequestParam(value = "apiPath") String apiPath,
                                                @RequestParam(value = "invokeTime") int invokeTime) {
        try {
            Map<String, Object> result = flowManagerService.createInvokeRule(microServiceName,subscriberName,interfaceType,apiPath,invokeTime);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 编辑流量规则
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,
     *  apiPath API路径，invokeTime 调用限数]
     * @Date 8:52 2020/4/9
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/editInvokeRule")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> editInvokeRule(@RequestParam(value = "microServiceName") String microServiceName,
                                                @RequestParam(value = "subscriberName") String subscriberName,
                                                @RequestParam(value = "apiPath") String apiPath,
                                                @RequestParam(value = "invokeTime") int invokeTime) {
        try {
            Map<String, Object> result = flowManagerService.editInvokeRule(microServiceName,subscriberName,apiPath,invokeTime);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 删除流量规则
     * @Param [microServiceName 微服务名称，subscriberName 租户名称,apiPath API路径]
     * @Date 8:52 2020/4/9
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/deleteInvokeRule")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteInvokeRule(@RequestParam(value = "microServiceName") String microServiceName,
                                                @RequestParam(value = "subscriberName") String subscriberName,
                                                @RequestParam(value = "apiPath") String apiPath) {
        try {
            Map<String, Object> result = flowManagerService.deleteInvokeRule(microServiceName,subscriberName,apiPath);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 查询所有的调用次数列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/listSubscriberInvokeStatistic")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> listSubscriberInvokeStatistic(@RequestParam(value = "pageNo") int pageNo,
                                                             @RequestParam(value = "pageSize") int pageSize,
                                                             @RequestParam(value = "subscriberName") String subscriberName,
                                                             @RequestParam(value = "microServiceName") String microServiceName,
                                                             @RequestParam(value = "interfaceType") String interfaceType,
                                                             @RequestParam(value = "apiPath") String apiPath,
                                                             @RequestParam(value = "startDate") String startDate,
                                                             @RequestParam(value = "endDate") String endDate) {
        try {
            Map<String, Object> result = flowManagerService.listSubscriberInvokeStatistic(pageNo,pageSize,subscriberName,microServiceName,interfaceType,apiPath,startDate,endDate);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * @Description 导出调用次数
     * @Date 8:52 2020/4/9
     * @Param [subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/invokeStatisticExport")
    @ResponseStatus(HttpStatus.OK)
    public void invokeStatisticExport(HttpServletResponse response,
                                      @RequestParam(value = "subscriberName") String subscriberName,
                                      @RequestParam(value = "microServiceName") String microServiceName,
                                      @RequestParam(value = "interfaceType") String interfaceType,
                                      @RequestParam(value = "apiPath") String apiPath,
                                      @RequestParam(value = "startDate") String startDate,
                                      @RequestParam(value = "endDate") String endDate) {
        flowManagerService.invokeStatisticExport(response,subscriberName,microServiceName,interfaceType,apiPath,startDate,endDate);
    }

    /**
     * @author ty
     * @Description 查询所有的调用日志列表
     * @Date 8:52 2020/4/9
     * @Param [pageNo 页码, pageSize 分页步长, subscriberName 租户名称,microServiceName 微服务名称,
     * interfaceType 接口类型, apiPath API路径, startDate 开始时间2020-04-09, endDate 结束时间2020-04-09]
     */
    @GetMapping(value = "/listStatisticLog")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> listStatisticLog(@RequestParam(value = "pageNo") int pageNo,
                                                @RequestParam(value = "pageSize") int pageSize,
                                                @RequestParam(value = "subscriberName") String subscriberName,
                                                @RequestParam(value = "microServiceName") String microServiceName,
                                                @RequestParam(value = "interfaceType") String interfaceType,
                                                @RequestParam(value = "apiPath") String apiPath,
                                                @RequestParam(value = "startDate") String startDate,
                                                @RequestParam(value = "endDate") String endDate) {
        try {
            Map<String, Object> result = flowManagerService.listStatisticLog(pageNo,pageSize,subscriberName,microServiceName,interfaceType,apiPath,startDate,endDate);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }
}
