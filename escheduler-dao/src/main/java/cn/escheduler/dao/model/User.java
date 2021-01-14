/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.dao.model;

import cn.escheduler.common.enums.UserType;

import java.util.Date;

/**
 * user
 */
public class  User {

    /**
     * id
     */
    private int id;

    /**
     * user chinese name
     */
    private String userCNName;

    /**
     * user name
     */
    private String userName;

    /**
     * user password
     */
    private String userPassword;

    /**
     * mail
     */
    private String email;

    /**
     * phone
     */
    private String phone;

    /**
     * user type
     */
    private UserType userType;

    /**
     *  tenant id
     */
    private int tenantId;

    /**
     * tenant code
     */
    private String tenantCode;

    /**
     * tenant name
     */
    private String tenantName;

    /**
     * queue name
     */
    private String queueName;

    /**
     * alert group
     */
    private String alertGroup;

    /**
     * user specified queue
     */
    private String queue;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * desc
     */
    private String desc;

    /**
     * status
     */
    private Integer status;

    /**
     * last login time
     */
    private Date lastLoginTime;

    /**
     * last login ip
     */
    private String ip;

    /**
     * password error count
     */
    private Integer errorCount;

    /**
     * is an administrator
     */
    private Boolean administrator;

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getAlertGroup() {
        return alertGroup;
    }

    public void setAlertGroup(String alertGroup) {
        this.alertGroup = alertGroup;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getUserCNName() {
        return userCNName;
    }

    public void setUserCNName(String userCNName) {
        this.userCNName = userCNName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getErrorCount() {
        if(errorCount == null) {
            return 0;
        } else {
            return errorCount;
        }
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (id != user.id) {
            return false;
        }
        return userName.equals(user.userName);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userType=" + userType +
                ", tenantId=" + tenantId +
                ", tenantCode='" + tenantCode + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", queueName='" + queueName + '\'' +
                ", alertGroup='" + alertGroup + '\'' +
                ", queue='" + queue + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
