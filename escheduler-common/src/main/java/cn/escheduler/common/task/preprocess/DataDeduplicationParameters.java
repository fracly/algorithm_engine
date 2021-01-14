package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/11/3
 * @version:
 */
public class DataDeduplicationParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;

    /**
     * duplicate columns
     */
    private List<String> duplicateColumns;

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

    public List<String> getDuplicateColumns() {
        return duplicateColumns;
    }

    public void setDuplicateColumns(List<String> duplicateColumns) {
        this.duplicateColumns = duplicateColumns;
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
    public String getResultTable(){
        return savebases + "." + newTable;
    }
}
