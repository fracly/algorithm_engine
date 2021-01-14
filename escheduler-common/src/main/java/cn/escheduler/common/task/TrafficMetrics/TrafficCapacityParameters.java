package cn.escheduler.common.task.TrafficMetrics;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/29
 * @version:
 */
public class TrafficCapacityParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;



    private List<String> vehpass;


    private List<String> dev;

    /**
     * hive table 左表
     */
    private String leftTable;

    /**
     * hive table 右表
     */
    private String rightTable;

    /**
     * datasource
     */
    private int datasource;

    /**
     * datasource
     */
    private int datasource2;

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
     * value
     */
    private String value;
    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public List<String> getVehpass() {
        return vehpass;
    }

    public void setVehpass(List<String> vehpass) {
        this.vehpass = vehpass;
    }

    public List<String> getDev() {
        return dev;
    }

    public void setDev(List<String> dev) {
        this.dev = dev;
    }



    public String getLeftTable() {
        return leftTable;
    }

    public void setLeftTable(String leftTable) {
        this.leftTable = leftTable;
    }

    public String getRightTable() {
        return rightTable;
    }

    public void setRightTable(String rightTable) {
        this.rightTable = rightTable;
    }

    public int getDatasource2() {
        return datasource2;
    }

    public void setDatasource2(int datasource2) {
        this.datasource2 = datasource2;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    @Override
    public String getResultTable(){
        return savebases + "." + newTable;
    }
}
