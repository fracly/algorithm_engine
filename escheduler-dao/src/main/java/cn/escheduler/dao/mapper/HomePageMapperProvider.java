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
public class HomePageMapperProvider {


  public static final String TABLE_MODEL = "t_escheduler_model_design";
  public static final String TABLE_DEFINITION = "t_escheduler_process_definition";
  public static final String TABLE_HIVESIZE="t_escheduler_hivesize";
  public static final String TABLE_HIVETABLESIZE="t_escheduler_hivetablesize";




  /**
   * getModelGroup
   * @param parameter
   * @return
   */
  public String getModelGroup(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" count(distinct(project_id)) modelGroup ");
      FROM(TABLE_DEFINITION );
      WHERE("user_id = #{userId}");
    }}.toString();
  }


  /**
   * getModel
   * @param parameter
   * @return
   */
  public String getModel(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" count(1) model ");
      FROM(TABLE_MODEL );
      WHERE("create_userid = #{userId}");
    }}.toString();
  }




  /**
   *
   * updaBeforDayHiveSize
   * @param parameter
   * @return
   */
  public String updateBeforHiveSize(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_HIVESIZE);
      SET("`hivesize` = #{hiveSize}");
    }}.toString();
  }

  /**
   * getBeforHiveSize
   * @param parameter
   * @return
   */
  public String getBeforHiveSize(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" hivesize ");
      FROM(TABLE_HIVESIZE );
    }}.toString();
  }


  /**
   *
   * insertBeforDayHiveSize
   * @param parameter
   * @return
   */
  public String insertBeforHiveTableSize(Map<String, Object> parameter) {
    return "insert IGNORE into "+TABLE_HIVETABLESIZE+" VALUES(#{hiveTableName},#{hiveTableSize})";
  }


  /**
   *
   * updaBeforDayHiveSize
   * @param parameter
   * @return
   */
  public String updateBeforHiveTableSize(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_HIVETABLESIZE);
      SET("`name` = #{hiveTableName}");
      SET("`size` = #{hiveTableSize}");
      WHERE(" name = #{hiveTableName}");
    }}.toString();
  }


  /**
   * getBeforHiveTableSize
   * @param parameter
   * @return
   */
  public String getBeforHiveTableSize(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT(" size ");
      FROM(TABLE_HIVETABLESIZE );
      WHERE( "name = #{name}");
    }}.toString();
  }

  /**
   * get user login PV
   * @param parameter
   * @return
   */
  public String getTopPV(Map<String, Object> parameter) {
    return new SQL() {{
//      SELECT(" v.sjmc, cast(u.pv as char) pv from (" +
//              "select  count(1) as pv,user from t_escheduler_user_login  GROUP BY user ) u " +
//              "INNER JOIN (" +
//              "select t.jybh,t.glbm,t1.bmmc as sjmc from (select a.jybh,a.glbm,b.bmmc,SUBSTR(a.glbm,1,4) as sjbm from base_t_police a " +
//              " left join frm_department b on a.glbm=b.glbm where b.glbm not like '53000000%' ) t " +
//              " INNER JOIN frm_department t1 on CONCAT(t.sjbm,'00000000')=t1.glbm  " +
//              " union all" +
//              " select a.id as jybh,a.glbm,b.bmmc as sjmc" +
//              " from base_t_police a   left join frm_department b on a.glbm=b.glbm where b.glbm  like '53000000%' " +
//              ") v on u.user=v.jybh order by pv desc");

      SELECT("t.id,t.name as sjmc,if(isnull(y.pv),0,y.pv) pv from t_escheduler_glbm_sort t LEFT JOIN\n" +
              "(\n" +
              "SELECT v.glbm,v.sjmc, sum(u.pv) as pv from (\n" +
              "select  count(1) as pv,user_name from t_escheduler_user_login where substr(login_time,1,7)=substr(now(),1,7)  GROUP BY user_name \n" +
              ") u \n" +
              "INNER JOIN (\n" +
              " select a.id as jybh,a.glbm,b.bmmc as sjmc\n" +
              " from base_t_police a   left join frm_department b on a.glbm=b.glbm where b.glbm  like '53000000%' \n" +
              ") v on u.user_name=v.jybh GROUP BY v.glbm,v.sjmc\n" +
              ") y on t.glbm=y.glbm ORDER BY pv desc,id");

    }}.toString();
  }


  /**
   * get user login PV
   * @param parameter
   * @return
   */
  public String getTopZD(Map<String, Object> parameter) {
    return new SQL() {{
//      SELECT(" v.sjmc, cast(u.pv as char) pv from (" +
//              "select  count(1) as pv,user from t_escheduler_user_login  GROUP BY user ) u " +
//              "INNER JOIN (" +
//              "select t.jybh,t.glbm,t1.bmmc as sjmc from (select a.jybh,a.glbm,b.bmmc,SUBSTR(a.glbm,1,4) as sjbm from base_t_police a " +
//              " left join frm_department b on a.glbm=b.glbm where b.glbm not like '53000000%' ) t " +
//              " INNER JOIN frm_department t1 on CONCAT(t.sjbm,'00000000')=t1.glbm  " +
//              " union all" +
//              " select a.id as jybh,a.glbm,b.bmmc as sjmc" +
//              " from base_t_police a   left join frm_department b on a.glbm=b.glbm where b.glbm  like '53000000%' " +
//              ") v on u.user=v.jybh order by pv desc");

      SELECT("d.bmmc as sjmc,sum(if(isnull(c.pv),0,c.pv)) pv from (select bmmc from frm_department where bmmc like '%支队' group by bmmc) d LEFT JOIN (\n" +
              "SELECT v.sjmc,u.pv  from ( select  count(1) as pv,user_name from t_escheduler_user_login where substr(login_time,1,7)=substr(now(),1,7)  GROUP BY user_name ) u  \n" +
              " INNER JOIN ( select t.jybh,t.glbm,t1.bmmc as sjmc from (\n" +
              "select a.jybh,a.glbm,b.bmmc,SUBSTR(a.glbm,1,4) as sjbm from base_t_police a  \n" +
              "  left join frm_department b on a.glbm=b.glbm where b.glbm not like '53000000%' ) t  \n" +
              "  INNER JOIN frm_department t1 on CONCAT(t.sjbm,'00000000')=t1.glbm   \n" +
              " ) v on u.user_name=v.jybh order by pv desc ) c on c.sjmc=d.bmmc group by d.bmmc order by  pv desc");

    }}.toString();
  }




}
