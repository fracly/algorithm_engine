package cn.escheduler.api.dto;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-12-9:00
 **/
public class CustomReport {
    private int code;
    private String msg;
    private Map report;
    private List<Map> reportParam;
    private Map reportDataSource;

    public Map getReport() {
        return report;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setReport(Map report) {
        this.report = report;
    }

    public List<Map> getReportParam() {
        return reportParam;
    }

    public void setReportParam(List<Map> reportParam) {
        this.reportParam = reportParam;
    }

    public Map getReportDataSource() {
        return reportDataSource;
    }

    public void setReportDataSource(Map reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    @Override
    public String toString() {
        return "customReport{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", report=" + report +
                ", reportParam=" + reportParam +
                ", reportDataSource=" + reportDataSource +
                '}';
    }
}
