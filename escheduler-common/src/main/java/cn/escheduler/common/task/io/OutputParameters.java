package cn.escheduler.common.task.io;

import cn.escheduler.common.task.AbstractParameters;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OutputParameters extends AbstractParameters {

    /**
     * data source type，eg  MYSQL, POSTGRES, HIVE ...
     */
    private String type;

    /**
     * datasource id
     */
    private int datasource;

    /**
     * databases
     */
    private String databases;


    /**
     * previous datasource id
     */
    private int typeDatasource;


    /**
     * table name
     */
    private String table;


    /**
     * modelName
     */
    private String lastTable;

    public String getLastTable() {
        return lastTable;
    }

    public void setLastTable(String lastTable) {
        this.lastTable = lastTable;
    }

    public int getTypeDatasource() {
        return typeDatasource;
    }

    public void setTypeDatasource(int typeDatasource) {
        this.typeDatasource = typeDatasource;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
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

    @Override
    public boolean checkParameters() {
        return datasource != 0 && StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(table);
    }

    @Override
    public String getResultTable(){
        return databases + "." + table.replace("-", "_");
    }

    @Override
    public List<String> getResourceFilesList() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "OutputParameters{" +
                "type='" + type + '\'' +
                ", datasource=" + datasource +
                ", databases='" + databases + '\'' +
                ", typeDatasource=" + typeDatasource +
                ", table='" + table + '\'' +
                ", lastTable='" + lastTable + '\'' +
                '}';
    }
}
