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
 * project mapper provider
 */
public class ProjectMapperProvider {

    private static final String TABLE_NAME = "t_escheduler_project";
    private static final String RELEATION_TABLE_NAME = "t_escheduler_relation_project_user";
    private static final String SCHEDULE_TABLE_NAME = "t_escheduler_schedules";
    private static final String PROCESS_DEFINE_TABLE_NAME = "t_escheduler_process_definition";

    /**
     * insert project
     *
     * @param parameter
     * @return
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_NAME);
                VALUES("`user_id`", "#{project.userId}");
                VALUES("`name`", "#{project.name}");
                VALUES("`desc`", "#{project.desc}");
            }
        }.toString();
    }

    /**
     * delete project
     *
     * @param parameter
     * @return
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("`id`=#{projectId}");
            }
        }.toString();
    }

    /**
     * update project
     *
     * @param parameter
     * @return
     */
    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);

                SET("`user_id`=#{project.userId}");
                SET("`name`=#{project.name}");
                SET("`desc`=#{project.desc}");
                SET("`update_time`=#{project.updateTime}");
                WHERE("`id`=#{project.id}");
            }
        }.toString();
    }

    /**
     * query project by id
     *
     * @param parameter
     * @return
     */
    public String queryById(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.user_id");
            SELECT("u.user_name");
            SELECT("p.*");

            FROM(TABLE_NAME + " p");

            JOIN("t_escheduler_user u on p.user_id = u.id");

            WHERE("p.id = #{projectId}");
        }}.toString();
    }

    /**
     * query data govern project by login user
     *
     * @param parameter
     * @return
     */
    public String queryDataGovernProjectByLoginUser(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.id");
            FROM(TABLE_NAME + " p");
            WHERE("p.user_id = #{userId} and p.type = '1'");
        }}.toString();
    }


    /**
     * query project by name
     *
     * @param parameter
     * @return
     */
    public String queryByName(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.user_id");
            SELECT("u.user_name");
            SELECT("p.*");

            FROM(TABLE_NAME + " p");

            JOIN("t_escheduler_user u on p.user_id = u.id");

            WHERE("p.name = #{name}");
            WHERE("p.flag = 1");
        }}.toString();
    }

    /**
     * query project by name
     *
     * @param parameter
     * @return
     */
    public String queryByNameAndUser(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.user_id");
            SELECT("u.user_name");
            SELECT("p.*");

            FROM(TABLE_NAME + " p");

            JOIN("t_escheduler_user u on p.user_id = u.id");

            WHERE("p.name = #{name}");
            WHERE("p.flag = 1");
            WHERE("p.user_id = #{userId}");
        }}.toString();
    }

    /**
     * count project by user id and search value
     * @param parameter
     * @return
     */
    public String countProjects(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(0)");

            FROM(TABLE_NAME + " p");
            WHERE("p.id in " +
                    "(select project_id from "+ RELEATION_TABLE_NAME+" where user_id=#{userId} " +
                    "union select id as project_id  from "+ TABLE_NAME+" where user_id=#{userId})");

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " p.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE("p.flag = 1");
        }}.toString();
    }

    /**
     * count data govern project by login user
     * @param parameter
     * @return
     */
    public String countDataGovernProjects(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(0)");
            FROM(TABLE_NAME + " p");
            WHERE("p.user_id=#{userId} and p.flag = 1 and p.type = 1");
        }}.toString();
    }

    /**
     * query project list paging
     * @param parameter
     * @return
     */
    public String queryProjectListPaging(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.*");
            SELECT("u.user_name as user_name");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition AS def WHERE def.project_id = p.id) AS def_count");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition def, t_escheduler_process_instance inst WHERE def.id = inst.process_definition_id AND def.project_id = p.id AND inst.state=1 ) as inst_running_count");
            FROM(TABLE_NAME + " p");
            JOIN("t_escheduler_user u on u.id=p.user_id");
            WHERE("p.id in " +
                    "(select project_id from "+ RELEATION_TABLE_NAME+" where user_id=#{userId} " +
                    "union select id as project_id  from "+ TABLE_NAME+" where user_id=#{userId})");

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " p.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE(" p.flag = 1 and p.type is null");
            ORDER_BY("p.create_time desc limit #{offset},#{pageSize} ");
        }}.toString();
    }

    /**
     * count all projects
     * @return
     */
    public String countAllProjects(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(0)");

            FROM(TABLE_NAME );

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " name like concat('%', #{searchVal}, '%') ");
            }
            WHERE("flag = 1 and type is null ");
        }}.toString();
    }

    /**
     * query all project list paging
     * @return
     */
    public String queryAllProjectListPaging(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.*");
            SELECT("u.user_name as user_name");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition AS def WHERE def.project_id = p.id) AS def_count");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition def, t_escheduler_process_instance inst WHERE def.id = inst.process_definition_id AND def.project_id = p.id AND inst.state=1 ) as inst_running_count");
            FROM(TABLE_NAME + " p");
            JOIN("t_escheduler_user u on p.user_id = u.id");

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " p.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE(" p.flag = 1 and p.type is null");
            ORDER_BY("p.create_time desc limit #{offset},#{pageSize} ");
        }}.toString();
    }

    public String queryAllProjectList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.*");
            SELECT("u.user_name as user_name");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition AS def WHERE def.project_id = p.id) AS def_count");
            SELECT("(SELECT COUNT(*) FROM t_escheduler_process_definition def, t_escheduler_process_instance inst WHERE def.id = inst.process_definition_id AND def.project_id = p.id AND inst.state=1 ) as inst_running_count");
            FROM(TABLE_NAME + " p");
            JOIN("t_escheduler_user u on p.user_id = u.id");

            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " p.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE(" p.flag = 1 and p.type is null ");
            Object userId = parameter.get("userId");
            if(userId != null && Integer.parseInt(parameter.get("userId").toString()) != 0) {
                WHERE( " p.user_id = #{userId}");
            }
            ORDER_BY("p.create_time desc ");
        }}.toString();
    }

    /**
     * authed project to user
     * @param parameter
     * @return
     */
    public String authedProject(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("p.*");
            FROM(TABLE_NAME + " p,t_escheduler_relation_project_user rel");
            WHERE(" p.id = rel.project_id AND p.flag = 1 AND rel.user_id = #{userId}");
        }}.toString();
    }

    /**
     * query project except user
     * @param parameter
     * @return
     */
    public String queryProjectExceptUserId(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("flag = 1 AND user_id <> #{userId}");
        }}.toString();
    }

    /**
     * query project schedule
     */
    public String queryProjectSchedule(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("a.id, a.process_definition_id, a.start_time, a.end_time, a.crontab, a.failure_strategy, a.user_id, a.release_state, a.warning_type" +
                    ", a.warning_group_id, a.process_instance_priority, a.worker_group_id, a.create_time, a.update_time, b.name, c.name as project_name, c.id as project_id");
            FROM(SCHEDULE_TABLE_NAME + " a");
            INNER_JOIN(PROCESS_DEFINE_TABLE_NAME + " b on a.process_definition_id = b.id");
            INNER_JOIN(TABLE_NAME + " c on b.project_id = c.id");
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
                WHERE("b.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE(" c.user_id = #{userId}");
            ORDER_BY("a.create_time desc limit #{offset}, #{pageSize}");
        }}.toString();
    }

    public String countProjectSchedule(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(a.id) as total");
            FROM(SCHEDULE_TABLE_NAME + " a");
            INNER_JOIN(PROCESS_DEFINE_TABLE_NAME + " b on a.process_definition_id = b.id");
            INNER_JOIN(TABLE_NAME + " c on b.project_id = c.id");
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
                WHERE("b.name like concat('%', #{searchVal}, '%') ");
            }
            WHERE(" c.user_id = #{userId}");
        }}.toString();
    }
}
