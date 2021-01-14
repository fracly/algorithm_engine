package cn.escheduler.common.task.io;

import cn.escheduler.common.task.AbstractParameters;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HiveOutputParameters extends AbstractParameters {

    /**
     * data source type，eg  MYSQL, POSTGRES, HIVE ...
     */
    private String type;

    /**
     * sql output 输出结果 datasource id
     */
    private int datasource;


    /**
     * sql output 获取上个组件 datasource id
     */
    private int typeDatasource;


    /**
     * sql output 输出结果 table name
     */
    private String table;


    /**
     * sql output 获取上个组件 table name
     */
    private String lastTable;

    /**
     * save （ W 代表 覆盖 ；I 代表 追加）
     */
    private String save;

    /**
     * column （ 分区列 ）
     */
    private String column;

    /**
     * databases
     */
    private String databases;


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

    public String getLastTable() {
        return lastTable;
    }

    public void setLastTable(String lastTable) {
        this.lastTable = lastTable;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public boolean checkParameters() {
        return datasource != 0 &&StringUtils.isNotEmpty(table);
    }

    @Override
    public String getResultTable(){
        return databases + "." + table;
    }

    @Override
    public List<String> getResourceFilesList() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "HiveOutputParameters{" +
                "type='" + type + '\'' +
                ", datasource=" + datasource +
                ", typeDatasource=" + typeDatasource +
                ", table='" + table + '\'' +
                ", lastTable='" + lastTable + '\'' +
                ", save='" + save + '\'' +
                ", column='" + column + '\'' +
                ", databases='" + databases + '\'' +
                '}';
    }
}
