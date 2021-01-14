package cn.escheduler.common.task.TrafficMetrics;

import cn.escheduler.common.task.common.spark.SparkParameters;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/11/3
 * @version:
 */
public class TrafficFlowParameters extends SparkParameters {
    /**
     * databases
     */
    private String databases;


    private String hphm;

    private String hpzl;

    private String jgsj;

    private String sbbh;

    private String lane_id;

    private String turn_dir_no_list;

    private String devc_no;

    private String loadnet_inter_id;

    private String loadnet_rid;

    private String angle;

    private String start_cross_id;

    private String end_cross_id;

    /**
     *
     */
    private String start_time;

    /**
     *
     */
    private String end_time;

    /**
     *
     */
    private String sign;

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
     * timeType
     */
    private String timeType;

    /**
     * timeType
     */
    private String timeParam;

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

    public String getHphm() {
        return hphm;
    }

    public void setHphm(String hphm) {
        this.hphm = hphm;
    }

    public String getHpzl() {
        return hpzl;
    }

    public void setHpzl(String hpzl) {
        this.hpzl = hpzl;
    }

    public String getJgsj() {
        return jgsj;
    }

    public void setJgsj(String jgsj) {
        this.jgsj = jgsj;
    }

    public String getSbbh() {
        return sbbh;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }

    public String getLane_id() {
        return lane_id;
    }

    public void setLane_id(String lane_id) {
        this.lane_id = lane_id;
    }

    public String getTurn_dir_no_list() {
        return turn_dir_no_list;
    }

    public void setTurn_dir_no_list(String turn_dir_no_list) {
        this.turn_dir_no_list = turn_dir_no_list;
    }

    public String getDevc_no() {
        return devc_no;
    }

    public void setDevc_no(String devc_no) {
        this.devc_no = devc_no;
    }

    public String getLoadnet_inter_id() {
        return loadnet_inter_id;
    }

    public void setLoadnet_inter_id(String loadnet_inter_id) {
        this.loadnet_inter_id = loadnet_inter_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getTimeParam() {
        return timeParam;
    }

    public void setTimeParam(String timeParam) {
        this.timeParam = timeParam;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLoadnet_rid() {
        return loadnet_rid;
    }

    public void setLoadnet_rid(String loadnet_rid) {
        this.loadnet_rid = loadnet_rid;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getStart_cross_id() {
        return start_cross_id;
    }

    public void setStart_cross_id(String start_cross_id) {
        this.start_cross_id = start_cross_id;
    }

    public String getEnd_cross_id() {
        return end_cross_id;
    }

    public void setEnd_cross_id(String end_cross_id) {
        this.end_cross_id = end_cross_id;
    }

    @Override
    public String getResultTable(){
        return savebases + "." + newTable;
    }
}
