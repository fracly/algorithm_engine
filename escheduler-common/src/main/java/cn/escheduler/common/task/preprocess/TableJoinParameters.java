package cn.escheduler.common.task.preprocess;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class TableJoinParameters extends SparkParameters {

    /**
     * databases
     */
    private String databases;

    /**
     * hive table 左表
     */
    private String leftTable;

    /**
     * hive table 右表
     */
    private String rightTable;

    /**
     * jointype 关联条件 left,right,inner;左连接、右连接、内连接三种
     */
    private String connectionType;

    /**
     * 左表关联列
     */
    private List<String> leftJoinColumn;

    /**
     * 右表关联列
     */
    private List<String> rightJoinColumn;

    /**
     * 关联符号 "等于"、"大于等于"、"小于等于"、"小于"、"大于"
     */
    private List<String> symbolList;

    /**
     * 左表选择列
     */
    private List<String> leftColumnList;

    /**
     * 右表选择列
     */
    private List<String> rightColumnList;

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

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
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

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public List<String> getLeftJoinColumn() {
        return leftJoinColumn;
    }

    public void setLeftJoinColumn(List<String> leftJoinColumn) {
        this.leftJoinColumn = leftJoinColumn;
    }

    public List<String> getRightJoinColumn() {
        return rightJoinColumn;
    }

    public void setRightJoinColumn(List<String> rightJoinColumn) {
        this.rightJoinColumn = rightJoinColumn;
    }

    public List<String> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<String> symbolList) {
        this.symbolList = symbolList;
    }

    public List<String> getLeftColumnList() {
        return leftColumnList;
    }

    public void setLeftColumnList(List<String> leftColumnList) {
        this.leftColumnList = leftColumnList;
    }

    public List<String> getRightColumnList() {
        return rightColumnList;
    }

    public void setRightColumnList(List<String> rightColumnList) {
        this.rightColumnList = rightColumnList;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public int getDatasource2() {
        return datasource2;
    }

    public void setDatasource2(int datasource2) {
        this.datasource2 = datasource2;
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
        return "TableJoinParameters{" +
                "databases='" + databases + '\'' +
                ", leftTable='" + leftTable + '\'' +
                ", rightTable='" + rightTable + '\'' +
                ", connectionType='" + connectionType + '\'' +
                ", leftJoinColumn=" + leftJoinColumn +
                ", rightJoinColumn=" + rightJoinColumn +
                ", symbolList=" + symbolList +
                ", leftColumnList=" + leftColumnList +
                ", rightColumnList=" + rightColumnList +
                ", datasource=" + datasource +
                ", datasource2=" + datasource2 +
                ", newTable='" + newTable + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
