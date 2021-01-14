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
 * model design mapper provider
 */
public class ModelDesignMapperProvider {

  public static final String TABLE_NAME = "t_escheduler_model_catalog";
  public static final String TABLE_MODEL = "t_escheduler_model_design";
  public static final String TABLE_TABRULE_CONF = "t_escheduler_tabruleconf";
  public static final String TABLE_COLRULE_CONF = "t_escheduler_colruleconf";
  public static final String TABLE_BASENAME = "t_escheduler_model_basename";
  public static final String TABLE_DEFINITION = "t_escheduler_process_definition";

  /**
   * 模型设计 - 新增模型
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(TABLE_NAME);

      VALUES("`base`", "#{base}");
      VALUES("`layer`", "#{layer}");
      VALUES("`id`", "#{id}");

    }}.toString();
  }

  /**
   * 模型设计 - 查询模型列表
   */
  public String getModelList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("a.tablename, a.tabledescribe, a.area, a.tabquality, a.type, a.create_time, a.update_time, a.create_userid, a.username," +
              "a.tabwarn as tblWarn,a.datasourceid as dataSourceId,a.tabrule as tabRule, a.layer as topic, b.name as layerCn, b.base as layer " +
              "from t_escheduler_model_design a left join t_escheduler_model_basename b on a.base=b.base");
      Object table = parameter.get("searchVal");
      if(table != null && StringUtils.isNotEmpty(table.toString())){
        WHERE( "a.tabledescribe like concat('%',  #{searchVal}, '%') ");
      }else {
        WHERE("a.base = #{layer} and a.layer = #{topic} ");
      }
      ORDER_BY("a.update_time desc limit #{offset},#{pageSize} ");
    }}.toString();
  }


  /**
   *
   * update layer
   * @param parameter
   * @return
   */
  public String updateLayer(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);
      SET("`layer` = #{newlayer}");
      SET("`id` = #{id}");
      WHERE("`base` = #{base} and `layer` = #{layer}");
    }}.toString();
  }

  /**
   *
   * orderLayer
   * @param parameter
   * @return
   */
  public String orderLayer(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);
      SET("`id` = #{id}");
      WHERE("`base` = #{base} and `layer` = #{layer}");
    }}.toString();
  }



  /**
   *
   * update layer
   * @param parameter
   * @return
   */
  public String updateLayer2(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_MODEL);
      SET("`layer` = #{newlayer}");
      WHERE("`base` = #{base} and `layer` = #{layer}");
    }}.toString();
  }


  /**
   * delete layer
   * @param parameter
   * @return
   */
  public String deleteLayer(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);

      WHERE("`base` = #{base} and `layer`=#{layer}");
    }}.toString();
  }


  /**
   * delete layer
   * @param parameter
   * @return
   */
  public String deleteTable(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_MODEL);

      WHERE("`base` = #{base} and `tablename` = #{tablename} ");
    }}.toString();
  }


  /**
   * query layer list by database
   * @param parameter
   * @return
   */
  public String queryLayerByDatabase(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("layer,id");
      FROM(TABLE_NAME );
      WHERE("base = #{base}");
      ORDER_BY("`id` asc");
    }}.toString();
  }

  /**
   *
   * update layer
   * @param parameter
   * @return
   */
  public String getBaseName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(TABLE_BASENAME);
    }}.toString();
  }

  /**
   * get tableinfo
   * @param parameter
   * @return
   */
  public String getTableInfo(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("tabrule");
      FROM(TABLE_MODEL );
      WHERE("base = #{base} and tablename = #{tblName}");
    }}.toString();
  }

  /**
   *  get ColumnInfo
   * @param parameter
   * @return
   */
  public String getColumnInfo(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("colrule");
      FROM(TABLE_MODEL );
      WHERE("base = #{base} and tablename = #{tblName}");
    }}.toString();
  }


  /**
   * getModelDesignList
   * @param parameter
   * @return
   */
  public String getModelDesignList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("a.tablename,a.tabledescribe,a.area,a.tabquality,a.type,a.create_time,a.update_time,a.create_userid,a.username," +
              "a.tabwarn as tblWarn,a.datasourceid as dataSourceId,a.tabrule as tabRule, concat(b.name,'-',a.layer) as layer " +
              "from t_escheduler_model_design a left join t_escheduler_model_basename b on a.base=b.base");
      Object table = parameter.get("tableName");
      if(table != null && StringUtils.isNotEmpty(table.toString())){
        WHERE( "a.tablename like concat('%',  #{tableName}, '%') ");
      }else {
        WHERE("a.base = #{base} and a.layer = #{layer} ");
      }
      ORDER_BY("a.update_time desc limit #{offset},#{pageSize} ");
    }}.toString();
  }

  public String getTableList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("a.tablename,a.tabledescribe,a.area,a.tabquality,a.type,a.create_time,a.update_time,a.create_userid,a.username," +
              "a.tabwarn as tblWarn,a.datasourceid as dataSourceId,a.tabrule as tabRule, a.layer as topic, b.name as layerCN, a.base as layer " +
              "from t_escheduler_model_design a left join t_escheduler_model_basename b on a.base=b.base");
      Object searchVal = parameter.get("searchVal");
      if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
        WHERE( "a.tabledescribe like concat('%',  #{searchVal}, '%') ");
      }
      Object base = parameter.get("base");
      if(base != null && StringUtils.isNotEmpty(base.toString()) && !base.equals("all")) {
        String[] list = base.toString().split(",");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < list.length; i ++) {
          if(i == list.length - 1) {
            sb.append("'" + list[i] + "'");
          } else {
            sb.append("'" + list[i] + "'" + ",");
          }
        }
        WHERE("a.base in (" + sb.toString() + ")");
      }
      Object topic = parameter.get("topic");
      if(topic != null && StringUtils.isNotEmpty(topic.toString()) && !topic.equals("all")) {
        WHERE("layer = #{topic}");
      }
      ORDER_BY("a.update_time desc limit #{offset},#{pageSize} ");
    }}.toString();
  }

  /**
   * countModelDesignList
   * @param parameter
   * @return
   */
  public String countModelDesignList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("count(1)");
      FROM(TABLE_MODEL );
      Object table = parameter.get("tableName");
      if(table != null && StringUtils.isNotEmpty(table.toString())){
        WHERE( "tablename like concat('%',  #{tableName}, '%')");
      }else {
        WHERE("base = #{base} and layer = #{layer}");
      }
    }}.toString();
  }

  public String countModel(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("count(1)");
      FROM(TABLE_MODEL);
      Object searchVal = parameter.get("searchVal");
      if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
        WHERE("tablename like concat('%',  #{searchVal}, '%')");
      }
      Object base = parameter.get("base");
      if(base != null && StringUtils.isNotEmpty(base.toString()) && !base.equals("all")) {
        String[] list = base.toString().split(",");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < list.length; i ++) {
          if(i == list.length - 1) {
            sb.append("'" + list[i] + "'");
          } else {
            sb.append("'" + list[i] + "'" + ",");
          }
        }
        WHERE("base in (" + sb.toString() + ")");
      }
      Object topic = parameter.get("topic");
      if(topic != null && StringUtils.isNotEmpty(topic.toString()) && !topic.equals("all")) {
        WHERE("layer = #{topic}");
      }
    }}.toString();
  }


  /**
   * getTableRuleConfList
   * @return
   */
  public String getTableRuleConfList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("type");
      FROM(TABLE_TABRULE_CONF );
      GROUP_BY("type");
    }}.toString();
  }

  /**
   * getTableRuleConfListCondition
   * @param parameter
   * @return
   */
  public String getTableRuleConfListCondition(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("condi");
      FROM(TABLE_TABRULE_CONF );
      WHERE(" type = #{type}");
      GROUP_BY("condi");
    }}.toString();
  }


  /**
   * getColumnRuleConfListCondition
   * @param parameter
   * @return
   */
  public String getColumnRuleConfListCondition(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("condi");
      FROM(TABLE_COLRULE_CONF );
      WHERE(" type = #{type}");
      GROUP_BY("condi");
    }}.toString();
  }


  /**
   * getTableRuleConfListCondition
   * @param parameter
   * @return
   */
  public String getTableRuleConfListConditionParam(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("param");
      FROM(TABLE_TABRULE_CONF );
      WHERE(" condi = #{condi} and type = #{type}");
    }}.toString();
  }


  /**
   * getColumnRuleConfListCondition
   * @param parameter
   * @return
   */
  public String getColumnRuleConfListConditionParam(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("param");
      FROM(TABLE_COLRULE_CONF );
      WHERE(" condi = #{condi} and type = #{type}");
    }}.toString();
  }


  /**
   * getColumnRuleConfList
   * @return
   */
  public String getColumnRuleConfList(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("type");
      FROM(TABLE_COLRULE_CONF );
      GROUP_BY("type");
    }}.toString();
  }


  /**
   *
   * @param parameter
   * @return
   */
  public String updateTableDescribe(Map<String, Object> parameter) {

    return new SQL() {{
      INSERT_INTO(TABLE_MODEL);
      VALUES("`base`" ,"#{base}");
      VALUES("`layer`" ,"#{layer}");
      VALUES("`area`" , "#{tableComment}");
      VALUES("`tabledescribe`" , "#{applicationName}");
      VALUES("`tablename`" , "#{tablename}");
      VALUES("`datasourceid`" , "#{dataSourceId}");
      VALUES("`type`" , "#{type}");
      VALUES("`create_time`" , "#{now}");
      VALUES("`update_time`" , "#{now}");
      VALUES("`create_userid`" , "#{userId}");
      VALUES("`username`" , "#{userName}");
    }}.toString();
  }


  /**
   *
   * update data source
   * @param parameter
   * @return
   */
  public String updateModelDesignList(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_MODEL);
      SET("`tabledescribe` = #{area}");
      SET("`area` = #{tabledescribe}");
      SET("`layer` = #{layer}");
      SET("`update_time` = #{now}");
      WHERE("`base` = #{base} and `tablename` = #{tblName}");
    }}.toString();
  }


  /**
   *
   * update data source
   * @param parameter
   * @return
   */
  public String updateTblRule(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_MODEL);
      Object trule = parameter.get("tabrule");

      if(trule != null && !trule.equals("")){
        SET("`tabrule` = "+"'"+trule.toString()+"'");
      }
      Object crule = parameter.get("colrule");

      if(crule != null && !crule.equals("")){
        SET("`colrule` = "+"'"+crule.toString()+"'");
      }
//      SET("`tabwarn` = #{tabwarn}");
//      SET("`tabquality` = #{tabquality}");

      WHERE("`base` = #{base} and `tablename` = #{tblName}");
    }}.toString();
  }

  /**
   * jump
   * @return
   */
  public String jump(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" id,name,process_definition_json ");
      FROM(TABLE_DEFINITION );
    }}.toString();
  }

  /**
   * getName
   * @return
   */
  public String getName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" tabledescribe ");
      FROM(TABLE_MODEL );
      WHERE("`base` = #{base} and `tablename` = #{tblName}");
    }}.toString();
  }

}
