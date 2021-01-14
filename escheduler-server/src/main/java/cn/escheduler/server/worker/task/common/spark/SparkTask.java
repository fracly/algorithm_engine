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
import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.ProcessInstance;
import cn.escheduler.server.utils.DateUtils;
import cn.escheduler.server.utils.ParamUtils;
import cn.escheduler.server.utils.SparkArgsUtils;
import cn.escheduler.server.worker.task.AbstractYarnTask;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.*;

/**
 *  spark task
 */
public class SparkTask extends AbstractYarnTask {

  /**
   *  spark command
   */
  private static final String SPARK_COMMAND = "spark-submit";

  /**
   *  spark parameters
   */
  private SparkParameters sparkParameters;

  public SparkTask(TaskProps props, Logger logger) {
    super(props, logger);
    if(!checkParams()){
      exitStatusCode = -2;
      return;
    }
  }

  @Override
  public void init() {

    logger.info("spark task params {}", taskProps.getTaskParams());

    sparkParameters = JSONUtils.parseObject(taskProps.getTaskParams(), SparkParameters.class);

    if (!sparkParameters.checkParameters()) {
      throw new RuntimeException("spark task params is not valid");
    }
    sparkParameters.setQueue(taskProps.getQueue());

    List<Property> localParams = sparkParameters.getLocalParams();
    List<Property> propTime = DateUtils.getPropTime(localParams);
    sparkParameters.setLocalParams(propTime);

    if (StringUtils.isNotEmpty(sparkParameters.getMainArgs())) {
      String args = sparkParameters.getMainArgs();
      // get process instance by task instance id
      ProcessInstance processInstance = processDao.findProcessInstanceByTaskId(taskProps.getTaskInstId());

      /**
       *  combining local and global parameters
       */
      Map<String, Property> paramsMap = ParamUtils.convert(taskProps.getUserDefParamsMap(),
              taskProps.getDefinedParams(),
              sparkParameters.getLocalParametersMap(),
              processInstance.getCmdTypeIfComplement(),
              processInstance.getScheduleTime());
      if (paramsMap != null ){
        args = ParameterUtils.convertParameterPlaceholders(args, ParamUtils.convert(paramsMap));
      }
      sparkParameters.setMainArgs(args);
    }
  }

  /**
   *  create command
   * @return
   */
  @Override
  protected String buildCommand() {

    logger.info("localParams ---------------: {}",sparkParameters.getLocalParams());

    List<String> args = new ArrayList<>();

    args.add(SPARK_COMMAND);

    // other parameters
    args.addAll(SparkArgsUtils.buildArgs(sparkParameters));

    String command = ParameterUtils
            .convertParameterPlaceholders(String.join(" ", args), taskProps.getDefinedParams());

    logger.info("spark task command : {}", command);

    return command;
  }

  @Override
  public AbstractParameters getParameters() {
    return sparkParameters;
  }

  @Override
  public boolean checkParams() {
    String taskParams = taskProps.getTaskParams();
    if("{}".equals(taskParams)){
      this.errorMsg="组件所有参数为空，请初始化组件参数";
      return false;
    }
    try {
      this.sparkParameters = JSONObject.parseObject(taskParams, SparkParameters.class);
    }catch (Exception e){
      this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
      return false;
    }
    if (sparkParameters.getNumExecutors() == 0){
      logger.error("NumExecutors is 0 or isEmpty");
      errorMsg="参数有误：NumExecutors is 0 or isEmpty";
      return false;
    }
    if (sparkParameters.getDriverCores() == 0 ){
      logger.error("DriverCores is 0 or isEmpty");
      errorMsg="参数有误：DriverCores is 0 or isEmpty";
      return false;
    }
    if (sparkParameters.getDriverMemory() == null || StringUtils.isEmpty(sparkParameters.getDriverMemory())){
      logger.error("DriverMemory is null or isEmpty");
      errorMsg="参数有误：DriverMemory is null or isEmpty";
      return false;
    }
    if (sparkParameters.getExecutorCores() == 0){
      logger.error("ExecutorCores is 0 or isEmpty");
      errorMsg="参数有误：ExecutorCores is 0 or isEmpty";
      return false;
    }
    if (sparkParameters.getExecutorMemory() == null || StringUtils.isEmpty(sparkParameters.getExecutorMemory())){
      logger.error("ExecutorMemory is null or isEmpty");
      errorMsg="参数有误：ExecutorMemory is null or isEmpty";
      return false;
    }
    if (sparkParameters.getMainJar() == null || StringUtils.isEmpty(sparkParameters.getMainJar().getRes())){
      logger.error("MainJar is null or isEmpty");
      errorMsg="参数有误：MainJar is null or isEmpty";
      return false;
    }
    if (sparkParameters.getMainClass() == null || StringUtils.isEmpty(sparkParameters.getMainClass())){
      logger.error("MainClass is null or isEmpty");
      errorMsg="参数有误：MainClass is null or isEmpty";
      return false;
    }
    return true;
  }
}
