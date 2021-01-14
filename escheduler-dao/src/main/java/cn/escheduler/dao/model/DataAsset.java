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

public class DataAsset {
  private int id;

  private String name;

  private String type;

  private String labels;

  private String labels2;

  private String detail;

  private Date createTime;

  private Date updateTime;

  private int state;

  private String stateStr;

  private int owner;

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

  public String getLabels() {
    return labels;
  }

  public void setLabels(String labels) {
    this.labels = labels;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
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

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public String getStateStr() {
    return stateStr;
  }

  public void setStateStr(String stateStr) {
    this.stateStr = stateStr;
  }

  public String getLabels2() {
    return labels2;
  }

  public void setLabels2(String labels2) {
    this.labels2 = labels2;
  }

  @Override
  public String toString() {
    return "DataAsset{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", labels='" + labels + '\'' +
            ", labels2='" + labels2 + '\'' +
            ", detail='" + detail + '\'' +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", state=" + state +
            ", stateStr='" + stateStr + '\'' +
            ", owner=" + owner +
            '}';
  }
}
