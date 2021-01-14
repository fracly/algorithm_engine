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


import cn.escheduler.api.service.ETLGroupService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.CREATE_PROJECT_ERROR;

/**
 * etl group controller
 */
@RestController
@RequestMapping("etlgroup")
public class ETLGroupController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ETLGroupController.class);

    @Autowired
    private ETLGroupService etlGroupService;

    /**
     * create project
     *
     * @param loginUser
     * @return returns an error if it exists
     */
    @PostMapping(value = "/initialize")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createProject(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        boolean exists = etlGroupService.checkExists(loginUser);
        if(!exists){
            logger.info("login user {}, don't have etl group project, now initialize one", loginUser.getUserName());
            Map<String, Object> result = etlGroupService.createProject(loginUser);
            return returnDataList(result);
        }else{
            logger.error("login user{} already has one etl group project, please check!", loginUser.getUserName());
            return error(CREATE_PROJECT_ERROR.getCode(), CREATE_PROJECT_ERROR.getMsg());
        }
    }


    /**
     * get data govern project's all processes
     *
     * @param loginUser
     * @return returns an error if it exists
     */
    @GetMapping(value = "/getAllProcess")
    @ResponseStatus(HttpStatus.OK)
    public Result getAllProcess(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = etlGroupService.queryAllProcessByLoginUser(loginUser);
        return returnDataList(result);
    }



}
