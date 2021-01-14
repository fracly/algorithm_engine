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


import java.util.List;
import java.util.Map;

public class DesignModel {
  /**
   * tablename
   */
  private String tblName;
  /**
   * tabdescribe
   */
  private String tblDescribe;
  /**
   * area
   */
  private String area;
  /**
   * tabquality
   */
  private String tblQuality;

  private String type;
  private String create_time;
  private String update_time;
  private String create_userid;
  private String userName;
  private List<Map<String,String>> tblWarn;
  private int dataSourceId;
  private String tabRule;
  private String layer;
  private String layerCn;
  private String topic;

  public String getLayer() {
    return layer;
  }

  public void setLayer(String layer) {
    this.layer = layer;
  }

  public String getLayerCn() {
    return layerCn;
  }

  public void setLayerCn(String layerCn) {
    this.layerCn = layerCn;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public DesignModel(){

  }

  public String getTabRule() {
    return tabRule;
  }

  public void setTabRule(String tabRule) {
    this.tabRule = tabRule;
  }

  public int getDataSourceId() {
    return dataSourceId;
  }

  public void setDataSourceId(int dataSourceId) {
    this.dataSourceId = dataSourceId;
  }

  public String getTblName() {
    return tblName;
  }

  public void setTblName(String tblName) {
    this.tblName = tblName;
  }

  public String getTblDescribe() {
    return tblDescribe;
  }

  public void setTblDescribe(String tblDescribe) {
    this.tblDescribe = tblDescribe;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getTblQuality() {
    return tblQuality;
  }

  public void setTblQuality(String tblQuality) {
    this.tblQuality = tblQuality;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCreate_time() {
    return create_time;
  }

  public void setCreate_time(String create_time) {
    this.create_time = create_time;
  }

  public String getUpdate_time() {
    return update_time;
  }

  public void setUpdate_time(String update_time) {
    this.update_time = update_time;
  }

  public String getCreate_userid() {
    return create_userid;
  }

  public void setCreate_userid(String create_userid) {
    this.create_userid = create_userid;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<Map<String, String>> getTblWarn() {
    return tblWarn;
  }

  public void setTblWarn(List<Map<String, String>> tblWarn) {
    this.tblWarn = tblWarn;
  }

}
