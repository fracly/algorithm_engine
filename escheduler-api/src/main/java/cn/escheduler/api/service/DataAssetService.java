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

import cn.escheduler.dao.mapper.DataAssetMapper;
import cn.escheduler.dao.mapper.EnumMapper;
import cn.escheduler.dao.model.DataAsset;
import cn.escheduler.dao.model.DataAssetRule;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * datasource service
 */
@Service
public class DataAssetService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(DataAssetService.class);

    @Autowired
    private DataAssetMapper dataAssetMapper;

    @Autowired
    private EnumMapper enumMapper;

    /**
     * get all data asset type
     */
    public List<Map<String, Object>> getType() {
        return enumMapper.getDataAssetType();
    }

    /**
     * get all data asset label
     */
    public List<Map<String, Object>> getLabel() {
        return enumMapper.getDataAssetLabel();
    }

    /**
     * get all data asset state
     */
    public List<Map<String, Object>> getState() {
        return enumMapper.getDataAssetState();
    }

    /**
     * get all data asset user
     */
    public List<Map<String, Object>> getUser() {
        return dataAssetMapper.getUser();
    }

    /**
     * get all data asset destroy method
     */
    public List<Map<String, Object>> getDetroyMethod() {
        return enumMapper.getDataAssetDestroyMethod();
    }

    /**
     * get data asset list
     */
    public List<Map<String, Object>> getDataAssetList(){
        return dataAssetMapper.getDataAssetList();
    }

    /**
     * get data asset list by parameters
     */
    public List<DataAsset> getListPaging(String type, String labels, String dateRange, Integer pageNo, Integer pageSize) {
        Integer offset = (pageNo - 1) * pageSize;
        List<DataAsset> list = dataAssetMapper.getDataAssetListPaging(type, labels, dateRange, offset, pageSize);
        for(DataAsset dataAsset : list) {
            List<String> convertList = new ArrayList<>();
            String label = dataAsset.getLabels();
            String[] labelArray = label.split(",");
            for(String l : labelArray) {
                String labelName = dataAssetMapper.queryLabelName(Integer.parseInt(l));
                convertList.add(labelName);
            }
            dataAsset.setLabels2(String.join(",", convertList));
            Integer state = dataAsset.getState();
            String stateStr = dataAssetMapper.queryStateName(state);
            dataAsset.setStateStr(stateStr);
        }
        return list;
    }

    /**
     * get data asset rule list by parameters
     */
    public List<DataAssetRule> getRuleListPaging(String name, Integer state, Integer userId, String dateRange, Integer pageNo, Integer pageSize) {
        Integer offset = (pageNo - 1) * pageSize;
        List<DataAssetRule> list = dataAssetMapper.getDataAssetRuleListPaging(name, state, userId, dateRange, offset, pageSize);
        for(DataAssetRule rule : list ) {
            String methodId = rule.getDestroyMethod();
            String destroyMethodStr = dataAssetMapper.queryMethodName(Integer.parseInt(methodId));
            rule.setDestroyMethodStr(destroyMethodStr);
            String stateId = rule.getAssetState();
            String stateStr = dataAssetMapper.queryStateName(Integer.parseInt(stateId));
            rule.setAssetStateStr(stateStr);
        }
        return list;
    }

    /**
     * get data asset total by parameters
     */
    public Long getListTotal(String type, String labels, String dateRange) {
        return dataAssetMapper.getDataAssetTotal(type, labels, dateRange);
    }

    /**
     * get data asset rule total by parameters
     */
    public Long getRuleListTotal(String name, Integer state, Integer userId, String dateRange) {
        return dataAssetMapper.getDataAssetRuleTotal(name, state, userId, dateRange);
    }

    /**
     * create data asset
     */
    public Integer create(User loginUser, String name, String type, String labels, String detail) {
        DataAsset dataAsset = new DataAsset();
        dataAsset.setCreateTime(new Date());
        dataAsset.setUpdateTime(new Date());
        dataAsset.setLabels(labels);
        dataAsset.setDetail(detail);
        dataAsset.setState(1);
        dataAsset.setOwner(loginUser.getId());
        dataAsset.setName(name);
        dataAsset.setType(type);
        return dataAssetMapper.insert(dataAsset);
    }

    /**
     * update data asset
     */
    public Integer update(Integer id, String name, String type, String labels, String detail) {
        DataAsset dataAsset = queryById(id);
        dataAsset.setDetail(detail);
        dataAsset.setType(type);
        dataAsset.setName(name);
        dataAsset.setLabels(labels);
        dataAsset.setUpdateTime(new Date());
        return dataAssetMapper.update(dataAsset);
    }

    /**
     * delete data asset by id
     */
    public Integer delete(Integer id) {
        return dataAssetMapper.delete(id);
    }

    /**
     * query data asset by id
     */
    public DataAsset queryById(Integer id) {
        return dataAssetMapper.queryById(id);
    }

    public DataAssetRule queryRuleById(Integer id) {
        DataAssetRule dataAssetRule = dataAssetMapper.queryRuleById(id);
        return dataAssetRule;
    }
    /**
     * create data asset rule
     */
    public Integer createRule(User loginUser, Integer assetId, String destroyMethod, Integer destroyPeriod) {
        DataAssetRule rule = new DataAssetRule();
        DataAsset dataAsset = queryById(assetId);
        rule.setAssetName(dataAsset.getName());
        rule.setAssetId(assetId);
        rule.setAssetState(dataAssetMapper.queryStateName(dataAsset.getState()));
        rule.setCreateTime(new Date());
        rule.setDestroyMethod(destroyMethod);
        rule.setDestroyPeriod(destroyPeriod);
        rule.setUpdateTime(new Date());
        rule.setOwner(loginUser.getUserName());
        return dataAssetMapper.insertRule(rule);
    }

    /**
     * update data asset rule
     */
    public Integer updateRule(User loginUser, Integer id, Integer assetId, String destroyMethod, Integer destroyPeriod) {
        DataAssetRule rule = queryRuleById(id);
        DataAsset asset = queryById(assetId);
        rule.setAssetId(assetId);
        rule.setAssetName(asset.getName());
        rule.setDestroyMethod(destroyMethod);
        rule.setDestroyPeriod(destroyPeriod);
        rule.setUpdateTime(new Date());
        return dataAssetMapper.updateRule(rule);
    }

    /**
     * delete data asset by id
     */
    public Integer deleteRule(Integer id) {
        return dataAssetMapper.deleteRule(id);
    }
}
