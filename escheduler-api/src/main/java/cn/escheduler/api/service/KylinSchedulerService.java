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


import cn.escheduler.api.dto.ScheduleParam;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.quartz.BuildKylinJob;
import cn.escheduler.api.quartz.QuartzExecutors;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.DateUtils;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.common.enums.FailureStrategy;
import cn.escheduler.common.enums.Priority;
import cn.escheduler.common.enums.ReleaseState;
import cn.escheduler.common.enums.WarningType;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.mapper.MasterServerMapper;
import cn.escheduler.dao.mapper.ProcessDefinitionMapper;
import cn.escheduler.dao.mapper.ProjectMapper;
import cn.escheduler.dao.mapper.ScheduleMapper;
import cn.escheduler.dao.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static cn.escheduler.common.enums.ReleaseState.OFFLINE;
import static cn.escheduler.common.enums.ReleaseState.ONLINE;

/**
 * scheduler service
 */
@Service
public class KylinSchedulerService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(KylinSchedulerService.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ProcessDao processDao;

    @Autowired
    private MasterServerMapper masterServerMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;



    /**
     * save schedule
     *
     * @param schedule
     * @param warningType
     * @param warningGroupId
     * @param failureStrategy
     * @param workerGroupId
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> insertSchedule(String schedule, String cubeName, String type, String time, WarningType warningType, int warningGroupId, FailureStrategy failureStrategy, int workerGroupId) throws IOException {

        Map<String, Object> result = new HashMap<String, Object>(5);



        List<Schedule> schedules = scheduleMapper.queryByCubeName(cubeName);
        if(schedules.size()>0){
            putMsg(result, Status.SCHEDUER_ERROR_EXIST);
            return result;
        }



        Schedule scheduleObj = new Schedule();
        Date now = new Date();


        ScheduleParam scheduleParam = JSONUtils.parseObject(schedule, ScheduleParam.class);
        scheduleObj.setStartTime(scheduleParam.getStartTime());
        scheduleObj.setEndTime(scheduleParam.getEndTime());
        if (!org.quartz.CronExpression.isValidExpression(scheduleParam.getCrontab())) {
            logger.error(scheduleParam.getCrontab() + " verify failure");

            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, scheduleParam.getCrontab());
            return result;
        }
        scheduleObj.setCrontab(scheduleParam.getCrontab());
        scheduleObj.setCreateTime(now);
        scheduleObj.setUpdateTime(now);
        scheduleObj.setCubeName(cubeName);
        scheduleObj.setType(type);
        scheduleObj.setTime(time);

        scheduleObj.setWarningType(warningType);
        scheduleObj.setWarningGroupId(warningGroupId);
        scheduleObj.setFailureStrategy(failureStrategy);
        scheduleObj.setWorkerGroupId(workerGroupId);
        scheduleObj.setReleaseState(OFFLINE);

        scheduleMapper.insert(scheduleObj);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * updateProcessInstance schedule
     *
     * @param workerGroupId
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)

    public Map<String, Object> updateSchedule(int id,String scheduleJson,
                                              String cubeName, String type, String time,WarningType warningType,int warningGroupId,FailureStrategy failureStrategy,
                                              int workerGroupId, ReleaseState scheduleStatus) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>(5);

        // check schedule exists
        Schedule schedule = scheduleMapper.queryById(id);

        if (schedule == null) {
            putMsg(result, Status.SCHEDULE_CRON_NOT_EXISTS, id);
            return result;
        }

        Date now = new Date();

        if (scheduleStatus != null) {
            schedule.setReleaseState(scheduleStatus);
        }
        ScheduleParam scheduleParam = JSONUtils.parseObject(scheduleJson, ScheduleParam.class);
        schedule.setStartTime(scheduleParam.getStartTime());
        schedule.setEndTime(scheduleParam.getEndTime());
        schedule.setCrontab(scheduleParam.getCrontab());
        schedule.setUpdateTime(now);
        schedule.setType(type);
        schedule.setTime(time);



        scheduleMapper.update(schedule);

        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * set schedule online or offline
     *
     * @param scheduleStatus
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> setScheduleState(  String cubeName, ReleaseState scheduleStatus) {

        Map<String, Object> result = new HashMap<String, Object>(5);
        String job="kylin";
        String group="kylinGroup";
        List<Schedule> schedules = scheduleMapper.queryByCubeName(cubeName);
        if(schedules.size()==0){
            putMsg(result, Status.SCHEDUER_ERROR_NOT_EXIST);
            return result;
        }else if(schedules.size()>1){
            putMsg(result, Status.SCHEDUER_ERROR_EXIST);
            return result;
        }
        int id = schedules.get(0).getId();
        job=job+id;
        group=group+id;

        Schedule schedule = schedules.get(0);
        schedule.setReleaseState(scheduleStatus);
        scheduleMapper.update(schedule);

        try {
            switch (scheduleStatus) {
                case ONLINE: {
                    setSchedule( cubeName,job,group);
                    break;
                }
                case OFFLINE: {
                    deleteSchedule(job,group);
                    break;
                }
                default: {
                    putMsg(result, Status.SCHEDULE_STATUS_UNKNOWN, scheduleStatus.toString());
                    return result;
                }
            }
        } catch (Exception e) {
            result.put(Constants.MSG, scheduleStatus == ONLINE ? "set online failure" : "set offline failure");
            throw new RuntimeException(result.get(Constants.MSG).toString());
        }

        putMsg(result, Status.SUCCESS);
        return result;
    }



    /**
     * query schedule
     *
     * @param loginUser
     * @param projectName
     * @param processDefineId
     * @return
     */
    public Map<String, Object> querySchedule(User loginUser, String projectName, Integer processDefineId, String searchVal, Integer pageNo, Integer pageSize) {

        HashMap<String, Object> result = new HashMap<>();

        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        // check project auth
        Map<String, Object> checkResult = checkAuth(loginUser, projectName, project);
        if (checkResult != null) {
            return checkResult;
        }

        ProcessDefinition processDefinition = processDao.findProcessDefineById(processDefineId);
        if (processDefinition == null) {
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, processDefineId);
            return result;
        }

        Integer count = scheduleMapper.countByProcessDefineId(processDefineId, searchVal);

        PageInfo pageInfo = new PageInfo<Schedule>(pageNo, pageSize);

        List<Schedule> scheduleList = scheduleMapper.queryByProcessDefineIdPaging(processDefinition.getId(), searchVal, pageInfo.getStart(), pageSize);

        pageInfo.setTotalCount(count);
        pageInfo.setLists(scheduleList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * query schedule list
     *
     * @param loginUser
     * @param projectName
     * @return
     */
    public Map<String, Object> queryScheduleList(User loginUser, String projectName) {
        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        // check project auth
        Map<String, Object> checkResult = checkAuth(loginUser, projectName, project);
        if (checkResult != null) {
            return checkResult;
        }

        List<Schedule> schedules = scheduleMapper.querySchedulerListByProjectName(projectName);

        result.put(Constants.DATA_LIST, schedules);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * set schedule
     *
     * @see
     */
    public void setSchedule(String cubeName, String job, String group) throws RuntimeException{

        String body;
        Schedule schedule = scheduleMapper.queryByCubeName(cubeName).get(0);
        Map<String, Object> dataMap = QuartzExecutors.buildDataMap(0, 0, schedule);

        String h=" "+schedule.getTime();
        Map kylinTime;
        String startTime;
        String endTime;
        String type=schedule.getType();
        dataMap.put("type",type);


        if(schedule.getType().equals("LM")){
            Map lm = DateUtils.getLM();
            startTime = lm.get("s").toString();
            endTime = lm.get("e").toString();
            body="{\"buildType\": \"BUILD\", \"startTime\": "+startTime+", \"endTime\": "+endTime+"}";
            dataMap.put("e1",lm.get("e1"));
            dataMap.put("s1",lm.get("s1"));

        }else if(schedule.getType().equals("M")){
            Map M = DateUtils.getM();
            startTime = M.get("s").toString();
            endTime = M.get("e").toString();
            body="{\"buildType\": \"BUILD\", \"startTime\": "+startTime+", \"endTime\": "+endTime+"}";
            dataMap.put("e1",M.get("e1"));
            dataMap.put("s1",M.get("s1"));
            dataMap.put("dd",M.get("dd"));

        }else {
            int n=Integer.valueOf(schedule.getType());
            kylinTime= DateUtils.getKylinTime(h, n);
            endTime =kylinTime.get("endTime").toString();
            startTime=kylinTime.get("startTime").toString();
            body="{\"buildType\": \"BUILD\", \"startTime\": "+startTime+", \"endTime\": "+endTime+"}";

        }

        Date startDate = schedule.getStartTime();
        Date endDate = schedule.getEndTime();

        dataMap.put("body",body);
        dataMap.put("Cube",cubeName);

        QuartzExecutors.getInstance().addJob(BuildKylinJob.class, job, group, startDate, endDate,
                schedule.getCrontab(), dataMap);
    }

    /**
     * delete schedule
     */
    public static void deleteSchedule( String job, String group) throws RuntimeException{


        if(!QuartzExecutors.getInstance().deleteJob(job, group)){
            throw new RuntimeException(String.format("set offline failure"));
        }

    }

    /**
     * check valid
     *
     * @param result
     * @param bool
     * @param status
     * @return
     */
    private boolean checkValid(Map<String, Object> result, boolean bool, Status status) {
        // timeout is valid
        if (bool) {
            putMsg(result, status);
            return true;
        }
        return false;
    }

    /**
     *
     * @param loginUser
     * @param projectName
     * @param project
     * @return
     */
    private Map<String, Object> checkAuth(User loginUser, String projectName, Project project) {
        // check project auth
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }
        return null;
    }

    /**
     * delete schedule by id
     *
     * @param scheduleId
     * @return
     */
    public Map<String, Object> deleteScheduleById(Integer scheduleId) {

        Map<String, Object> result = new HashMap<>(5);



        Schedule schedule = scheduleMapper.queryById(scheduleId);

        if (schedule == null) {
            putMsg(result, Status.SCHEDULE_CRON_NOT_EXISTS, scheduleId);
            return result;
        }

        // check schedule is already online
        if(schedule.getReleaseState() == ONLINE){
            putMsg(result, Status.SCHEDULE_CRON_STATE_ONLINE,schedule.getId());
            return result;
        }


        int delete = scheduleMapper.delete(scheduleId);

        if (delete > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.DELETE_SCHEDULE_CRON_BY_ID_ERROR);
        }
        return result;
    }
}