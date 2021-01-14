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
package cn.escheduler.api.service;

import cn.escheduler.common.utils.StringUtils;
import cn.escheduler.dao.mapper.reportMapper;
import cn.escheduler.dao.model.DataSource;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * base DAG service
 */
@Service
public class reportService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(reportService.class);

    @Autowired
    private reportMapper reportMapper;

    @Autowired
    private DataSourceService dataSourceService;


    /*服务封装接口请求地址*/
    public Map queryParms(String reportCode,String status,String glbmbm) {
        HashMap<String, Object> resMap = new HashMap<>();
        //获取报表相关信息
        Map map = reportMapper.queryReport(reportCode);
        if (map != null && !"1".equals(String.valueOf(map.get("status"))) && !"1".equals(status)) {
            resMap.put("code", "-1");
            resMap.put("msg", "报表已下线 无法查看");
            return resMap;
        }
        if (map == null) {
            resMap.put("code", "-1");
            resMap.put("msg", "报表编码不存在，请联系管理员");
            return resMap;
        }
        resMap.put("report", map);
        //获取报表数据源信息
        try {
            DataSource dataSource = dataSourceService.queryDataSourceById(Integer.parseInt(String.valueOf(map.get("datasourceId"))));
            if (dataSource == null) {
                resMap.put("code", "-1");
                resMap.put("msg", "报表数据源信息配置有误，请联系管理员");
                return resMap;
            }
            if (!dataSourceService.checkConnection(dataSource.getType(), dataSource.getConnectionParams())) {
                resMap.put("code", "-1");
                resMap.put("msg", "报表数据源连接有误，请联系管理员");
                return resMap;
            }
            resMap.put("reportDataSource", dataSource);
        } catch (Exception e) {
            logger.error("获取数据源信息出错：" + e);
            resMap.put("code", "-1");
            resMap.put("msg", "报表数据源连接有误，请联系管理员");
            return resMap;
        }
        //获取报表查询条件信息
        List<Map<String, Object>> reportParam = reportMapper.queryReportParam(String.valueOf(map.get("reportId")));
        chooseParams(reportParam,glbmbm);
        resMap.put("code", "1");
        resMap.put("msg", "Successful call");
        resMap.put("reportParam", reportParam);
        return resMap;
    }


    /**
     * 封装时间查询参数
     *
     * @param param
     */
    private void putTimeParam(Map<String, Object> param) {
        if (!param.containsKey("attributeValue")) {
            return;
        }
        String attributeValue = String.valueOf(param.get("attributeValue"));
        Map map = JSON.parseObject(attributeValue, Map.class);
        if (!map.containsKey("dataFormat") || !map.containsKey("queryGrain")) {
            return;
        }
        String queryGrain = String.valueOf(map.get("queryGrain"));
        String dataFormat = String.valueOf(map.get("dataFormat"));
        List<Map> maps = new ArrayList<>();
        for (String item : queryGrain.split("\\|")) {
            maps.add(judgeTimeMap(item, dataFormat));
        }
        //保存查询条件时间格式
        param.put("timeFormat",dataFormat);
        param.put("child",maps);
    }

    /**
     * 处理时间格式中的粒度 并封装对象
     *
     * @param grain
     * @param dataFormat
     */
    private Map<String, Object> judgeTimeMap(String grain, String dataFormat) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", grain);
        map.put("type", grain.substring(0, 1));
        switch (grain) {
            case "11": case "21":
                if("11".equals(grain)){
                    map.put("label", "按年查询");
                }else{
                    map.put("label", "按年区间查询");
                }
                map.put("dataFormat", dataFormat.substring(0, 4));
                break;
            case "12":case "22":
                if("12".equals(grain)){
                    map.put("label", "按月查询");
                }else{
                    map.put("label", "按月区间查询");
                }
                if (dataFormat.indexOf("-") != -1 || dataFormat.indexOf("/") != -1) {
                    map.put("dataFormat", dataFormat.substring(0, 7));
                }else{
                    map.put("dataFormat", dataFormat.substring(0, 6));
                }
                break;
            case "13":case "23":
                if("13".equals(grain)){
                    map.put("label", "按日查询");
                }else{
                    map.put("label", "按日区间查询");
                }
                if (dataFormat.indexOf("-") != -1 || dataFormat.indexOf("/") != -1) {
                    map.put("dataFormat", dataFormat.substring(0, 10));
                }else{
                    map.put("dataFormat", dataFormat.substring(0, 8));
                }
                break;
            case "14": case "24":
                if("14".equals(grain)){
                    map.put("label", "按时查询");
                }else{
                    map.put("label", "按时区间查询");
                }
                if (dataFormat.indexOf("-") != -1 || dataFormat.indexOf("/") != -1) {
                    map.put("dataFormat", dataFormat.substring(0, 13));
                }else{
                    map.put("dataFormat", dataFormat.substring(0, 11));
                }
                map.put("HourFormat", "HH");
                break;
            case "15": case "25":
                if("15".equals(grain)){
                    map.put("label", "按分查询");
                }else{
                    map.put("label", "按分区间查询");
                }
                if (dataFormat.indexOf("-") != -1 || dataFormat.indexOf("/") != -1) {
                    map.put("dataFormat", dataFormat.substring(0, 16));

                }else{
                    map.put("dataFormat", dataFormat.substring(0, 14));
                }
                map.put("HourFormat", "HH:mm");
                break;
            case "16": case "26":
                if("16".equals(grain)){
                    map.put("label", "按秒查询");
                }else{
                    map.put("label", "按秒区间查询");
                }
                if (dataFormat.indexOf("-") != -1 || dataFormat.indexOf("/") != -1) {
                    map.put("dataFormat", dataFormat.substring(0, 19));
                }else{
                    map.put("dataFormat", dataFormat.substring(0, 17));
                }
                map.put("HourFormat", "HH:mm:ss");
                break;
            default:
                break;
        }
        return map;
    }


    /**
     * 封裝字典表满足前台展示
     *
     * @param param
     * @param type
     */
    private void putMapParam(Map<String, Object> param, String type) {
        if ("tree".equals(type)) {
            if (param.containsKey("dictDesc")) {
                param.put("title", param.get("dictDesc"));
            }
            if (param.containsKey("dictCode")) {
                param.put("key", param.get("dictCode"));
                param.put("value", param.get("dictCode"));
            }
        }
        if ("s-select".equals(type) || "department".equals(type)) {
            if (param.containsKey("dictDesc")) {
                param.put("label", param.get("dictDesc"));
            }
            if (param.containsKey("dictCode")) {
                param.put("key", param.get("dictCode"));
                param.put("value", param.get("dictCode"));
            }
        }
    }


    /*递归查询*/
    private void getTreeForTree(List<Map<String, Object>> listTree, String paramType) {
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> param : listTree) {
            putMapParam(param, paramType);
            retList = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("dictName")), String.valueOf(param.get("dictId")),"");
            if (retList.size() > 0) {
                param.put("children", retList);
                //循环调用自己
                getTreeForTree(retList, paramType);
            }
        }
    }

    /*处理查询参数*/
    private void chooseParams( List<Map<String, Object>> reportParam,String glbmbm){
        //循环查询条件，找到需要封装的select信息
        for (Map<String, Object> param : reportParam) {
            //下拉框数据查询
            if ("select".equals(String.valueOf(param.get("paramType")))) {
                List<Map<String, Object>> Dict = reportMapper.queryReportParamDict(String.valueOf(param.get("attributeValue")));
                param.put("child", Dict);
            }
            //树数据查询
            if ("tree".equals(String.valueOf(param.get("paramType")))) {
                List<Map<String, Object>> DictChild = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), "","");
                if (DictChild.size() > 0) {
                    //数据赋值 满足前台展示
                    putMapParam(DictChild.get(0), String.valueOf(param.get("paramType")));
                    List<Map<String, Object>> listTree = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), String.valueOf(DictChild.get(0).get("dictId")),"");
                    getTreeForTree(listTree, String.valueOf(param.get("paramType")));
                    DictChild.get(0).put("children", listTree);
                    param.put("child", DictChild);
                }
            }
            //二级下拉框数据查询
            if ("s-select".equals(String.valueOf(param.get("paramType")))) {
                List<Map<String, Object>> DictChild = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), "","");
                if (DictChild.size() > 0) {
                    //数据赋值 满足前台展示
                    putMapParam(DictChild.get(0), String.valueOf(param.get("paramType")));
                    List<Map<String, Object>> listTree = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), String.valueOf(DictChild.get(0).get("dictId")),"");
                    getTreeForTree(listTree, String.valueOf(param.get("paramType")));
                    param.put("child", listTree);
                }
            }
            //部门数据查询
            if ("department".equals(String.valueOf(param.get("paramType")))) {
                //部门查询 先找自己 再找父节点和子节点
                List<Map<String, Object>> DictChild = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), "","");
                if (DictChild.size() == 0) {
                   return;
                }
                //数据赋值 满足前台展示
                Map<String, Object> objectMap = DictChild.get(0);
                String dictCode = String.valueOf(objectMap.get("dictCode"));
                if(!StringUtils.isEmpty(glbmbm) && glbmbm.length()>=10 && !StringUtils.isEmpty(dictCode)){
                    if(dictCode.length()<glbmbm.length()){
                        glbmbm=glbmbm.substring(0,dictCode.length());
                    }
                }
                List<Map<String, Object>> listTree = new ArrayList<>();
                if(glbmbm.equals(DictChild.get(0).get("dictCode"))){
                    listTree = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), String.valueOf(DictChild.get(0).get("dictId")),"");
                }else{
                    listTree = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), String.valueOf(DictChild.get(0).get("dictId")),glbmbm);
                }
                if(listTree.size()==0){
                    listTree = reportMapper.queryReportParamDictForTree(String.valueOf(param.get("attributeValue")), "NoQueryRequired",glbmbm);
                    if(listTree.size()==0){
                        param.put("child", listTree);
                        continue;
                    }
                    Map<String, Object> department = listTree.get(0);
                    String parentId = String.valueOf(department.get("parentId"));
                    Map<String, Object> parent = reportMapper.queryReportParamDictForParent(parentId);
                    putMapParam(parent, String.valueOf(parent.get("paramType")));
                }else{
                    putMapParam(objectMap, String.valueOf(objectMap.get("paramType")));
                }
                getTreeForTree(listTree, String.valueOf(param.get("paramType")));
                param.put("child", listTree);
            }
            //时间数据查询
            if ("time".equals(String.valueOf(param.get("paramType")))) {
                putTimeParam(param);
            }
        }
    }
}
