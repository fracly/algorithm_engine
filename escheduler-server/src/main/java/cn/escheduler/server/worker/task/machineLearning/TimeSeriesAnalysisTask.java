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
package cn.escheduler.server.worker.task.machineLearning;


import cn.escheduler.common.Constants;
import cn.escheduler.common.process.Property;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.machineLearning.TimeSeriesAnalysisParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.ProcessInstance;
import cn.escheduler.server.utils.ParamUtils;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.ShellCommandExecutor;
import cn.escheduler.server.worker.task.TaskProps;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Set;

/**
 *  shell task
 */
public class TimeSeriesAnalysisTask extends AbstractTask {

  private TimeSeriesAnalysisParameters timeSeriesAnalysisParameters;

  /**
   * task dir
   */
  private String taskDir;

  private ShellCommandExecutor processTask;

  /**
   * process database access
   */
  private ProcessDao processDao;


  public TimeSeriesAnalysisTask(TaskProps props, Logger logger) {
    super(props, logger);

    this.taskDir = props.getTaskDir();

    this.processTask = new ShellCommandExecutor(this::logHandle,
        props.getTaskDir(), props.getTaskAppId(),
        props.getTenantCode(), props.getEnvFile(), props.getTaskStartTime(),
        props.getTaskTimeout(), logger);
    this.processDao = DaoFactory.getDaoInstance(ProcessDao.class);
  }

  @Override
  public void init() {
    logger.info("shell task params {}", taskProps.getTaskParams());

    timeSeriesAnalysisParameters = JSONUtils.parseObject(taskProps.getTaskParams(), TimeSeriesAnalysisParameters.class);

    if (!timeSeriesAnalysisParameters.checkParameters()) {
      throw new RuntimeException("TimeSeriesAnalysis task params is not valid");
    }
  }

  @Override
  public void handle() throws Exception {
    try {
      // construct process
      exitStatusCode = processTask.run(buildCommand(), processDao);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      exitStatusCode = -1;
    }
  }

  @Override
  public void cancelApplication(boolean cancelApplication) throws Exception {
    // cancel process
    processTask.cancelApplication();
  }

  /**
   *  create command
   * @return
   * @throws Exception
   */
  private String buildCommand() throws Exception {
    // generate scripts
    String fileName = String.format("%s/%s_node.sh", taskDir, taskProps.getTaskAppId());
    Path path = new File(fileName).toPath();

    if (Files.exists(path)) {
      return fileName;
    }

    String script = "python /opt/python_algorithms/arima.py";

    // find process instance by task id
    ProcessInstance processInstance = processDao.findProcessInstanceByTaskId(taskProps.getTaskInstId());

    /**
     *  combining local and global parameters
     */
    Map<String, Property> paramsMap = ParamUtils.convert(taskProps.getUserDefParamsMap(),
            taskProps.getDefinedParams(),
            timeSeriesAnalysisParameters.getLocalParametersMap(),
            processInstance.getCmdTypeIfComplement(),
            processInstance.getScheduleTime());
    if (paramsMap != null){

      DataSource dataSource = processDao.findDataSourceById(timeSeriesAnalysisParameters.getDatasource());
      Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
      String database=map.get("database");
      timeSeriesAnalysisParameters.setDatabases(database);
      String inTable=timeSeriesAnalysisParameters.getTable();

      String getTime=timeSeriesAnalysisParameters.getTimeColumn();
      String getNum=timeSeriesAnalysisParameters.getNumColumn();
      String clo= getTime+","+getNum;

      String outTable=database+"."+timeSeriesAnalysisParameters.getTaskId().replace("-","_");
      String sql="select "+clo+" from "+database+"."+inTable;
      int num=timeSeriesAnalysisParameters.getStepNum();

      script = ParameterUtils.convertParameterPlaceholders(script, ParamUtils.convert(paramsMap))+" \""+sql+"\" "+num+" "+getTime+" "+getNum+" "+outTable+" ";
    }


    timeSeriesAnalysisParameters.setRawScript(script);

    logger.info("raw script : {}", timeSeriesAnalysisParameters.getRawScript());
    logger.info("task dir : {}", taskDir);

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    Files.createFile(path, attr);

    Files.write(path, timeSeriesAnalysisParameters.getRawScript().getBytes(), StandardOpenOption.APPEND);

    return fileName;
  }

  @Override
  public AbstractParameters getParameters() {
    return timeSeriesAnalysisParameters;
  }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }

}
