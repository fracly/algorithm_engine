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


import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.KylinSchedulerService;
import cn.escheduler.api.service.SchedulerService;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.FailureStrategy;
import cn.escheduler.common.enums.Priority;
import cn.escheduler.common.enums.ReleaseState;
import cn.escheduler.common.enums.WarningType;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.escheduler.api.enums.Status.*;
import static cn.escheduler.api.utils.Constants.SESSION_USER;

/**
 * schedule controller
 */
@RestController
@RequestMapping("/projects/kylinSchedule")
public class KylinSchedulerController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(KylinSchedulerController.class);
    public static final String DEFAULT_WARNING_TYPE = "NONE";
    public static final String DEFAULT_NOTIFY_GROUP_ID = "1";
    public static final String DEFAULT_FAILURE_POLICY = "CONTINUE";


    @Autowired
    private KylinSchedulerService kylinSchedulerService;


    /**
     * create schedule
     *
     * @param schedule
     * @return
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createSchedule( @RequestParam(value = "schedule") String schedule,
                                  @RequestParam(value = "cubeName") String cubeName,
                                  @RequestParam(value = "type") String type,
                                  @RequestParam(value = "time",required = false) String time,
                                  @RequestParam(value = "warningType", required = false,defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                  @RequestParam(value = "warningGroupId", required = false,defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                                  @RequestParam(value = "failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                                  @RequestParam(value = "workerGroupId", required = false, defaultValue = "-1") int workerGroupId) {

        try {
            Map<String, Object> result = kylinSchedulerService.insertSchedule( schedule,
                    cubeName, type, time,warningType,warningGroupId,failureStrategy,workerGroupId);

            return returnDataList(result);
        }catch (Exception e){
            logger.error(CREATE_SCHEDULE_ERROR.getMsg(),e);
            return error(CREATE_SCHEDULE_ERROR.getCode(), CREATE_SCHEDULE_ERROR.getMsg());
        }
    }

    /**
     * updateProcessInstance schedule
     *
     * @param id
     * @param schedule
     * @param warningType
     * @param warningGroupId
     * @param failureStrategy
     * @return
     */
  @PostMapping("/update")
  public Result updateSchedule(@RequestParam(value = "id") Integer id,
                               @RequestParam(value = "schedule") String schedule,
                               @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                               @RequestParam(value = "warningGroupId", required = false,defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                               @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                               @RequestParam(value = "workerGroupId", required = false, defaultValue = "-1") int workerGroupId,
                               @RequestParam(value = "cubeName") String cubeName,
                               @RequestParam(value = "type") String type,
                               @RequestParam(value = "time",required = false) String time) {

      try {
          Map<String, Object> result = kylinSchedulerService.updateSchedule(id,schedule,
                  cubeName, type, time,warningType,warningGroupId,failureStrategy,workerGroupId,null);
          return returnDataList(result);

      }catch (Exception e){
          logger.error(UPDATE_SCHEDULE_ERROR.getMsg(),e);
          return error(Status.UPDATE_SCHEDULE_ERROR.getCode(), Status.UPDATE_SCHEDULE_ERROR.getMsg());
      }
  }

    /**
     * publish schedule setScheduleState
     *
     * @param cubeName
     * @return
     * @throws Exception
     */
  @PostMapping("/online")
  public Result online(@RequestParam("cubeName") String cubeName)  {

      try {
          Map<String, Object> result = kylinSchedulerService.setScheduleState( cubeName, ReleaseState.ONLINE);
          return returnDataList(result);

      }catch (Exception e){
          logger.error(PUBLISH_SCHEDULE_ONLINE_ERROR.getMsg(),e);
          return error(Status.PUBLISH_SCHEDULE_ONLINE_ERROR.getCode(), Status.PUBLISH_SCHEDULE_ONLINE_ERROR.getMsg());
      }
  }

    /**
     * offline schedule
     *
     * @return
     */
  @PostMapping("/offline")
  public Result offline(@RequestParam("cubeName") String cubeName)  {

      try {
          Map<String, Object> result = kylinSchedulerService.setScheduleState(  cubeName, ReleaseState.OFFLINE);
          return returnDataList(result);

      }catch (Exception e){
          logger.error(OFFLINE_SCHEDULE_ERROR.getMsg(),e);
          return error(Status.OFFLINE_SCHEDULE_ERROR.getCode(), Status.OFFLINE_SCHEDULE_ERROR.getMsg());
      }
  }

    /**
     * delete schedule by id
     *
     * @param scheduleId
     * @return
     */
    @GetMapping(value="/delete")
    @ResponseStatus(HttpStatus.OK)
    public Result deleteScheduleById(
                                     @RequestParam("scheduleId") Integer scheduleId
    ){
        try{
            Map<String, Object> result = kylinSchedulerService.deleteScheduleById(scheduleId);
            return returnDataList(result);
        }catch (Exception e){
            logger.error(DELETE_SCHEDULE_CRON_BY_ID_ERROR.getMsg(),e);
            return error(Status.DELETE_SCHEDULE_CRON_BY_ID_ERROR.getCode(), Status.DELETE_SCHEDULE_CRON_BY_ID_ERROR.getMsg());
        }
    }


}
