package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class AddColumnParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;

    /**
     * 选择新列类型“constant”：新增常数列、“order”：新增序号列
     */
    private String typeselect;

    /**
     * 常数列的值
     */
    private String value;

    /**
     * 排序列
     */
    private String orderColumn;

    /**
     * datasource
     */
    private int datasource;

    /**
     * hive table
     */
    private String table;

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

    public String getTypeselect() {
        return typeselect;
    }

    public void setTypeselect(String typeselect) {
        this.typeselect = typeselect;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
        return "AddColumnParameters{" +
                "databases='" + databases + '\'' +
                ", typeselect='" + typeselect + '\'' +
                ", value='" + value + '\'' +
                ", orderColumn='" + orderColumn + '\'' +
                ", datasource=" + datasource +
                ", table='" + table + '\'' +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
