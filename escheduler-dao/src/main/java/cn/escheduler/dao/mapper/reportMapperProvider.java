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

public class reportMapperProvider {

    public static final String REPORT_TABLE_NAME = "report";
    public static final String REPORT_PARAM_TABLE_NAME = "report_param";
    public static final String DICT_TABLE_NAME = "dict";

    public String queryReport(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("report_id as reportId");
            SELECT("report_name as reportName");
            SELECT("report_code as reportCode");
            SELECT("report_type as reportType");
            SELECT("template_path as templatePath");
            SELECT("datasource_type as datasourceType");
            SELECT("datasource_id as datasourceId");
            SELECT("datasource_name as datasourceName");
            SELECT("report_sql as reportSql");
            SELECT("status as status");
            SELECT("create_user AS operNo");
            SELECT("create_time AS operTime");
            SELECT("report_head AS reportHead");
            SELECT("publish_path AS publishPath");
            SELECT("group_id AS groupId");
            SELECT("role_id AS roleId");
            SELECT("role_name AS roleName");
            SELECT("report_head_mid AS reportHeadMid");
            FROM(REPORT_TABLE_NAME );
            WHERE("report_code = #{reportCode}");
        }}.toString();
    }

    public String queryReportParam(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("param_id as paramId");
            SELECT("param_name as paramName");
            SELECT("param_desc as paramDesc");
            SELECT("param_type as paramType");
            SELECT("report_id as reportId");
            SELECT("attribute_value as attributeValue");
            FROM(REPORT_PARAM_TABLE_NAME );
            WHERE("report_id = #{reportId}");
        }}.toString();
    }

    public String queryReportParamDict(Map<String, Object> parameter){
        return new SQL(){{
            SELECT("dict_id as dictId");
            SELECT("dict_name as dictName");
            SELECT("dict_desc as dictDesc");
            SELECT("dict_type as dictType");
            SELECT("dict_code as dictCode");
            SELECT("create_user as operNo");
            SELECT("create_time as operTime");
            SELECT("parent_Id as parentId");
            FROM(DICT_TABLE_NAME );
            WHERE("dict_name = #{attributeValue}");
            WHERE("parent_Id is not null");
            WHERE("dict_type ='select'");
        }}.toString();
    }

    public String queryReportParamDictForTree(Map<String, Object> parameter){
        String s = new SQL() {{
            SELECT("dict_id as dictId");
            SELECT("dict_name as dictName");
            SELECT("dict_desc as dictDesc");
            SELECT("dict_type as dictType");
            SELECT("dict_code as dictCode");
            SELECT("create_user as operNo");
            SELECT("create_time as operTime");
            SELECT("parent_Id as parentId");
            FROM(DICT_TABLE_NAME);
            WHERE("dict_name = #{paramType}");
            Object parentId = parameter.get("parentId");
            if (parentId != null && StringUtils.isNotEmpty(parentId.toString()) && !"NoQueryRequired".equals(parentId.toString())) {
                WHERE("parent_Id = #{parentId}");
            }else if("NoQueryRequired".equals(parentId.toString())){

            }else{
                WHERE("parent_Id is null");
            }
            Object glbmbm = parameter.get("glbmbm");
            if (glbmbm != null && StringUtils.isNotEmpty(glbmbm.toString())) {
                WHERE("dict_code = #{glbmbm}");
            }else{
            }
        }}.toString();
        return s;
    }


    public String queryReportParamDictForParent(Map<String, Object> parameter){
        String s = new SQL() {{
            SELECT("dict_id as dictId");
            SELECT("dict_name as dictName");
            SELECT("dict_desc as dictDesc");
            SELECT("dict_type as dictType");
            SELECT("dict_code as dictCode");
            SELECT("create_user as operNo");
            SELECT("create_time as operTime");
            SELECT("parent_Id as parentId");
            FROM(DICT_TABLE_NAME);
            WHERE("dict_id = #{parentId}");
        }}.toString();
        return s;
    }

    public String countReport(Map<String, Object> paramter) {
        return new SQL() {{
            SELECT("count(1)");
            FROM(REPORT_TABLE_NAME);
        }}.toString();
    }

    public String reportAvgInvokeTimes(Map<String, Object> paramter) {
        return new SQL() {{
            SELECT("sum(use_count)");
            FROM(REPORT_TABLE_NAME);
        }}.toString();
    }
}
