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
package cn.escheduler.server.worker.task.common.spark;

import cn.escheduler.common.process.Property;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.common.spark.Spark2Parameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.ProcessInstance;
import cn.escheduler.server.utils.ParamUtils;
import cn.escheduler.server.utils.SparkArgsUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskProps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  spark task
 */
public class Spark2Task extends AbstractYarnTask {

    /**
     *  spark command
     */
    private static final String SPARK_COMMAND = "spark-submit";

    /**
     *  spark parameters
     */
    private Spark2Parameters spark2Parameters;

    public Spark2Task(TaskProps props, Logger logger) {
        super(props, logger);
    }

    @Override
    public void init() {

        logger.info("spark task params {}", taskProps.getTaskParams());

        spark2Parameters = JSONUtils.parseObject(taskProps.getTaskParams(), Spark2Parameters.class);

        if (!spark2Parameters.checkParameters()) {
            throw new RuntimeException("spark2 task params is not valid");
        }
        spark2Parameters.setQueue(taskProps.getQueue());

        if (StringUtils.isNotEmpty(spark2Parameters.getMainArgs())) {
            String args = spark2Parameters.getMainArgs();
            // get process instance by task instance id
            ProcessInstance processInstance = processDao.findProcessInstanceByTaskId(taskProps.getTaskInstId());

            /**
             *  combining local and global parameters
             */
            Map<String, Property> paramsMap = ParamUtils.convert(taskProps.getUserDefParamsMap(),
                    taskProps.getDefinedParams(),
                    spark2Parameters.getLocalParametersMap(),
                    processInstance.getCmdTypeIfComplement(),
                    processInstance.getScheduleTime());
            if (paramsMap != null ){
                args = ParameterUtils.convertParameterPlaceholders(args, ParamUtils.convert(paramsMap));
            }
            spark2Parameters.setMainArgs(args);
        }
    }

    /**
     *  create command
     * @return
     */
    @Override
    protected String buildCommand() {
        List<String> args = new ArrayList<>();

        args.add(SPARK_COMMAND);
        args.add(spark2Parameters.getInput() + "");
        args.add(spark2Parameters.getInTable() + "");
        args.add(spark2Parameters.getInValue() + "");
        args.add(spark2Parameters.getInValue2() + "");
        args.add(spark2Parameters.getOutput() + "");
        args.add(spark2Parameters.getOutTable() + "");

        // other parameters
        args.addAll(SparkArgsUtils.buildArgs(spark2Parameters));

        StringBuilder sb = new StringBuilder("spark-submit --master yarn");
        sb.append(" --deploy-mode " + spark2Parameters.getDeployMode());
        sb.append(" --class " + spark2Parameters.getMainClass());
        sb.append(" --driver-cores " + spark2Parameters.getDriverCores());
        sb.append(" --driver-memory " + spark2Parameters.getDriverMemory());
        sb.append(" --num-executors " + spark2Parameters.getNumExecutors());
        sb.append(" --executors-cores " + spark2Parameters.getExecutorCores());
        sb.append(" --executors-memory " + spark2Parameters.getExecutorMemory());
        sb.append(" --queue default ");
        sb.append(spark2Parameters.getMainJar().getRes());
        String json = "\"{\\\"in_type\\\":\\\"KAFKA\\\",\\\"in_table\\\":\\\"" + spark2Parameters.getInTable() +"\\\", \\\"in_value\\\"" +
                ":\\\"" + spark2Parameters.getInValue() + "\\\", \\\"in_value2\\\":\\\"" + spark2Parameters.getInValue2() + "\\\"" +
                ",\\\"out_type\\\":\\\"ELASTICSERACH\\\", \\\"out_table\\\":\\\"" + spark2Parameters.getOutTable() + "\\\", " +
                "\\\"application_name\\\":\\\"" + spark2Parameters.getLocalParametersMap().get("application_name").getValue() + "\\\"," +
                "\\\"offset_principle\\\":\\\"" + spark2Parameters.getLocalParametersMap().get("offset_principle").getValue() + "\\\"}'";
        sb.append(" " + json);



        String command = sb.toString();
        logger.info("spark task command : {}", command);

        return command;
    }

    @Override
    public AbstractParameters getParameters() {
        return spark2Parameters;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
