package cn.escheduler.api.controller;


import cn.escheduler.api.dto.ReportData;
import cn.escheduler.api.service.CustomReportService;
import cn.escheduler.api.service.ReportFromService;
import cn.escheduler.api.utils.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-10-14:47
 **/
@RestController
@RequestMapping(value = "customReport")
public class CustomReportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CustomReportController.class);
    @Autowired
    private CustomReportService customreportService;


    @Autowired
    private ReportFromService feportFromService;

    /**
     * 报表预览页面
     * @param parms
     * @param reportCode
     * @return
     */
    @PostMapping("/view/{reportCode}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> report(@RequestBody Map<String,Object> parms, @PathVariable String reportCode) {
        String status = String.valueOf(parms.get("status"));
        String glbmbm = String.valueOf(parms.get("glbmbm"));
        //加载报表
        Map<String, Object> stringObjectMap = customreportService.loadReport(reportCode,status,glbmbm);
        //算使用次数
        feportFromService.addUseCountByCode(reportCode);
        return stringObjectMap;
    }

    /**
     * 自定义报表查询
     * @param parms
     * @param reportCode
     * @return
     */
    @PostMapping("/queryList/{reportCode}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> queryList(@RequestBody Map<String,Object> parms, @PathVariable String reportCode) {
        //加载报表
        Map<String, Object> stringObjectMap = customreportService.loadReportData(reportCode,parms);
        return stringObjectMap;
    }

    /**
     * 自定义报表导出公共方法
     * @param parms
     * @param reportCode
     * @return
     */
    @PostMapping("/exportExcel/{reportCode}")
    @ResponseStatus(HttpStatus.OK)
    public void exportExcel(@RequestBody Map<String,Object> parms, @PathVariable String reportCode, HttpServletResponse response) {
        //获取生成的excel 对象
        HSSFWorkbook sheets = customreportService.exportExcel(reportCode, parms);
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ DateUtils.toTimeStr(new Date()) + ".xls");
            response.setHeader("content-type", "application/vnd.ms-excel");
            response.setContentType("application/vnd.ms-excel");
            String os = System.getProperty("os.name");
            if(os != null){
                os = os.toLowerCase();
            }
            String path="";
            if(os != null && os.indexOf("linux") != -1){
                path="/tmp/";
            }else{
                path="E://";
            }
            File file = new File( path+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString() + ".xls");
            FileOutputStream out = new FileOutputStream(file);
            sheets.write(out);
            sheets.write(response.getOutputStream());
            sheets.close();
            //创建临时文件 使用完成后删除
            if(file.exists()){
                file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*导出不带分页*/
    @PostMapping("/exportExcelNoPagination/{reportCode}")
    @ResponseStatus(HttpStatus.OK)
    public void exportExcelNoPagination(@RequestBody Map<String,Object> parms, @PathVariable String reportCode, HttpServletResponse response) {
        //需要重新获取数据 最大65536
        parms.put("pageSize",65536);
        parms.put("pageNum",1);
        try {
            Map<String, Object> stringObjectMap = customreportService.loadReportData(reportCode,parms);
            Map<String, Object> data = (Map<String, Object>) stringObjectMap.get("data");
            ReportData reportData1 = (ReportData) data.get("reportData");
            List<Map> reportData = reportData1.getReportData();
            //重新查询不带分页的数据
            parms.put("tableData",reportData);
        }catch (Exception e){
            parms.put("tableData",null);
        }
        HSSFWorkbook sheets = customreportService.exportExcel(reportCode, parms);
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ DateUtils.toTimeStr(new Date()) + ".xls");
            response.setHeader("content-type", "application/vnd.ms-excel");
            response.setContentType("application/vnd.ms-excel");
            String os = System.getProperty("os.name");
            if(os != null){
                os = os.toLowerCase();
            }
            String path="";
            if(os != null && os.indexOf("linux") != -1){
                path="/tmp/";
            }else{
                path="E://";
            }
            File file = new File( path+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString() + ".xls");
            FileOutputStream out = new FileOutputStream(file);
            sheets.write(out);
            sheets.write(response.getOutputStream());
            sheets.close();
            //创建临时文件 使用完成后删除
            if(file.exists()){
                file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
