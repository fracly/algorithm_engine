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


public class ModelDesignColumn {

  /**
   * colname
   */
  private String colname;
  /**
   * coltype
   */
  private String coltype;
  /**
   * coldescribe
   */
  private String coldescribe;
  /**
   * colquality
   */
  private String colquality;
  /**
   * colwarn
   */
  private String colwarn;

  public ModelDesignColumn(){

  }

  public String getColname() {
    return colname;
  }

  public void setColname(String colname) {
    this.colname = colname;
  }

  public String getColtype() {
    return coltype;
  }

  public void setColtype(String coltype) {
    this.coltype = coltype;
  }

  public String getColdescribe() {
    return coldescribe;
  }

  public void setColdescribe(String coldescribe) {
    this.coldescribe = coldescribe;
  }

  public String getColquality() {
    return colquality;
  }

  public void setColquality(String colquality) {
    this.colquality = colquality;
  }

  public String getColwarn() {
    return colwarn;
  }

  public void setColwarn(String colwarn) {
    this.colwarn = colwarn;
  }

  @Override
  public String toString() {
    return "ModelDesignColumn{" +
            "colname='" + colname + '\'' +
            ", coltype='" + coltype + '\'' +
            ", coldescribe='" + coldescribe + '\'' +
            ", colquality='" + colquality + '\'' +
            ", colwarn='" + colwarn + '\'' +
            '}';
  }
}
