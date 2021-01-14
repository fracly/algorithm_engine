package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class CharactorReplaceParameters extends SparkParameters {

    /**
     * databases
     */
    private String databases;
    /**
     * character column
     */
    private String column;

    /**
     * c字符串替换方式。“0”：（删除空值）删除为空值的行，“1”：（非空字符串替换）不处理空值，做字符串替换，“2”：（空值替换）空值替换为 str_new
     */
    private String rownull;

    /**
     * 原字符串
     */
    private String str_old;

    /**
     * 新字符串
     */
    private String str_new;

    /**
     * hive table
     */
    private String table;

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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getRownull() {
        return rownull;
    }

    public void setRownull(String rownull) {
        this.rownull = rownull;
    }

    public String getStr_old() {
        return str_old;
    }

    public void setStr_old(String str_old) {
        this.str_old = str_old;
    }

    public String getStr_new() {
        return str_new;
    }

    public void setStr_new(String str_new) {
        this.str_new = str_new;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
        return "CharactorReplaceParameters{" +
                "databases='" + databases + '\'' +
                ", column='" + column + '\'' +
                ", rownull='" + rownull + '\'' +
                ", str_old='" + str_old + '\'' +
                ", str_new='" + str_new + '\'' +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
