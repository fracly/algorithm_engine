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

import cn.escheduler.common.enums.*;
import cn.escheduler.dao.model.DefinitionGroupByUser;
import cn.escheduler.dao.model.ProcessDefinition;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * process definition mapper
 */
public interface ProcessDefinitionMapper {

    /**
     * insert process define
     * @param processDefinition
     * @return
     */
    @InsertProvider(type = ProcessDefinitionMapperProvider.class, method = "insert")
    @Options(useGeneratedKeys = true,keyProperty = "processDefinition.id")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "processDefinition.id", before = false, resultType = int.class)
    int insert(@Param("processDefinition") ProcessDefinition processDefinition);


    /**
     * delete process define
     * @param processDefinitionId
     * @return
     */
    @DeleteProvider(type = ProcessDefinitionMapperProvider.class, method = "delete")
    int delete(@Param("processDefinitionId") int processDefinitionId);


    /**
     * update process define
     *
     * @param processDefinition
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "update")
    int update(@Param("processDefinition") ProcessDefinition processDefinition);

    /**
     * update release state
     * @param processDefinitionId
     * @param releaseState
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "updateProcessDefinitionReleaseState")
    int updateProcessDefinitionReleaseState(@Param("processDefinitionId") int processDefinitionId,
                                            @Param("releaseState") ReleaseState releaseState);


    /**
     * query definition by id
     * @param processDefinitionId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processDefinitionJson", column = "process_definition_json", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "globalParams", column = "global_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "locations", column = "locations", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "connects", column = "connects", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "receivers", column = "receivers", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "receiversCc", column = "receivers_cc", javaType = String.class, jdbcType = JdbcType.VARCHAR)

    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryByDefineId")
    ProcessDefinition queryByDefineId(@Param("processDefinitionId") int processDefinitionId);

    /**
     * query process definition by project id and name
     * @param projectId
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processDefinitionJson", column = "process_definition_json", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "locations", column = "locations", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "connects", column = "connects", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryByDefineName")
    ProcessDefinition queryByDefineName(@Param("projectId") int projectId,
                                        @Param("processDefinitionName") String name);


    /**
     * count definition number
     * @param projectId
     * @param userId
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "countDefineNumber")
    Integer countDefineNumber(@Param("projectId") int projectId,
                              @Param("userId") int userId,
                              @Param("searchVal") String searchVal
    );

    /**
     * count definition number
     * @param projectId
     * @param userId
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "countDefineNumber2")
    Integer countDefineNumber2(@Param("projectId") String projectId,
                              @Param("userId") int userId,
                              @Param("searchVal") String searchVal
    );


    /**
     * query all definition list
     * @param projectId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryAllDefinitionList")
    List<ProcessDefinition> queryAllDefinitionList(@Param("projectId") int projectId);

    /**
     * query definition list paging
     * @param projectId
     * @param searchVal
     * @param userId
     * @param offset
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "scheduleReleaseState", column = "schedule_release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryDefineListPaging")
    List<ProcessDefinition> queryDefineListPaging(@Param("projectId") int projectId,
                                                  @Param("searchVal") String searchVal,
                                                  @Param("userId") Integer userId,
                                                  @Param("offset") int offset,
                                                  @Param("pageSize") int pageSize);


    /**
     * query definition list paging
     * @param projectId
     * @param searchVal
     * @param userId
     * @param offset
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "scheduleReleaseState", column = "schedule_release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryDefineListPaging2")
    List<ProcessDefinition> queryDefineListPaging2(@Param("projectId") String projectId,
                                                  @Param("searchVal") String searchVal,
                                                  @Param("userId") Integer userId,
                                                  @Param("offset") int offset,
                                                  @Param("pageSize") int pageSize);




    /**
     * query definition list by define id list
     * @param ids
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "releaseState", column = "release_state",  typeHandler = EnumOrdinalTypeHandler.class, javaType = ReleaseState.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "processDefinitionJson", column = "process_definition_json", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "flag", column = "flag", typeHandler = EnumOrdinalTypeHandler.class, javaType = Flag.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "locations", column = "locations", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "connects", column = "connects", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "queryDefinitionListByIdList")
    List<ProcessDefinition> queryDefinitionListByIdList(@Param("ids") List<String> ids);

    /**
     * count definition number group by users
     * @param userId
     * @param userType
     * @param projectId
     * @return
     */
    @Results(value = {
            @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "count", column = "count", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "countDefinitionGroupByUser")
    List<DefinitionGroupByUser> countDefinitionGroupByUser(
            @Param("userId") Integer userId,
            @Param("userType") UserType userType,
            @Param("projectId") Integer projectId);

    /**
     * update receivers and cc by definition id
     * @param receivers
     * @param receiversCc
     * @param processDefinitionId
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "updateReceiversAndCcById")
    int updateReceiversAndCcById(@Param("receivers") String receivers,
                                 @Param("receiversCc") String receiversCc,
                                 @Param("processDefinitionId") int processDefinitionId);


    /**
     * get definition id
     * @param projectName
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)

    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "getDefineId")
    int getDefineId(@Param("projectName") String projectName,
                    @Param("name") String name);

    /**
     * get definition id
     * @param projectName
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)

    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "getDefineIdNormal")
    int getDefineIdNormal(@Param("projectName") String projectName,
                    @Param("name") String name);


    /**
     * get definition id
     * @param processDefinitionId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)

    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "getMaxInstanceId")
    String getMaxInstanceId(@Param("processDefinitionId")int processDefinitionId);


    /**
     * getWorkGroupId
     * @param definitionId
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "definitionId", column = "definition_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "workerGroupId", column = "group_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "failureStrategy", column = "failure_strategy", javaType = FailureStrategy.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "warningType", column = "warning_type", javaType = WarningType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "processInstancePriority", column = "process_instance_priority", javaType = Priority.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "execType", column = "exec_type", javaType = CommandType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "scheduleTime", column = "schedule_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "runMode", column = "run_mode", javaType = RunMode.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "warningGroupId", column = "warning_group_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "getWorkId")
    Map getWorkId(@Param("definitionId")int definitionId);


    /**
     * updateWorkId
     * @param definitionId
     * @param groupId
     * @param failureStrategy
     * @param warningType
     * @param processInstancePriority
     * @param warningGroupId
     * @param execType
     * @param runMode
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "updateWorkId")
    int updateWorkId(@Param("definitionId") int definitionId,
                     @Param("groupId") int groupId,
                     @Param("failureStrategy") FailureStrategy failureStrategy,
                     @Param("warningType") WarningType warningType,
                     @Param("processInstancePriority") Priority processInstancePriority,
                     @Param("warningGroupId") Integer warningGroupId,
                     @Param("execType") CommandType execType,
                     @Param("scheduleTime") String scheduleTime,
                     @Param("runMode") RunMode runMode);

    /**
     * insertWorkId
     * @param definitionId
     * @param groupId
     * @param failureStrategy
     * @param warningType
     * @param processInstancePriority
     * @param warningGroupId
     * @param execType
     * @param runMode
     * @return
     */
    @InsertProvider(type = ProcessDefinitionMapperProvider.class, method = "insertWorkId")
    int insertWorkId(@Param("definitionId") int definitionId,
                     @Param("groupId") int groupId,
                     @Param("failureStrategy") FailureStrategy failureStrategy,
                     @Param("warningType") WarningType warningType,
                     @Param("processInstancePriority") Priority processInstancePriority,
                     @Param("warningGroupId") Integer warningGroupId,
                     @Param("execType") CommandType execType,
                     @Param("scheduleTime") String scheduleTime,
                     @Param("runMode") RunMode runMode);

    /**
     * count definition number
     */
    @SelectProvider(type = ProcessDefinitionMapperProvider.class, method = "countProcessDefinition")
    Integer countProcessDefinition();




    /**
     * update process define
     *
     * @param processDefinition
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "edit")
    int edit(@Param("processDefinition") ProcessDefinition processDefinition);


    /**
     * update release state
     * @param processDefinitionId
     * @param flag
     * @return
     */
    @UpdateProvider(type = ProcessDefinitionMapperProvider.class, method = "updateProcessDefinitionFlag")
    int updateProcessDefinitionFlag(@Param("processDefinitionId") int processDefinitionId,
                                            @Param("flag") int  flag);

}


