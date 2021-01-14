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
package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.*;
import cn.escheduler.dao.mapper.HybqLebalMapper;
import cn.escheduler.dao.model.HybqLebal;
import cn.escheduler.dao.model.Resource;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * datasource service
 */
@Service
public class HybqLebalService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(HybqLebalService.class);

    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String TYPE = "type";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DATABASE = "database";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String OTHER = "other";


    @Autowired
    private HybqLebalMapper dataSourceMapper;

    /**
     * query datasource list by keyword
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> queryDataSourceListPaging(User loginUser, Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();

        Integer count = getTotalCount(loginUser);

        PageInfo pageInfo = new PageInfo<Resource>(pageNo, pageSize);
        pageInfo.setTotalCount(count);
        List<HybqLebal> datasourceList = getDataSources(loginUser, pageSize, pageInfo);

        pageInfo.setLists(datasourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * get list paging
     *
     * @param loginUser
     * @param pageSize
     * @param pageInfo
     * @return
     */
    private List<HybqLebal> getDataSources(User loginUser, Integer pageSize, PageInfo pageInfo) {
            return dataSourceMapper.queryAllLabelPaging( pageInfo.getStart(), pageSize);
    }

    /**
     * get datasource total num
     *
     * @param loginUser
     * @return
     */
    private Integer getTotalCount(User loginUser) {
//        if (isAdmin(loginUser)) {
//            return dataSourceMapper.countAllDatasource();
//        }
        return dataSourceMapper.countAllLabele();
//        return dataSourceMapper.countUserDatasource(loginUser.getId());
    }

}
