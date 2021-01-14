package cn.escheduler.api.dto;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-12-10:21
 **/
public class ReportData {
    private List<Map> reportData;
    private List<String> columName;
    private Map<String,Object> Pagination;


    public Map<String, Object> getPagination() {
        return Pagination;
    }

    public void setPagination(Map<String, Object> pagination) {
        Pagination = pagination;
    }

    public List<Map> getReportData() {
        return reportData;
    }

    public void setReportData(List<Map> reportData) {
        this.reportData = reportData;
    }

    public List<String> getColumName() {
        return columName;
    }

    public void setColumName(List<String> columName) {
        this.columName = columName;
    }
}
