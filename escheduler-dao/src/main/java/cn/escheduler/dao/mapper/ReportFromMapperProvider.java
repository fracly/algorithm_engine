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
import java.util.Map;

/**
 * process instance mapper provider
 */
public class ReportFromMapperProvider {

    private static final String TABLE_NAME = "report";



    /**
     * queryReportFromList
     * @param parameter
     * @return
     */
    public String queryReportFromList(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "report_id,report_name,report_code,create_user,create_time,status");
                FROM(TABLE_NAME);
                WHERE("create_user = #{createUser}" );

                Object projectId = parameter.get("status");
                if(projectId != null && StringUtils.isNotEmpty(projectId.toString())){
                    WHERE( " status = #{status} ");
                }
                Object searchVal = parameter.get("reportName");
                if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                    WHERE( " report_name like concat('%', #{reportName}, '%') ");
                }
                Object roleId = parameter.get("roleId");
                if(roleId != null && StringUtils.isNotEmpty(roleId.toString())){
                    WHERE( "INSTR(role_id,#{roleId})>0 ");
                }
                ORDER_BY(" create_time desc limit #{offset},#{pageSize} ");
            }
        }.toString();
    }

    /**
     * queryReportFromList
     * @param parameter
     * @return
     */
    public String queryReportFromInfo(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "report_id,report_name,report_code,create_user,create_time,status");
                FROM(TABLE_NAME);

                Object groupId = parameter.get("groupId");
                if(groupId != null && StringUtils.isNotEmpty(groupId.toString())){
                    WHERE("group_id = #{groupId}" );
                }
                Object projectId = parameter.get("status");
                if(projectId != null && StringUtils.isNotEmpty(projectId.toString())){
                    WHERE( " status = #{status} ");
                }
                Object searchVal = parameter.get("reportName");
                if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                    WHERE( " report_name like concat('%', #{reportName}, '%') ");
                }
                Object roleId = parameter.get("roleId");
                if(roleId != null && StringUtils.isNotEmpty(roleId.toString())){
                    WHERE( "INSTR(role_id,#{roleId})>0 ");
                }
                ORDER_BY(" create_time desc limit #{offset},#{pageSize} ");
            }
        }.toString();
    }


    /**
     * queryReportFromList
     * @param parameter
     * @return
     */
    public String list(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "report_id,report_name,report_code,create_user,create_time,status");
                FROM(TABLE_NAME);
                Object status = parameter.get("status");
                if(status != null && StringUtils.isNotEmpty(status.toString())){
                    WHERE( " status = #{status} ");
                }
                Object roleId = parameter.get("roleId");
                if(roleId != null && StringUtils.isNotEmpty(roleId.toString())){
                    WHERE( "INSTR(role_id,#{roleId})>0 ");
                }
                ORDER_BY(" create_time desc  ");
            }
        }.toString();
    }

    /**
     * queryReportFromList
     * @param parameter
     * @return
     */
    public String countReportFromList(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "count(1)");
                FROM(TABLE_NAME);
                WHERE("create_user = #{createUser}" );

                Object status = parameter.get("status");
                if(status != null && StringUtils.isNotEmpty(status.toString())){
                    WHERE( " status = #{status} ");
                }
                Object reportName = parameter.get("reportName");
                if(reportName != null && StringUtils.isNotEmpty(reportName.toString())){
                    WHERE( " report_name like concat('%', #{reportName}, '%') ");
                }
                Object roleId = parameter.get("roleId");
                if(roleId != null && StringUtils.isNotEmpty(roleId.toString())){
                    WHERE( "INSTR(role_id,#{roleId})>0 ");
                }

            }
        }.toString();
    }

    /**
     * queryReportFromList
     * @param parameter
     * @return
     */
    public String countReportFromInfo(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "count(1)");
                FROM(TABLE_NAME);
                Object groupId = parameter.get("groupId");
                if(groupId != null && StringUtils.isNotEmpty(groupId.toString())){
                    WHERE("group_id = #{groupId}" );
                }
                Object status = parameter.get("status");
                if(status != null && StringUtils.isNotEmpty(status.toString())){
                    WHERE( " status = #{status} ");
                }
                Object reportName = parameter.get("reportName");
                if(reportName != null && StringUtils.isNotEmpty(reportName.toString())){
                    WHERE( " report_name like concat('%', #{reportName}, '%') ");
                }
                Object roleId = parameter.get("roleId");
                if(roleId != null && StringUtils.isNotEmpty(roleId.toString())){
                    WHERE( "INSTR(role_id,#{roleId})>0 ");
                }

            }
        }.toString();
    }


    /**
     * insert process instance
     *
     * @param parameter
     * @return
     */
    public String createGroup(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO("report_group");
                VALUES("`group_name`","#{groupName}");
                VALUES("`status`", "#{status}");
                VALUES("`desc`", "#{desc}");
                VALUES("`oper_no`", "#{userName}");
                VALUES("`oper_time`", "#{now}");
            }
        }.toString();
    }

    /**
     * 更新流程实例
     *
     * @param parameter
     * @return
     */
    public String updateGroup(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE("report_group");

                SET("`group_name`=#{groupName}");
                SET("`desc`=#{desc}");
                WHERE("`group_id`=#{id}");

            }
        }.toString();
    }

    public String deleteGroup(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM("report_group");
                WHERE("group_id=#{groupId}");
            }
        }.toString();
    }

    /**
     * delete process instance
     *
     * @param parameter
     * @return
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("report_id=#{reportId}");
            }
        }.toString();
    }



    /**
     * 更新流程实例
     *
     * @param parameter
     * @return
     */
    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);

                SET("`report_name`=#{reportName}");
                SET("`report_code`=#{reportCode}");
                WHERE("`report_id`=#{reportId}");

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
                WHERE("`report_id`=#{reportId}");

            }
        }.toString();
    }

    /**
     * 获取全部分组信息
     *
     * @param parameter
     * @return
     */
    public String getGroup(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "g.group_id,group_name,`desc`,oper_time as createTime,count(r.report_id) as num from report_group g " +
                        "LEFT JOIN  report r on g.group_id=r.group_id ");
                GROUP_BY("g.group_id");
                ORDER_BY("oper_time desc" );
            }
        }.toString();
    }
    /**
     * 获取全部分组信息
     *
     * @param parameter
     * @return
     */
    public String queryGroupList(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "g.group_id,group_name,`desc`,oper_time as createTime,count(r.report_id) as num from report_group g " +
                        "LEFT JOIN  report r on g.group_id=r.group_id ");
                if(!StringUtils.isEmpty(parameter.get("name").toString()) ){
                    WHERE( " group_name like concat('%', #{name}, '%') ");
                }
                GROUP_BY("g.group_id");
                ORDER_BY("oper_time desc limit #{offset},#{pageSize} " );
            }
        }.toString();
    }

    /**
     * getDict
     *
     * @param parameter
     * @return
     */
    public String getDict(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "dict_id,dict_name,dict_desc,dict_type,create_user,create_time,parent_id,dict_code  from dict where parent_id is null");
            }
        }.toString();
    }


    /**
     * getReportInfo
     *
     * @param parameter
     * @return
     */
    public String getReportInfo(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "report_id,create_user,create_time,report_name,report_code,report_type,template_path" +
                        ",datasource_type,datasource_id,datasource_name,report_sql,status,report_head,group_id," +
                        " role_id,role_name,report_head_mid,origin_file_name from report where report_id =#{id}");
            }
        }.toString();
    }


    /**
     * getParamById
     *
     * @param parameter
     * @return
     */
    public String getParamById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "param_name,param_desc,param_type,report_id,attribute_value from report_param where report_id =#{id}");
            }
        }.toString();
    }

    /**
     * getDict
     *
     * @param parameter
     * @return
     */
    public String getDictInfo(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "dict_id,dict_name,dict_desc,dict_type,create_user,create_time,parent_id,dict_code  from dict where parent_id =#{id}");
            }
        }.toString();
    }

    /**
     * getReportCode
     *
     * @param parameter
     * @return
     */
    public String getReportCode(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "report_code from report");
                if(parameter.get("id") != null && (int)parameter.get("id") != 0){
                    WHERE( "report_id != #{id} ");
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
                SELECT( "report_id from report where report_code=#{code}");
            }
        }.toString();
    }

    /**
     * createReport
     *
     * @param parameter
     * @return
     */
    public String createReport(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO("report");
                VALUES("`create_user`","#{userName}");
                VALUES("`create_time`", "#{now}");
                VALUES("`report_name`", "#{reportName}");
                VALUES("`report_code`", "#{reportCode}");
                VALUES("`report_type`", "#{reportType}");
                VALUES("`template_path`", "#{templatePath}");
                VALUES("`datasource_type`", "#{dataSourceType}");
                VALUES("`datasource_id`", "#{dataSourceId}");
                VALUES("`datasource_name`", "#{dataSourceName}");
                VALUES("`report_sql`", "#{sql}");
                VALUES("`status`", "#{status}");
                VALUES("`group_id`", "#{groupId}");
                VALUES("`role_id`", "#{roleId}");
                VALUES("`role_name`", "#{roleName}");
                VALUES("`origin_file_name`", "#{originFileName}");
            }
        }.toString();
    }


    /**
     * createReport
     *
     * @param parameter
     * @return
     */
    public String updateReport(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                SET("`create_user`=#{userName}");
                SET("`report_name`=#{reportName}");
                SET("`report_code`=#{reportCode}");
                SET("`report_type`=#{reportType}");
                SET("`template_path`=#{templatePath}");
                SET("`datasource_type`=#{dataSourceType}");
                SET("`datasource_id`=#{dataSourceId}");
                SET("`datasource_name`=#{dataSourceName}");
                SET("`report_sql`=#{sql}");
                SET("`status`=#{status}");
                SET("`group_id`=#{groupId}");
                SET("`role_id`=#{roleId}");
                SET("`role_name`=#{roleName}");
                SET("`origin_file_name`=#{originFileName}");
                WHERE("`report_id`=#{reportId}");
            }
        }.toString();
    }

    /**
     * insertParam
     *
     * @param parameter
     * @return
     */
    public String insertParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO("report_param");
                VALUES("`param_name`","#{name}");
                VALUES("`param_desc`", "#{desc}");
                VALUES("`param_type`", "#{type}");
                VALUES("`attribute_value`", "#{value}");
                VALUES("`report_id`", "#{reportId}");
            }
        }.toString();
    }

    /**
     * delete Param by Id
     *
     * @param parameter
     * @return
     */
    public String deleteParam(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM("report_param");
                WHERE("`report_id`=#{reportId}");
            }
        }.toString();
    }

    /**
     * updateParas
     *
     * @param parameter
     * @return
     */
    public String updateParas(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                SET("`report_head`=#{head}");
                SET("`report_head_mid`=#{headMid}");
                WHERE("`report_id`=#{id}");
            }
        }.toString();
    }



    /**
     * updateParas
     *
     * @param parameter
     * @return
     */
    public String addUseCountByCode(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                SET("`use_count`=#{count}");
                WHERE("`report_code`=#{code}");
            }
        }.toString();
    }

    /**
     * getReportCode
     *
     * @param parameter
     * @return
     */
    public String getUseCountByCode(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT( "IFNULL(use_count,0) as use_count from report where report_code=#{code}");
            }
        }.toString();
    }
}
