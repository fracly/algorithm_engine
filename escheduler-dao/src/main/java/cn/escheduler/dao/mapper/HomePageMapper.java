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

import cn.escheduler.dao.model.TopPV;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;

/**
 * data source mapper
 */
public interface HomePageMapper {



  /**
   * getModelGroup
   * @param userId
   * @return
   */
  @Results(value = {
          @Result(property = "modelGroup", column = "modelGroup", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getModelGroup")
  String getModelGroup(@Param("userId") String userId);

  /**
   * getModel
   * @param userId
   * @return
   */
  @Results(value = {
          @Result(property = "model", column = "model", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getModel")
  String getModel(@Param("userId") String userId);



  /**
   * upda beforDay hive size
   *
   * @param hivesize
   * @return
   */
  @UpdateProvider(type = HomePageMapperProvider.class, method = "updateBeforHiveSize")
  int updateBeforHiveSize(@Param("hiveSize") double hivesize);


  /**
   * getBeforHiveSize
   *
   * @return
   */
  @Results(value = {
          @Result(property = "hiveSize", column = "hiveSize", javaType = double.class, jdbcType = JdbcType.DOUBLE)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getBeforHiveSize")
  double getBeforHiveSize();





  /**
   *  insert beforDay hive table size
   * @param hiveTableSize
   * @param hiveTableName
   * @return
   */
  @InsertProvider(type = HomePageMapperProvider.class, method = "insertBeforHiveTableSize")
  int insertBeforHiveTableSize(@Param("hiveTableSize") long hiveTableSize,
                               @Param("hiveTableName") String hiveTableName);




  /**
   * upda beforDay hive table size
   *
   * @param hiveTableSize
   * @param hiveTableName
   * @return
   */
  @UpdateProvider(type = HomePageMapperProvider.class, method = "updateBeforHiveTableSize")
  int updateBeforHiveTableSize(@Param("hiveTableSize") long hiveTableSize
                            ,@Param("hiveTableName") String hiveTableName);


  /**
   * getBeforHiveTableSize
   *
   * @return
   */
  @Results(value = {
          @Result(property = "size", column = "size", javaType = Long.class, jdbcType = JdbcType.INTEGER)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getBeforHiveTableSize")
  Long getBeforHiveTableSize(@Param("name") String name);

  /**
   * getModel
   * @return
   */
  @Results(value = {
          @Result(property = "sjbm", column = "sjbm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "pv", column = "pv", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getTopPV")
  List<TopPV> getTopPV();

  /**
   * getModel
   * @return
   */
  @Results(value = {
          @Result(property = "sjbm", column = "sjbm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "pv", column = "pv", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = HomePageMapperProvider.class, method = "getTopZD")
  List<TopPV> getTopZD();


}
