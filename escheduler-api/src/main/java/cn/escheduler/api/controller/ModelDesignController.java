package cn.escheduler.api.controller;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.ModelDesignService;
import cn.escheduler.api.service.SchemaService;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.TableInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scala.annotation.meta.param;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/modelcatalog")
public class ModelDesignController extends BaseController {
    @Autowired
    private ModelDesignService modelDesignService;

    @Autowired
    private SchemaService schemaService;

    /** Topic 操作 **/

    /**
     * create layer's topic
     */
    @GetMapping(value = "/createTopic")
    @ResponseStatus(HttpStatus.OK)
    public Result createTopic(@RequestParam(value = "layer") String layer,
                         @RequestParam(value = "topic") String topic,
                         @RequestParam(value = "sort") int sort){
        Map<String, Object> result = modelDesignService.insert(layer, topic, sort);
        return returnDataList(result);
    }

    /**
     * delete layer's topic
     */
    @GetMapping(value = "/deleteTopic")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteTopic(@RequestParam(value = "layer") String layer
            ,@RequestParam(value = "topic") String topic){
        Map<String, Object> result = modelDesignService.deleteLayer(layer, topic);
        return returnDataList(result);
    }

    /**
     * update layer's topic
     */
    @GetMapping(value = "/updateTopic")
    @ResponseStatus(HttpStatus.OK)
    public Result updateTopic(@RequestParam(value = "layer") String layer
            ,@RequestParam(value = "topic") String topic
            ,@RequestParam(value = "newTopic") String newTopic
            ,@RequestParam(value = "sort") String sort){
        modelDesignService.updateLayer(layer, topic ,newTopic, sort);

        //同步修改 主表中 所有layer
        Map<String, Object> stringObjectMap = modelDesignService.updateLayer2(layer, topic, newTopic);
        return returnDataList(stringObjectMap);

    }

    /**
     * query layer's topic
     */
    @GetMapping(value = "/queryTopic")
    @ResponseStatus(HttpStatus.OK)
    public Result getTopic(@RequestParam(value = "layer") String layer){
        Map<String, Object> result = modelDesignService.getLayer(layer);
        return returnDataList(result);
    }

    /** Hive Table 操作 **/


