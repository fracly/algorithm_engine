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

import cn.escheduler.common.enums.FailureStrategy;
import cn.escheduler.common.enums.Priority;
import cn.escheduler.common.enums.ReleaseState;
import cn.escheduler.common.enums.WarningType;
import cn.escheduler.dao.model.Project;
import cn.escheduler.dao.model.Schedule;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

/**
 * project mapper
 */
public interface ProjectMapper {

    /**
     * insert project
     * @param project
     * @return
     */
    @InsertProvider(type = ProjectMapperProvider.class, method = "insert")
    @Options(useGeneratedKeys = true,keyProperty = "project.id")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "project.id", before = false, resultType = int.class)
    int insert(@Param("project") Project project);


    /**
     * delete project
     * @param projectId
     * @return
     */
    @DeleteProvider(type = ProjectMapperProvider.class, method = "delete")
    int delete(@Param("projectId") int projectId);


    /**
     * update project
     *
     * @param project
     * @return
     */
    @UpdateProvider(type = ProjectMapperProvider.class, method = "update")
    int update(@Param("project") Project project);


    /**
     * query project by name
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryByName")
    Project queryByName(@Param("name") String name);

    /**
     * queryByNameAndUser
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryByNameAndUser")
    Project queryByNameAndUser(@Param("name") String name,@Param("userId") int userId);



    /**
     * query project by id
     * @param projectId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryById")
    Project queryById(@Param("projectId") Integer projectId);

    /**
     * query data govern project by login user
     * @param userId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryDataGovernProjectByLoginUser")
    Project queryDataGovernProjectByLoginUser(@Param("userId") Integer userId);

    /**
     * count project by user id and search value
     * @param userId
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ProjectMapperProvider.class, method = "countProjects")
    Integer countProjects(@Param("userId") Integer userId,
                          @Param("searchVal") String searchVal
                          );

    /**
     * count data govern project by login user
     * @param userId
     * @return
     */
    @SelectProvider(type = ProjectMapperProvider.class, method = "countDataGovernProjects")
    Integer countDataGovernProjects(@Param("userId") Integer userId);

    /**
     * query project list paging
     * @param userId
     * @param offset
     * @param pageSize
     * @param searchVal
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "defCount", column = "def_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "instRunningCount", column = "inst_running_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryProjectListPaging")
    List<Project> queryProjectListPaging(@Param("userId") Integer userId,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize,
                                         @Param("searchVal") String searchVal);

    /**
     * count all projects
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ProjectMapperProvider.class, method = "countAllProjects")
    Integer countAllProjects(@Param("searchVal") String searchVal);

    /**
     * query all project list paging
     * @param offset
     * @param pageSize
     * @param searchVal
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "defCount", column = "def_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "instRunningCount", column = "inst_running_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryAllProjectListPaging")
    List<Project> queryAllProjectListPaging(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("searchVal") String searchVal);

    /**
     * query all project list
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "defCount", column = "def_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "instRunningCount", column = "inst_running_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryAllProjectList")
    List<Project> queryAllProjectList(@Param("userId") Integer userId,
            @Param("searchVal") String searchVal);

    /**
     * authed project to user
     * @param userId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "authedProject")
    List<Project> authedProject(@Param("userId") Integer userId);

    /**
     * query project except user
     * @param userId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryProjectExceptUserId")
    List<Project> queryProjectExceptUserId(@Param("userId") Integer userId);

    @Results(value={
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processDefinitionId", column = "process_definition_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processDefinitionName", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "endTime", column = "end_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "failureStrategy", column = "failure_strategy", typeHandler = EnumOrdinalTypeHandler.class, javaType = FailureStrategy.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state", typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "warningGroupId", column = "warning_group_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processInstancePriority", column = "process_instance_priority", javaType = Priority.class, typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "workerGroupId", column = "worker_group_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
    })
    @SelectProvider(type = ProjectMapperProvider.class, method = "queryProjectSchedule")
    List<Schedule> queryProjectSchedule(@Param("userId") Integer userId, @Param("searchVal") String searchVal, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @SelectProvider(type = ProjectMapperProvider.class, method = "countProjectSchedule")
    Integer countProjectSchedule(@Param("userId") Integer userId, @Param("searchVal") String searchVal);

}
