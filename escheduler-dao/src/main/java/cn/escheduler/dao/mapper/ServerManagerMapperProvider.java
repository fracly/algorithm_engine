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


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * ServerManagerMapperProvider
 */
public class ServerManagerMapperProvider {

    private static final String TABLE_NAME = "t_ms_atomic_service";
    private static final String TABLE_PARAM = "t_ms_service_params";


    /**
     * countServerList
     * @param parameter
     * @return
     */
    public String countServerList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" count(0) ");
            FROM(TABLE_NAME);
            WHERE( " create_user=#{userName}");

            Object status = parameter.get("status");
            if(status != null && StringUtils.isNotEmpty(status.toString())){
                WHERE( " status = #{status} ");
            }

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " service_name like concat('%', #{searchVal}, '%') ");
            }
            List<String> type = (List<String>) parameter.get("type");
            if(type != null&& type.size()>0){
                WHERE("service_type in ('" + String.join(",", type) + "')");
            }
        }}.toString();
    }

    /**
     * countServerList2
     * @param parameter
     * @return
     */
    public String countServerList2(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" count(0) ");
            FROM(TABLE_NAME);
            Object status = parameter.get("status");
            if(status != null && StringUtils.isNotEmpty(status.toString())){
                WHERE( " status = #{status} ");
            }

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " service_name like concat('%', #{searchVal}, '%') ");
            }
            List<String> type = (List<String>) parameter.get("type");
            if(type != null&& type.size()>0){
                WHERE("service_type in ('" + String.join(",", type) + "')");
            }
        }}.toString();
    }
    /**
     * getServerList2
     * @param parameter
     * @return
     */
    public String getServerList2(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" * ");
            FROM(TABLE_NAME);

            Object status = parameter.get("status");
            if(status != null && StringUtils.isNotEmpty(status.toString())){
                WHERE( " status = #{status} ");
            }

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " service_name like concat('%', #{searchVal}, '%') ");
            }
            List<String> type = (List<String>) parameter.get("type");
            if(type != null&& type.size()>0){
                WHERE("service_type in ('" + String.join(",", type) + "')");
            }
            ORDER_BY(" create_time desc,status desc limit #{start},#{pageSize} ");
        }}.toString();
    }

    /**
     * getServerList
     * @param parameter
     * @return
     */
    public String getServerList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" * ");
            FROM(TABLE_NAME);
            WHERE( " create_user=#{userName}");

            Object status = parameter.get("status");
            if(status != null && StringUtils.isNotEmpty(status.toString())){
                WHERE( " status = #{status} ");
            }

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " service_name like concat('%', #{searchVal}, '%') ");
            }
            List<String> type = (List<String>) parameter.get("type");
            if(type != null&& type.size()>0){
                WHERE("service_type in ('" + String.join(",", type) + "')");
            }
            ORDER_BY(" create_time desc,status desc limit #{start},#{pageSize} ");
        }}.toString();
    }
    /**
     * insert process instance
     *
     * @param parameter
     * @return
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_NAME);
                VALUES("`service_name`","#{serviceName}");
                VALUES("`service_code`", "#{serviceCode}");
                VALUES("`develop_mode`", "#{developMode}");
                VALUES("`service_type`", "#{serviceType}");
                VALUES("`datasource_type`", "#{datasourceType}");
                VALUES("`datasource_name`", "#{datasourceName}");
                VALUES("`service_sql`","#{serviceSql}");
                VALUES("`status`", "#{status}");
                VALUES("`create_user`", "#{createUser}");
                VALUES("`create_time`", "#{createTime}");
                VALUES("`des`", "#{des}");
                VALUES("`datasource_Id`","#{datasourceId}");
                VALUES("`service_params`","#{serviceParams}");
                VALUES("`table_name`","#{tableName}");
                VALUES("`in_param`","#{inParam}");
                VALUES("`out_param`","#{outParam}");
            }
        }.toString();
    }
    /**
     * update
     * @param parameter
     * @return
     */
    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);

                SET("`service_name`=#{serviceName}");
                SET("`service_code`=#{serviceCode}");
                SET("`develop_mode`=#{developMode}");
                SET("`service_type`=#{serviceType}");
                SET("`datasource_type`=#{datasourceType}");
                SET("`datasource_name`=#{datasourceName}");
                SET("`service_sql`=#{serviceSql}");
                SET("`status`=#{status}");
                SET("`des`=#{des}");
                SET("`datasource_Id`=#{datasourceId}");
                SET("`service_params`=#{serviceParams}");
                SET("`table_name`=#{tableName}");
                SET("`in_param`=#{inParam}");
                SET("`out_param`=#{outParam}");
                WHERE("`service_id`=#{serviceId}");

            }
        }.toString();
    }

    /**
     * update
     * @param parameter
     * @return
     */
    public String updateServiceParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);

                SET("`service_params`=#{param}");

                WHERE("`service_id`=#{id}");
            }
        }.toString();
    }


    /**
     * getInfo
     * @param parameter
     * @return
     */
    public String getInfo(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT(" * ");
                FROM(TABLE_NAME);
                WHERE("`service_id`=#{serviceId}");
            }
        }.toString();
    }

    /**
     * delete
     * @param parameter
     * @return
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("`service_id`=#{serviceId}");
            }
        }.toString();
    }

    /**
     * getReportCode
     *
     * @param parameter
     * @return
     */
    public String getServiceCode(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "service_code ");
                FROM(TABLE_NAME);
                if(parameter.get("id") != null && (int)parameter.get("id") != 0){
                    WHERE( "service_id != #{id} ");
                }

            }
        }.toString();
    }

    /**
     * getReportCode
     *
     * @param parameter
     * @return
     */
    public String getIdByCode(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "service_id ");
                FROM(TABLE_NAME);
                WHERE("`service_code`=#{serviceCode}");

            }
        }.toString();
    }

    /**
     * 更新流程实例
     *
     * @param parameter
     * @return
     */
    public String onLine(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);

                SET("`status`=#{status}");
                WHERE("`service_id`=#{serviceId}");

            }
        }.toString();
    }




    /**
     * insert process instance
     *
     * @param parameter
     * @return
     */
    public String insertParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_PARAM);
                VALUES("`param_name`","#{param_name}");
                VALUES("`param_type`", "#{param_type}");
                VALUES("`param_des`", "#{param_des}");
                VALUES("`type`", "#{type}");
                VALUES("`param_join`", "#{param_join}");
                VALUES("`param_exam`","#{param_exam}");
                VALUES("`service_id`", "#{service_id}");
                VALUES("`param_variable`", "#{param_variable}");
                VALUES("`is_required`","#{isRequired}");
            }
        }.toString();
    }

    /**
     * update
     * @param parameter
     * @return
     */
    public String updateParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_PARAM);
                SET("`param_name`=#{param_name}");
                SET("`param_type`=#{param_type}");
                SET("`param_des`=#{param_des}");
                SET("`type`=#{type}");
                SET("`param_join`=#{param_join}");
                SET("`param_exam`=#{param_exam}");
                SET("`service_id`=#{service_id}");
                SET("`param_variable`=#{param_variable}");
                WHERE("`id`=#{id}");
            }
        }.toString();
    }

    /**
     * igetParam
     *
     * @param parameter
     * @return
     */
    public String getParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT(" * ");
                FROM(TABLE_PARAM );
                WHERE("`service_id`=#{serviceId}");
                Object type = parameter.get("type");
                if(type != null && StringUtils.isNotEmpty(type.toString())){
                    WHERE( " type = #{type} ");
                }
            }
        }.toString();
    }

    /**
     * igetParam
     *
     * @param parameter
     * @return
     */
    public String deleteParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_PARAM);
                WHERE("`service_id`=#{serviceId}");
            }
        }.toString();
    }


}
