package cn.escheduler.api.controller;

import cn.escheduler.api.service.InterfaceService;
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
 * @description 预定义自定义接口服务
 * @Date: 2020-05-09 16:03
 */
@RestController
@RequestMapping("interface")
public class InterfaceController {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceController.class);

    @Autowired
    private InterfaceService interfaceService;

    /**
     * @author ty
     * get datasource table list
     */
    @GetMapping(value = "/tableList")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getDatabaseTableList() {
        try {
            Map<String, Object> result = interfaceService.tableList();
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @author ty
     * get datasource table column list
     * @Param table 表名
     */
    @GetMapping(value = "/columnListByTable")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getColumnListByTable(@RequestParam("table") String table) {
        try {
            Map<String, Object> result = interfaceService.getColumnListByTable(table);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @return
     * @author ty
     * get datasource table app_traffic_sbbh_cllx_day data
     */
    @GetMapping(value = "/getTableApp_traffic_sbbh_cllx_day")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getTableApp_traffic_sbbh_cllx_day() {
        try {
            Map<String, Object> result = interfaceService.getTableApp_traffic_sbbh_cllx_day();
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @return
     * @author ty
     * get user datasource defined table data by query params
     * @Param [ userName 用户名,table 表名,selectParam 查询参数, whereParam 条件参数，comment 自定义接口描述]
     * selectParam: "查询参数(多个字段逗号分隔)"
     * whereParam: "条件参数[{'column':'列名1','term':'>','value':'10'},{'column':'列名2','term':'>=','value':'10'}]"
     */
    @GetMapping(value = "/getTable")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getTable(@RequestParam("userName") String userName,
                                        @RequestParam("table") String table,
                                        @RequestParam("selectParams") String selectParams,
                                        @RequestParam("whereParams") String whereParams,
                                        @RequestParam("comments") String comments) {
        try {
            Map<String, Object> result = interfaceService.getTable(userName, table, selectParams, whereParams, comments);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    @GetMapping(value = "/saveUserInterface")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> saveUserInterface(@RequestParam("userName") String userName,
                                                 @RequestParam("table") String table,
                                                 @RequestParam("selectParams") String selectParams,
                                                 @RequestParam("whereParams") String whereParams,
                                                 @RequestParam("comments") String comments) {
        try {
            Map<String, Object> result = interfaceService.saveUserInterface(userName, table, selectParams, whereParams, comments);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @return
     * @author ty
     * get all uesr defined datasource table data
     * @Param [ userName 用户名]
     */
    @GetMapping(value = "/getUserTable")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getUserTable(@RequestParam("userName") String userName) {
        try {
            Map<String, Object> result = interfaceService.getUserTable(userName);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * @return
     * @author ty
     * delet uesr defined datasource table params
     * @Param [ userName 用户名，comment 自定义接口描述]
     */
    @GetMapping(value = "/deleteUserTable")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteUserTable(@RequestParam("userName") String userName,
                                               @RequestParam("comments") String comments) {
        try {
            Map<String, Object> result = interfaceService.deleteUserTable(userName, comments);
            return result;
        } catch (Exception e) {
            logger.error(QUERY_ZOOKEEPER_STATE_ERROR.getMsg(), e);
            return null;
        }
    }
}
