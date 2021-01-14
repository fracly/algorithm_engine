package cn.escheduler.dao.model;

import java.util.List;

public class ColRule {

    private String condi;
    private String colQuality;
    private List data;
    private String param;
    private String column;
    private int colWarn;
    private String isRun;
    private String type;

    public String getCondi() {
        return condi;
    }

    public void setCondi(String condi) {
        this.condi = condi;
    }

    public String getColQuality() {
        return colQuality;
    }

    public void setColQuality(String colQuality) {
        this.colQuality = colQuality;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getColWarn() {
        return colWarn;
    }

    public void setColWarn(int colWarn) {
        this.colWarn = colWarn;
    }

    public String getIsRun() {
        return isRun;
    }

    public void setIsRun(String isRun) {
        this.isRun = isRun;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "condi:'" + condi + '\'' +
                ", colQuality:'" + colQuality + '\'' +
                ", data:" + data +
                ", param:'" + param + '\'' +
                ", column:'" + column + '\'' +
                ", colWarn:" + colWarn +
                ", isRun:'" + isRun + '\'' +
                ", type:'" + type + '\'' +
                '}';
    }
}
