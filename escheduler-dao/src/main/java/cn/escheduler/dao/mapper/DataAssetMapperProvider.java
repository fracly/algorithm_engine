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
package cn.escheduler.dao.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data asset mapper provider
 */
public class DataAssetMapperProvider {

    public static final String TABLE_NAME = "t_escheduler_dataasset";
    public static final String LABEL_TABLE_NAME = "t_escheduler_dataasset_label";
    public static final String USER_TABLE_NAME = "t_escheduler_user";
    public static final String RULE_TABLE_NAME = "t_escheduler_dataasset_rule";
    public static final String STATE_TABLE_NAME = "t_escheduler_dataasset_state";
    public static final String DESTROY_METHOD_TABLE_NAME = "t_escheduler_dataasset_destroy_method";

    public String insert(Map<String, Object> parameter) {
        return new SQL() {{
            INSERT_INTO(TABLE_NAME);
            VALUES("`name`", "#{dataAsset.name}");
            VALUES("`type`", "#{dataAsset.type}");
            VALUES("`labels`", "#{dataAsset.labels}");
            VALUES("`detail`", "#{dataAsset.detail}");
            VALUES("`create_time`", "#{dataAsset.createTime}");
            VALUES("`update_time`", "#{dataAsset.updateTime}");
            VALUES("`state`", "#{dataAsset.state}");
            VALUES("`owner`", "#{dataAsset.owner}");
        }}.toString();
    }

