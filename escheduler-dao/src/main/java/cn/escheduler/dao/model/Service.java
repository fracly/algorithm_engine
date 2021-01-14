package cn.escheduler.dao.model;

import java.util.Date;

/**
 * Service Entity
 */
public class Service {

    /**
     * 一级分类：数据所属层级
     */
    private String layer;

    private String CNLayer;

    /**
     * 二级分类：数据所属主题
     */
    private String topic;

    /**
     * 对应Hive元数据的表名
     */
    private String tablename;

    /**
     * Hive表的表描述
     */
    private String tabledescribe;

    /**
     * Hive表的表的详细描述，包括表的设计目的与用途
     */
    private String tabledetail;

    /**
     * 元数据来源，目前只有Hive
     */
    private String resource;

    /**
     * 元数据创建日期
     */
    private Date createTime;

    /**
     * 元数据创建用户
     */
    private String createUser;

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getCNLayer() {
        return CNLayer;
    }

    public void setCNLayer(String CNLayer) {
        this.CNLayer = CNLayer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTabledescribe() {
        return tabledescribe;
    }

    public void setTabledescribe(String tabledescribe) {
        this.tabledescribe = tabledescribe;
    }

    public String getTabledetail() {
        return tabledetail;
    }

    public void setTabledetail(String tabledetail) {
        this.tabledetail = tabledetail;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
