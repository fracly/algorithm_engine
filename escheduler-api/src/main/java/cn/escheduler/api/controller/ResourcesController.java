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

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.ResourcesService;
import cn.escheduler.api.service.UdfFuncService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.ResourceType;
import cn.escheduler.common.enums.UdfType;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.User;
import org.apache.commons.lang.StringUtils;
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
 * resources controller
 */
@RestController
@RequestMapping("resources")
public class ResourcesController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ResourcesController.class);


    @Autowired
    private ResourcesService resourceService;
    @Autowired
    private UdfFuncService udfFuncService;

    /**
     * create resource
     *
     * @param loginUser
     * @param alias
     * @param desc
     * @param file
     */
    @PostMapping(value = "/create")
    public Result createResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value = "type") ResourceType type,
                                 @RequestParam(value ="name")String alias,
                                 @RequestParam(value = "desc", required = false) String desc,
                                 @RequestParam("file") MultipartFile file) {
        try {
            logger.info("login user {}, create resource, type: {}, resource alias: {}, desc: {}, file: {},{}",
                    loginUser.getUserName(),type, alias, desc, file.getName(), file.getOriginalFilename());
            return resourceService.createResource(loginUser,alias, desc,type ,file);
        } catch (Exception e) {
            logger.error(CREATE_RESOURCE_ERROR.getMsg(),e);
            return error(CREATE_RESOURCE_ERROR.getCode(), CREATE_RESOURCE_ERROR.getMsg());
        }
    }

    /**
     * update resource
     *
     * @param loginUser
     * @param alias
     * @param desc
     */
    @PostMapping(value = "/update")
    public Result updateResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value ="id") int resourceId,
                                 @RequestParam(value = "type") ResourceType type,
                                 @RequestParam(value ="name")String alias,
                                 @RequestParam(value = "desc", required = false) String desc) {
        try {
            logger.info("login user {}, update resource, type: {}, resource alias: {}, desc: {}",
                    loginUser.getUserName(),type, alias, desc);
            return resourceService.updateResource(loginUser,resourceId,alias, desc,type);
        } catch (Exception e) {
            logger.error(UPDATE_RESOURCE_ERROR.getMsg(),e);
            return error(Status.UPDATE_RESOURCE_ERROR.getCode(), Status.UPDATE_RESOURCE_ERROR.getMsg());
        }
    }

    /**
     * query resources list
     *
     * @param loginUser
     * @return
     */
    @GetMapping(value="/list")
    @ResponseStatus(HttpStatus.OK)
    public Result querytResourceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @RequestParam(value ="type")ResourceType type
    ){
        try{
            logger.info("query resource list, login user:{}, resource type:{}", loginUser.getUserName(), type.toString());
            Map<String, Object> result = resourceService.queryResourceList(loginUser, type);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_RESOURCES_LIST_ERROR.getMsg(),e);
            return error(Status.QUERY_RESOURCES_LIST_ERROR.getCode(), Status.QUERY_RESOURCES_LIST_ERROR.getMsg());
        }
    }

    /**
     * query resources list paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value="/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result querytResourceListPaging(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @RequestParam(value ="type")ResourceType type,
                                     @RequestParam("pageNo") Integer pageNo,
                                     @RequestParam(value = "searchVal", required = false) String searchVal,
                                     @RequestParam("pageSize") Integer pageSize
    ){
        try{
            logger.info("query resource list, login user:{}, resource type:{}, search value:{}",
                    loginUser.getUserName(), type.toString(), searchVal);
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }

            searchVal = ParameterUtils.handleEscapes(searchVal);
            result = resourceService.queryResourceListPaging(loginUser,type,searchVal,pageNo, pageSize);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_RESOURCES_LIST_PAGING.getMsg(),e);
            return error(Status.QUERY_RESOURCES_LIST_PAGING.getCode(), Status.QUERY_RESOURCES_LIST_PAGING.getMsg());
        }
    }


    /**
     * delete resource
     *
     * @param loginUser
     * @param resourceId
     */
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value ="id") int resourceId
    ) {
        try{
            logger.info("login user {}, delete resource id: {}",
                    loginUser.getUserName(),resourceId);
            return resourceService.delete(loginUser,resourceId);
        }catch (Exception e){
            logger.error(DELETE_RESOURCE_ERROR.getMsg(),e);
            return error(Status.DELETE_RESOURCE_ERROR.getCode(), Status.DELETE_RESOURCE_ERROR.getMsg());
        }
    }


    /**
     * verify resource by alias and type
     *
     * @param loginUser
     * @param alias
     * @param type
     * @return
     */
    @GetMapping(value = "/verify-name")
    @ResponseStatus(HttpStatus.OK)
    public Result verifyResourceName(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @RequestParam(value ="name") String alias,
                                     @RequestParam(value ="type")ResourceType type
    ) {
        try {
            logger.info("login user {}, verfiy resource alias: {},resource type: {}",
                    loginUser.getUserName(), alias,type);

            return resourceService.verifyResourceName(alias,type,loginUser);
        } catch (Exception e) {
            logger.error(VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR.getMsg(), e);
            return error(Status.VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR.getCode(), Status.VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR.getMsg());
        }
    }

    /**
     * view resource file online
     *
     * @param loginUser
     * @param resourceId
     */
    @GetMapping(value = "/view")
    public Result viewResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                               @RequestParam(value = "id") int resourceId,
                               @RequestParam(value = "skipLineNum") int skipLineNum,
                               @RequestParam(value = "limit") int limit
    ) {
        try{
            logger.info("login user {}, view resource : {}, skipLineNum {} , limit {}",
                    loginUser.getUserName(),resourceId,skipLineNum,limit);

            return resourceService.readResource(resourceId,skipLineNum,limit);
        }catch (Exception e){
            logger.error(VIEW_RESOURCE_FILE_ON_LINE_ERROR.getMsg(),e);
            return error(Status.VIEW_RESOURCE_FILE_ON_LINE_ERROR.getCode(), Status.VIEW_RESOURCE_FILE_ON_LINE_ERROR.getMsg());
        }
    }

    /**
     * create resource file online
     *
     * @param loginUser
     * @param type
     * @param fileName
     * @param fileSuffix
     * @param desc
     * @param content
     * @return
     */
    @PostMapping(value = "/online-create")
    public Result onlineCreateResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @RequestParam(value = "type") ResourceType type,
                                       @RequestParam(value ="fileName")String fileName,
                                       @RequestParam(value ="suffix")String fileSuffix,
                                       @RequestParam(value = "desc", required = false) String desc,
                                       @RequestParam(value = "content") String content
    ) {
        try{
            logger.info("login user {}, online create resource! fileName : {}, type : {}, suffix : {},desc : {},content : {}",
                    loginUser.getUserName(),type,fileName,fileSuffix,desc,content);
            if(StringUtils.isEmpty(content)){
                logger.error("resource file contents are not allowed to be empty");
                return error(Status.RESOURCE_FILE_IS_EMPTY.getCode(), RESOURCE_FILE_IS_EMPTY.getMsg());
            }
            return resourceService.onlineCreateResource(loginUser,type,fileName,fileSuffix,desc,content);
        }catch (Exception e){
            logger.error(CREATE_RESOURCE_FILE_ON_LINE_ERROR.getMsg(),e);
            return error(Status.CREATE_RESOURCE_FILE_ON_LINE_ERROR.getCode(), Status.CREATE_RESOURCE_FILE_ON_LINE_ERROR.getMsg());
        }
    }

    /**
     * edit resource file online
     *
     * @param loginUser
     * @param file
     */
    @PostMapping(value = "/update-content")
    public Result updateResourceContent(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @RequestBody Map<String,Object> file
    ) {
        try{

            int resourceId = Integer.parseInt(file.get("id").toString());
            String content = file.get("content").toString();
            logger.info("login user {}, updateProcessInstance resource : {}",
                    loginUser.getUserName(),resourceId);
            if(StringUtils.isEmpty(content)){
                logger.error("The resource file contents are not allowed to be empty");
                return error(Status.RESOURCE_FILE_IS_EMPTY.getCode(), RESOURCE_FILE_IS_EMPTY.getMsg());
            }
            return resourceService.updateResourceContent(resourceId,content);
        }catch (Exception e){
            logger.error(EDIT_RESOURCE_FILE_ON_LINE_ERROR.getMsg(),e);
            return error(Status.EDIT_RESOURCE_FILE_ON_LINE_ERROR.getCode(), Status.EDIT_RESOURCE_FILE_ON_LINE_ERROR.getMsg());
        }
    }

    /**
     * download resource file
     *
     * @param loginUser
     * @param resourceId
     */
    @GetMapping(value = "/download")
    @ResponseBody
    public ResponseEntity downloadResource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @RequestParam(value = "id") int resourceId) {
        try{
            logger.info("login user {}, download resource : {}",
                    loginUser.getUserName(), resourceId);
            Resource file = resourceService.downloadResource(resourceId);
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
     * create udf function
     * @param loginUser
     * @param type
     * @param funcName
     * @param argTypes
     * @param database
     * @param desc
     * @param resourceId
     * @return
     */
    @PostMapping(value = "/udf-func/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createUdfFunc(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "type") UdfType type,
                                @RequestParam(value ="funcName")String funcName,
                                @RequestParam(value ="className")String className,
                                @RequestParam(value ="argTypes", required = false)String argTypes,
                                @RequestParam(value ="database", required = false)String database,
                                @RequestParam(value = "desc", required = false) String desc,
                                @RequestParam(value = "resourceId") int resourceId) {
        logger.info("login user {}, create udf function, type: {},  funcName: {},argTypes: {} ,database: {},desc: {},resourceId: {}",
                loginUser.getUserName(),type, funcName, argTypes,database,desc, resourceId);
        Result result = new Result();

        try {
            return udfFuncService.createUdfFunction(loginUser,funcName,className,argTypes,database,desc,type,resourceId);
        } catch (Exception e) {
            logger.error(CREATE_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.CREATE_UDF_FUNCTION_ERROR.getCode(), Status.CREATE_UDF_FUNCTION_ERROR.getMsg());
        }
    }

    /**
     * view udf function
     *
     * @param loginUser
     * @param id
     * @return
     */
    @GetMapping(value = "/udf-func/update-ui")
    @ResponseStatus(HttpStatus.OK)
    public Result updateUIUdfFunction(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                      @RequestParam("id") int id)
    {
        Result result = new Result();
        logger.info("login user {}, query udf{}",
                loginUser.getUserName(), id);
        try {
            Map<String, Object> map = udfFuncService.queryUdfFuncDetail(id);
            return returnDataList(map);
        } catch (Exception e) {
            logger.error(VIEW_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.VIEW_UDF_FUNCTION_ERROR.getCode(), Status.VIEW_UDF_FUNCTION_ERROR.getMsg());
        }
    }

    /**
     * updateProcessInstance udf function
     *
     * @param loginUser
     * @param type
     * @param funcName
     * @param argTypes
     * @param database
     * @param desc
     * @param resourceId
     * @return
     */
    @PostMapping(value = "/udf-func/update")
    public Result updateUdfFunc(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "id") int udfFuncId,
                                @RequestParam(value = "type") UdfType type,
                                @RequestParam(value ="funcName")String funcName,
                                @RequestParam(value ="className")String className,
                                @RequestParam(value ="argTypes", required = false)String argTypes,
                                @RequestParam(value ="database", required = false)String database,
                                @RequestParam(value = "desc", required = false) String desc,
                                @RequestParam(value = "resourceId") int resourceId) {
        try {
            logger.info("login user {}, updateProcessInstance udf function id: {},type: {},  funcName: {},argTypes: {} ,database: {},desc: {},resourceId: {}",
                    loginUser.getUserName(),udfFuncId,type, funcName, argTypes,database,desc, resourceId);
            Map<String, Object> result = udfFuncService.updateUdfFunc(udfFuncId,funcName,className,argTypes,database,desc,type,resourceId);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(UPDATE_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.UPDATE_UDF_FUNCTION_ERROR.getCode(), Status.UPDATE_UDF_FUNCTION_ERROR.getMsg());
        }
    }

    /**
     * query udf function list paging
     *
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value="/udf-func/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryUdfFuncList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @RequestParam("pageNo") Integer pageNo,
                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                   @RequestParam("pageSize") Integer pageSize
    ){
        try{
            logger.info("query udf functions list, login user:{},search value:{}",
                    loginUser.getUserName(), searchVal);
            Map<String, Object> result = checkPageParams(pageNo, pageSize);
            if(result.get(Constants.STATUS) != Status.SUCCESS){
                return returnDataListPaging(result);
            }

            result = udfFuncService.queryUdfFuncListPaging(loginUser,searchVal,pageNo, pageSize);
            return returnDataListPaging(result);
        }catch (Exception e){
            logger.error(QUERY_UDF_FUNCTION_LIST_PAGING_ERROR.getMsg(),e);
            return error(Status.QUERY_UDF_FUNCTION_LIST_PAGING_ERROR.getCode(), Status.QUERY_UDF_FUNCTION_LIST_PAGING_ERROR.getMsg());
        }
    }

    /**
     * query data resource by type
     *
     * @param loginUser
     * @return
     */
    @GetMapping(value="/udf-func/list")
    @ResponseStatus(HttpStatus.OK)
    public Result queryResourceList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @RequestParam("type") UdfType type){
        try{
            logger.info("query datasource list, user:{}, type:{}", loginUser.getUserName(), type.toString());
            Map<String, Object> result = udfFuncService.queryResourceList(loginUser,type.ordinal());
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUERY_DATASOURCE_BY_TYPE_ERROR.getMsg(),e);
            return error(Status.QUERY_DATASOURCE_BY_TYPE_ERROR.getCode(),QUERY_DATASOURCE_BY_TYPE_ERROR.getMsg());
        }
    }

    /**
     * verify udf function name can use or not
     *
     * @param loginUser
     * @param name
     * @return
     */
    @GetMapping(value = "/udf-func/verify-name")
    @ResponseStatus(HttpStatus.OK)
    public Result verifyUdfFuncName(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @RequestParam(value ="name") String name
    ) {
        logger.info("login user {}, verfiy udf function name: {}",
                loginUser.getUserName(),name);

        try{

            return udfFuncService.verifyUdfFuncByName(name);
        }catch (Exception e){
            logger.error(VERIFY_UDF_FUNCTION_NAME_ERROR.getMsg(),e);
            return error(Status.VERIFY_UDF_FUNCTION_NAME_ERROR.getCode(), Status.VERIFY_UDF_FUNCTION_NAME_ERROR.getMsg());
        }
    }

    /**
     * delete udf function
     *
     * @param loginUser
     * @param udfFuncId
     */
    @GetMapping(value = "/udf-func/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteUdfFunc(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value ="id") int udfFuncId
    ) {
        try{

            logger.info("login user {}, delete udf function id: {}", loginUser.getUserName(),udfFuncId);
            return udfFuncService.delete(udfFuncId);
        }catch (Exception e){
            logger.error(DELETE_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.DELETE_UDF_FUNCTION_ERROR.getCode(), Status.DELETE_UDF_FUNCTION_ERROR.getMsg());
        }
    }

    /**
     * authorized file resource list
     *
     * @param loginUser
     * @param userId
     * @return
     */
    @GetMapping(value = "/authed-file")
    @ResponseStatus(HttpStatus.CREATED)
    public Result authorizedFile(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam("userId") Integer userId) {
        try{
            logger.info("authorized file resource, user: {}, user id:{}", loginUser.getUserName(), userId);
            Map<String, Object> result =  resourceService.authorizedFile(loginUser, userId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(AUTHORIZED_FILE_RESOURCE_ERROR.getMsg(),e);
            return error(Status.AUTHORIZED_FILE_RESOURCE_ERROR.getCode(), Status.AUTHORIZED_FILE_RESOURCE_ERROR.getMsg());
        }
    }


    /**
     * unauthorized file resource list
     *
     * @param loginUser
     * @param userId
     * @return
     */
    @GetMapping(value = "/unauth-file")
    @ResponseStatus(HttpStatus.CREATED)
    public Result unauthorizedFile(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @RequestParam("userId") Integer userId) {
        try{
            logger.info("resource unauthorized file, user:{}, unauthorized user id:{}", loginUser.getUserName(), userId);
            Map<String, Object> result =  resourceService.unauthorizedFile(loginUser, userId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(UNAUTHORIZED_FILE_RESOURCE_ERROR.getMsg(),e);
            return error(Status.UNAUTHORIZED_FILE_RESOURCE_ERROR.getCode(), Status.UNAUTHORIZED_FILE_RESOURCE_ERROR.getMsg());
        }
    }


    /**
     * unauthorized udf function
     *
     * @param loginUser
     * @param userId
     * @return
     */
    @GetMapping(value = "/unauth-udf-func")
    @ResponseStatus(HttpStatus.CREATED)
    public Result unauthUDFFunc(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam("userId") Integer userId) {
        try{
            logger.info("unauthorized udf function, login user:{}, unauthorized user id:{}", loginUser.getUserName(), userId);

            Map<String, Object>  result =  resourceService.unauthorizedUDFFunction(loginUser, userId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(UNAUTHORIZED_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.UNAUTHORIZED_UDF_FUNCTION_ERROR.getCode(), Status.UNAUTHORIZED_UDF_FUNCTION_ERROR.getMsg());
        }
    }


    /**
     * authorized udf function
     *
     * @param loginUser
     * @param userId
     * @return
     */
    @GetMapping(value = "/authed-udf-func")
    @ResponseStatus(HttpStatus.CREATED)
    public Result authorizedUDFFunction(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @RequestParam("userId") Integer userId) {
        try{
            logger.info("auth udf function, login user:{}, auth user id:{}", loginUser.getUserName(), userId);
            Map<String, Object> result =  resourceService.authorizedUDFFunction(loginUser, userId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(AUTHORIZED_UDF_FUNCTION_ERROR.getMsg(),e);
            return error(Status.AUTHORIZED_UDF_FUNCTION_ERROR.getCode(), Status.AUTHORIZED_UDF_FUNCTION_ERROR.getMsg());
        }
    }
}
