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
import cn.escheduler.api.service.DataAssetService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.DataAsset;
import cn.escheduler.dao.model.DataAssetRule;
import cn.escheduler.dao.model.User;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * data asset controller
 */
@RestController
@RequestMapping("dataasset")
public class DataAssetController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataAssetController.class);

    @Autowired
    private DataAssetService dataAssetService;

    /**
     * get all data asset type
     */
    @GetMapping(value = "/getType")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetType() {

        List<Map<String, Object>> list = dataAssetService.getType();
        Result result = new Result();result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get all data asset label
     */
    @GetMapping(value = "/getLabel")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetLabel() {
        List<Map<String, Object>> list = dataAssetService.getLabel();
        Result result = new Result();result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get all data asset state
     */
    @GetMapping(value = "/getState")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetState() {
        List<Map<String, Object>> list = dataAssetService.getState();
        Result result = new Result();result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get all data asset destroy method
     */
    @GetMapping(value = "/getDestroyMethod")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetDestroyMethod () {
        List<Map<String, Object>> list = dataAssetService.getDetroyMethod();
        Result result = new Result();
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get all data asset owner
     */
    @GetMapping(value = "/getUser")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetUser() {
        List<Map<String, Object>> list = dataAssetService.getUser();
        Result result = new Result();result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get data asset list
     */
    @GetMapping(value = "/getDataAssetList")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetList() {
        List<Map<String, Object>> list = dataAssetService.getDataAssetList();
        Result result = new Result();result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get data asset list paging
     */
    @GetMapping(value = "/getListPaging")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetListPaging(String type, String labels, String dateRange, Integer pageNo, Integer pageSize) {
        List<DataAsset> list = dataAssetService.getListPaging(type, labels, dateRange, pageNo, pageSize);
        Result result = new Result();
        Long total = dataAssetService.getListTotal(type, labels, dateRange);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        result.setDataMap(map);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get data asset by primary key：id
     */
    @GetMapping(value = "/queryById")
    @ResponseStatus(HttpStatus.OK)
    public Result queryById(Integer id) {
        Result result = new Result();
        DataAsset dataAsset = dataAssetService.queryById(id);
        if(dataAsset != null ) {
            result.setData(dataAsset);
            putMsg(result, Status.CUSTOM_SUCESSS, "成功获取数据资产信息！");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "未查询到数据资产信息！");
        }
        return result;
    }

    /**
     * create data asset
     */
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createDataAsset(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  String name, String type, String labels, String detail) {
        Result result = new Result();
        logger.info("login user {} create dataasset name: {},  type: {}, labels: {}, detail: {}",
                loginUser.getUserName(), name, type, labels, detail);
        int insertCount = dataAssetService.create(loginUser, name, type, labels, detail);
        if(insertCount > 0 ){
            putMsg(result, Status.CUSTOM_SUCESSS, "添加数据资产成功！");
        } else{
            putMsg(result, Status.CUSTOM_FAILED, "添加数据资产失败！");
        }
        return result;
    }

    /**
     * update data asset
     */
    @PostMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    public Result updateDataAsset(User loginUser, Integer id, String name, String type, String labels, String detail) {
        Result result = new Result();

        logger.info("login user {} update dataasset id: {}",  id);
        int updateCount = dataAssetService.update(id, name, type, labels, detail);
        if( updateCount > 0 ) {
            putMsg(result, Status.CUSTOM_SUCESSS, "修改数据资产成功！");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "修改数据资产失败！");
        }
        return result;
    }

    /**
     * delete datasource by id
     */
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(User loginUser, int id) {
        Result result = new Result();
        logger.info("delete datasource,login user:{}, id:{}", loginUser.getUserName(), id);
        int deleteCount = dataAssetService.delete(id);
        if(deleteCount > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除数据资产成功！");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除数据资产失败");
        }
        return result;
    }

    /**
     * download data asset
     */
    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity download(HttpServletResponse response, String type, String labels, String dateRange) {
        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd hh:MM:ss");

        List<DataAsset> list = dataAssetService.getListPaging(type, labels, dateRange, 1, 1000000);
        for(DataAsset dataAsset : list) {
//            Sheet sheet = wb.createSheet(schema.getTablename());
//            Row header = sheet.createRow(0);
//            header.createCell(0).setCellValue(helper.createRichTextString("表名"));
//            header.createCell(1).setCellValue(helper.createRichTextString("表描述"));
//            header.createCell(2).setCellValue(helper.createRichTextString("数据层级"));
//            header.createCell(3).setCellValue(helper.createRichTextString("业务主题"));
//            header.createCell(4).setCellValue(helper.createRichTextString("详细描述"));
//            header.createCell(5).setCellValue(helper.createRichTextString("创建时间"));
//            header.createCell(6).setCellValue(helper.createRichTextString("创建人员"));
//
//            Row headerValue = sheet.createRow(1);
//            headerValue.createCell(0).setCellValue(helper.createRichTextString(schema.getTablename()));
//            headerValue.createCell(1).setCellValue(helper.createRichTextString(schema.getTabledescribe()));
//            headerValue.createCell(2).setCellValue(helper.createRichTextString(schema.getLayer()));
//            headerValue.createCell(3).setCellValue(helper.createRichTextString(schema.getTopic()));
//            headerValue.createCell(4).setCellValue(helper.createRichTextString(schema.getTabledetail()));
//            headerValue.createCell(5).setCellValue(helper.createRichTextString(simpleDateFormat.format(schema.getCreateTime())));
//            headerValue.createCell(6).setCellValue(helper.createRichTextString(schema.getCreateUser()));
//
//            Row fourthRow = sheet.createRow(3);
//            fourthRow.createCell(0).setCellValue(helper.createRichTextString("字段详情如下："));
//            Row columnHeader = sheet.createRow(4);
//            columnHeader.createCell(0).setCellValue(helper.createRichTextString("字段名"));
//            columnHeader.createCell(1).setCellValue(helper.createRichTextString("字段类型"));
//            columnHeader.createCell(2).setCellValue(helper.createRichTextString("字段描述"));
//
//            //获取Schema最新的版本信息
//            SchemaVersion schemaVersion = schemaService.queryLastestVersionSchema(schema.getLayer(), schema.getTablename(), "master");
//            String schemaText = schemaVersion.getSchemaText();
//            Map<String, String> map = JSONUtils.toMap(schemaText);
//            List<Map> columns = JSONUtils.toList(map.get("columns"), Map.class);
//            for(int i = 0; i < columns.size(); i ++) {
//                Row row = sheet.createRow( 5 + i );
//                row.createCell(0).setCellValue(helper.createRichTextString(columns.get(i).get("name").toString()));
//                row.createCell(1).setCellValue(helper.createRichTextString(columns.get(i).get("type").toString()));
//                Object comment = columns.get(i).get("comment");
//                if(comment != null) {
//                    row.createCell(2).setCellValue(helper.createRichTextString(comment.toString()));
//                }
//            }
        }
        String localFileName = "dataasset-" + new Date().getTime() + ".xls";
        try{
            File downloadDir = new File("download");
            if(!downloadDir.exists()){downloadDir.mkdir();}
            OutputStream fileOutputStream = new FileOutputStream("download" + File.separator + localFileName);
            wb.write(fileOutputStream);
            fileOutputStream.close();
            wb.close();
            org.springframework.core.io.Resource file = cn.escheduler.api.utils.FileUtils.file2Resource("download" + File.separator + localFileName);
            if(file == null){
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("下载元数据文件失败！");
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + localFileName + "\"")
                    .body(file);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("下载元数据文件出错！");
        }
    }

    /**
     * create data asset rule
     */
    @PostMapping(value = "/createRule")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createDataAssetRule(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  Integer assetId, String destroyMethod, Integer destroyPeriod) {
        Result result = new Result();
        int insertCount = dataAssetService.createRule(loginUser, assetId, destroyMethod, destroyPeriod);
        if(insertCount > 0 ){
            putMsg(result, Status.CUSTOM_SUCESSS, "添加数据资产规则成功！");
        } else{
            putMsg(result, Status.CUSTOM_FAILED, "添加数据资产规则失败！");
        }
        return result;
    }

    /**
     * update data asset rule
     */
    @PostMapping(value = "/updateRule")
    @ResponseStatus(HttpStatus.OK)
    public Result updateDataAssetRule(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer id,
                                      Integer assetId, String destroyMethod, Integer destroyPeriod) {
        Result result = new Result();
        int updateCount = dataAssetService.updateRule(loginUser, id, assetId, destroyMethod, destroyPeriod);
        if(updateCount > 0 ){
            putMsg(result, Status.CUSTOM_SUCESSS, "修改数据资产规则成功！");
        } else{
            putMsg(result, Status.CUSTOM_FAILED, "修改数据资产规则失败！");
        }
        return result;
    }

    /**
     * delete data asset rule
     */
    @GetMapping(value = "/deleteRule")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteDataAssetRule(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer id) {
        Result result = new Result();
        int deleteCount = dataAssetService.deleteRule(id);
        if(deleteCount > 0 ){
            putMsg(result, Status.CUSTOM_SUCESSS, "删除数据资产规则成功！");
        } else{
            putMsg(result, Status.CUSTOM_FAILED, "删除数据资产规则失败！");
        }
        return result;
    }

    /**
     * get data asset rule list paging
     */
    @GetMapping(value = "/getRuleListPaging")
    @ResponseStatus(HttpStatus.OK)
    public Result getDataAssetRuleListPaging(String name, Integer state, Integer userId, String dateRange, Integer pageNo, Integer pageSize) {
        List<DataAssetRule> list = dataAssetService.getRuleListPaging(name, state, userId, dateRange, pageNo, pageSize);
        Result result = new Result();
        Long total = dataAssetService.getRuleListTotal(name, state, userId, dateRange);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        result.setDataMap(map);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get data asset by primary key：id
     */
    @GetMapping(value = "/queryRuleById")
    @ResponseStatus(HttpStatus.OK)
    public Result queryRuleById(Integer id) {
        Result result = new Result();
        DataAssetRule dataAssetRule = dataAssetService.queryRuleById(id);
        if(dataAssetRule != null ) {
            result.setData(dataAssetRule);
            putMsg(result, Status.CUSTOM_SUCESSS, "成功获取数据资产规则信息！");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "未查询到数据资产规则信息！");
        }
        return result;
    }
}
