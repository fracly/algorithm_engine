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

import cn.escheduler.dao.model.DesignModel;
import cn.escheduler.dao.model.TableInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * data source mapper
 */
public interface ModelDesignMapper {
  /**
   * insert data source user relation
   *
   * @param base
   * @param layer
   * @return
   */
  @InsertProvider(type = ModelDesignMapperProvider.class, method = "insert")
  int insert(@Param("base") String base,
             @Param("layer") String layer,
             @Param("id") int id
  );


  /**
   * query data source list by type
   * @param base
   * @return
   */
  @Results(value = {
          @Result(property = "layer", column = "layer", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "queryLayerByDatabase")
  List<Map> queryLayerByDatabase( @Param("base") String base);


  /**
   * orderLayer
   *
   * @param base
   * @param layer
   * @param id
   * @return
   */
  @UpdateProvider(type = ModelDesignMapperProvider.class, method = "orderLayer")
  int orderLayer(@Param("base") String base,
                  @Param("layer") String layer,
                  @Param("id") int id
  );



  /**
   * get base name
   * @return
   */
  @Results(value = {
          @Result(property = "layer", column = "layer", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getBaseName")
  List<Map> getBaseName();


  /**
   * updateLayer
   *
   * @param base
   * @param layer
   * @param newlayer
   * @param id
   * @return
   */
  @UpdateProvider(type = ModelDesignMapperProvider.class, method = "updateLayer")
  int updateLayer(@Param("base") String base,
                  @Param("layer") String layer,
                  @Param("newlayer") String newlayer,
                  @Param("id") String id
  );

  /**
   * updateLayer2
   *
   * @param base
   * @param layer
   * @param newlayer
   * @return
   */
  @UpdateProvider(type = ModelDesignMapperProvider.class, method = "updateLayer2")
  int updateLayer2(@Param("base") String base,
                   @Param("layer") String layer,
                   @Param("newlayer") String newlayer
  );


  /**
   * deleteLayer
   *
   * @param base
   * @param layer
   * @return
   */
  @DeleteProvider(type = ModelDesignMapperProvider.class, method = "deleteLayer")
  int deleteLayer(@Param("base") String base, @Param("layer") String layer);


  /**
   * get tableinfo
   * @param base
   * @param tblName
   * @return
   */
  @Results(value = {
          @Result(property = "tabRule", column = "tabrule", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getTableInfo")
  TableInfo getTableInfo(@Param("base") String base, @Param("tblName") String tblName);

  /**
   * get tableinfo
   * @param base
   * @param tblName
   * @return
   */
  @Results(value = {
          @Result(property = "colRule", column = "colrule", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getColumnInfo")
  TableInfo getColumnInfo(@Param("base") String base, @Param("tblName") String tblName);



  /**
   *  updata table describe info
   * @param base
   * @param layer
   * @param applicationName
   * @param tableComment
   * @return
   */
  @InsertProvider(type = ModelDesignMapperProvider.class, method = "updateTableDescribe")
  int insertTableDescribe(@Param("base") String base,
                          @Param("layer") String layer,
                          @Param("applicationName") String applicationName,
                          @Param("tableComment") String tableComment,
                          @Param("tablename") String tablename,
                          @Param("dataSourceId") int dataSourceId,
                          @Param("type") String type,
                          @Param("now") Date now,
                          @Param("userId") int userId,
                          @Param("userName") String userName);


  /**
   * deleteTable
   *
   * @param base
   * @param tablename
   * @return
   */
  @DeleteProvider(type = ModelDesignMapperProvider.class, method = "deleteTable")
  int deleteTable(@Param("base") String base, @Param("tablename") String tablename);


  /**
   * get design model list
   */
  @Results(value = {
          @Result(property = "tblName", column = "tablename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblDescribe", column = "tabledescribe", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "area", column = "area", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblQuality", column = "tabquality", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getModelList")
  List<DesignModel> getModelList(@Param("layer") String layer,
                                       @Param("topic") String topic,
                                       @Param("offset")Integer offset,
                                       @Param("pageSize") int pageSize,
                                       @Param("searchVal") String searchVal);

  @Results(value = {
          @Result(property = "tblName", column = "tablename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblDescribe", column = "tabledescribe", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "area", column = "area", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblQuality", column = "tabquality", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getModelDesignList")
  List<DesignModel> getModelDesignList(@Param("base") String base,
                                 @Param("topic") String topic,
                                 @Param("offset")Integer offset,
                                 @Param("pageSize") int pageSize,
                                 @Param("searchVal") String searchVal);

  @Results(value = {
          @Result(property = "tblName", column = "tablename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblDescribe", column = "tabledescribe", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "area", column = "area", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tblQuality", column = "tabquality", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getTableList")
  List<DesignModel> getTableList(@Param("base") String base,
                                       @Param("topic") String topic,
                                       @Param("offset")Integer offset,
                                       @Param("pageSize") int pageSize,
                                       @Param("searchVal") String searchVal);


  /**
   * getModelDesignListCount
   * @param base
   * @param layer
   * @return
   */
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "countModelDesignList")
  int countModelDesignList(@Param("base") String base, @Param("layer") String layer, @Param("tableName") String tableName);

  @SelectProvider(type = ModelDesignMapperProvider.class, method = "countModel")
  int countModel(@Param("base") String base, @Param("topic") String topic, @Param("searchVal") String searchVal);




  /**
   * getTableRuleConfList
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "condition", column = "condition", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getTableRuleConfList")
  List<Map> getTableRuleConfList();



  /**
   * getTableRuleConfListCondition
   * @return
   */
  @Results(value = {
          @Result(property = "condi", column = "condi", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getTableRuleConfListCondition")
  List<Map> getTableRuleConfListCondition(@Param("type") String type);

  /**
   * getColumnRuleConfListCondition
   * @return
   */
  @Results(value = {
          @Result(property = "condi", column = "condi", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getColumnRuleConfListCondition")
  List<Map> getColumnRuleConfListCondition(@Param("type") String type);

  /**
   * getTableRuleConfListCondition
   * @return
   */
  @Results(value = {
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getTableRuleConfListConditionParam")
  List<Map> getTableRuleConfListConditionParam(@Param("type") String type, @Param("condi") String condi);

  /**
   * getColumnRuleConfListCondition
   * @return
   */
  @Results(value = {
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getColumnRuleConfListConditionParam")
  List<Map> getColumnRuleConfListConditionParam(@Param("type") String type, @Param("condi") String condi);


  /**
   * getTableRuleConfList
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "condition", column = "condition", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getColumnRuleConfList")
  List<Map> getColumnRuleConfList();



  /**
   * updateModelDesignList
   *
   * @param base
   * @param tblName
   * @param layer
   * @param area
   * @param now
   * @return
   */
  @UpdateProvider(type = ModelDesignMapperProvider.class, method = "updateModelDesignList")
  int updateModelDesignList(@Param("base") String base,
                            @Param("tblName") String tblName,
                            @Param("layer") String layer,
                            @Param("area") String area,
                            @Param("now") Date now,
                            @Param("tabledescribe") String tabledescribe

  );


  /**
   * insert data source user relation
   *
   * @param base
   * @param tblName
   * @param tabrule
   * @param colrule
   * @return
   */
  @UpdateProvider(type = ModelDesignMapperProvider.class, method = "updateTblRule")
  int updateTblRule(@Param("base") String base,
                    @Param("tblName") String tblName,
                    @Param("tabrule") List tabrule,
                    @Param("colrule") List colrule,
                    @Param("tabwarn") String tabwarn,
                    @Param("tabquality") String tabquality
  );

  /**
   *  获取跳转信息
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "process_definition_json", column = "process_definition_json", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "jump")
  List<Map> jump();

  /**
   *  atlas getName
   * @return
   */
  @Results(value = {
          @Result(property = "tabledescribe", column = "tabledescribe", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ModelDesignMapperProvider.class, method = "getName")
  List<Map> getName(@Param("base") String base, @Param("tblName") String tblName);


}
