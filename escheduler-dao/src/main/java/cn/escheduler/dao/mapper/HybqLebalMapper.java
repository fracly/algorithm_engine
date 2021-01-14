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

import cn.escheduler.common.enums.DbType;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.HybqLebal;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

/**
 * data source mapper
 */
public interface HybqLebalMapper {

  /**
   * count data source by user id
   *
   * @param userId
   * @return
   */
  @SelectProvider(type = HybqlebalMapperProvider.class, method = "countUserDatasource")
  int countUserDatasource(@Param("userId") int userId);

  /**
   * count data source number
   *
   * @return
   */
  @SelectProvider(type = HybqlebalMapperProvider.class, method = "countAllLabele")
  int countAllLabele();

  /**
   * query data source list paging
   *
   * @param userId
   * @param offset
   * @param pageSize
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "note", column = "note", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "connectionParams", column = "connection_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
          @Result(property = "updateTime", column = "update_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = HybqlebalMapperProvider.class, method = "queryDataSourcePaging")
  List<DataSource> queryDataSourcePaging(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

  /**
   * query data source list paging
   *
   * @param offset
   * @param pageSize
   * @return
   */
  @Results(value = {
          @Result(property = "label_id", column = "label_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "label_code", column = "label_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "label_name", column = "label_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "label_type", column = "label_type",  javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "updata_freg", column = "updata_freg", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "catalogue", column = "catalogue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "create_time", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = HybqlebalMapperProvider.class, method = "queryAllLabelPaging")
  List<HybqLebal> queryAllLabelPaging(@Param("offset") int offset,
                                      @Param("pageSize") int pageSize);

}
