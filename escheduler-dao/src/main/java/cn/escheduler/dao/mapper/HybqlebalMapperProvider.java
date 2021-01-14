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
import cn.escheduler.common.utils.EnumFieldUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.Map;

/**
 * data source mapper provider
 */
public class HybqlebalMapperProvider {

  public static final String TABLE_NAME = "t_escheduler_label";
  public static final String USER_TABLE_NAME = "t_escheduler_user";
  public static final String PROJECT_TABLE_NAME = "t_escheduler_project";
  public static final String USER_DATASOURCE_RELATION_TABLE_NAME = "t_escheduler_relation_datasource_user";


  /**
   * query data source paging
   * @param parameter
   * @return
   */
  public String queryDataSourcePaging(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(TABLE_NAME );
      WHERE("id in (select datasource_id from "+USER_DATASOURCE_RELATION_TABLE_NAME+" where user_id=#{userId} union select id as datasource_id  from "+TABLE_NAME+" where user_id=#{userId})");
      Object searchVal = parameter.get("searchVal");
      if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
        WHERE( " name like concat('%', #{searchVal}, '%') ");
      }
      ORDER_BY("update_time desc limit #{offset},#{pageSize} ");

    }}.toString();
  }

  /**
   *
   * query data source list paging
   * @param parameter
   * @return
   */
  public String queryAllLabelPaging(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(TABLE_NAME );
      ORDER_BY("create_time desc limit #{offset},#{pageSize} ");

    }}.toString();
  }

  /**
   * count data source by user id
   *
   * @param parameter
   * @return
   */
  public String countUserDatasource(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("count(0)");

      FROM(TABLE_NAME);
      WHERE("id in (select datasource_id from "+USER_DATASOURCE_RELATION_TABLE_NAME+" where user_id=#{userId} union select id as datasource_id  from "+TABLE_NAME+" where user_id=#{userId})");
    }}.toString();
  }

  /**
   * 查询总的标签数目
   *
   * @param parameter
   * @return
   */
  public String countAllLabele(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("count(0)");
      FROM(TABLE_NAME);
    }}.toString();
  }

}
