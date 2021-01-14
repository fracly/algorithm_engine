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


import cn.escheduler.common.enums.DbType;

import java.util.Date;
import java.util.Objects;

public class HybqLebal {

  private int label_id;

  private String label_code;

  private String label_name;

  private String label_type;

  private String desc;

  private String updata_freg;

  private String catalogue;

  private Date create_time;

  public HybqLebal() {
  }

  public HybqLebal(int label_id, String label_code, String label_name, String label_type, String desc, String updata_freg, String catalogue, Date create_time) {
    this.label_id = label_id;
    this.label_code = label_code;
    this.label_name = label_name;
    this.label_type = label_type;
    this.desc = desc;
    this.updata_freg = updata_freg;
    this.catalogue = catalogue;
    this.create_time = create_time;
  }

  public int getLabel_id() {
    return label_id;
  }

  public void setLabel_id(int label_id) {
    this.label_id = label_id;
  }

  public String getLabel_code() {
    return label_code;
  }

  public void setLabel_code(String label_code) {
    this.label_code = label_code;
  }

  public String getLabel_name() {
    return label_name;
  }

  public void setLabel_name(String label_name) {
    this.label_name = label_name;
  }

  public String getLabel_type() {
    return label_type;
  }

  public void setLabel_type(String label_type) {
    this.label_type = label_type;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getUpdata_freg() {
    return updata_freg;
  }

  public void setUpdata_freg(String updata_freg) {
    this.updata_freg = updata_freg;
  }

  public String getCatalogue() {
    return catalogue;
  }

  public void setCatalogue(String catalogue) {
    this.catalogue = catalogue;
  }

  public Date getCreate_time() {
    return create_time;
  }

  public void setCreate_time(Date create_time) {
    this.create_time = create_time;
  }

  @Override
  public String toString() {
    return "HybqLebal{" +
            "label_id=" + label_id +
            ", label_code='" + label_code + '\'' +
            ", label_name='" + label_name + '\'' +
            ", label_type='" + label_type + '\'' +
            ", desc='" + desc + '\'' +
            ", updata_freg='" + updata_freg + '\'' +
            ", catalogue='" + catalogue + '\'' +
            ", create_time=" + create_time +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HybqLebal hybqLebal = (HybqLebal) o;
    return label_id == hybqLebal.label_id &&
            Objects.equals(label_code, hybqLebal.label_code) &&
            Objects.equals(label_name, hybqLebal.label_name) &&
            Objects.equals(label_type, hybqLebal.label_type) &&
            Objects.equals(desc, hybqLebal.desc) &&
            Objects.equals(updata_freg, hybqLebal.updata_freg) &&
            Objects.equals(catalogue, hybqLebal.catalogue) &&
            Objects.equals(create_time, hybqLebal.create_time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label_id, label_code, label_name, label_type, desc, updata_freg, catalogue, create_time);
  }
}