    /**
     *  create hive table(model)
     */
    @PostMapping(value = "/createTable")
    @ResponseStatus(HttpStatus.OK)
    public Result createTable(@RequestParam(value = "levelName") String levelName,
                           @RequestParam(value = "applicationName") String applicationName,
                           @RequestParam(value = "tableName") String tableName,
                           @RequestParam(value = "tableStatement") String tableStatement,
                           @RequestParam(value = "tableComment") String tableComment,
                           @RequestParam(value = "dataSourceId") int dataSourceId,
                           @RequestParam(value = "userId") int userId,
                           @RequestParam(value = "userName") String userName,
                           @RequestParam(value = "tableTypes") String tableTypes,
                           @RequestParam(value = "mysqlcolumnList") String mysqlcolumnList,
                           @RequestParam(value = "escolumnList") String escolumnList,
                           @RequestParam(value = "createMethod") String createMethod) throws Exception {

        String[] arr=levelName.split("_");
        String base = arr[0], layer = arr[1];
        Result result =modelDesignService.createTable(base, layer, applicationName, tableStatement,
                tableComment, tableName, dataSourceId, userId, userName,tableTypes,mysqlcolumnList,escolumnList,createMethod);
        boolean createSchemaSuccess = schemaService.initializeNewTableSchema(base, tableName);
        if(createSchemaSuccess) {
            return result;
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "建立元数据失败");
            return result;
        }
    }



    /**
     *  delete hive table
     */
    @GetMapping(value = "/deleteTable")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteTable(@RequestParam(value = "layer") String layer,
                           @RequestParam(value = "tablename") String tablename) throws SQLException {
        Map<String, Object> result =modelDesignService.deleteTable(layer, tablename);
        return returnDataList(result);
    }


    /**
     * update hive table
     */
    @GetMapping(value = "/updateTable")
    @ResponseStatus(HttpStatus.OK)
    public Result updateTable(@RequestParam(value = "layer") String layer,
                                        @RequestParam(value = "tblName")String tblName,
                                        @RequestParam(value = "topic")String topic,
                                        @RequestParam(value = "area")String area,
                                        @RequestParam(value = "tblDescribe")String tableDescribe) {

        Map<String, Object> result = modelDesignService.updateModelDesignList(layer, tblName, topic, area, tableDescribe);
        return returnDataList(result);
    }

    /**
     * query hive table
     */
    @GetMapping(value = "/queryTable")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTable(
            @RequestParam(value = "layer") String layer,
            @RequestParam(value = "topic") String topic,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam(value = "pageNo") int pageNo,
            @RequestParam(value = "pageSize") int pageSize
    ){
        Map<String, Object> result = modelDesignService.getModelDesignList(layer, topic, pageNo, pageSize, searchVal);
        return returnDataListPaging(result);
    }

    /**
     * query hive table, but base and topic is not neccessary
     */
    @GetMapping(value = "/queryModel")
    @ResponseStatus(HttpStatus.OK)
    public Result queryModel(
            @RequestParam(value = "base") String base,
            @RequestParam(value = "topic") String topic,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam(value = "pageNo") int pageNo,
            @RequestParam(value = "pageSize") int pageSize
    ){
        Map<String, Object> result = modelDesignService.getModelList(base, topic, pageNo, pageSize, searchVal);
        return returnDataListPaging(result);
    }

    /** Hive Table Column 操作 **/

    /**
     *  add hive table column
     */
    @PostMapping(value = "/table/add-column")
    @ResponseStatus(HttpStatus.OK)
    public Result addTableColumn(@RequestParam(value = "base") String base,
                         @RequestParam(value = "table") String table,
                         @RequestParam(value = "name") String name,
                         @RequestParam(value = "type") String type,
                         @RequestParam(value = "comment") String comment) {
        try {
            Map<String, Object> map = modelDesignService.addColumn(base, table, name, type, comment);
            return returnDataList(map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *  update hive table column
     */
    @PostMapping(value = "/table/update-column")
    @ResponseStatus(HttpStatus.OK)
    public Result updateTableColumn(@RequestParam(value = "base") String base,
                            @RequestParam(value = "table") String table,
                            @RequestParam(value = "oldName") String oldName,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "type") String type,
                            @RequestParam(value = "comment") String comment) {
        try{
            Map<String, Object> result = modelDesignService.updateColumn(base, table, oldName, name, type, comment);
            return  returnDataList(result);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * query hive table column
     */
    @GetMapping(value = "/table/query-column")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTableColumn(@RequestParam(value = "base") String base, @RequestParam(value = "type")String type,
                             @RequestParam(value = "tblName")String tblName) throws SQLException {
        Map<String, Object> result = modelDesignService.getColumnInfo(base, tblName,type);
        return  returnDataList(result);
    }


    /** Hive Table Rule 操作 **/
    /**
     * 增删改查 table rule
     */
    @PostMapping(value = "/table/cud-rule")
    @ResponseStatus(HttpStatus.OK)
    public Result cudTableRule(@RequestParam(value = "base") String base,
                                 @RequestParam(value = "tblName") String table,
                                 @RequestParam(value = "tableRule") String tableRule) {
        List<Map> tableRuleMap = JSON.parseObject(tableRule, List.class);
        return modelDesignService.addTableRule(base, table, tableRuleMap);
    }

    @GetMapping(value = "/table-rule/query-type")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTableRuleType() {
        Map<String, Object> result = modelDesignService.getTableRuleConfList();
        return returnDataList(result);
    }

    @GetMapping(value = "/table-rule/query-condi")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTableRuleTypeCondition(@RequestParam(value = "type") String type) {
        Map<String, Object> result = modelDesignService.getTableRuleConfListCondition(type);
        return returnDataList(result);
    }

    @GetMapping(value = "/table-rule/query-param")
    @ResponseStatus(HttpStatus.OK)
    public Result queryTableRuleTypeConditionParam(@RequestParam(value = "type") String type, @RequestParam(value = "condi") String condi) {
        Map<String, Object> result = modelDesignService.getTableRuleConfListConditionParam(type, condi);
        return returnDataList(result);
    }

    /** Hive Table Column Rule 操作 **/
    @PostMapping(value = "/table-column/cud-rule")
    @ResponseStatus(HttpStatus.OK)
    public Result cudTableColumnRule(@RequestParam(value = "base") String base,
                               @RequestParam(value = "tblName") String table,
                               @RequestParam(value = "columnRule") String columnRule) {
        List<Map> columnRuleMap = JSON.parseObject(columnRule, List.class);
        return modelDesignService.cudColumnRule(base, table, columnRuleMap);
    }

    @GetMapping(value = "/column-rule/query")
    @ResponseStatus(HttpStatus.OK)
    public Result queryColumnRule(@RequestParam(value = "base") String base,
                                  @RequestParam(value = "tblName") String table) throws SQLException {
        Result result = new Result();
        List<Map> info = modelDesignService.getColumnRule(base, table);
        result.setData(info);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping(value = "/column-rule/query-type")
    @ResponseStatus(HttpStatus.OK)
    public Result queryColumnRuleType() {
        Map<String, Object> result = modelDesignService.getColumnRuleConfList();
        return returnDataList(result);
    }

    @GetMapping(value = "/column-rule/query-condi")
    @ResponseStatus(HttpStatus.OK)
    public Result queryColumnRuleTypeCondition(@RequestParam(value = "type") String type) {
        Map<String, Object> result = modelDesignService.getColumnRuleConfListCondition(type);
        return returnDataList(result);
    }

    @GetMapping(value = "/column-rule/query-param")
    @ResponseStatus(HttpStatus.OK)
    public Result queryColumnRuleTypeConditionParam(@RequestParam(value = "type") String type, @RequestParam(value = "condi") String condi) {
        Map<String, Object> result = modelDesignService.getColumnRuleConfListConditionParam(type, condi);
        return returnDataList(result);
    }

    /**
     * updateTblRule
     *
     * @param base
     * @param tblName
     * @param tabrule
     * @param colrule
     * @return
     */
    @GetMapping(value = "/updateTblRule")
    @ResponseStatus(HttpStatus.OK)
    public Result updateTblRule(@RequestParam(value = "base") String base,
                             @RequestParam(value = "tblName")String tblName,
                             @RequestParam(value = "tblRule")String tabrule,
                             @RequestParam(value = "colRule")String colrule) {
        JSONArray coljson= JSONArray.parseArray(colrule);
        JSONArray tabjson= JSONArray.parseArray(tabrule);
        Map<String, Object> result = modelDesignService.updateTblRule(base,tblName,tabjson,coljson);
        return returnDataList(result);

    }

    /**
     * addTblRule
     *
     * @param base
     * @param tblName
     * @param tabrule
     * @param colrule
     * @return
     */
    @GetMapping(value = "/addTblRule")
    @ResponseStatus(HttpStatus.OK)
    public Result addTblRule(@RequestParam(value = "base") String base,
                             @RequestParam(value = "tblName")String tblName,
                             @RequestParam(value = "tblRule")String tabrule,
                             @RequestParam(value = "colRule")String colrule) {
        JSONArray coljson= JSONArray.parseArray(colrule);
        JSONArray tabjson= JSONArray.parseArray(tabrule);
        Map<String, Object> result = modelDesignService.addTblRule(base,tblName,tabjson,coljson);
        return returnDataList(result);

    }

    /**
     * jump
     *
     * @param dataSourceId
     * @param table
     * @return
     */
    @GetMapping(value = "/jump")
    @ResponseStatus(HttpStatus.OK)
    public Result jump(@RequestParam(value = "dataSourceId")  int dataSourceId,
                                  @RequestParam(value = "table") String table){
        Map<String, Object> result = modelDesignService.jump(dataSourceId, table);
        return returnDataList(result);

    }


    /**
     * preview
     *
     * @param base
     * @param tblName
     * @return
     */
    @GetMapping(value = "/preview")
    @ResponseStatus(HttpStatus.OK)
    public Result preview(@RequestParam(value = "base")  String base,
                          @RequestParam(value = "tblName") String tblName) throws SQLException {
        Map<String, Object> result = modelDesignService.preview(base, tblName);
        return returnDataList(result);

    }
}