    public String delete(Map<String, Object> parameter) {
        return new SQL() {{
            DELETE_FROM(TABLE_NAME);
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String update(Map<String, Object> parameter) {
        return new SQL() {{
            UPDATE(TABLE_NAME);
            SET("`name` = #{dataAsset.name}");
            SET("`type` = #{dataAsset.type}");
            SET("`labels` = #{dataAsset.labels}");
            SET("`detail` = #{dataAsset.detail}");
            SET("`update_time` = #{dataAsset.updateTime}");
            WHERE("`id` = #{dataAsset.id}");
        }}.toString();
    }

    public String queryById(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("id = #{id}");
        }}.toString();
    }

    public String queryRuleById(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("a.id, a.asset_id, b.name, b.state, a.destroy_method, a.destroy_period, a.create_time, a.update_time, c.user_name");
            FROM(RULE_TABLE_NAME + " a");
            INNER_JOIN(TABLE_NAME + " b on a.asset_id = b.id");
            INNER_JOIN(USER_TABLE_NAME + " c on a.create_user=c.user_name");
            WHERE("a.id = #{id}");
        }}.toString();
    }

    public String getDataAssetListPaging(Map<String, Object> parameter) {
        return dealListSql(parameter, "*");
    }

    public String getDataAssetRuleListPaging(Map<String, Object> parameter) {
        return dealRuleListSql(parameter, "a.id, b.name, b.state, a.destroy_method, " +
                "a.destroy_period, a.create_time, a.update_time, c.user_name");
    }

    public String getDataAssetTotal(Map<String, Object> parameter) {
        return dealListSql(parameter, "count(*)");
    }

    public String getDataAssetRuleTotal(Map<String, Object> parameter) {
        return dealRuleListSql(parameter, "count(*)");
    }

    /**
     * query label name by id
     */
    public String queryLabelName(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("name");
            FROM(LABEL_TABLE_NAME);
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * query state name by id
     */
    public String queryStateName(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("name");
            FROM(STATE_TABLE_NAME);
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * query method name by id
     */
    public String queryMethodName(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("name");
            FROM(DESTROY_METHOD_TABLE_NAME);
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * get all data asset owner
     */
    public String getUser(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("distinct b.user_name as name, a.owner as value");
            FROM(TABLE_NAME + " a");
            INNER_JOIN(USER_TABLE_NAME + " b on a.owner = b.id");
        }}.toString();
    }

    /**
     * get data asset list
     */
    public String getDataAssetList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("name, id as value");
            FROM(TABLE_NAME );
        }}.toString();
    }

    public String insertRule(Map<String, Object> parameter) {
        return new SQL() {{
            INSERT_INTO(RULE_TABLE_NAME);
            VALUES("`asset_id`", "#{rule.assetId}");
            VALUES("`destroy_method`", "#{rule.destroyMethod}");
            VALUES("`destroy_period`", "#{rule.destroyPeriod}");
            VALUES("`create_time`", "#{rule.createTime}");
            VALUES("`update_time`", "#{rule.updateTime}");
            VALUES("`create_user`", "#{rule.owner}");
        }}.toString();
    }

    public String updateRule(Map<String, Object> parameter) {
        return new SQL() {{
            UPDATE(RULE_TABLE_NAME);
            SET("`destroy_method` = #{rule.destroyMethod}");
            SET("`destroy_period` = #{rule.destroyPeriod}");
            SET("`update_time` = #{rule.updateTime}");
            SET("`asset_id` = #{rule.assetId}");
            WHERE("`id` = #{rule.id}");
        }}.toString();
    }

    public String deleteRule(Map<String, Object> parameter) {
        return new SQL() {{
            DELETE_FROM(RULE_TABLE_NAME);
            WHERE("`id` = #{id}");
        }}.toString();
    }

    private String dealListSql(Map<String, Object> parameter, String select) {
        return new SQL() {{
            SELECT(select);
            FROM(TABLE_NAME );
            Object type = parameter.get("type");
            if(type != null && StringUtils.isNotEmpty(type.toString())&& !type.toString().equals("all")) {
                WHERE(" type = '" + type + "'");
            }
            Object labels = parameter.get("labels");
            if(labels != null && StringUtils.isNotEmpty(labels.toString()) && !labels.toString().equals("[]")){
                String[] labelArray = labels.toString().replace("[", "").replace("]", "").split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < labelArray.length; i ++) {
                    stringBuilder.append(" FIND_IN_SET('" + labelArray[i] + "', labels)");
                    if(i != (labelArray.length - 1))stringBuilder.append( " or ");
                }
                WHERE(stringBuilder.toString());
            }
            Object dateRange = parameter.get("dateRange");
            if(dateRange != null && StringUtils.isNotEmpty(dateRange.toString())){
                String startDate = dateRange.toString().split(",")[0].replace("[", "").trim();
                String endDate = dateRange.toString().split(",")[1].replace("]", "").trim();
                WHERE(" create_time >= " + startDate);
                WHERE(" create_time <= " + endDate);
            }
            if(select.equals("*")){
                ORDER_BY("update_time desc limit #{offset},#{pageSize} ");
            }
        }}.toString();
    }

    private String dealRuleListSql(Map<String, Object> parameter, String select) {
        return new SQL() {{
            SELECT(select);
            FROM(RULE_TABLE_NAME + " a");
            INNER_JOIN(TABLE_NAME + " b on a.asset_id = b.id");
            INNER_JOIN(USER_TABLE_NAME + " c on a.create_user=c.user_name");
            Object searchVal = parameter.get("name");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " b.name like concat('%', #{name}, '%') ");
            }

            Object state = parameter.get("state");
            if(state != null && StringUtils.isNotEmpty(state.toString()) && !state.toString().equals("0")) {
                WHERE("b.state = " + state);
            }

            Object userId = parameter.get("userId");
            if(userId != null && StringUtils.isNotEmpty(userId.toString()) && !userId.toString().equals("0")) {
                WHERE("c.id = " + userId.toString());
            }

            Object dateRange = parameter.get("dateRange");
            if(dateRange != null && StringUtils.isNotEmpty(dateRange.toString())){
                String startDate = dateRange.toString().split(",")[0].replace("[", "").trim();
                String endDate = dateRange.toString().split(",")[1].replace("]", "").trim();
                WHERE(" a.create_time >= " + startDate);
                WHERE(" a.create_time <= " + endDate);
            }
            if(select.length() > 10){
                ORDER_BY("a.update_time desc limit #{offset},#{pageSize} ");
            }
        }}.toString();
    }
}
