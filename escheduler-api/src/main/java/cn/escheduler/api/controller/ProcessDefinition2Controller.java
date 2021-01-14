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
import cn.escheduler.api.service.ProcessDefinitionService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.*;
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
 * process definition controller
 */
@RestController
@RequestMapping("projects/process")
public class ProcessDefinition2Controller extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ProcessDefinition2Controller.class);

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    public static final String DEFAULT_WARNING_TYPE = "NONE";
    public static final String DEFAULT_NOTIFY_GROUP_ID = "1";
    public static final String DEFAULT_FAILURE_POLICY = "CONTINUE";

    /**
     * query proccess definition list paging
     * @param loginUser
     * @param projectName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value="/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryProcessDefinitionListPaging(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                   @RequestParam(value = "projectName", required = false)  String projectName,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                   @RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
                                                   @RequestParam("pageSize") Integer pageSize){
        try{
            logger.info("query proccess definition list paging, login user:{}, project name:{}", loginUser.getUserName(), projectName);
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }
            searchVal = ParameterUtils.handleEscapes(searchVal);
            result = processDefinitionService.queryProcessDefinitionListPaging(loginUser, projectName, searchVal, pageNo, pageSize, userId);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_PROCCESS_DEFINITION_LIST_PAGING_ERROR.getMsg(),e);
            return error(QUERY_PROCCESS_DEFINITION_LIST_PAGING_ERROR.getCode(), QUERY_PROCCESS_DEFINITION_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * get work group id
     * @param definitionId
     * @return
     */
    @GetMapping(value="/getWorkId")
    @ResponseStatus(HttpStatus.OK)
    public Result getWorkId(@RequestParam("definitionId") Integer definitionId){
        try{
            logger.info("definitionId:{}", definitionId);

            Map<String, Object> workId = processDefinitionService.getWorkId(definitionId);
            return returnDataList(workId);
        }catch (Exception e){
            logger.error(GET_WORK_GROUP_ID_ERROR.getMsg(),e);
            return error(GET_WORK_GROUP_ID_ERROR.getCode(), GET_WORK_GROUP_ID_ERROR.getMsg());
        }
    }

    /**
     * updateWorkId
     * @param definitionId
     * @return
     */
    @GetMapping(value="/updateWorkId")
    @ResponseStatus(HttpStatus.OK)
    public Result updateWorkId(@RequestParam("definitionId") Integer definitionId,
                               @RequestParam(value ="workerGroupId", required = false, defaultValue = "-1") Integer groupId,
                               @RequestParam(value ="failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                               @RequestParam(value ="warningType", required = false,defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                               @RequestParam(value ="processInstancePriority", required = false) Priority processInstancePriority,
                               @RequestParam(value ="warningGroupId", required = false,defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                               @RequestParam(value ="execType", required = false) CommandType execType,
                               @RequestParam(value ="scheduleTime", required = false) String scheduleTime,
                               @RequestParam(value ="runMode", required = false) RunMode runMode){
        try{
            logger.info("definitionId:{}", definitionId);

            Map<String, Object> workId = processDefinitionService.updateWorkId(definitionId,groupId,failureStrategy,warningType,processInstancePriority
                    ,warningGroupId,execType,scheduleTime,runMode);
            return returnDataList(workId);
        }catch (Exception e){
            logger.error(UPDATE_WORK_GROUP_ID_ERROR.getMsg(),e);
            return error(UPDATE_WORK_GROUP_ID_ERROR.getCode(), UPDATE_WORK_GROUP_ID_ERROR.getMsg());
        }
    }



}
