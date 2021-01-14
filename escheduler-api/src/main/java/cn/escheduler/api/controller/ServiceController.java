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
import cn.escheduler.api.service.ServiceService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.Application;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_SERVICE_CALL_STATIC_ERROR;
import static cn.escheduler.api.enums.Status.QUERY_SERVICE_LIST_PAGING_ERROR;

/**
 * service controller
 */
@RestController
@RequestMapping("service")
public class ServiceController extends  BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private ServiceService serviceService;

    /**
     * application list
     */
    @GetMapping(value = "/application/list")
    @ResponseStatus(HttpStatus.OK)
    public Result applicationList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("pageNo") Integer pageNo,
                                  @RequestParam("searchVal") String searchVal) {
        logger.info("login user: {}, query all application", loginUser.getUserName());
        Result result = new Result();
        PageInfo pageInfo = new PageInfo<Application>(pageNo, pageSize);
        Integer total = serviceService.applicationListTotal(searchVal);
        pageInfo.setTotalCount(total);
        List<Application> applicationList = serviceService.applicationList(pageNo, pageSize, searchVal);
        pageInfo.setLists(applicationList);

        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * create application
     */
    @PostMapping("/application/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Result applicationCreate(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value = "name") String name,
                                 @RequestParam(value = "type") String type,
                                 @RequestParam(value = "desc",required = false) String desc,
                                 @RequestParam(value = "status") Integer status,
                                 @RequestParam(value = "interfaces") String interfaces,
                                 @RequestParam(value = "id") Integer id) {
        logger.info("login User: {}, create application with name {}", loginUser.getUserName(), name);
        Result result = new Result();
        int count = 0;
        if(id != null && id > 0) {
            count = serviceService.applicationUpdate(id, name, type, desc, status, interfaces);
        } else {
             count = serviceService.applicationCreate(loginUser, name, type, desc, status, interfaces);
        }

        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "操作应用成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "操作应用失败");
        }
        return result;
    }

    /**
     * interface list
     */
    @GetMapping("/interface/list")
    @ResponseStatus(HttpStatus.OK)
    public Result interfaceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "applicationId", required = false) String applicationId,
                                @RequestParam(value = "key", required = false) String key) {
        logger.info("login user: {}, query all interface", loginUser.getUserName());
        Result result = new Result();
        List<Map<String, Object>> interfaceList = serviceService.interfaceList(applicationId,key);
        result.setData(interfaceList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * application delete
     */
    @GetMapping("/application/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result applicationDelete(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Long applicationId) {
        logger.info("login user: {}, delete application with id", loginUser.getUserName(), applicationId);
        Result result = new Result();
        int count = serviceService.applicationDelete(applicationId);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除应用成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除应用失败");
        }
        return result;
    }

    /**
     * application detail
     */
    @GetMapping("/application/detail")
    @ResponseStatus(HttpStatus.OK)
    public Result applicationDetail(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer applicationId) {
        logger.info("login user: {}, query application detail with id", loginUser.getUserName(), applicationId);
        Result result = new Result();
        Application app = serviceService.applicationDetail(applicationId);
        result.setData(app);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * interface invoke statistic
     */
    @GetMapping("/interface/invoke")
    @ResponseStatus(HttpStatus.OK)
    public Result interfaceInvoke(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("pageNo") Integer pageNo,
                                  @RequestParam("searchVal") String searchVal) {
        logger.info("login user: {}, statistic interface invoke count", loginUser.getUserName());
        Result result = new Result();
        PageInfo pageInfo = new PageInfo<Application>(pageNo, pageSize);
        List<Map<String, Object>> statisticList = serviceService.interfaceInvoke(searchVal, pageNo, pageSize);
        Integer total = serviceService.interfaceInvokeTotal(searchVal);
        pageInfo.setLists(statisticList);
        pageInfo.setTotalCount(total);

        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * list-paging
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value="/callStatList")
    @ResponseStatus(HttpStatus.OK)
    public Result getServerList(@RequestParam("pageNo") Integer pageNo,
                                @RequestParam("pageSize") Integer pageSize){
        try{
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }
            result = serviceService.callStatList( pageNo, pageSize);

            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_SERVICE_CALL_STATIC_ERROR.getMsg(),e);
            return error(QUERY_SERVICE_CALL_STATIC_ERROR.getCode(), QUERY_SERVICE_CALL_STATIC_ERROR.getMsg());
        }
    }

    /**
     * getApplicationInfo
     * @return
     */
    @GetMapping(value="/getApplicationInfo")
    @ResponseStatus(HttpStatus.OK)
    public Result getApplicationInfo( @RequestParam(value = "applicationName", required = false) String applicationName,
                                      @RequestParam(value = "serviceName", required = false) String serviceName,
                                      @RequestParam("status") String status,
                                      @RequestParam("time") String time,
                                      @RequestParam("mode") String mode){
        try{

            Map<String, Object> applicationInfo = serviceService.getApplicationInfo(applicationName, serviceName, status, time, mode);

            return returnDataList(applicationInfo);
        }catch (Exception e){
            logger.error(QUERY_SERVICE_CALL_STATIC_ERROR.getMsg(),e);
            return error(QUERY_SERVICE_CALL_STATIC_ERROR.getCode(), QUERY_SERVICE_CALL_STATIC_ERROR.getMsg());
        }
    }

    /**
     * getApplicationFailInfo
     * @return
     */
    @GetMapping(value="/getApplicationFailInfo")
    @ResponseStatus(HttpStatus.OK)
    public Result getApplicationFailInfo( @RequestParam(value = "applicationName", required = false) String applicationName,
                                      @RequestParam(value = "serviceName", required = false) String serviceName,
                                      @RequestParam("status") String status,
                                      @RequestParam("time") String time,
                                      @RequestParam("mode") String mode,
                                      @RequestParam("pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize){
        try{

            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }
             result = serviceService.getApplicationFailInfo(applicationName, serviceName, status, time, mode,pageNo,pageSize);

            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_SERVICE_CALL_STATIC_ERROR.getMsg(),e);
            return error(QUERY_SERVICE_CALL_STATIC_ERROR.getCode(), QUERY_SERVICE_CALL_STATIC_ERROR.getMsg());
        }
    }

    /**
     * getApplicationInfo
     * @return
     */
    @GetMapping(value="/getTypeList")
    @ResponseStatus(HttpStatus.OK)
    public Result getTypeList(@RequestParam("code") String code){
        try{

            Map<String, Object>  result = serviceService.getTypeList(code);

            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_SERVICE_CALL_STATIC_ERROR.getMsg(),e);
            return error(QUERY_SERVICE_CALL_STATIC_ERROR.getCode(), QUERY_SERVICE_CALL_STATIC_ERROR.getMsg());
        }
    }
}
