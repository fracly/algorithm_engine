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
package cn.escheduler.common.task.common.spark;

import org.apache.commons.lang.StringUtils;

/**
 * spark parameters
 */
public class Spark2Parameters extends SparkParameters {


    /**
     * input datasource id
     */
    private Integer input;

    /**
     * output datasource id
     */
    private Integer output;

    /**
     * input datasource table
     */
    private String inTable;

    /**
     * input datasource parameter1
     */
    private String inValue;

    /**
     * input datasource parameter2
     */
    private String inValue2;

    /**
     * output datasource table
     */
    private String outTable;

    public Integer getInput() {
        return input;
    }

    public void setInput(Integer input) {
        this.input = input;
    }

    public Integer getOutput() {
        return output;
    }

    public void setOutput(Integer output) {
        this.output = output;
    }

    public String getInTable() {
        return inTable;
    }

    public void setInTable(String inTable) {
        this.inTable = inTable;
    }

    public String getInValue() {
        return inValue;
    }

    public void setInValue(String inValue) {
        this.inValue = inValue;
    }

    public String getInValue2() {
        return inValue2;
    }

    public void setInValue2(String inValue2) {
        this.inValue2 = inValue2;
    }

    public String getOutTable() {
        return outTable;
    }

    public void setOutTable(String outTable) {
        this.outTable = outTable;
    }

    @Override
    public boolean checkParameters() {
        return this.getMainArgs() != null && this.getProgramType() != null
                && StringUtils.isNotEmpty(this.inTable) && StringUtils.isNotEmpty(this.outTable);
    }

    @Override
    public String toString() {
        return "Spark2Parameters{" +
                "input=" + input +
                ", output=" + output +
                ", inTable='" + inTable + '\'' +
                ", inValue='" + inValue + '\'' +
                ", inValue2='" + inValue2 + '\'' +
                ", outTable='" + outTable + '\'' +
                '}';
    }
}
