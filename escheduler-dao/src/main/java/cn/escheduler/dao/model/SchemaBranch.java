package cn.escheduler.dao.model;

import java.util.Date;

/**
 * Schema Branch Entity
 */
public class SchemaBranch {
    private int id;

    private String name;

    private String base;

    private String schemaName;

    private Date createTime;

    private String desc;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public String toString() {
        return "SchemaBranch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", base='" + base + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", createTime=" + createTime +
                ", desc='" + desc + '\'' +
                '}';
    }
}
