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

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * worker group mapper
 */
public interface ServerManagerMapper {


    /**
     * countServerList
     * @param userName
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "countServerList")
    Integer countServerList( @Param("userName") String userName,
                             @Param("searchVal") String searchVal,
                             @Param("status") Integer status,
                             @Param("type") List<String> type
    );

    /**
     * countServerList
     * @param searchVal
     * @return
     */
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "countServerList2")
    Integer countServerList2(@Param("searchVal") String searchVal,
                             @Param("status") Integer status,
                             @Param("type") List<String> type
    );


    /**
     * getServerList
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "serviceId", column = "service_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "serviceName", column = "service_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "serviceCode", column = "service_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "developMode", column = "develop_mode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceType", column = "datasource_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceName", column = "datasource_name",  javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "serviceSql", column = "service_sql", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "des", column = "des", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceId", column = "datasource_Id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getServerList")
    List<Map> getServerList(@Param("userName") String userName,
                            @Param("searchVal") String searchVal,
                            @Param("status") Integer status,
                            @Param("start") Integer start,
                            @Param("pageSize") Integer pageSize,
                            @Param("type")List<String> type);

    /**
     * getServerList
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "serviceId", column = "service_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "serviceName", column = "service_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "serviceCode", column = "service_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "developMode", column = "develop_mode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceType", column = "datasource_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceName", column = "datasource_name",  javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "serviceSql", column = "service_sql", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "des", column = "des", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceId", column = "datasource_Id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getServerList2")
    List<Map> getServerList2(@Param("searchVal") String searchVal,
                            @Param("status") Integer status,
                            @Param("start") Integer start,
                            @Param("pageSize") Integer pageSize,
                            @Param("type")List<String> type);

    /**
     * insert
     *
     * @return
     */
    @InsertProvider(type = ServerManagerMapperProvider.class, method = "insert")
    int insert(@Param("serviceName") String serviceName,
               @Param("serviceCode") String serviceCode,
               @Param("developMode") String developMode,
               @Param("serviceType") String type,
               @Param("datasourceType") String datasourceType,
               @Param("datasourceName") String datasourceName,
               @Param("serviceSql") String serviceSql,
               @Param("status") String status,
               @Param("createUser") String createUser,
               @Param("createTime") Date createTime,
               @Param("des") String des,
               @Param("datasourceId") Integer datasourceId,
               @Param("serviceParams") String serviceParams,
               @Param("tableName") String tableName,
               @Param("inParam") String inParam,
               @Param("outParam") String outParam);






    /**
     * update
     *
     * @return
     */
    @UpdateProvider(type = ServerManagerMapperProvider.class, method = "update")
    int update(@Param("serviceId") int serviceId,
               @Param("serviceName") String serviceName,
               @Param("serviceCode") String serviceCode,
               @Param("serviceType") String type,
               @Param("developMode") String developMode,
               @Param("datasourceType") String datasourceType,
               @Param("datasourceName") String datasourceName,
               @Param("serviceSql") String serviceSql,
               @Param("status") String status,
               @Param("des") String des,
               @Param("datasourceId") Integer datasourceId,
               @Param("serviceParams") String serviceParams,
               @Param("tableName") String tableName,
               @Param("inParam") String inParam,
               @Param("outParam") String outParam);

    /**
     * getInfo
     *
     * @return
     */
    @Results(value = {@Result(property = "id", column = "service_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "service_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "service_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "mode", column = "develop_mode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "service_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceType", column = "datasource_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceName", column = "datasource_name",  javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "sql", column = "service_sql", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "desc", column = "des", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "datasourceId", column = "datasource_Id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "params", column = "service_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "param_variable", column = "param_variable", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "table", column = "table_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "inParam", column = "in_param", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "outParam", column = "out_param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getInfo")
    Map getInfo(@Param("serviceId")int serviceId);

    /**
     * getInfo
     *
     * @return
     */
    @Results(value = {@Result(property = "id", column = "service_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getIdByCode")
    int getIdByCode(@Param("serviceCode")String serviceCode);





    /**
     * delete
     *
     * @return
     */
    @DeleteProvider(type = ServerManagerMapperProvider.class, method = "delete")
    void delete(@Param("serviceId")int serviceId);

    /**
     * getServiceCode
     * @return
     */
    @Results(value = {@Result(property = "serviceCode", column = "service_code", javaType = String.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getServiceCode")
    List<Map> getServiceCode(@Param("id") int id);



    /**
     * insert
     *
     * @return
     */
    @InsertProvider(type = ServerManagerMapperProvider.class, method = "insertParam")
    int insertParam(@Param("param_name") String param_name,
                    @Param("param_type") String param_type,
                    @Param("param_des") String param_des,
                    @Param("type") String type,
                    @Param("param_join") String param_join,
                    @Param("param_exam") String param_exam,
                    @Param("service_id") int service_id,
                    @Param("param_variable")  String param_variable,
                    @Param("isRequired") String isRequired);

    /**
     * updataParam
     *
     * @return
     */
    @InsertProvider(type = ServerManagerMapperProvider.class, method = "updateParam")
    int updateParam(@Param("id") int id,
                    @Param("param_name") String param_name,
                    @Param("param_type") String param_type,
                    @Param("param_des") String param_des,
                    @Param("type") String type,
                    @Param("param_join") String param_join,
                    @Param("param_exam") String param_exam,
                    @Param("service_id") int service_id,
                    @Param("param_variable")  String param_variable);

    /**
     * update process instance
     *
     * @param serviceId
     * @param status
     * @return
     */
    @UpdateProvider(type = ServerManagerMapperProvider.class, method = "onLine")
    int onLine(@Param("serviceId") int serviceId
            ,@Param("status") int status);


    /**
     * updateServiceParam
     *
     * @param id
     * @param param
     * @return
     */
    @UpdateProvider(type = ServerManagerMapperProvider.class, method = "updateServiceParam")
    int updateServiceParam(@Param("id") int id
            ,@Param("param") String param);


    /**
     * deleteParam
     *
     * @param serviceId
     * @return
     */
    @DeleteProvider(type = ServerManagerMapperProvider.class, method = "deleteParam")
    int deleteParam(@Param("serviceId") int serviceId);


    /**
     * getParam
     *
     * @param serviceId
     * @param type
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "param_name", column = "param_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "param_type", column = "param_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "param_des", column = "param_des", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "param_join", column = "param_join", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "param_exam", column = "param_exam", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "service_id", column = "service_id", javaType = String.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "param_variable", column = "param_variable", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "is_required", column = "is_required", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ServerManagerMapperProvider.class, method = "getParam")
    List<Map> getParam(@Param("serviceId") int serviceId,
                       @Param("type") String type);


}
