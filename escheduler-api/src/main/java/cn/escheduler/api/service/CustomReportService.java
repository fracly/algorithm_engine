package cn.escheduler.api.service;

import cn.escheduler.api.dto.CustomReport;
import cn.escheduler.api.dto.ReportData;
import cn.escheduler.api.utils.DataSourceUtil;
import cn.escheduler.api.utils.OfficeUtil;
import cn.escheduler.api.utils.SqlUtil;
import cn.escheduler.api.utils.StringUtils;
import cn.escheduler.common.enums.DbType;
import cn.escheduler.common.utils.JSONUtils;
import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-10-14:48
 **/
@Service
public class CustomReportService extends BaseDAGService {
    private static final Logger logger = LoggerFactory.getLogger(CustomReportService.class);

    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Autowired
    private reportService reportService;

    /**
     * 加载报表
     *
     * @param reportCode 报表编码
     */

    public Map<String, Object> loadReport(String reportCode,String status,String glbmbm) {
        //加载报表基本信息
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> resData = new HashMap<>();
        Map<String, Object> report = new HashMap<>();
        List<Map> reportParam = new ArrayList<>();
        Map<String, Object> reportDataSource = new HashMap<>();

        resMap.put("code", 1);
        resMap.put("msg", "Successful request");
        try {
            CustomReport reportBasicInfo = loadReportBasicInfo(reportCode,status,glbmbm);
            if (reportBasicInfo.getCode() != 1) {
                resMap.put("code", "-1");
                resMap.put("msg", reportBasicInfo.getMsg());
                return resMap;
            }
            //报表基本信息
            report = reportBasicInfo.getReport();
            //报表参数基本信息
            reportParam = reportBasicInfo.getReportParam();
            //报表数据源信息
            reportDataSource = reportBasicInfo.getReportDataSource();
        } catch (Exception e) {
            resMap.put("code", "-1");
            resMap.put("msg", "调用AI引擎服务获取报表信息接口失败，请联系管理员！");
            return resMap;
        }
        //分别报表信息，进行封装
        resData.put("reportParam", reportParam);
        resData.put("reportInfo", report);
        resData.put("reportHead", report.get("reportHead"));
        //获取数据库连接
        Connection connection = loadReportDataSource(reportDataSource);

        if (connection == null) {
            resMap.put("code", "-1");
            resMap.put("msg", "获取报表数据库连接失败，请联系管理员");
            return resMap;
        }
        //处理sql
        String reportSql = String.valueOf(report.get("reportSql"));
        if (!StringUtils.isNotEmpty(reportSql)) {
            resMap.put("code", "-1");
            resMap.put("msg", "报表sql为空，无法加载，请重试");
            return resMap;
        }
        //第一次加载没有传参
        reportSql = processingSql(reportSql, null,null);
        String cloums = getCloums(reportSql);
        //获取数据
        ReportData reportData = loadReportData(connection, cloums);
        reportData.setReportData(null);
        resData.put("reportData", reportData);
        resMap.put("data", resData);

        return resMap;
    }

    /**
     * 处理sql公用方法 以便复用
     */
    private String processingSql(String sql, Map<String, Object> params,List<Map> reportParam) {
        sql = SqlUtil.processSql(sql, params,reportParam);
        return sql;
    }

    private String getCloums(String sql){
        sql = "select * from ("+sql+") t_Nuu_up where 1=-1";
        return sql;
    }

    private String getCloum(String sql){
        sql = "select * from ("+sql+") t_Nuu_up";
        return sql;
    }


