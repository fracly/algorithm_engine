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
import cn.escheduler.api.service.HybqLebalService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.*;


/**
 * data source controller
 */
@RestController
@RequestMapping("hybqLebal")
public class HybqLebalController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(HybqLebalController.class);

    @Autowired
    private HybqLebalService hybqLebalService;


//    @GetMapping(value = "/list")
//    @ResponseStatus(HttpStatus.OK)
//    public Result queryDataSourceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                      @RequestParam("type") DbType type) {
//        try {
//            Map<String, Object> result = dataSourceService.queryDataSourceList(loginUser, type.ordinal());
//            return returnDataList(result);
//        } catch (Exception e) {
//            logger.error(QUERY_DATASOURCE_ERROR.getMsg(),e);
//            return error(Status.QUERY_DATASOURCE_ERROR.getCode(), Status.QUERY_DATASOURCE_ERROR.getMsg());
//        }
//    }

    /**
     * query datasource with paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryDataSourceListPaging(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @RequestParam("pageNo") Integer pageNo,
                                            @RequestParam("pageSize") Integer pageSize) {
        try {
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if (result.get(Constants.STATUS) != Status.SUCCESS) {
                return returnDataListPaging(result);
            }
            result = hybqLebalService.queryDataSourceListPaging(loginUser, pageNo, pageSize);
                return returnDataListPaging(result);
        } catch (Exception e) {
            logger.error(QUERY_DATASOURCE_ERROR.getMsg(),e);
            return error(QUERY_DATASOURCE_ERROR.getCode(), QUERY_DATASOURCE_ERROR.getMsg());
        }
    }

}





