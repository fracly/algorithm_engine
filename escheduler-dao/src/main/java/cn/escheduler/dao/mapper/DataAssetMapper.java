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

import cn.escheduler.dao.model.DataAsset;
import cn.escheduler.dao.model.DataAssetRule;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * data asset mapper
 */
public interface DataAssetMapper {
    /**
     * insert data asset
     */
    @InsertProvider(type = DataAssetMapperProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", resultType = Integer.class, keyProperty = "dataAsset.id", before = false)
    int insert(@Param("dataAsset") DataAsset dataAsset);

    /**
     * update data asset
     */
    @UpdateProvider(type = DataAssetMapperProvider.class, method = "update")
    int update(@Param("dataAsset") DataAsset dataAsset);

    /**
     * delete data asset by id
     */
    @DeleteProvider(type = DataAssetMapperProvider.class, method = "delete")
    int delete(@Param("id") int id);

    /**
     * get data asset list by parameters
     * @param type
     * @param labels
     * @param dateRange
     * @param offset
     * @param pageSize
     * @return List<DataAsset>
     */
    @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "labels", column = "labels", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "state", column = "state", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
  })
    @SelectProvider(type = DataAssetMapperProvider.class, method = "getDataAssetListPaging")
    List<DataAsset> getDataAssetListPaging(@Param("type") String type,
                                           @Param("labels") String labels,
                                           @Param("dateRange") String dateRange,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    @Results(value = {
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "assetName", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "assetState", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "destroyMethod", column = "destroy_method", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "destroyPeriod", column = "destroy_period", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "owner", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = DataAssetMapperProvider.class, method = "getDataAssetRuleListPaging")
    List<DataAssetRule> getDataAssetRuleListPaging(@Param("name") String name,
                                                   @Param("state") Integer state,
                                                   @Param("userId") Integer userId,
                                                   @Param("dateRange") String dateRange,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    @SelectProvider(type = DataAssetMapperProvider.class, method = "getDataAssetRuleTotal")
    Long getDataAssetRuleTotal(@Param("name") String name,
                               @Param("state") Integer state,
                               @Param("userId") Integer user,
                               @Param("dateRange") String dateRange);
    /**
     * get data asset total by parameters
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "getDataAssetTotal")
    Long getDataAssetTotal(@Param("type") String type,
                           @Param("labels") String labels,
                           @Param("dateRange") String dateRange);
    /**
     * query data asset by id
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "queryById")
    DataAsset queryById(@Param("id") Integer id);

    /**
     * query data asset rule by id
     */
    @Results(value = {
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "assetId", column = "asset_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "assetName", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "assetState", column = "state", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "destroyMethod", column = "destroy_method", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "destroyPeriod", column = "destroy_period", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "owner", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = DataAssetMapperProvider.class, method = "queryRuleById")
    DataAssetRule queryRuleById(@Param("id") Integer id);

    /**
     * query label name by id
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "queryLabelName")
    String queryLabelName(@Param("id") Integer id);

    /**
     * query state name by id
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "queryStateName")
    String queryStateName(@Param("id") Integer id);

    /**
     * query method name by id
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "queryMethodName")
    String queryMethodName(@Param("id") Integer id);

    /**
     * get all data asset user
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "getUser")
    List<Map<String, Object>> getUser();

    /**
     * get data asset list
     */
    @SelectProvider(type = DataAssetMapperProvider.class, method = "getDataAssetList")
    List<Map<String, Object>> getDataAssetList();

    /**
     * insert data asset rule
     */
    @InsertProvider(type = DataAssetMapperProvider.class, method = "insertRule")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", resultType = Integer.class, keyProperty = "rule.id", before = false)
    int insertRule(@Param("rule") DataAssetRule rule);

    /**
     * update data asset rule
     */
    @UpdateProvider(type = DataAssetMapperProvider.class, method = "updateRule")
    int updateRule(@Param("rule") DataAssetRule rule);

    /**
     * delete data asset rule by id
     */
    @DeleteProvider(type = DataAssetMapperProvider.class, method = "deleteRule")
    int deleteRule(@Param("id") int id);

}
