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


public class ModelDesign {

  /**
   * 数据库
   */
  private String base;
  /**
   * 业务领域
   */
  private String layer;

  /**
   * 表名称
   */
  private String tblName;

  /**
   *  表描述
   */
  private String tblDescribe;

  /**
   * 应用领域
   */
  private String area;

  /**
   * 所有列规则(json格式)
   */
  private String colRule;

  /**
   * 表规则(json格式)
   */
  private String tblRule;

  /**
   * 表质量(json格式)
   */
  private String tblQuality;

  /**
   * 表预警(json格式)
   */
  private String tblWarn;

  /**
   * 所有列质量(json格式)
   */
  private String colQuality;

  /**
   * 所有列预警(json格式)
   */
  private String colWarn;

  @Override
  public String toString() {
    return "ModelDesign{" +
            "base='" + base + '\'' +
            ", layer='" + layer + '\'' +
            ", tablename='" + tblName + '\'' +
            ", tblDescribe='" + tblDescribe + '\'' +
            ", area='" + area + '\'' +
            ", colrule='" + colRule + '\'' +
            ", tblRule='" + tblRule + '\'' +
            ", tblQuality='" + tblQuality + '\'' +
            ", tabwarn='" + tblWarn + '\'' +
            ", colquality='" + colQuality + '\'' +
            ", colwarn='" + colWarn + '\'' +
            '}';
  }

  public ModelDesign() {
  }

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getLayer() {
    return layer;
  }

  public void setLayer(String layer) {
    this.layer = layer;
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

  public String getColRule() {
    return colRule;
  }

  public void setColRule(String colRule) {
    this.colRule = colRule;
  }

  public String getTblRule() {
    return tblRule;
  }

  public void setTblRule(String tblRule) {
    this.tblRule = tblRule;
  }

  public String getTblQuality() {
    return tblQuality;
  }

  public void setTblQuality(String tblQuality) {
    this.tblQuality = tblQuality;
  }

  public String getTblWarn() {
    return tblWarn;
  }

  public void setTblWarn(String tblWarn) {
    this.tblWarn = tblWarn;
  }

  public String getColQuality() {
    return colQuality;
  }

  public void setColQuality(String colQuality) {
    this.colQuality = colQuality;
  }

  public String getColWarn() {
    return colWarn;
  }

  public void setColWarn(String colWarn) {
    this.colWarn = colWarn;
  }
}
