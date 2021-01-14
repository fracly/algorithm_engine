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
package cn.escheduler.api.controller;

import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.ReportFromService;
import cn.escheduler.api.service.ResourcesService;
import cn.escheduler.api.utils.Constants;

import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static cn.escheduler.api.enums.Status.*;

/**
 * process instance controller
 */
@RestController
@RequestMapping("reportFrom")
public class ReportFormController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ReportFormController.class);


    @Autowired
    ReportFromService reportFromService;

    @Autowired
    private ResourcesService resourceService;

    /**
     * query process instance list paging
     *
     * @param loginUser
     * @param searchVal
     * @param status
     * @return
     */
    @GetMapping(value="list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryProcessInstanceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                                   @RequestParam(value = "status", required = false) String status,
                                                                   @RequestParam(value = "roleId", required = false) String roleId,
                                                                   @RequestParam("pageNo") Integer pageNo,
                                                                   @RequestParam("pageSize") Integer pageSize){
        try{
            logger.info("query all process instance list, login user:{},searchVal:{},state type:{},page number:{}, page size:{}",
                    loginUser.getUserName(), searchVal, status, pageNo, pageSize);
            Map<String, Object> result = reportFromService.queryReportFromList(
                    loginUser, searchVal,status, pageNo, pageSize,roleId);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * query process instance list
     *
     * @param status
     * @return
     */
    @GetMapping(value="getListForInterlayer")
    @ResponseStatus(HttpStatus.OK)
    public Result list(@RequestParam(value = "groupId", required = false) String groupId,
                       @RequestParam(value = "searchVal", required = false) String searchVal,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "roleId", required = false) String roleId,
                       @RequestParam("pageNo") Integer pageNo,
                       @RequestParam("pageSize") Integer pageSize){
        try{
            logger.info("query all process instance list, groupId:{},searchVal:{},state type:{},page number:{}, page size:{}",
                    groupId, searchVal, status, pageNo, pageSize);
            Map<String, Object> result = reportFromService.getReportFromInfo(
                    groupId, searchVal,status, pageNo, pageSize,roleId);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * updateProcessInstance data source
     *
     * @param reportId
     * @param status
     * @return
     */
    @GetMapping(value = "/onLine")
    @ResponseStatus(HttpStatus.OK)
    public Result onLine(@RequestParam("id") int reportId,
                                   @RequestParam("status") int status) {
        logger.info("reportId {} status: {}",
                reportId, status);
        try {
            Map<String, Object> update = reportFromService.onLine(reportId, status);
            return returnDataList(update);
        } catch (Exception e) {
            return error(UPDATE_STATUS_ERROR.getCode(), UPDATE_STATUS_ERROR.getMsg());
        }
    }

    /**
     * updateProcessInstance data source
     *
     * @param reportId
     * @return
     */
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@RequestParam("id") int reportId) {
        logger.info("reportId {}",
                reportId);
        try {
            Map<String, Object> update = reportFromService.delete(reportId);
            return returnDataList(update);
        } catch (Exception e) {
            return error(DELETE_REPORT_ERROR.getCode(), DELETE_REPORT_ERROR.getMsg());
        }
    }

    /**
     * query process instance list paging
     *
     * @return
     */
    @GetMapping(value="/getGroup")
    @ResponseStatus(HttpStatus.OK)
    public Result getGroup(){
        try{

            Map<String, Object> result = reportFromService.getGroup();
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * query process instance list paging
     *
     * @return
     */
    @GetMapping(value="/getGroupList")
    @ResponseStatus(HttpStatus.OK)
    public Result getGroupListPage(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                            @RequestParam("searchVal") String searchVal,
                            @RequestParam("pageNo") Integer pageNo,
                            @RequestParam("pageSize") Integer pageSize){
        try{

            Map<String, Object> result = reportFromService.getGroupList(searchVal,pageNo,pageSize);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }
    /**
     * query process instance list paging
     *
     * @return
     */
    @GetMapping(value="/createGroup")
    @ResponseStatus(HttpStatus.OK)
    public Result createGroup(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @RequestParam(value ="name")String groupName,
                              @RequestParam(value = "status") String status,
                              @RequestParam(value = "desc") String desc){
        try{
            Map<String, Object> result = reportFromService.createGroup(loginUser.getUserName(),groupName,status,desc);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(CREATE_REPORT_GROUP_ERROR.getMsg(),e);
            return error(Status.CREATE_REPORT_GROUP_ERROR.getCode(), Status.CREATE_REPORT_GROUP_ERROR.getMsg());
        }
    }
    /**
     *
     *
     * @return
     */
    @GetMapping(value="/updateGroup")
    @ResponseStatus(HttpStatus.OK)
    public Result updateGroup(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @RequestParam(value ="name")String groupName,
                              @RequestParam(value = "id") Integer id,
                              @RequestParam(value = "desc") String desc){
        try{
            Map<String, Object> result = reportFromService.updateGroup(groupName,id,desc);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(CREATE_REPORT_GROUP_ERROR.getMsg(),e);
            return error(Status.CREATE_REPORT_GROUP_ERROR.getCode(), Status.CREATE_REPORT_GROUP_ERROR.getMsg());
        }
    }
    /**
     *
     * @param groupId
     * @return
     */
    @GetMapping(value = "/deleteGroup")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteGroup(@RequestParam("id") int groupId) {
        logger.info("groupId {}",
                groupId);
        try {
            Map<String, Object> update = reportFromService.deleteGroup(groupId);
            return returnDataList(update);
        } catch (Exception e) {
            return error(DELETE_REPORT_ERROR.getCode(), DELETE_REPORT_ERROR.getMsg());
        }
    }
    /**
     * get Roles
     *
     * @return
     */
    @GetMapping(value="/getRoles")
    @ResponseStatus(HttpStatus.OK)
    public Result getRoles(){
        try{

            Map<String, Object> result = reportFromService.getRoles();
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }


    /**
     * getDict
     *
     * @return
     */
    @GetMapping(value="/getDict")
    @ResponseStatus(HttpStatus.OK)
    public Result getDicr(){
        try{

            Map<String, Object> result = reportFromService.getDict();
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * getDictInfo
     *
     * @return
     */
    @GetMapping(value="/getDictInfo")
    @ResponseStatus(HttpStatus.OK)
    public Result getDicrInfo(@RequestParam(value = "id") int parentId){
        try{

            Map<String, Object> result = reportFromService.getDictInfo(parentId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getCode(), Status.QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * getReportInfo
     *
     * @return
     */
    @GetMapping(value="/getReportInfo")
    @ResponseStatus(HttpStatus.OK)
    public Result getReportInfo(@RequestParam(value = "id") int id){
        try{

            Map<String, Object> result = reportFromService.getReportInfo(id);
            return returnDataList(result);
        }catch (Exception e){
            return error(FAILED.getCode(), Status.FAILED.getMsg());
        }
    }

    /**
     * query process instance list paging
     *
     * @return
     */
    @PostMapping(value="/createReport")
    @ResponseStatus(HttpStatus.OK)
    public Result createReport(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                               @RequestBody Map<String,Object> report){
        try{
            String reportId = report.get("id").toString();
            String reportName = report.get("name").toString();
            String reportCode = report.get("code").toString();
            String reportType = report.get("type").toString();
            String path = report.get("path").toString();
            String dataSourceType = report.get("dataSourceType")==null?"":report.get("dataSourceType").toString();
            int dataSourceId =  report.get("dataSourceId")==null?0 :Integer.parseInt(report.get("dataSourceId").toString());
            String dataSourceName = report.get("dataSourceName")==null?"":report.get("dataSourceName").toString();
            String sql = report.get("sql")==null?"":report.get("sql").toString();
            String status = report.get("status").toString();
            int groupId = report.get("groupId")==null?0:Integer.parseInt(report.get("groupId").toString());
            String roleId = report.get("roleId")==null?"":report.get("roleId").toString();
            String roleName = "";
            String param = report.get("param")==null?null:report.get("param").toString();
            String origin_file_name = report.get("origin_file_name").toString();

            Map<String, Object> result = reportFromService.createReport(loginUser.getUserName(),reportId,reportName,reportCode,reportType
                    ,dataSourceType,dataSourceId,dataSourceName
                    ,sql,status,groupId,roleId,roleName,param,path,origin_file_name);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(CREATE_REPORT_GROUP_ERROR.getMsg(),e);
            return error(Status.CREATE_REPORT_GROUP_ERROR.getCode(), Status.CREATE_REPORT_GROUP_ERROR.getMsg());
        }
    }

    /**
     * create resource
     *
     * @param file
     */
    @PostMapping(value = "/create")
    public Result createFtpResource(@RequestParam("file") MultipartFile file) {
        try {
            logger.info(" create resource,  file: {},{}",
                    file.getName(), file.getOriginalFilename());
            return resourceService.createFtpResource(file);
        } catch (Exception e) {
            logger.error(CREATE_RESOURCE_ERROR.getMsg(),e);
            return error(CREATE_RESOURCE_ERROR.getCode(), CREATE_RESOURCE_ERROR.getMsg());
        }
    }

    /**
     * download excel file
     *
     * @param loginUser
     * @param name
     */
    @GetMapping(value = "/download")
    @ResponseBody
    public ResponseEntity downloadResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @RequestParam(value = "name") String name) {
        try{
            logger.info("login user {}, download resource : {}",
                    loginUser.getUserName(), name);
            org.springframework.core.io.Resource file = cn.escheduler.api.utils.FileUtils.file2Resource(ConfigurationManager.getProperty(Constants.EXCLEPATH)+"/"+name);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Status.RESOURCE_NOT_EXIST.getMsg());
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        }catch (Exception e){
            logger.error(DOWNLOAD_RESOURCE_FILE_ERROR.getMsg(),e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Status.DOWNLOAD_RESOURCE_FILE_ERROR.getMsg());
        }
    }

    /**
     * parseSqlAndExcle
     *
     */
    @GetMapping(value = "/parseSqlAndExcle")
    public Result parseSqlAndExcle(@RequestParam(value = "id") int id) {
        try {
            Map<String, Object> stringObjectMap = reportFromService.parseSqlAndExcle(id);
            return returnDataList(stringObjectMap);
        } catch (Exception e) {
            logger.error(PARAS_SQL_EXCLE_ERROR.getMsg(),e);
            return error(PARAS_SQL_EXCLE_ERROR.getCode(), PARAS_SQL_EXCLE_ERROR.getMsg());
        }
    }


    /**
     * 报表的调用次数
     *
     * @param code
     * @return
     */
    @GetMapping(value="addUseCountByCode")
    @ResponseStatus(HttpStatus.OK)
    public Result addUseCountByCode(@RequestParam(value = "code", required = false) String code){
        try{
            Map<String, Object> result = reportFromService.addUseCountByCode(code);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(ADD_REPORT_USE_COUNT_ERROR.getMsg(),e);
            return error(Status.ADD_REPORT_USE_COUNT_ERROR.getCode(), Status.ADD_REPORT_USE_COUNT_ERROR.getMsg());
        }
    }

}
