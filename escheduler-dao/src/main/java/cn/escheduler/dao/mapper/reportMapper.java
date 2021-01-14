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

import cn.escheduler.dao.model.AtomicService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;

public interface reportMapper {

    @Results(value = {
            @Result(property = "reportCode", column = "reportCode", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = reportMapperProvider.class, method = "queryReport")
    Map queryReport(@Param("reportCode") String reportCode);

    @Results(value = {
            @Result(property = "reportCode", column = "reportCode", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = reportMapperProvider.class, method = "queryReportParam")
    List<Map<String,Object>> queryReportParam(@Param("reportId") String reportId);

    @Results(value = {
            @Result(property = "reportCode", column = "reportCode", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = reportMapperProvider.class, method = "queryReportParamDict")
    List<Map<String,Object>> queryReportParamDict(@Param("attributeValue") String attributeValue);

    @Results(value = {
            @Result(property = "reportCode", column = "reportCode", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = reportMapperProvider.class, method = "queryReportParamDictForTree")
    List<Map<String,Object>> queryReportParamDictForTree(@Param("paramType") String paramType,@Param("parentId") String parentId,@Param("glbmbm") String glbmbm);


    @Results(value = {
            @Result(property = "reportCode", column = "reportCode", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = reportMapperProvider.class, method = "queryReportParamDictForParent")
    Map<String, Object> queryReportParamDictForParent(@Param("parentId") String parentId);

    @SelectProvider(type = reportMapperProvider.class, method = "countReport")
    Integer countReport();

    @SelectProvider(type = reportMapperProvider.class, method = "reportAvgInvokeTimes")
    Integer reportAvgInvokeTimes();
}
