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
import cn.escheduler.api.service.ProcessInstanceService;
import cn.escheduler.api.service.ProjectService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.ExecutionStatus;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.Project;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.escheduler.api.enums.Status.*;

/**
 * process instance controller
 */
@RestController
@RequestMapping("projects/instance")
public class ProcessInstance2Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstance2Controller.class);


    @Autowired
    ProcessInstanceService processInstanceService;

    @Autowired
    private ProjectService projectService;

    /**
     * query process instance list paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryProcessInstanceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @RequestParam(value = "projectId", required = false, defaultValue = "0") Integer projectId,
                                           @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                           @RequestParam(value = "searchVal", required = false) String searchVal,
                                           @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                           @RequestParam(value = "host", required = false) String host,
                                           @RequestParam(value = "startDate", required = false) String startTime,
                                           @RequestParam(value = "endDate", required = false) String endTime,
                                           @RequestParam("pageNo") Integer pageNo,
                                           @RequestParam("pageSize") Integer pageSize) {
        try {
            logger.info("query all process instance list, login user:{},define id:{}," +
                            "search value:{},state type:{},host:{},start time:{}, end time:{},page number:{}, page size:{}",
                    loginUser.getUserName(), processDefinitionId, searchVal, stateType, host,
                    startTime, endTime, pageNo, pageSize);
            searchVal = ParameterUtils.handleEscapes(searchVal);
            Map<String, Object> result = processInstanceService.queryProcessInstanceList2(
                    loginUser,projectId, processDefinitionId, startTime, endTime, searchVal, stateType, host, pageNo, pageSize);
            return returnDataListPaging(result);
        } catch (Exception e) {
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(), e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * search group
     *
     * @return
     */
    @GetMapping(value = "search")
    @ResponseStatus(HttpStatus.OK)
    public Result search(@RequestAttribute(value = Constants.SESSION_USER) User loginUser
    , @RequestParam(value = "projectId", required = false, defaultValue ="0") Integer projectId
            , @RequestParam(value = "state", required = false, defaultValue ="1") Integer state){

        try {
            logger.info("login user {}, query project list paging", loginUser.getUserName());

            Map<String, Object>  map = projectService.queryDefinition(projectId,state,loginUser.getId());
            return returnDataList(map);
        } catch (Exception e) {
            logger.error(LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR.getMsg(), e);
            return null;
        }
    }

    /**
     * search group
     *
     * @return
     */
    @GetMapping(value = "searchFlag")
    @ResponseStatus(HttpStatus.OK)
    public Result search(@RequestAttribute(value = Constants.SESSION_USER) User loginUser){

        try {
            logger.info("login user {}, query project list paging", loginUser.getUserName());

            Map<String, Object>  map = projectService.queryFlagDefinition();
            return returnDataList(map);
        } catch (Exception e) {
            logger.error(LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR.getMsg(), e);
            return null;
        }
    }

    @GetMapping(value = "workGroupList")
    @ResponseStatus(HttpStatus.OK)
    public Result workGroupList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser
            , @RequestParam(value = "projectId", required = false, defaultValue ="0") Integer projectId){

        try {
            logger.info("login user {}, query project list paging", loginUser.getUserName());

            Map<String, Object> result = projectService.queryProjectListPaging(loginUser, 999, 1, "");
            PageInfo data = (PageInfo) result.get("data");
            List<Project> lists = data.getLists();
            List list=new ArrayList();
            for(int i=0;i<lists.size();i++){
                Map map=new HashMap();
                String name = lists.get(i).getName();
                String desc = lists.get(i).getDesc();
                int id = lists.get(i).getId();
                map.put("name",name);
                map.put("id",id);
                map.put("desc",desc);
                list.add(map);
            }
            return success(list);
        } catch (Exception e) {
            logger.error(LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR.getMsg(), e);
            return error(-1,"获取模型组报错："+e);
        }
    }



}
