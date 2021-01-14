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

import cn.escheduler.api.dto.treeview.Instance;
import cn.escheduler.api.dto.treeview.TreeViewDto;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.common.enums.*;
import cn.escheduler.common.graph.DAG;
import cn.escheduler.common.model.TaskNode;
import cn.escheduler.common.model.TaskNodeRelation;
import cn.escheduler.common.process.ProcessDag;
import cn.escheduler.common.process.Property;
import cn.escheduler.common.thread.Stopper;
import cn.escheduler.common.utils.CollectionUtils;
import cn.escheduler.common.utils.DateUtils;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.mapper.*;
import cn.escheduler.dao.model.*;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.escheduler.api.enums.Status.UPDATE_PROCESS_DEFINITION_ERROR;
import static cn.escheduler.api.service.SchedulerService.deleteSchedule;
import static cn.escheduler.api.utils.CheckUtils.checkOtherParams;
import static cn.escheduler.api.utils.CheckUtils.checkTaskNodeParameters;
import static cn.escheduler.common.Constants.CMDPARAM_SUB_PROCESS_DEFINE_ID;

/**
 * process definition service
 */
@Service
public class ProcessDefinitionService extends BaseDAGService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDefinitionService.class);


    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProcessDefinitionMapper processDefineMapper;

    @Autowired
    private ProcessInstanceMapper processInstanceMapper;


    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private ProcessDao processDao;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    /**
     * create process definition
     *
     * @param loginUser
     * @param projectName
     * @param name
     * @param processDefinitionJson
     * @param desc
     * @param locations
     * @param connects
     * @return
     */
    public Map<String, Object> createProcessDefinition(User loginUser, String projectName, String name,
                                                       String processDefinitionJson, String desc, String locations, String connects) throws JsonProcessingException {

        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());
        // check project auth
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }

        List<Map> maps = projectUserMapper.queryDefinition(project.getId(),1,loginUser.getId());
        for (Map map:maps){
            if(map.get("name").equals(name)){
                putMsg(result, Status.CREATE_DEF_NAME_EXIST);
                return result;
            }
        }

        ProcessDefinition processDefine = new ProcessDefinition();
        Date now = new Date();

        ProcessData processData = JSONUtils.parseObject(processDefinitionJson, ProcessData.class);
        Map<String, Object> checkProcessJson = checkProcessNodeList(processData, processDefinitionJson);

        // 注释掉 创建模型时 DAG 检验
