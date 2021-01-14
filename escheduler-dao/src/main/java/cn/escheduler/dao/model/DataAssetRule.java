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


import java.util.Date;

public class DataAssetRule {

    private int id;

    private String assetName;

    private int assetId;

    private String assetState;

    private String assetStateStr;

    private String destroyMethod;

    private String destroyMethodStr;

    private int destroyPeriod;

    private Date createTime;

    private Date updateTime;

    private String owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetState() {
        return assetState;
    }

    public void setAssetState(String assetState) {
        this.assetState = assetState;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public int getDestroyPeriod() {
        return destroyPeriod;
    }

    public void setDestroyPeriod(int destroyPeriod) {
        this.destroyPeriod = destroyPeriod;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssetStateStr() {
        return assetStateStr;
    }

    public void setAssetStateStr(String assetStateStr) {
        this.assetStateStr = assetStateStr;
    }

    public String getDestroyMethodStr() {
        return destroyMethodStr;
    }

    public void setDestroyMethodStr(String destroyMethodStr) {
        this.destroyMethodStr = destroyMethodStr;
    }

    @Override
    public String toString() {
        return "DataAssetRule{" +
                "id=" + id +
                ", assetName='" + assetName + '\'' +
                ", assetId=" + assetId +
                ", assetState='" + assetState + '\'' +
                ", assetStateStr='" + assetStateStr + '\'' +
                ", destroyMethod='" + destroyMethod + '\'' +
                ", destroyMethodStr='" + destroyMethodStr + '\'' +
                ", destroyPeriod=" + destroyPeriod +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", owner='" + owner + '\'' +
                '}';
    }
}
