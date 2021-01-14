package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * Created by fracly on 2019/8/15.
 */

public class DefaultFillingParameters extends SparkParameters {
    /**
     * datasource id
     */
    private String databases;

    /**
     * hive table
     */
    private String table;

    /**
     * filling column
     */
    private String fillingColumn;

    /**
     * fill style
     */
    private String fillStyle;

    /**
     * fixed value
     */
    private String fixedValue;

    /**
     * fill table
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
     * datasource
     */
    private int datasource;

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

    public String getFillingColumn() {
        return fillingColumn;
    }

    public void setFillingColumn(String fillingColumn) {
        this.fillingColumn = fillingColumn;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
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

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
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
        return "DefaultFillingParameters{" +
                "databases=" + databases +
                ", table='" + table + '\'' +
                ", fillingColumn='" + fillingColumn + '\'' +
                ", fillStyle='" + fillStyle + '\'' +
                ", fixedValue='" + fixedValue + '\'' +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                ", datasource=" + datasource +
                '}';
    }
}