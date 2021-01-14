package cn.escheduler.common.task.machineLearning;

import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/25.
 */

public class ForecastParameters extends SparkParameters {
    /**
     * character columns
     */
    private List<String> characterColumns;

    /**
     * databases
     */
    private String databases;

    /**
     * input hive table
     */
    private String table;

    /**
     * input hive table
     */
    private String newTable;

    /**
     * datasource
     */
    private int datasource;

    /**
     * modelName
     */
    private String modelName;

    /**
     * lastTaskType
     */
    private String lastTaskType;

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

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public List<String> getCharacterColumns() {
        return characterColumns;
    }

    public void setCharacterColumns(List<String> characterColumns) {
        this.characterColumns = characterColumns;
    }
    public String getNewTable() {
        return newTable;
    }

    public void setNewTable(String newTable) {
        this.newTable = newTable;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getLastTaskType() {
        return lastTaskType;
    }

    public void setLastTaskType(String lastTaskType) {
        this.lastTaskType = lastTaskType;
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
        return "ForecastParameters{" +
                "characterColumns=" + characterColumns +
                ", databases='" + databases + '\'' +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", modelName='" + modelName + '\'' +
                ", lastTaskType='" + lastTaskType + '\'' +
                ", columnList=" + columnList +
                ", dataStorage=" + dataStorage +
                ", savebases='" + savebases + '\'' +
                '}';
    }
}
