package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class TimeFunctionParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;

    /**
     * character columns
     */
    private String column;

    /**
     * select functions
     */
    private List<String> functions;

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

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
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
        return "TimeFunctionParameters{" +
                "databases='" + databases + '\'' +
                ", column='" + column + '\'' +
                ", functions=" + functions +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
