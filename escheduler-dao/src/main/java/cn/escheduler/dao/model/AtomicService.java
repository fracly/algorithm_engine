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
 * 服务封装
 */
public class AtomicService {

    private String serviceName;
    private String serviceCode;
    private String developMode;
    private String dataSourceType;
    private String dataSourceName;
    private int dataSourceId;
    private String serviceSql;
    private int status;
    private String operNo;
    private Date operTime;
    private String des;

    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getDevelopMode() {
        return developMode;
    }

    public void setDevelopMode(String developMode) {
        this.developMode = developMode;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperNo() {
        return operNo;
    }

    public void setOperNo(String operNo) {
        this.operNo = operNo;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getServiceSql() {
        return serviceSql;
    }

    public void setServiceSql(String serviceSql) {
        this.serviceSql = serviceSql;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "AtomicService{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", developMode='" + developMode + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                ", dataSourceName='" + dataSourceName + '\'' +
                ", dataSourceId=" + dataSourceId +
                ", serviceSql='" + serviceSql + '\'' +
                ", status=" + status +
                ", operNo='" + operNo + '\'' +
                ", operTime=" + operTime +
                ", des='" + des + '\'' +
                '}';
    }
}
