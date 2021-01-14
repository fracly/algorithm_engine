package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class DataDiscretizationParameters extends SparkParameters {

    /**
     * databases
     */
    private String databases;

    /**
     * hive table
     */
    private String table;

    /**
     * 选择需要离散的列
     */
    private String column;

    /**
     * 自动离散化:"0" | 手动离散化:其他
     */
    private String way;

    /**
     * 自动-类别数
     */
    private String autonum;

    /**
     * 手动-区间下限
     */
    private List<String> nondown;

    /**
     *  手动-区间上限
     */
    private List<String> nonup;

    /**
     * 手动-类别
     */
    private List<String> nonnum;

    /**
     * datasource
     */
    private int datasource;

    /**
     * newTable
     */
    private String newTable;

    /**
     * comment
     */
    private JSONArray columnList;

    /**
     * save数据源
     */
    private int dataStorage;

    /**
     * savedatabases
     */
    private String savebases;

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getAutonum() {
        return autonum;
    }

    public void setAutonum(String autonum) {
        this.autonum = autonum;
    }

    public List<String> getNondown() {
        return nondown;
    }

    public void setNondown(List<String> nondown) {
        this.nondown = nondown;
    }

    public List<String> getNonup() {
        return nonup;
    }

    public void setNonup(List<String> nonup) {
        this.nonup = nonup;
    }

    public List<String> getNonnum() {
        return nonnum;
    }

    public void setNonnum(List<String> nonnum) {
        this.nonnum = nonnum;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public String getNewTable() {
        return newTable;
    }

    public void setNewTable(String newTable) {
        this.newTable = newTable;
    }

    public JSONArray getColumnList() {
        return columnList;
    }

    public void setColumnList(JSONArray columnList) {
        this.columnList = columnList;
    }

    public int getDataStorage() {
        return dataStorage;
    }

    public void setDataStorage(int dataStorage) {
        this.dataStorage = dataStorage;
    }

    public String getSavebases() {
        return savebases;
    }

    public void setSavebases(String savebases) {
        this.savebases = savebases;
    }

    @Override
    public boolean checkParameters() {
//        if(characterColumns.size() == 0 || StringUtils.isEmpty(labelColumn) || StringUtils.isEmpty(hiveTable)){
//            return false;
//        }
        return true;
    }

    @Override
    public String getResultTable(){
        return savebases + "." + newTable;
    }

    @Override
    public String toString() {
        return "DataDiscretizationParameters{" +
                "databases='" + databases + '\'' +
                ", table='" + table + '\'' +
                ", column='" + column + '\'' +
                ", way='" + way + '\'' +
                ", autonum='" + autonum + '\'' +
                ", nondown=" + nondown +
                ", nonup=" + nonup +
                ", nonnum=" + nonnum +
                ", datasource=" + datasource +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
