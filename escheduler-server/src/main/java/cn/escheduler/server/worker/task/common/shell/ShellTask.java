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
package cn.escheduler.server.worker.task.common.shell;


import cn.escheduler.common.Constants;
import cn.escheduler.common.process.Property;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.common.shell.ShellParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.ProcessInstance;
import cn.escheduler.server.utils.ParamUtils;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.ShellCommandExecutor;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
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
public class ShellTask extends AbstractTask {

  private ShellParameters shellParameters;

  /**
   * task dir
   */
  private String taskDir;

  private ShellCommandExecutor processTask;

  /**
   * process database access
   */
  private ProcessDao processDao;


  public ShellTask(TaskProps props, Logger logger) {
    super(props, logger);
    if(!checkParams()){
      exitStatusCode = -2;
      return;
    }

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

    shellParameters = JSONUtils.parseObject(taskProps.getTaskParams(), ShellParameters.class);

    if (!shellParameters.checkParameters()) {
      throw new RuntimeException("shell task params is not valid");
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

    String script = shellParameters.getRawScript().replaceAll("\\r\\n", "\n");

    // find process instance by task id
    ProcessInstance processInstance = processDao.findProcessInstanceByTaskId(taskProps.getTaskInstId());

    /**
     *  combining local and global parameters
     */
    Map<String, Property> paramsMap = ParamUtils.convert(taskProps.getUserDefParamsMap(),
            taskProps.getDefinedParams(),
            shellParameters.getLocalParametersMap(),
            processInstance.getCmdTypeIfComplement(),
            processInstance.getScheduleTime());
    if (paramsMap != null){
      script = ParameterUtils.convertParameterPlaceholders(script, ParamUtils.convert(paramsMap));
    }


    shellParameters.setRawScript(script);

    logger.info("raw script : {}", shellParameters.getRawScript());
    logger.info("task dir : {}", taskDir);

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    Files.createFile(path, attr);

    Files.write(path, shellParameters.getRawScript().getBytes(), StandardOpenOption.APPEND);

    return fileName;
  }

  @Override
  public AbstractParameters getParameters() {
    return shellParameters;
  }

    @Override
    public boolean checkParams() {
      String taskParams = taskProps.getTaskParams();
      if("{}".equals(taskParams)){
        this.errorMsg="组件所有参数为空，请初始化组件参数";
        return false;
      }
      try {
        this.shellParameters = JSONObject.parseObject(taskParams, ShellParameters.class);
      }catch (Exception e){
        this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
        return false;
      }
      if (shellParameters.getRawScript() == null || StringUtils.isEmpty(shellParameters.getRawScript())){
        logger.error("rawScript is null or isEmpty");
        errorMsg="参数有误：rawScript is null or isEmpty";
        return false;
      }
      return true;
    }

}
