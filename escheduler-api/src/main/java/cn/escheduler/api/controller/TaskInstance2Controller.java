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


import cn.escheduler.api.service.TaskInstanceService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.ExecutionStatus;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.TaskInstance;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.escheduler.api.enums.Status.QUERY_TASK_LIST_PAGING_ERROR;

/**
 * task instance controller
 */
@RestController
@RequestMapping("/projects/task-instance")
public class TaskInstance2Controller extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(TaskInstance2Controller.class);

    @Autowired
    TaskInstanceService taskInstanceService;


    /**
     * query task list paging
     *
     * @param loginUser
     * @return
     */
    @GetMapping("/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTaskListPaging(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                      @RequestParam(value = "projectId", required = false, defaultValue = "0") Integer projectId,
                                      @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                      @RequestParam(value = "searchVal", required = false) String searchVal,
                                      @RequestParam(value = "taskName", required = false) String taskName,
                                      @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                      @RequestParam(value = "host", required = false) String host,
                                      @RequestParam(value = "startDate", required = false) String startTime,
                                      @RequestParam(value = "endDate", required = false) String endTime,
                                      @RequestParam("pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize){

        try{
            logger.info("query task instance list, processDefinitionId:{}, search value:{},task name:{}, state type:{}, host:{}, start:{}, end:{}"
                    , processDefinitionId, searchVal, taskName, stateType, host, startTime, endTime);
            searchVal = ParameterUtils.handleEscapes(searchVal);
            Map<String, Object> result = taskInstanceService.queryTaskListPaging2(
                    loginUser, projectId, processDefinitionId, taskName, startTime, endTime, searchVal, stateType, host, pageNo, pageSize);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_TASK_LIST_PAGING_ERROR.getMsg(),e);
            return error(QUERY_TASK_LIST_PAGING_ERROR.getCode(), QUERY_TASK_LIST_PAGING_ERROR.getMsg());
        }

    }

    /**
     * query task by process instance
     */
    @GetMapping("/query-by-process-instance")
    @ResponseStatus(HttpStatus.OK)
    public Result queryByProcessInstance(@RequestParam(value = "processInstanceId") Long processInstanceId){
        Result result = new Result();
        List<TaskInstance> list = taskInstanceService.queryByProcessInstance(processInstanceId);
        result.setData(list);
        result.setCode(0);
        result.setMsg("获取成功");
        return result;
    }

}
