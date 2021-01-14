package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class ColumnnameAlterParameters extends SparkParameters {

    /**
     * databases
     */
    private String databases;
    /**
     * character columns
     */
    private List<String> beforeColumnList;
    /**
     * character columns
     */
    private List<String> afterColumnList;

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

    public List<String> getBeforeColumnList() {
        return beforeColumnList;
    }

    public void setBeforeColumnList(List<String> beforeColumnList) {
        this.beforeColumnList = beforeColumnList;
    }

    public List<String> getAfterColumnList() {
        return afterColumnList;
    }

    public void setAfterColumnList(List<String> afterColumnList) {
        this.afterColumnList = afterColumnList;
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
        return "ColumnnameAlterParameters{" +
                "databases='" + databases + '\'' +
                ", beforeColumnList=" + beforeColumnList +
                ", afterColumnList=" + afterColumnList +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
