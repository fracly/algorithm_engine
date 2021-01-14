/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.api.controller;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.ServerManagerService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.*;


/**
 * 服务管理 controller
 */
@RestController
@RequestMapping("serverManager")
public class ServerManagerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ServerManagerController.class);

    @Autowired
    private ServerManagerService serverManagerService;

    /**
     * list-paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result getServerList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "searchVal", required = false) String searchVal,
                                @RequestParam(value = "status", required = false) Integer status,
                                @RequestParam("pageNo") Integer pageNo,
                                @RequestParam("pageSize") Integer pageSize,
                                @RequestParam(value = "type", required = false) String type) {
        try {
            logger.info("query proccess definition list paging, login user:{}", loginUser.getUserName());
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if (result.get(Constants.STATUS) != Status.SUCCESS) {
                return returnDataListPaging(result);
            }
            searchVal = ParameterUtils.handleEscapes(searchVal);
            result = serverManagerService.getServerList(loginUser, searchVal, status, pageNo, pageSize,type);

            return returnDataListPaging(result);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * list-paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list-paging-all")
    @ResponseStatus(HttpStatus.OK)
    public Result getServerList2(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value = "searchVal", required = false) String searchVal,
                                 @RequestParam(value = "status", required = false) Integer status,
                                 @RequestParam("pageNo") Integer pageNo,
                                 @RequestParam("pageSize") Integer pageSize,
                                 @RequestParam(value = "type", required = false) String type) {
        try {
            logger.info("query proccess definition list paging, login user:{}", loginUser.getUserName());
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if (result.get(Constants.STATUS) != Status.SUCCESS) {
                return returnDataListPaging(result);
            }
            searchVal = ParameterUtils.handleEscapes(searchVal);
            result = serverManagerService.getServerList2(searchVal, status, pageNo, pageSize,type);

            return returnDataListPaging(result);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * createServer
     *
     * @param loginUser
     * @return
     */
    @PostMapping(value = "/createServer")
    @ResponseStatus(HttpStatus.OK)
    public Result createServer(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                               @RequestBody Map<String, String> server) {
        try {
            logger.info("query proccess definition list paging, login user:{}", loginUser.getUserName());
            String createUser = loginUser.getUserName();
            String serviceName = server.get("name");
            String serviceCode = server.get("code");
            String developMode = server.get("mode");
            String type = server.get("type");
            String datasourceType = server.get("datasourceType");
            String datasourceName = "";
            String serviceSql = server.get("sql");
            String des = server.get("desc") == null ? "" : server.get("desc");
            String status = server.get("status");
            Integer datasourceId = Integer.parseInt(server.get("datasourceId"));
            String serviceParams = server.get("params");
            String table = server.get("table");
            String inParam = server.get("inParam");
            String outParam = server.get("outParam");
            Map<String, Object> insert = serverManagerService.insert(serviceName, serviceCode, developMode, type, datasourceType,
                    datasourceName, serviceSql, status, createUser, des, datasourceId, serviceParams, table, inParam, outParam);
            return returnDataList(insert);

        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * updateServer
     *
     * @return
     */
    @PostMapping(value = "/updateServer")
    @ResponseStatus(HttpStatus.OK)
    public Result updateServer(@RequestBody Map<String, String> server) {


        try {
            int serviceId = Integer.parseInt(server.get("id"));
            String serviceName = server.get("name");
            String serviceCode = server.get("code");
            String developMode = server.get("mode");
            String type = server.get("type");
            String datasourceType = server.get("datasourceType");
            String datasourceName = "";
            String serviceSql = server.get("sql");
            String des = server.get("desc") == null ? "" : server.get("desc");
            String status = server.get("status");
            Integer datasourceId = Integer.parseInt(server.get("datasourceId"));
            String serviceParams = server.get("params");
            String table = server.get("table");
            String inParam = server.get("inParam");
            String outParam = server.get("outParam");
            Map<String, Object> update = serverManagerService.update(serviceId, serviceName, serviceCode, type, developMode, datasourceType,
                    datasourceName, serviceSql, status, des, datasourceId, serviceParams, table, inParam, outParam);
            return returnDataList(update);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * updateProcessInstance data source
     *
     * @param serviceId
     * @param status
     * @return
     */
    @GetMapping(value = "/onLine")
    @ResponseStatus(HttpStatus.OK)
    public Result onLine(@RequestParam("id") int serviceId,
                         @RequestParam("status") int status) {
        logger.info("reportId {} status: {}",
                serviceId, status);
        try {
            Map<String, Object> update = serverManagerService.onLine(serviceId, status);
            return returnDataList(update);
        } catch (Exception e) {
            return error(UPDATE_STATUS_ERROR.getCode(), UPDATE_STATUS_ERROR.getMsg());
        }
    }

    /**
     * getInfo
     *
     * @return
     */
    @GetMapping(value = "/getInfo")
    @ResponseStatus(HttpStatus.OK)
    public Result getInfo(@RequestParam("id") int serviceId) {
        try {
            Map<String, Object> getInfo = serverManagerService.getInfo(serviceId);
            return returnDataList(getInfo);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * delete
     *
     * @return
     */
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@RequestParam("id") int serviceId) {
        try {
            Map<String, Object> getInfo = serverManagerService.delete(serviceId);
            return returnDataList(getInfo);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * analysis sql
     *
     * @return
     */
    @GetMapping(value = "/analysisSql")
    @ResponseStatus(HttpStatus.OK)
    public Result analysisSql(@RequestParam(value = "sql", required = false) String sql,
                              @RequestParam("datasourceId") int datasourceId,
                              @RequestParam("mode") String mode) {
        try {
            if (sql == null && sql.equals("")) {
                return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
            }
            Map<String, Object> getInfo = serverManagerService.analysisSql(sql, datasourceId, mode);
            return returnDataList(getInfo);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

    @PostMapping(value = "/createParam")
    @ResponseStatus(HttpStatus.OK)
    public Result createParam(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @RequestBody Map<String, String> params) {
        try {
            String id = params.get("id");
            String param = params.get("param");
            Map<String, Object> result = serverManagerService.createParam(id, param);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(CREATE_SERVER_PARAM_ERROR.getMsg(), e);
            return error(Status.CREATE_SERVER_PARAM_ERROR.getCode(), Status.CREATE_SERVER_PARAM_ERROR.getMsg());
        }
    }

    /**
     * @return
     */
    @GetMapping(value = "/getParam")
    @ResponseStatus(HttpStatus.OK)
    public Result getParam(@RequestParam("id") int serviceId,
                           @RequestParam(value = "type", required = false) String type) {
        try {
            Map<String, Object> getInfo = serverManagerService.getParam(serviceId, type);
            return returnDataList(getInfo);
        } catch (Exception e) {
            logger.error(QUERY_SERVICE_LIST_PAGING_ERROR.getMsg(), e);
            return error(QUERY_SERVICE_LIST_PAGING_ERROR.getCode(), QUERY_SERVICE_LIST_PAGING_ERROR.getMsg());
        }
    }

}
