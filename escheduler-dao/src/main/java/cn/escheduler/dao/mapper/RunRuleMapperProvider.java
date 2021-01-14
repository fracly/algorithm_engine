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

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data source mapper provider
 */
public class RunRuleMapperProvider {

  public static final String TABLE_MODEL = "t_escheduler_model_design";



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

      if(trule != null && !trule.toString().equals("")){
        SET("`tabrule` = "+"'"+trule.toString()+"'");
      }
      Object crule = parameter.get("colrule");

      if(crule != null && !crule.toString().equals("")){
        SET("`colrule` = "+"'"+crule.toString()+"'");
      }
      WHERE("`base` = #{base} and `tablename` = #{tblName}");
    }}.toString();
  }

}