    /*加载报表数据*/
    private ReportData loadReportData(Connection connection, String sql){
        ReportData reportData = new ReportData();
        //使用sql 查询数据
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            String s = JSONUtils.resultSetToJson(resultSet);
            //数据集
            List<Map> maps = JSON.parseArray(s, Map.class);
            //表头
            List<String> cols = new ArrayList<String>();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                String columnName = rsmd.getColumnName(i);
                cols.add(columnName);
            }
            reportData.setReportData(maps);
            reportData.setColumName(cols);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }
        } catch (Exception e) {
            List<Map> objects = new ArrayList<>();
            Map<Object, Object> map = new HashMap<>();
            map.put("sql", sql);
            map.put("error", e.toString());
            objects.add(map);
            reportData.setReportData(objects);
            return reportData;
        }
        return reportData;
    }

    /**
     * 加载数据源信息
     *
     * @return
     */
    @Nullable
    private Connection loadReportDataSource(Map<String, Object> reportDataSource) {
        String dataSourceType = String.valueOf(reportDataSource.get("type"));
        String connectionParams = String.valueOf(reportDataSource.get("connectionParams"));
        try {
            Connection con = dataSourceUtil.getConnection(DbType.valueOf(dataSourceType), connectionParams);
            return con;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加载报表基础信息
     *
     * @param reportCode
     * @return
     */
    private CustomReport loadReportBasicInfo(String reportCode,String status,String glbmbm) {
        Map result = reportService.queryParms(reportCode,status,glbmbm);
        String body = JSON.toJSONString(result);
        CustomReport customReport = JSON.parseObject(body,CustomReport.class);
        return customReport;
    }

    /**
     * 导出excel
     *
     * @param reportCode
     * @param parms
     * @return
     */

    public HSSFWorkbook exportExcel(String reportCode, Map<String, Object> parms) {
        /*报表数据*/
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) parms.get("tableData");
        if(tableData==null){
            tableData = new ArrayList<Map<String, Object>>();
        }
        /*表头数据*/
        List<Map<String, Object>> reportData = (List<Map<String, Object>>) parms.get("reportData");
        if(reportData==null){
            reportData = new ArrayList<Map<String, Object>>();
        }
        /*字段数据*/
        List<String> cloumList = (List<String>) parms.get("cloumList");
        if(cloumList==null){
            cloumList = new ArrayList<String>();
        }
        String[] objects = cloumList.toArray(new String[0]);
        /*中间表头数据*/
        List<List<String>> headList = (List<List<String>>) parms.get("reportHeadMid");
        if(headList==null){
            headList = new ArrayList<List<String>>();
        }


        HSSFWorkbook sheets = new HSSFWorkbook();
        try {
            sheets = OfficeUtil.loadExcelForHeadJson(objects, tableData, new HSSFWorkbook(), headList, reportData);
        }catch (Exception e){
            return null;
        }
        return sheets;
    }

    /**
     * 报表查询公共方法
     *
     * @param reportCode
     * @param res
     * @return
     */

    public Map<String, Object> loadReportData(String reportCode, Map<String, Object> res) {
        Integer pageSize = 15;
        Integer pageNum = 1;
        Integer totalCount = 0;
        //加载报表基本信息
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> resData = new HashMap<>();
        Map<String, Object> report = new HashMap<>();
        List<Map> reportParam = new ArrayList<>();
        Map<String, Object> reportDataSource = new HashMap<>();

        resMap.put("code", 1);
        resMap.put("msg", "Successful request");
        try {
            CustomReport reportBasicInfo = loadReportBasicInfo(reportCode,String.valueOf(res.get("status")),"");
            if (reportBasicInfo.getCode() != 1) {
                resMap.put("code", "-1");
                resMap.put("msg", reportBasicInfo.getMsg());
                return resMap;
            }
            //报表基本信息
            report = reportBasicInfo.getReport();
            //报表参数基本信息
            reportParam = reportBasicInfo.getReportParam();
            //报表数据源信息
            reportDataSource = reportBasicInfo.getReportDataSource();
        } catch (Exception e) {
            resMap.put("code", "-1");
            resMap.put("msg", "调用AI引擎服务获取报表信息接口失败，请联系管理员！");
            return resMap;
        }
        //获取数据库连接
        Connection connection = loadReportDataSource(reportDataSource);
        if (connection == null) {
            resMap.put("code", "-1");
            resMap.put("msg", "获取报表数据库连接失败，请联系管理员");
            return resMap;
        }
        //处理sql
        String reportSql = String.valueOf(report.get("reportSql"));
        if (!StringUtils.isNotEmpty(reportSql)) {
            resMap.put("code", "-1");
            resMap.put("msg", "报表sql为空，无法加载，请重试");
            return resMap;
        }
        //拼接sql之前对时间等特殊参数进行处理
        SqlUtil.processParms(res);
        reportSql = processingSql(reportSql, res,reportParam);
        //如果报表类型为清单 则报表需要分页参数
        if(report.containsKey("reportType") && "2".equals(String.valueOf(report.get("reportType")))){
            //分页参数判断
            if (res.containsKey("pageSize")) {
                pageSize = Integer.parseInt(res.get("pageSize").toString());
            }
            if (res.containsKey("pageNum")) {
                pageNum = Integer.parseInt(res.get("pageNum").toString());
            }
            String countSql = SqlUtil.countSql(reportSql);
            try {
                PreparedStatement preparedStatementCount = connection.prepareStatement(countSql);
                ResultSet resultSetCount = preparedStatementCount.executeQuery();
                while (resultSetCount.next()) {
                    totalCount = resultSetCount.getInt("sql_count");
                }
            } catch (Exception e) {
                resMap.put("msg", "解析Sql 获取查询数量报错" + e);
                return resMap;
            }
            try {


                String pagination = SqlUtil.Pagination(reportSql, pageSize, pageNum, DbType.MYSQL);
                String cloums = getCloum(pagination);
                ReportData reportData = loadReportData(connection, cloums);
                Map<String, Object> map = new HashMap<>();
                map.put("pageSize", pageSize);
                map.put("pageNum", pageNum);
                map.put("totalCount", totalCount);
                map.put("totalPage", (totalCount + pageSize - 1) / pageSize);
                reportData.setPagination(map);
                resData.put("reportData", reportData);
                resMap.put("data", resData);
                return resMap;
            }catch (Exception e){
                resMap.put("msg", "查询报错：" + e);
                return resMap;
            }
        }
        String cloums = getCloum(reportSql);
        //获取数据
        ReportData reportData = loadReportData(connection, cloums);
        resData.put("reportData", reportData);
        resMap.put("data", resData);
        return resMap;
    }



}
