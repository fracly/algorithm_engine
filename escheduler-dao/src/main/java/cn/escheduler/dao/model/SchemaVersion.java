package cn.escheduler.dao.model;

import java.util.Date;

/**
 * Schema Version Entity
 */
public class SchemaVersion {

    private int id;

    private String name;

    private int version;

    private String schemaText;

    private String desc;

    private int state;

    private Date createTime;

    private Date updateTime;

    private String base;

    private String schemaTableName;

    private int branchId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSchemaText() {
        return schemaText;
    }

    public void setSchemaText(String schemaText) {
        this.schemaText = schemaText;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSchemaTableName() {
        return schemaTableName;
    }

    public void setSchemaTableName(String schemaTableName) {
        this.schemaTableName = schemaTableName;
    }

    @Override
    public String toString() {
        return "SchemaVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", schemaText='" + schemaText + '\'' +
                ", desc='" + desc + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", base='" + base + '\'' +
                ", schemaTableName='" + schemaTableName + '\'' +
                ", branchId=" + branchId +
                '}';
    }
}
