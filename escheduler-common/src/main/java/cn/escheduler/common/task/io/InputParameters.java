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
package cn.escheduler.common.task.io;

import cn.escheduler.common.task.AbstractParameters;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Input parameter
 */
public class InputParameters extends AbstractParameters {
    /**
     * data source type，eg  MYSQL, POSTGRES, HIVE ...
     */
    private String type;

    /**
     * datasource id
     */
    private int datasource;

    /**
     * table name
     */
    private String table;

    private String topic;

    private String groupId;

    private String offset;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    @Override
    public boolean checkParameters() {
        return datasource != 0 && StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(table);
    }

    @Override
    public List<String> getResourceFilesList() {
        return new ArrayList<>();
    }

    @Override
    public String getResultTable(){
        return table;
    }

    @Override
    public String toString() {
        return "InputParameters{" +
                "type='" + type + '\'' +
                ", datasource=" + datasource +
                ", table='" + table + '\'' +
                ", topic='" + topic + '\'' +
                ", groupId='" + groupId + '\'' +
                ", offset='" + offset + '\'' +
                '}';
    }
}
