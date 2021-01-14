package cn.escheduler.dao.model;

import java.util.Date;

public class Application {

    /**
     * id
     */
    private int id;

    /**
     * name
     */
    private String name;

    /**
     * type
     */
    private String type;

    /**
     * desc
     */
    private String desc;

    /**
     * API key
     */
    private String APIKey;

    /**
     * Secret Key
     */
    private String SecretKey;

    /**
     * create user
     */
    private String createUser;

    /**
     * create time
     */
    private Date createTime;

    /**
     * status
     */
    private int status;

    /**
     * service ids
     */
    private String interfaces;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public String getSecretKey() {
        return SecretKey;
    }

    public void setSecretKey(String secretKey) {
        SecretKey = secretKey;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }
}
