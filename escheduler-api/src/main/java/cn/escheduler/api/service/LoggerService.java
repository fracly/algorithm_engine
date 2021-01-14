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
package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.log.LogClient;
import cn.escheduler.api.utils.HiveDataSourceUtil;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.Constants;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.TaskInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * log service
 */
@Service
public class LoggerService {

  private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

  @Autowired
  private ProcessDao processDao;

  /**
   * view log
   *
   * @param taskInstId
   * @param skipLineNum
   * @param limit
   * @return
   */
  public Result queryLog(int taskInstId, int skipLineNum, int limit) {

    TaskInstance taskInstance = processDao.findTaskInstanceById(taskInstId);
    String host = taskInstance.getHost();
    if(StringUtils.isEmpty(host)){
      return new Result(Status.TASK_INSTANCE_HOST_NOT_FOUND.getCode(), Status.TASK_INSTANCE_HOST_NOT_FOUND.getMsg());
    }
    logger.info("log host : {} , logPath : {} , logServer port : {}",host,taskInstance.getLogPath(),Constants.RPC_PORT);

    Result result = new Result(Status.SUCCESS.getCode(), Status.SUCCESS.getMsg());

    if(host != null){
      LogClient logClient = new LogClient(host, Constants.RPC_PORT);
      String log = logClient.rollViewLog(taskInstance.getLogPath(),skipLineNum,limit);
      result.setData(log);
      logger.info(log);
    }

    return result;
  }

  public Result queryResult(int taskInstId, int skipLineNum, int limit) throws SQLException {
    TaskInstance taskInstance = processDao.findTaskInstanceById(taskInstId);
    String resultTable = taskInstance.getResultTable();
    logger.info("查看结果，表名：{}" + resultTable);

    Result result = new Result();

    if(StringUtils.isEmpty(resultTable)){
      result.setData("该组件没有结果表，无法查看结果数据！");
      return result;
    }
    String[] dbAndTable = resultTable.split("\\.");

    List<List<String>> resultList = new ArrayList<>();

    DataSource dataSource = HiveDataSourceUtil.getHiveDataSource();
    Connection connection = null;
    Statement stmt = null;

    ResultSet columnRS = null;
    ResultSet dataRS = null;

    try{
        connection = dataSource.getConnection();
        stmt = connection.createStatement();
        columnRS = stmt.executeQuery("desc " + dbAndTable[0] + "." + dbAndTable[1]);

        List<String> columnList = new ArrayList<>();
        while(columnRS.next()) {
          if(columnRS.getString(2) == null || columnRS.getString(1).equals("")) {
            break;
          } else if(columnRS.getString(3) != null && !columnRS.getString(3).equals("")) {
              columnList.add(columnRS.getString(3));
          } else {
              columnList.add(columnRS.getString(1));
          }

        }
        resultList.add(columnList);

        dataRS = stmt.executeQuery("select * from " + resultTable + " limit " + limit);
        while(dataRS.next()) {
            List<String> tmpList = new ArrayList<>();
            for(int j = 1; j <= columnList.size(); j ++) {
              tmpList.add(dataRS.getString(j));
            }
            resultList.add(tmpList);
        }
    } catch (SQLException e) {
      logger.error("读取数据失败，请查看报错信息！" + e.getMessage());
    } finally {
      HiveDataSourceUtil.close(connection, stmt, columnRS);
      HiveDataSourceUtil.close(connection, stmt, dataRS);
    }

    result.setData(resultList);
    result.setCode(0);
    result.setMsg("查询成功");
    return result;
  }

  /**
   * get log size
   *
   * @param taskInstId
   * @return
   */
  public byte[] getLogBytes(int taskInstId) {
    TaskInstance taskInstance = processDao.findTaskInstanceById(taskInstId);
    if (taskInstance == null){
      throw new RuntimeException("task instance is null");
    }
    String host = taskInstance.getHost();
    LogClient logClient = new LogClient(host, Constants.RPC_PORT);
    return logClient.getLogBytes(taskInstance.getLogPath());
  }
}
