package cn.escheduler.common.task.io;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;

/**
 * Created by Administrator on 2019/7/25.
 */

public class MysqlInputParameters extends SparkParameters {


    /**
     * databases
     */
    private String databases;

    /**
     * input hive table
     */
    private String table;

    private String newTable;

    private String cond;

    /**
     * comment
     */
    private String address;
    private String user;
    private String password;


    /**
     * datasource
     */
    private int datasource;


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

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return savebases + "." +newTable;
    }



}
