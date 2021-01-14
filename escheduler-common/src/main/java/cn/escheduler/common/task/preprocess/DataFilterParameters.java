package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;

public class DataFilterParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;
    /**
     * label column
     */
    private String labelColumn;

    /**
     * hive table
     */
    private String table;

    /**
     * judging condition
     */
    private String condition;

    /**
     * judging value
     */
    private String num;

    /**
     * comment
     */
    private JSONArray columnList;

    /**
     * save数据源
     */
    private int dataStorage;

    /**
     * datasource
     */
    private int datasource;

    /**
     * newTable
     */
    private String newTable;

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

    public String getLabelColumn() {
        return labelColumn;
    }

    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public String getSavebases() {
        return savebases;
    }

    public void setSavebases(String savebases) {
        this.savebases = savebases;
    }

    @Override
    public boolean checkParameters() {
//        if(StringUtils.isEmpty(labelColumn) || StringUtils.isEmpty(hiveTable)){
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
        return "DataFilterParameters{" +
                "databases='" + databases + '\'' +
                ", labelColumn='" + labelColumn + '\'' +
                ", table='" + table + '\'' +
                ", condition='" + condition + '\'' +
                ", num='" + num + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", datasource=" + datasource +
                ", newTable='" + newTable + '\'' +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
