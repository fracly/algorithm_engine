package cn.escheduler.common.task.TrafficMetrics;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/15
 * @version:
 */
public class CorrelationAnalysisParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;


    private List<String> cols;

    /**
     *
     */
    private String typeSelect;


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

    /**
     * hive table
     */
    private String table;


    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }

    public String getTypeSelect() {
        return typeSelect;
    }

    public void setTypeSelect(String typeSelect) {
        this.typeSelect = typeSelect;
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String getResultTable(){
        return savebases + "." + newTable;
    }
}
