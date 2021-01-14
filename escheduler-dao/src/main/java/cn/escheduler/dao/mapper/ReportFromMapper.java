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

import cn.escheduler.dao.model.ReportFrom;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * process instance mapper
 */
public interface ReportFromMapper {

    /**
     * update process instance
     *
     * @param reportId
     * @param status
     * @return
     */
    @UpdateProvider(type = ReportFromMapperProvider.class, method = "onLine")
    int onLine(@Param("reportId") int reportId
            ,@Param("status") int status);

    /**
     * delete process instance
     * @param reportId
     * @return
     */
    @DeleteProvider(type = ReportFromMapperProvider.class, method = "delete")
    int delete(@Param("reportId") int reportId);


    /**
     * queryReportFromList
     * @param createUser
     * @param reportName
     * @param status
     * @param offset
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "reportName", column = "report_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "id", column = "report_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportCode", column = "report_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "queryReportFromList")
    List<ReportFrom> queryReportFromList(@Param("createUser") String createUser,
                                         @Param("reportName") String reportName,
                                         @Param("status") String status,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize,
                                         @Param("roleId") String roleId);

    /**
     * queryReportFromList
     * @param groupId
     * @param reportName
     * @param status
     * @param offset
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "reportName", column = "report_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "id", column = "report_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportCode", column = "report_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "queryReportFromInfo")
    List<ReportFrom> queryReportFromInfo(@Param("groupId") String groupId,
                                         @Param("reportName") String reportName,
                                         @Param("status") String status,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize,
                                         @Param("roleId") String roleId);


    /**
     * list
     * @return
     */
    @Results(value = {@Result(property = "reportName", column = "report_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "id", column = "report_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "reportCode", column = "report_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "list")
    List<ReportFrom> list(@Param("status") String status,
                          @Param("roleId") String roleId);


    /**
     * countReportFromList
     * @param createUser
     * @param reportName
     * @param status
     * @param offset
     * @param pageSize
     * @return
     */
    @SelectProvider(type = ReportFromMapperProvider.class, method = "countReportFromList")
    Integer countReportFromList(@Param("createUser") String createUser,
                                @Param("reportName") String reportName,
                                @Param("status") String status,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize,
                                @Param("roleId") String roleId);

    /**
     * countReportFromList
     * @param groupId
     * @param reportName
     * @param status
     * @param offset
     * @param pageSize
     * @return
     */
    @SelectProvider(type = ReportFromMapperProvider.class, method = "countReportFromInfo")
    Integer countReportFromInfo(@Param("groupId") String groupId,
                                @Param("reportName") String reportName,
                                @Param("status") String status,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize,
                                @Param("roleId") String roleId);




    /**
     * getGroup
     * @return
     */
    @Results(value = {@Result(property = "id", column = "group_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "group_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "createTime", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "num", column = "num", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getGroup")
    List<Map> getGroup();

    /**
     * getGroup
     * @return
     */
    @Results(value = {@Result(property = "id", column = "group_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "group_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "createTime", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "num", column = "num", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "queryGroupList")
    List<Map> queryGroupList(@Param("name") String name,
                             @Param("offset") int offset,
                             @Param("pageSize") int pageSize);

    /**
     * insert data source
     *
     * @return
     */
    @InsertProvider(type = ReportFromMapperProvider.class, method = "createGroup")
    int createGroup(@Param("userName") String userName,
                    @Param("groupName") String groupName,
                    @Param("status") String status,
                    @Param("desc") String desc,
                    @Param("now") Date now);


    /**
     * insert data source
     *
     * @return
     */
    @UpdateProvider(type = ReportFromMapperProvider.class, method = "updateGroup")
    int updateGroup(@Param("groupName") String groupName,
                    @Param("id") Integer id,
                    @Param("desc") String desc);

    @DeleteProvider(type = ReportFromMapperProvider.class, method = "deleteGroup")
    int deleteGroup(@Param("groupId") int groupId);

    /**
     * getDict
     * dict_id,dict_name,dict_desc,dict_tyoe,create_user,create_time,parent_id,dict_code
     * @return
     */
    @Results(value = {@Result(property = "dictId", column = "dict_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dictName", column = "dict_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dictDesc", column = "dict_desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dictType", column = "dict_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "parentId", column = "parent_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dictCode", column = "dict_code", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getDict")
    List<Map> getDict();

    /**
     * getDictInfo
     * dict_id,dict_name,dict_desc,dict_tyoe,create_user,create_time,parent_id,dict_code
     * @return
     */
    @Results(value = {@Result(property = "dictId", column = "dict_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dictName", column = "dict_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dictDesc", column = "dict_desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dictType", column = "dict_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "parentId", column = "parent_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dictCode", column = "dict_code", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getDictInfo")
    List<Map> getDictInfo(@Param("id") int id);

    /**
     * getReportInfo
     * @return
     */

    @Results(value = {@Result(property = "userName", column = "create_user", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "now", column = "create_time", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "name", column = "report_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "report_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "report_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "path", column = "template_path", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "dataSourceType", column = "datasource_type", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dataSourceId", column = "datasource_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dataSourceName", column = "datasource_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "sql", column = "report_sql", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "groupId", column = "group_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "roleId", column = "role_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "roleName", column = "role_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "id", column = "report_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "origin_file_name", column = "origin_file_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getReportInfo")
    Map getReportInfo(@Param("id") int id);


    /**
     * getParamById
     * @return
     */

    @Results(value = {@Result(property = "name", column = "param_name", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "param_desc", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "type", column = "param_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "report_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "report_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "value", column = "attribute_value", javaType = String.class, jdbcType = JdbcType.DATE),
            @Result(property = "reportId", column = "report_id", javaType = String.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getParamById")
    List<Map> getParamById(@Param("id") int id);


    /**
     * getReportCode
     * dict_id,dict_name,dict_desc,dict_tyoe,create_user,create_time,parent_id,dict_code
     * @return
     */
    @Results(value = {@Result(property = "reportCode", column = "report_code", javaType = String.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getReportCode")
    List<Map> getReportCode(@Param("id") int id);

    /**
     * getIdByCode
     * @return
     */
    @Results(value = {@Result(property = "reportId", column = "report_id", javaType = String.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getIdByCode")
    int getIdByCode( @Param("code") String code);

    /**
     * getIdByCode
     * @return
     */
    @Results(value = {@Result(property = "count", column = "use_count", javaType = String.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ReportFromMapperProvider.class, method = "getUseCountByCode")
    int getUseCountByCode( @Param("code") String code);
    /**
     * insert create Report
     *
     * @return
     */
    @InsertProvider(type = ReportFromMapperProvider.class, method = "createReport")
    int createReport(@Param("userName") String userName,
                     @Param("now") Date now,
                     @Param("reportName") String reportName,
                     @Param("reportCode") String reportCode,
                     @Param("reportType") String reportType,
                     @Param("templatePath") String templatePath,
                     @Param("dataSourceType") String dataSourceType,
                     @Param("dataSourceId") int dataSourceId,
                     @Param("dataSourceName") String dataSourceName,
                     @Param("sql") String sql,
                     @Param("status") String status,
                     @Param("groupId") int groupId,
                     @Param("roleId") String roleId,
                     @Param("roleName") String roleName,
                     @Param("originFileName") String origin_file_name);


    /**
     * update create Report
     *
     * @return
     */
    @UpdateProvider(type = ReportFromMapperProvider.class, method = "updateReport")
    int updateReport(@Param("reportId") int reportId,
                     @Param("userName") String userName,
                     @Param("now") Date now,
                     @Param("reportName") String reportName,
                     @Param("reportCode") String reportCode,
                     @Param("reportType") String reportType,
                     @Param("templatePath") String templatePath,
                     @Param("dataSourceType") String dataSourceType,
                     @Param("dataSourceId") int dataSourceId,
                     @Param("dataSourceName") String dataSourceName,
                     @Param("sql") String sql,
                     @Param("status") String status,
                     @Param("groupId") int groupId,
                     @Param("roleId") String roleId,
                     @Param("roleName") String roleName,
                     @Param("originFileName") String origin_file_name);

    /**
     * insert data source
     *
     * @return
     */
    @InsertProvider(type = ReportFromMapperProvider.class, method = "insertParam")
    int insertParam(@Param("name") String name,
                    @Param("desc") String desc,
                    @Param("type") String type,
                    @Param("value") String value,
                    @Param("reportId") int reportId);

    /**
     * insert data source
     *
     * @return
     */
    @DeleteProvider(type = ReportFromMapperProvider.class, method = "deleteParam")
    int deleteParam(@Param("reportId") int reportId);


    /**
     * updateParas
     *
     * @return
     */
    @UpdateProvider(type = ReportFromMapperProvider.class, method = "updateParas")
    int updateParas(@Param("id") int id,
                    @Param("headMid") String headMid,
                    @Param("head") String head);

    @UpdateProvider(type = ReportFromMapperProvider.class, method = "addUseCountByCode")
    int addUseCountByCode(@Param("code") String code,@Param("count") int count);
}
