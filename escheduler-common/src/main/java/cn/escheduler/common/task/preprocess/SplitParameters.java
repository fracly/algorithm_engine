package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * Created by Administrator on 2019/7/25.
 */

public class SplitParameters extends SparkParameters {


    /**
     * databases
     */
    private String databases;

    /**
     * input hive table
     */
    private String table;

    private String newTable1;

    private String newTable2;

    /**
     * datasource
     */
    private int datasource;

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
     * 比例
     */
    private Double  Fraction;

    public String getNewTable1() {
        return newTable1;
    }

    public void setNewTable1(String newTable1) {
        this.newTable1 = newTable1;
    }

    public String getNewTable2() {
        return newTable2;
    }

    public void setNewTable2(String newTable2) {
        this.newTable2 = newTable2;
    }

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

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public Double getFraction() {
        return Fraction;
    }

    public void setFraction(Double fraction) {
        Fraction = fraction;
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
        return savebases + "." + table.replace("-", "_")+"_x";
    }

    @Override
    public String toString() {
        return "SplitParameters{" +
                "databases='" + databases + '\'' +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                ", Fraction=" + Fraction +
                '}';
    }
}
