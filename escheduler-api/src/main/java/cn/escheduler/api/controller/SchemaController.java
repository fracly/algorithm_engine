package cn.escheduler.api.controller;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.SchemaService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.utils.DateUtils;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.model.Schema;
import cn.escheduler.dao.model.SchemaBranch;
import cn.escheduler.dao.model.SchemaVersion;
import cn.escheduler.dao.model.User;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("schemas")
public class SchemaController extends BaseController{
    public static final Logger logger = LoggerFactory.getLogger(SchemaController.class);

    @Autowired
    private SchemaService schemaService;

    /**
     * get all schema resource
     */
    @GetMapping("/getAllSchemaResources")
    @ResponseStatus(HttpStatus.OK)
    public Result getAllSchemaSources(){
        Result result = new Result();
        List<Map<String, Object>> list = schemaService.getAllSchemaResouces();
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get schema business topic by layer
     */
    @GetMapping("/getSchemaBusinessTopic")
    @ResponseStatus(HttpStatus.OK)
    public Result getSchemaBusinessTopic(@RequestParam String layer){
        Result result = new Result();
        List<Map<String, Object>> list = new ArrayList<>();
        if(layer.equals("all")) {
            list = returnAllMap();
        } else {
            list.addAll(returnAllMap());
            list.addAll(schemaService.getSchemaBusinessTopic(layer));
        }
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * download all schema in excel format
     */
    @GetMapping("/download")
    @ResponseBody
    public void download(HttpServletResponse response, String layer, String topic, String searchVal) {

        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        Result<List<Schema>> result = schemaService.querySchemas(0, Integer.MAX_VALUE, layer, topic, searchVal);
        for(Schema schema : result.getData()) {
            Sheet sheet = wb.createSheet(schema.getLayer() + "-" + schema.getTablename());
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(helper.createRichTextString("表名"));
            header.createCell(1).setCellValue(helper.createRichTextString("表描述"));
            header.createCell(2).setCellValue(helper.createRichTextString("数据层级"));
            header.createCell(3).setCellValue(helper.createRichTextString("业务主题"));
            header.createCell(4).setCellValue(helper.createRichTextString("详细描述"));
            header.createCell(5).setCellValue(helper.createRichTextString("创建时间"));
            header.createCell(6).setCellValue(helper.createRichTextString("创建人员"));

            Row headerValue = sheet.createRow(1);
            headerValue.createCell(0).setCellValue(helper.createRichTextString(schema.getTablename()));
            headerValue.createCell(1).setCellValue(helper.createRichTextString(schema.getTabledescribe()));
            headerValue.createCell(2).setCellValue(helper.createRichTextString(schema.getLayer()));
            headerValue.createCell(3).setCellValue(helper.createRichTextString(schema.getTopic()));
            headerValue.createCell(4).setCellValue(helper.createRichTextString(schema.getTabledetail()));
            headerValue.createCell(5).setCellValue(helper.createRichTextString(simpleDateFormat.format(schema.getCreateTime())));
            headerValue.createCell(6).setCellValue(helper.createRichTextString(schema.getCreateUser()));

            Row fourthRow = sheet.createRow(3);
            fourthRow.createCell(0).setCellValue(helper.createRichTextString("字段详情如下："));
            Row columnHeader = sheet.createRow(4);
            columnHeader.createCell(0).setCellValue(helper.createRichTextString("字段名"));
            columnHeader.createCell(1).setCellValue(helper.createRichTextString("字段类型"));
            columnHeader.createCell(2).setCellValue(helper.createRichTextString("字段描述"));

            //获取Schema最新的版本信息
            SchemaVersion schemaVersion = schemaService.queryLastestVersionSchema(schema.getLayer(), schema.getTablename(), "master");
            String schemaText = schemaVersion.getSchemaText();
            Map<String, String> map = JSONUtils.toMap(schemaText);
            List<Map> columns = JSONUtils.toList(map.get("columns"), Map.class);
            for(int i = 0; i < columns.size(); i ++) {
                Row row = sheet.createRow( 5 + i );
                row.createCell(0).setCellValue(helper.createRichTextString(columns.get(i).get("name").toString()));
                row.createCell(1).setCellValue(helper.createRichTextString(columns.get(i).get("type").toString()));
                Object comment = columns.get(i).get("comment");
                if(comment != null) {
                    row.createCell(2).setCellValue(helper.createRichTextString(comment.toString()));
                }
            }
        }
        try {
            response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''" + URLEncoder.encode("元数据-" + DateUtils.getCurrentTime("yyyyMMdd-hhmmss") + ".xls", "UTF-8"));
            wb.write(response.getOutputStream());
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sync all schema exist in hive
     */
    @GetMapping("clean2init")
    @ResponseStatus(HttpStatus.OK)
    public Result clean2init() throws SQLException {
        Result result = new Result();
        Map<String, Object> map = schemaService.clean2init();
        result.setData(map);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get schemas by parameters
     */
    @GetMapping("querySchema")
    @ResponseStatus(HttpStatus.OK)
    public Result getSchemasByParameters(Integer pageNo,
                                         Integer pageSize,
                                         String layer,
                                         String topic,
                                         String searchVal) {
        Result schemas = schemaService.querySchemas(pageNo, pageSize, layer, topic, searchVal);
        return schemas;
    }

    /**
     * get schema branches and versions
     */
    @GetMapping("querySchemaBranchAndVersion")
    @ResponseStatus(HttpStatus.OK)
    public Result querySchemaBranchAndVersion(@RequestParam String layer, @RequestParam String tablename) {
        Result result = new Result();
        Schema schema = schemaService.querySchema(layer, tablename);
        List<SchemaBranch> branches = schemaService.getSchemaBranches(layer, tablename);
        List<SchemaVersion> versions = schemaService.getSchemaVersions(layer, tablename);
        Map<String, Object> map = new HashMap();
        map.put("branch", branches);
        map.put("version", versions);
        map.put("schemaName", schema.getTablename());
        result.setData(map);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /** util functions **/
    private List<Map<String, Object>> returnAllMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "全部");
        map.put("topic", "all");
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        return list;
    }
}
