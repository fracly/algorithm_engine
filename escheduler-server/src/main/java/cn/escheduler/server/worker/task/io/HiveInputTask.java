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
package cn.escheduler.server.worker.task.io;

import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.io.HiveInputParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.TaskProps;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.Map;

/**
 *  input task
 */
public class HiveInputTask extends AbstractTask {

    /**
     *  input parameters
     */
    private HiveInputParameters hiveInputParameters;

    /**
     *  process database access
     */
    private ProcessDao processDao;


    public HiveInputTask(TaskProps props, Logger logger) {
        super(props, logger);
        if(!checkParams()){
            exitStatusCode = -2;
            return;
        }
        logger.info("input task params {}", taskProps.getTaskParams());
        this.hiveInputParameters = JSONObject.parseObject(props.getTaskParams(), HiveInputParameters.class);
        if (!hiveInputParameters.checkParameters()) {
            throw new RuntimeException("input task params is not valid");
        }
        this.processDao = DaoFactory.getDaoInstance(ProcessDao.class);
    }

    @Override
    public void handle(){
        // set the name of the current thread
        String threadLoggerInfoName = String.format("TaskLogInfo-%s", taskProps.getTaskAppId());
        Thread.currentThread().setName(threadLoggerInfoName);
        /*if(!this.checkParams()){
            exitStatusCode = -2;
            return;
        }*/
        logger.info(hiveInputParameters.toString());
        // determine whether there is a data source
        if (hiveInputParameters.getDatasource() == 0 || StringUtils.isEmpty(hiveInputParameters.getTable())){
            logger.error("datasource is null or not specify a table");
            errorMsg="参数有误：datasource is null or not specify a table";
            exitStatusCode = -2;
        }else {
            DataSource dataSource = processDao.findDataSourceById(hiveInputParameters.getDatasource());
            Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
            String table = hiveInputParameters.getTable();
            if(!table.contains(".")){
               hiveInputParameters.setTable(map.get("database") + "." + table);
            }
            logger.info("datasource name : {} , type : {} , desc : {}  , user_id : {} , parameter : {}",
                    dataSource.getName(),dataSource.getType(),dataSource.getNote(),
                    dataSource.getUserId(),dataSource.getConnectionParams());

            if (dataSource != null){
                exitStatusCode = 0;
            }
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return this.hiveInputParameters;
    }

    @Override
    public boolean checkParams() {
        String taskParams = taskProps.getTaskParams();
        if("{}".equals(taskParams)){
            this.errorMsg="组件所有参数为空，请初始化组件参数";
            return false;
        }
        try {
            this.hiveInputParameters = JSONObject.parseObject(taskParams, HiveInputParameters.class);
        }catch (Exception e){
            this.errorMsg="组件所有参数为空，任务流程参数都为空，请初始化组件参数";
            return false;
        }
        if (hiveInputParameters.getDatasource() == 0 || StringUtils.isEmpty(hiveInputParameters.getTable())){
            logger.error("datasource is null or not specify a table");
            errorMsg="参数有误：datasource is null or not specify a table";
            return false;
        }
        return true;
    }
}
