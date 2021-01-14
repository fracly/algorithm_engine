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
package cn.escheduler.api.controller;


import cn.escheduler.api.service.DataAnalysisService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.*;

/**
 * data analysis controller
 */
@RestController
@RequestMapping("projects/analysis")
public class DataAnalysisController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisController.class);


    @Autowired
    DataAnalysisService dataAnalysisService;


    /**
     * statistical task instance status data
     *
     * @param loginUser
     * @param projectId
     * @return
     */
    @GetMapping(value="/task-state-count")
    @ResponseStatus(HttpStatus.OK)
    public Result countTaskState(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                         @RequestParam(value="startDate", required=false) String startDate,
                                                         @RequestParam(value="endDate", required=false) String endDate,
                                                         @RequestParam(value="projectId", required=false, defaultValue = "0") int projectId){
        try{
            logger.info("count task state, user:{}, start date: {}, end date:{}, project id {}",
                    loginUser.getUserName(), startDate, endDate, projectId);
            Map<String, Object> result = dataAnalysisService.countTaskStateByProject(loginUser,projectId, startDate, endDate);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(TASK_INSTANCE_STATE_COUNT_ERROR.getMsg(),e);
            return error(TASK_INSTANCE_STATE_COUNT_ERROR.getCode(), TASK_INSTANCE_STATE_COUNT_ERROR.getMsg());
        }
    }

    /**
     * statistical process instance status data
     *
     * @param loginUser
     * @param projectId
     * @return
     */
    @GetMapping(value="/process-state-count")
    @ResponseStatus(HttpStatus.OK)
    public Result countProcessInstanceState(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @RequestParam(value="startDate", required=false) String startDate,
                                            @RequestParam(value="endDate", required=false) String endDate,
                                            @RequestParam(value="projectId", required=false, defaultValue = "0") int projectId){
        try{
            logger.info("count process instance state, user:{}, start date: {}, end date:{}, project id",
                    loginUser.getUserName(), startDate, endDate, projectId);
            Map<String, Object> result = dataAnalysisService.countProcessInstanceStateByProject(loginUser, projectId, startDate, endDate);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(COUNT_PROCESS_INSTANCE_STATE_ERROR.getMsg(),e);
            return error(COUNT_PROCESS_INSTANCE_STATE_ERROR.getCode(), COUNT_PROCESS_INSTANCE_STATE_ERROR.getMsg());
        }
    }

    /**
     * statistics the process definition quantities of certain person
     *
     * @param loginUser
     * @param projectId
     * @return
     */
    @GetMapping(value="/define-user-count")
    @ResponseStatus(HttpStatus.OK)
    public Result countDefinitionByUser(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @RequestParam(value="projectId", required=false, defaultValue = "0") int projectId){
        try{
            logger.info("count process definition , user:{}, project id",
                    loginUser.getUserName(), projectId);
            Map<String, Object> result = dataAnalysisService.countDefinitionByUser(loginUser, projectId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(COUNT_PROCESS_DEFINITION_USER_ERROR.getMsg(),e);
            return error(COUNT_PROCESS_DEFINITION_USER_ERROR.getCode(), COUNT_PROCESS_DEFINITION_USER_ERROR.getMsg());
        }
    }


    /**
     * statistical command status data
     *
     * @param loginUser
     * @param projectId
     * @return
     */
    @GetMapping(value="/command-state-count")
    @ResponseStatus(HttpStatus.OK)
    public Result countCommandState(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value="startDate", required=false) String startDate,
                                 @RequestParam(value="endDate", required=false) String endDate,
                                 @RequestParam(value="projectId", required=false, defaultValue = "0") int projectId){
        try{
            logger.info("count command state, user:{}, start date: {}, end date:{}, project id {}",
                    loginUser.getUserName(), startDate, endDate, projectId);
            Map<String, Object> result = dataAnalysisService.countCommandState(loginUser, projectId, startDate, endDate);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(COMMAND_STATE_COUNT_ERROR.getMsg(),e);
            return error(COMMAND_STATE_COUNT_ERROR.getCode(), COMMAND_STATE_COUNT_ERROR.getMsg());
        }
    }

    /**
     * queue count
     *
     * @param loginUser
     * @param projectId
     * @return
     */
    @GetMapping(value="/queue-count")
    @ResponseStatus(HttpStatus.OK)
    public Result countQueueState(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @RequestParam(value="projectId", required=false, defaultValue = "0") int projectId){
        try{
            logger.info("count command state, user:{}, start date: {}, end date:{}, project id {}",
                    loginUser.getUserName(), projectId);
            Map<String, Object> result = dataAnalysisService.countQueueState(loginUser, projectId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(QUEUE_COUNT_ERROR.getMsg(),e);
            return error(QUEUE_COUNT_ERROR.getCode(), QUEUE_COUNT_ERROR.getMsg());
        }
    }

    /**
     * 模型任务总数
     */
    @GetMapping(value="/process/statistic")
    @ResponseStatus(HttpStatus.OK)
    public Result processStatistic() {
        return dataAnalysisService.countProcessDefinition();
    }

    /**
     * 模型任务日均运行数量
     */
    @GetMapping(value="/process/statistic/avg-run-times")
    @ResponseStatus(HttpStatus.OK)
    public Result processAvgRunTimesStatistic() {
        return dataAnalysisService.countProcessAvgRunTimesStatistic();
    }

    /**
     * 报表数量
     */
    @GetMapping(value="/report/statistic")
    @ResponseStatus(HttpStatus.OK)
    public Result countReport() {
        return dataAnalysisService.countReport();
    }

    /**
     * 报表总调用数量
     */
    @GetMapping(value="/report/statistic/avg-invoke-times")
    @ResponseStatus(HttpStatus.OK)
    public Result reportAvgInvokeTimes() {
        return dataAnalysisService.reportAvgInvokeTimes();
    }

    /**
     * 接口服务总数
     */
    @GetMapping(value="/service/statistic")
    @ResponseStatus(HttpStatus.OK)
    public Result serviceCount() {
        return dataAnalysisService.countService();
    }

    /**
     * 近30天日均调用总数
     */
    @GetMapping(value="/service/statistic/avg-invoke-times")
    @ResponseStatus(HttpStatus.OK)
    public Result serviceAvgInvokeTimes() {
        return dataAnalysisService.serviceAvgInvokeTimes();
    }

    /**
     * 服务总调数
     */
    @GetMapping(value="/service/statistic/invoke-times")
    @ResponseStatus(HttpStatus.OK)
    public Result serviceInvokeTimes() {
        return dataAnalysisService.serviceInvokeTimes();
    }

    /**
     * 当前用户今日运行模型实例数
     */
    @GetMapping(value="/process/statistic/today-invoke-times")
    @ResponseStatus(HttpStatus.OK)
    public Result processTodayInvokeTimes(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return dataAnalysisService.countProcessTodayRunTimesStatistic(loginUser.getId());
    }

    /**
     * 当前用户今日运行模型实例成功数
     */
    @GetMapping(value="/process/statistic/today-invoke/success-times")
    @ResponseStatus(HttpStatus.OK)
    public Result processTodayInvokeSuccessTimes(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return dataAnalysisService.countProcessTodaySuccessTimesStatistic(loginUser.getId());
    }

    /**
     * 当前用户今日运行模型实例失败数
     */
    @GetMapping(value="/process/statistic/today-invoke/failed-times")
    @ResponseStatus(HttpStatus.OK)
    public Result processTodayInvokeFailedTimes(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return dataAnalysisService.countProcessTodayFailedTimesStatistic(loginUser.getId());
    }

    /**
     * 近10天服务调用次数统计
     */
    @GetMapping(value="/service/statistic/invoke-times/by-day")
    @ResponseStatus(HttpStatus.OK)
    public Result serviceInvokeTimesByDay(@RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime) {
        return dataAnalysisService.serviceInvokeTimesByDay(startTime, endTime);
    }

    /**
     * hive 表数据量排行Top10（按记录条数）
     */
    @GetMapping(value="/hive/record-rank/top10")
    @ResponseStatus(HttpStatus.OK)
    public Result hiveRecordRankTop10() {
        return dataAnalysisService.hiveRecordRankTop10();
    }

    /**
     * hive 记录总数
     */
    @GetMapping(value="/hive/record-total")
    @ResponseStatus(HttpStatus.OK)
    public Result hiveRecordTotal() {
        return dataAnalysisService.hiveRecordTotal();
    }

    /**
     * hive 表总数
     */
    @GetMapping(value="/hive/table-total")
    @ResponseStatus(HttpStatus.OK)
    public Result hiveTableTotal() {
        return dataAnalysisService.hiveTableTotal();
    }
}