//        if (checkProcessJson.get(Constants.STATUS) != Status.SUCCESS) {
//            return checkProcessJson;
//        }

        processDefine.setName(name);
        processDefine.setReleaseState(ReleaseState.OFFLINE);
        processDefine.setProjectId(project.getId());
        processDefine.setUserId(loginUser.getId());
        processDefine.setProcessDefinitionJson(processDefinitionJson);
        processDefine.setDesc(desc);
        processDefine.setLocations(locations);
        processDefine.setConnects(connects);
        processDefine.setTimeout(processData.getTimeout());

        //custom global params
        List<Property> globalParamsList = processData.getGlobalParams();
        if (globalParamsList != null && globalParamsList.size() > 0) {
            Set<Property> globalParamsSet = new HashSet<>(globalParamsList);
            globalParamsList = new ArrayList<>(globalParamsSet);
            processDefine.setGlobalParamList(globalParamsList);
        }
        processDefine.setCreateTime(now);
        processDefine.setUpdateTime(now);
        processDefine.setFlag(Flag.YES);
        processDefineMapper.insert(processDefine);
        int defineId = processDefineMapper.getDefineIdNormal(projectName, name);
        Map map=new HashMap();
        map.put("id",defineId);

        result.put(Constants.DATA_LIST, map);
        putMsg(result, Status.SUCCESS,map);


        result.put("id",defineId);


        return result;
    }


    /**
     * create process definition
     *
     * @param loginUser
     * @param projectName
     * @return
     */
    public Map<String, Object> createProcessDefinitionKylin(User loginUser, String projectName ,String cubeName) throws JsonProcessingException {

        Date now=new Date();

        String wId=getSixNum();
        String tId=getSixNum();

        String processDefinitionJson = "{\"workId\":\"work_"+wId+"\",\"globalParams\":[],\"tasks\":[{\"id\":\"tasks_"+tId+"\",\"type\":\"SHELL\",\"name\":\""+cubeName+"\",\"desc\":\"\",\"dependence\":{},\"preTasks\":[],\"runFlag\":\"NORMAL\",\"maxRetryTimes\":\"0\",\"retryInterval\":\"1\",\"taskInstancePriority\":\"MEDIUM\",\"params\":{\"cloumList\":[],\"rawScript\":\"cubeName="+cubeName+"\\nkylin_server=10.20.10.174\\ntdTs1=`date -d\\\"2020-12-06 08:00:00\\\" +%s`\\ntdTs2=`date -d\\\"2020-12-07 08:00:00\\\" +%s`\\nstartTime=$(($tdTs1*1000))\\nendTime=$(($tdTs2*1000))\\ncurl --user ADMIN:KYLIN -X PUT -H 'Content-Type: application/json' -d '{\\\"startTime\\\":'$startTime',\\\"endTime\\\":'$endTime', \\\"buildType\\\":\\\"BUILD\\\"}' http://${kylin_server}:7070/kylin/api/cubes/${cubeName}/rebuild\",\"resourceList\":[],\"localParams\":[]},\"workerGroupId\":-1,\"timeout\":{\"strategy\":\"\",\"enable\":false}}],\"timeout\":0}";
        String locations =  JSON.toJSONString(new HashMap<>());
        String desc = "";
        String connects =  JSON.toJSONString(new ArrayList<>());


        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());
        // check project auth
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }

        List<Map> maps = projectUserMapper.queryDefinition(project.getId(),1,loginUser.getId());
        for (Map map:maps){
            if(map.get("name").equals(cubeName)){
                putMsg(result, Status.CREATE_DEF_NAME_EXIST);
                return result;
            }
        }

        ProcessDefinition processDefine = new ProcessDefinition();
        processDefine.setName(cubeName);
        processDefine.setReleaseState(ReleaseState.OFFLINE);
        processDefine.setProjectId(project.getId());
        processDefine.setUserId(loginUser.getId());
        processDefine.setProcessDefinitionJson(processDefinitionJson);
        processDefine.setDesc(desc);
        processDefine.setLocations(locations);
        processDefine.setConnects(connects);
        processDefine.setTimeout(0);

        //custom global params
        List<Property> globalParamsList = new ArrayList();
        processDefine.setGlobalParamList(globalParamsList);

        processDefine.setCreateTime(now);
        processDefine.setUpdateTime(now);
        processDefine.setFlag(Flag.YES);
        processDefineMapper.insert(processDefine);
        int defineId = processDefineMapper.getDefineIdNormal(projectName, cubeName);
        Map map=new HashMap();
        map.put("id",defineId);

        result.put(Constants.DATA_LIST, map);
        putMsg(result, Status.SUCCESS,map);

        result.put("id",defineId);

        return result;
    }


    /**
     * query proccess definition list
     *
     * @param loginUser
     * @param projectName
     * @return
     */
    public Map<String, Object> queryProccessDefinitionList(User loginUser, String projectName) {

        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }

        List<ProcessDefinition> resourceList = processDefineMapper.queryAllDefinitionList(project.getId());
        result.put(Constants.DATA_LIST, resourceList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * getWorkId
     *
     * @param definitionId
     * @return
     */
    public Map<String, Object> getWorkId(int definitionId) {
        HashMap<String, Object> result = new HashMap();
        Map workId = processDefineMapper.getWorkId(definitionId);
        result.put(Constants.DATA_LIST,workId);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * updateWorkId
     *
     * @param definitionId
     * @param failureStrategy
     * @param warningType
     * @param processInstancePriority
     * @param warningGroupId
     * @param execType
     * @param runMode
     * @return
     */
    public Map<String, Object> updateWorkId(int definitionId, int groupId, FailureStrategy failureStrategy, WarningType warningType, Priority processInstancePriority,
                                            Integer warningGroupId, CommandType execType, String scheduleTime, RunMode runMode ) {
        HashMap<String, Object> result = new HashMap();
        Map workId = processDefineMapper.getWorkId(definitionId);
        if(workId==null){
            processDefineMapper.insertWorkId(definitionId,groupId,failureStrategy,warningType,processInstancePriority,warningGroupId,execType,scheduleTime,runMode);
            putMsg(result, Status.SUCCESS);
        }else {
            if(processInstancePriority==null){
                processInstancePriority= (Priority) workId.get("processInstancePriority");
            }
            processDefineMapper.updateWorkId(definitionId,groupId,failureStrategy,warningType,processInstancePriority,warningGroupId,execType,scheduleTime,runMode);
            scheduleMapper.updateWork(definitionId,groupId,failureStrategy,warningType,processInstancePriority,warningGroupId);
            putMsg(result, Status.SUCCESS);
        }
        return result;
    }

    /**
     * query proccess definition list paging
     *
     * @param loginUser
     * @param projectName
     * @param searchVal
     * @param pageNo
     * @param pageSize
     * @param userId
     * @return
     */
    public Map<String, Object> queryProcessDefinitionListPaging(User loginUser, String projectName, String searchVal, Integer pageNo, Integer pageSize, Integer userId) {

        Map<String, Object> result = new HashMap<>(5);

        Project project=null;
        String projectId=null;
        if(projectName!=null&& StringUtils.isNotEmpty(projectName)){
            project = projectMapper.queryByName(projectName);
            projectId= String.valueOf(project.getId());
            Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
            Status resultStatus = (Status) checkResult.get(Constants.STATUS);
            if (resultStatus != Status.SUCCESS) {
                return checkResult;
            }
        }

        userId=loginUser.getId();


        Integer count = processDefineMapper.countDefineNumber2(projectId, userId, searchVal);

        PageInfo pageInfo = new PageInfo<ProcessData>(pageNo, pageSize);
        List<ProcessDefinition> resourceList = processDefineMapper.queryDefineListPaging2(projectId,
                searchVal, userId, pageInfo.getStart(), pageSize);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(resourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * query datail of process definition
     *
     * @param loginUser
     * @param projectName
     * @param processId
     * @return
     */
    public Map<String, Object> queryProccessDefinitionById(User loginUser, String projectName, Integer processId) {


        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

//        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
//        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
//        if (resultStatus != Status.SUCCESS) {
//            return checkResult;
//        }

        ProcessDefinition processDefinition = processDefineMapper.queryByDefineId(processId);
        if (processDefinition == null) {
            putMsg(result, Status.PROCESS_INSTANCE_NOT_EXIST, processId);
        } else {
            result.put(Constants.DATA_LIST, processDefinition);
            putMsg(result, Status.SUCCESS);
        }
        return result;
    }

    /**
     * update  process definition
     *
     * @param loginUser
     * @param projectName
     * @param id
     * @param name
     * @param processDefinitionJson
     * @param desc
     * @param locations
     * @param connects
     * @return
     */
    public Map<String, Object> updateProcessDefinition(User loginUser, String projectName, int id, String name,
                                                       String processDefinitionJson, String desc,
                                                       String locations, String connects) {
        Map<String, Object> result = new HashMap<>(5);

        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }

        ProcessData processData = JSONUtils.parseObject(processDefinitionJson, ProcessData.class);
        Map<String, Object> checkProcessJson = checkProcessNodeList(processData, processDefinitionJson);
        if ((checkProcessJson.get(Constants.STATUS) != Status.SUCCESS)) {
            return checkProcessJson;
        }
        ProcessDefinition processDefinition = processDao.findProcessDefineById(id);
        if (processDefinition == null) {
            // check process definition exists
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, id);
            return result;
//        } else if (processDefinition.getReleaseState() == ReleaseState.ONLINE) {
//            // online can not permit edit
//            putMsg(result, Status.PROCESS_DEFINE_NOT_ALLOWED_EDIT, processDefinition.getName());
//            return result;
        } else {
            putMsg(result, Status.SUCCESS);
        }

        ProcessDefinition processDefine = processDao.findProcessDefineById(id);
        Date now = new Date();

        processDefine.setId(id);
        processDefine.setName(name);
        processDefine.setReleaseState(ReleaseState.OFFLINE);
        processDefine.setProjectId(project.getId());
        processDefine.setProcessDefinitionJson(processDefinitionJson);
        processDefine.setDesc(desc);
        processDefine.setLocations(locations);
        processDefine.setConnects(connects);
        processDefine.setTimeout(processData.getTimeout());

        //custom global params
        List<Property> globalParamsList = new ArrayList<>();
        if (processData.getGlobalParams() != null && processData.getGlobalParams().size() > 0) {
            Set<Property> userDefParamsSet = new HashSet<>(processData.getGlobalParams());
            globalParamsList = new ArrayList<>(userDefParamsSet);
        }
        processDefine.setGlobalParamList(globalParamsList);
        processDefine.setUpdateTime(now);
        processDefine.setFlag(Flag.YES);
        if (processDefineMapper.update(processDefine) > 0) {
            putMsg(result, Status.SUCCESS);

        } else {
            putMsg(result, UPDATE_PROCESS_DEFINITION_ERROR);
        }
        return result;
    }

    /**
     * verify process definition name unique
     *
     * @param loginUser
     * @param projectName
     * @param name
     * @return
     */
    public Map<String, Object> verifyProccessDefinitionName(User loginUser, String projectName, String name) {

        Map<String, Object> result = new HashMap<>();
            Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

            Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
            Status resultEnum = (Status) checkResult.get(Constants.STATUS);
            if (resultEnum != Status.SUCCESS) {
                return checkResult;
            }
            ProcessDefinition processDefinition = processDefineMapper.queryByDefineName(project.getId(), name);
            if (processDefinition == null) {
                putMsg(result, Status.SUCCESS);
            } else {
                putMsg(result, Status.PROCESS_INSTANCE_EXIST, name);
            }
        return result;
    }

    /**
     * delete process definition by id
     *
     * @param loginUser
     * @param projectName
     * @param processDefinitionId
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> deleteProcessDefinitionById(User loginUser, String projectName, Integer processDefinitionId) {

        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }

        ProcessDefinition processDefinition = processDefineMapper.queryByDefineId(processDefinitionId);

        if (processDefinition == null) {
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, processDefinitionId);
            return result;
        }

        // Determine if the login user is the owner of the process definition
        if (loginUser.getId() != processDefinition.getUserId()) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        // check process definition is already online
//        if (processDefinition.getReleaseState() == ReleaseState.ONLINE) {
//            putMsg(result, Status.PROCESS_DEFINE_STATE_ONLINE,processDefinitionId);
//            return result;
//        }

        // get the timing according to the process definition
        List<Schedule> schedules = scheduleMapper.queryByProcessDefinitionId(processDefinitionId);
        if (!schedules.isEmpty() && schedules.size() > 1) {
            logger.warn("scheduler num is {},Greater than 1",schedules.size());
            putMsg(result, Status.DELETE_PROCESS_DEFINE_BY_ID_ERROR);
            return result;
        }else if(schedules.size() == 1){
            Schedule schedule = schedules.get(0);
            if(schedule.getReleaseState() == ReleaseState.OFFLINE){
                scheduleMapper.delete(schedule.getId());
            }else if(schedule.getReleaseState() == ReleaseState.ONLINE){
                putMsg(result, Status.SCHEDULE_CRON_STATE_ONLINE,schedule.getId());
                return result;
            }
        }

        int delete = processDefineMapper.delete(processDefinitionId);

        if (delete > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.DELETE_PROCESS_DEFINE_BY_ID_ERROR);
        }
        return result;
    }

    /**
     * batch delete process definition by ids
     *
     * @param loginUser
     * @param projectName
     * @param processDefinitionIds
     * @return
     */
    public Map<String, Object> batchDeleteProcessDefinitionByIds(User loginUser, String projectName, String processDefinitionIds) {

        Map<String, Object> result = new HashMap<>(5);

        Map<String, Object> deleteReuslt = new HashMap<>(5);

        List<Integer> deleteFailedIdList = new ArrayList<Integer>();
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }


        if(StringUtils.isNotEmpty(processDefinitionIds)){
            String[] processInstanceIdArray = processDefinitionIds.split(",");

            for (String strProcessInstanceId:processInstanceIdArray) {
                int processInstanceId = Integer.parseInt(strProcessInstanceId);
                try {
                    deleteReuslt = deleteProcessDefinitionById(loginUser, projectName, processInstanceId);
                    if(!Status.SUCCESS.equals(deleteReuslt.get(Constants.STATUS))){
                        deleteFailedIdList.add(processInstanceId);
                        logger.error((String)deleteReuslt.get(Constants.MSG));
                    }
                } catch (Exception e) {
                    deleteFailedIdList.add(processInstanceId);
                }
            }
        }

        if(deleteFailedIdList.size() > 0){
            putMsg(result, Status.BATCH_DELETE_PROCESS_DEFINE_BY_IDS_ERROR,StringUtils.join(deleteFailedIdList.toArray(),","));
        }else{
            putMsg(result, Status.SUCCESS);
        }
        return result;
    }

    /**
     * release process definition: online / offline
     *
     * @param loginUser
     * @param projectName
     * @param id
     * @param releaseState
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> releaseProcessDefinition(User loginUser, String projectName, int id, int releaseState) {
        HashMap<String, Object> result = new HashMap<>();
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }

        ReleaseState state = ReleaseState.getEnum(releaseState);

        switch (state) {
            case ONLINE: {
                processDefineMapper.updateProcessDefinitionReleaseState(id, state);
                break;
            }
            case OFFLINE: {
                processDefineMapper.updateProcessDefinitionReleaseState(id, state);
                List<Schedule> scheduleList = scheduleMapper.selectAllByProcessDefineArray(new int[]{id});

                for(Schedule schedule:scheduleList){
                    logger.info("set schedule offline, schedule id: {}, process definition id: {}", project.getId(), schedule.getId(), id);
                    // set status
                    schedule.setReleaseState(ReleaseState.OFFLINE);
                    scheduleMapper.update(schedule);
                    deleteSchedule(project.getId(), id);
                }
                break;
            }
            default: {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "releaseState");
                return result;
            }
        }

        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * check the process definition node meets the specifications
     *
     * @param processData
     * @param processDefinitionJson
     * @return
     */
    public Map<String, Object> checkProcessNodeList(ProcessData processData, String processDefinitionJson) {

        Map<String, Object> result = new HashMap<>(5);
        try {
            if (processData == null) {
                logger.error("process data is null");
                putMsg(result,Status.DATA_IS_NOT_VALID, processDefinitionJson);
                return result;
            }

            // Check whether the task node is normal
            List<TaskNode> taskNodes = processData.getTasks();

            if (taskNodes == null) {
                logger.error("process node info is empty");
                putMsg(result, Status.DATA_IS_NULL, processDefinitionJson);
                return result;
            }

            // check has cycle
            if (graphHasCycle(taskNodes)) {
                logger.error("process DAG has cycle");
                putMsg(result, Status.PROCESS_NODE_HAS_CYCLE);
                return result;
            }

            // check whether the process definition json is normal
            for (TaskNode taskNode : taskNodes) {
                //暂时不进行参数校验
                if (!checkTaskNodeParameters(taskNode.getParams(), taskNode.getType())) {
                    /*logger.error("getParams"  + taskNode.getParams());
                    logger.error("getType"  + taskNode.getType());
                    logger.error("task node {} parameter invalid", taskNode.getName());
                    putMsg(result, Status.PROCESS_NODE_S_PARAMETER_INVALID, taskNode.getName());
                    return result;*/
                }
                // check extra params
                checkOtherParams(taskNode.getExtras());
            }
            putMsg(result,Status.SUCCESS);
        } catch (Exception e) {
            result.put(Constants.STATUS, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
            result.put(Constants.MSG, e.getMessage());
        }
        return result;
    }

    /**
     * get task node details based on process definition
     */
    public Map<String, Object> getTaskNodeListByDefinitionId(Integer defineId) throws Exception {
        Map<String, Object> result = new HashMap<>();

        ProcessDefinition processDefinition = processDefineMapper.queryByDefineId(defineId);
        if (processDefinition == null) {
            logger.info("process define not exists");
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, processDefinition.getId());
            return result;
        }


        String processDefinitionJson = processDefinition.getProcessDefinitionJson();

        ProcessData processData = JSONUtils.parseObject(processDefinitionJson, ProcessData.class);

        List<TaskNode> taskNodeList = (processData.getTasks() == null) ? new ArrayList<>() : processData.getTasks();

        result.put(Constants.DATA_LIST, taskNodeList);
        putMsg(result, Status.SUCCESS);

        return result;

    }

    /**
     * get task node details based on process definition
     */
    public Map<String, Object> getTaskNodeListByDefinitionIdList(String defineIdList) throws Exception {
        Map<String, Object> result = new HashMap<>();


        Map<Integer, List<TaskNode>> taskNodeMap = new HashMap<>();
        String[] idList = defineIdList.split(",");
        List<String> definitionIdList = Arrays.asList(idList);
        List<ProcessDefinition> processDefinitionList = processDefineMapper.queryDefinitionListByIdList(definitionIdList);
        if (processDefinitionList == null || processDefinitionList.size() ==0) {
            logger.info("process definition not exists");
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, defineIdList);
            return result;
        }

        for(ProcessDefinition processDefinition : processDefinitionList){
            String processDefinitionJson = processDefinition.getProcessDefinitionJson();
            ProcessData processData = JSONUtils.parseObject(processDefinitionJson, ProcessData.class);
            List<TaskNode> taskNodeList = (processData.getTasks() == null) ? new ArrayList<>() : processData.getTasks();
            taskNodeMap.put(processDefinition.getId(), taskNodeList);
        }

        result.put(Constants.DATA_LIST, taskNodeMap);
        putMsg(result, Status.SUCCESS);

        return result;

    }

    /**
     * Encapsulates the TreeView structure
     *
     * @param processId
     * @param limit
     * @return
     */
    public Map<String, Object> viewTree(Integer processId, Integer limit) throws Exception {
        Map<String, Object> result = new HashMap<>();

        ProcessDefinition processDefinition = processDefineMapper.queryByDefineId(processId);
        if (processDefinition == null) {
            logger.info("process define not exists");
            throw new RuntimeException("process define not exists");
        }
        DAG<String, TaskNode, TaskNodeRelation> dag = genDagGraph(processDefinition);
        /**
         * nodes that is running
         */
        Map<String, List<TreeViewDto>> runningNodeMap = new ConcurrentHashMap<>();

        /**
         * nodes that is waiting torun
         */
        Map<String, List<TreeViewDto>> waitingRunningNodeMap = new ConcurrentHashMap<>();

        /**
         * List of process instances
         */
        List<ProcessInstance> processInstanceList = processInstanceMapper.queryByProcessDefineId(processId, limit);


        if (limit > processInstanceList.size()) {
            limit = processInstanceList.size();
        }

        TreeViewDto parentTreeViewDto = new TreeViewDto();
        parentTreeViewDto.setName("DAG");
        parentTreeViewDto.setType("");
        // Specify the process definition, because it is a TreeView for a process definition

        for (int i = limit - 1; i >= 0; i--) {
            ProcessInstance processInstance = processInstanceList.get(i);

            Date endTime = processInstance.getEndTime() == null ? new Date() : processInstance.getEndTime();
            parentTreeViewDto.getInstances().add(new Instance(processInstance.getId(), processInstance.getName(), "", processInstance.getState().toString()
                    , processInstance.getStartTime(), endTime, processInstance.getHost(), DateUtils.format2Readable(endTime.getTime() - processInstance.getStartTime().getTime())));
        }

        List<TreeViewDto> parentTreeViewDtoList = new ArrayList<>();
        parentTreeViewDtoList.add(parentTreeViewDto);
        // Here is the encapsulation task instance
        for (String startNode : dag.getBeginNode()) {
            runningNodeMap.put(startNode, parentTreeViewDtoList);
        }

        while (Stopper.isRunning()) {
            Set<String> postNodeList = null;
            Iterator<Map.Entry<String, List<TreeViewDto>>> iter = runningNodeMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, List<TreeViewDto>> en = iter.next();
                String nodeName = en.getKey();
                parentTreeViewDtoList = en.getValue();

                TreeViewDto treeViewDto = new TreeViewDto();
                treeViewDto.setName(nodeName);
                TaskNode taskNode = dag.getNode(nodeName);
                treeViewDto.setType(taskNode.getType());


                //set treeViewDto instances
                for (int i = limit - 1; i >= 0; i--) {
                    ProcessInstance processInstance = processInstanceList.get(i);
                    TaskInstance taskInstance = taskInstanceMapper.queryByInstanceIdAndName(processInstance.getId(), nodeName);
                    if (taskInstance == null) {
                        treeViewDto.getInstances().add(new Instance(-1, "not running", "null"));
                    } else {
                        Date startTime = taskInstance.getStartTime() == null ? new Date() : taskInstance.getStartTime();
                        Date endTime = taskInstance.getEndTime() == null ? new Date() : taskInstance.getEndTime();

                        int subProcessId = 0;
                        /**
                         * if process is sub process, the return sub id, or sub id=0
                         */
                        if (taskInstance.getTaskType().equals(TaskType.SUB_PROCESS.name())) {
                            String taskJson = taskInstance.getTaskJson();
                            taskNode = JSON.parseObject(taskJson, TaskNode.class);
                            subProcessId = Integer.parseInt(JSON.parseObject(
                                    taskNode.getParams()).getString(CMDPARAM_SUB_PROCESS_DEFINE_ID));
                        }
                        treeViewDto.getInstances().add(new Instance(taskInstance.getId(), taskInstance.getName(), taskInstance.getTaskType(), taskInstance.getState().toString()
                                , taskInstance.getStartTime(), taskInstance.getEndTime(), taskInstance.getHost(), DateUtils.format2Readable(endTime.getTime() - startTime.getTime()), subProcessId));
                    }
                }
                for (TreeViewDto pTreeViewDto : parentTreeViewDtoList) {
                    pTreeViewDto.getChildren().add(treeViewDto);
                }
                postNodeList = dag.getSubsequentNodes(nodeName);
                if (postNodeList != null && postNodeList.size() > 0) {
                    for (String nextNodeName : postNodeList) {
                        List<TreeViewDto> treeViewDtoList = waitingRunningNodeMap.get(nextNodeName);
                        if (treeViewDtoList != null && treeViewDtoList.size() > 0) {
                            treeViewDtoList.add(treeViewDto);
                            waitingRunningNodeMap.put(nextNodeName, treeViewDtoList);
                        } else {
                            treeViewDtoList = new ArrayList<>();
                            treeViewDtoList.add(treeViewDto);
                            waitingRunningNodeMap.put(nextNodeName, treeViewDtoList);
                        }
                    }
                }
                runningNodeMap.remove(nodeName);
            }

            if (waitingRunningNodeMap == null || waitingRunningNodeMap.size() == 0) {
                break;
            } else {
                runningNodeMap.putAll(waitingRunningNodeMap);
                waitingRunningNodeMap.clear();
            }
        }
        result.put(Constants.DATA_LIST, parentTreeViewDto);
        result.put(Constants.STATUS, Status.SUCCESS);
        result.put(Constants.MSG, Status.SUCCESS.getMsg());
        return result;
    }


    /**
     * Generate the DAG Graph based on the process definition id
     *
     * @param processDefinition
     * @return
     * @throws Exception
     */
    private DAG<String, TaskNode, TaskNodeRelation> genDagGraph(ProcessDefinition processDefinition) throws Exception {

        String processDefinitionJson = processDefinition.getProcessDefinitionJson();

        ProcessData processData = JSONUtils.parseObject(processDefinitionJson, ProcessData.class);

        List<TaskNode> taskNodeList = processData.getTasks();

        processDefinition.setGlobalParamList(processData.getGlobalParams());


        List<TaskNodeRelation> taskNodeRelations = new ArrayList<>();

        // Traverse node information and build relationships
        for (TaskNode taskNode : taskNodeList) {
            String preTasks = taskNode.getPreTasks();
            List<String> preTasksList = JSONUtils.toList(preTasks, String.class);

            // If the dependency is not empty
            if (preTasksList != null) {
                for (String depNode : preTasksList) {
                    taskNodeRelations.add(new TaskNodeRelation(depNode, taskNode.getName()));
                }
            }
        }

        ProcessDag processDag = new ProcessDag();
        processDag.setEdges(taskNodeRelations);
        processDag.setNodes(taskNodeList);


        // Generate concrete Dag to be executed
        return genDagGraph(processDag);


    }

    /**
     * Generate the DAG of process
     *
     * @return DAG
     */
    private DAG<String, TaskNode, TaskNodeRelation> genDagGraph(ProcessDag processDag) {
        DAG<String, TaskNode, TaskNodeRelation> dag = new DAG<>();

        /**
         * Add the ndoes
         */
        if (CollectionUtils.isNotEmpty(processDag.getNodes())) {
            for (TaskNode node : processDag.getNodes()) {
                dag.addNode(node.getName(), node);
            }
        }

        /**
         * Add the edges
         */
        if (CollectionUtils.isNotEmpty(processDag.getEdges())) {
            for (TaskNodeRelation edge : processDag.getEdges()) {
                dag.addEdge(edge.getStartNode(), edge.getEndNode());
            }
        }

        return dag;
    }


    /**
     * whether the graph has a ring
     *
     * @param taskNodeResponseList
     * @return
     */
    private boolean graphHasCycle(List<TaskNode> taskNodeResponseList) {
        DAG<String, TaskNode, String> graph = new DAG<>();

        // Fill the vertices
        for (TaskNode taskNodeResponse : taskNodeResponseList) {
            graph.addNode(taskNodeResponse.getName(), taskNodeResponse);
        }

        // Fill edge relations
        for (TaskNode taskNodeResponse : taskNodeResponseList) {
            taskNodeResponse.getPreTasks();
            List<String> preTasks = JSONUtils.toList(taskNodeResponse.getPreTasks(),String.class);
            if (CollectionUtils.isNotEmpty(preTasks)) {
                for (String preTask : preTasks) {
                    if (!graph.addEdge(preTask, taskNodeResponse.getName())) {
                        return true;
                    }
                }
            }
        }

        return graph.hasCycle();
    }

    /**
     * edit  process definition
     *
     * @param loginUser
     * @param projectName
     * @param id
     * @param name
     * @param desc
     * @return
     */
    public Map<String, Object> editProcessDefinition(User loginUser, String projectName, int id, String name, String desc) {
        Map<String, Object> result = new HashMap<>(5);

        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());
        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultStatus = (Status) checkResult.get(Constants.STATUS);
        if (resultStatus != Status.SUCCESS) {
            return checkResult;
        }

        ProcessDefinition processDefinition = processDao.findProcessDefineById(id);
        if (processDefinition == null) {
            putMsg(result, Status.PROCESS_DEFINE_NOT_EXIST, id);
            return result;
        } else {
            putMsg(result, Status.SUCCESS);
        }

        ProcessDefinition processDefine = processDao.findProcessDefineById(id);
        Date now = new Date();

        processDefine.setId(id);
        processDefine.setName(name);
        processDefine.setProjectId(project.getId());
        processDefine.setDesc(desc);
        processDefine.setUpdateTime(now);
        processDefine.setFlag(Flag.YES);
        if (processDefineMapper.update(processDefine) > 0) {
            putMsg(result, Status.SUCCESS);

        } else {
            putMsg(result, UPDATE_PROCESS_DEFINITION_ERROR);
        }
        return result;
    }


    /**
     * shared process definition: 0/1
     *
     * @param loginUser
     * @param projectName
     * @param id
     * @param flag
     * @return
     */
    @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> shareProcessDefinition(User loginUser, String projectName, int id, int  flag) {
        HashMap<String, Object> result = new HashMap<>();
        Project project = projectMapper.queryByNameAndUser(projectName,loginUser.getId());

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status) checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }
        processDefineMapper.updateProcessDefinitionFlag(id, flag);

        putMsg(result, Status.SUCCESS);
        return result;
    }

    public static String getSixNum() {
        String str = "0123456789";
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 6; i++) {
            char ch = str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }

}

