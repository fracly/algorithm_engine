package cn.escheduler.api.service;


import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.KylinHttpHelper;
import cn.escheduler.api.utils.StringUtils;
import cn.escheduler.dao.mapper.ScheduleMapper;
import cn.escheduler.dao.model.Schedule;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.escheduler.api.enums.Status.GET_CUBE_INFO_ERROR;


/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/9
 * @version:
 */
@Service
public class KylinService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(KylinService.class);

    @Autowired
    private ScheduleMapper scheduleMapper;
    /**
     *   获取 cube
     */
    public Map<String, Object> getCubesList(String searchVal) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();

            kylinHttpHelper.login();
            /**
             * GET Cube_List
             */
            String path= "";
            if(StringUtils.isEmpty(searchVal)){
                path = "/cubes?limit=10000&offset=0";
            }else {
                path = "/cubes?limit=10000&offset=0&cubeName="+searchVal;
            }
            String cube_array = KylinHttpHelper.excute(path, "GET", null);
            JSONArray list =JSONArray.parseArray(cube_array);
            for(int i=0;i<list.size();i++){
                JSONObject cube=JSONObject.parseObject(list.get(i).toString());
                String cubeName =cube.getString("name");
                Schedule schedule = scheduleMapper.queryByCubeName(cubeName).size()>0?scheduleMapper.queryByCubeName(cubeName).get(0):new Schedule();
                cube.put("crontab",schedule.getCrontab());
                cube.put("cronStartTime",schedule.getStartTime());
                cube.put("cronEndTime",schedule.getEndTime());
                cube.put("releaseState",schedule.getReleaseState());
                cube.put("id",schedule.getId());
                cube.put("time",schedule.getTime());
                cube.put("type",schedule.getType());
                list.set(i,cube);
            }

            System.out.println(cube_array);
            result.put(Constants.DATA_LIST, list);
            putMsg(result, Status.SUCCESS);

        } catch (Exception e) {
            logger.error(GET_CUBE_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }


    /**
     *   build
     */
    public Map<String, Object> build(String name,String body) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();
            kylinHttpHelper.login();
            /**
             * put rebuild
             */
            String cube_array = KylinHttpHelper.excute("/cubes/"+name+"/rebuild", "PUT", body);
            System.out.println(cube_array);
            putMsg(result, Status.SUCCESS);

        } catch (Exception e) {
            logger.error(GET_CUBE_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }



    /**
     *   获取 某个cube的历史日志
     */
    public Map<String, Object> getLogsList(String name,String status) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();

            kylinHttpHelper.login();
            /**
             * GET Cube_List
             */
            String path= "";
            if(StringUtils.isEmpty(status)){
                path = "/jobs?jobSearchMode=all&limit=1000&offset=0&timeFilter=5&cubeName="+name;
            }else {
                path = "/jobs?jobSearchMode=all&limit=1000&offset=0&timeFilter=5&cubeName="+name+"&status="+status;
            }
            String cube_array = KylinHttpHelper.excute(path, "GET", null);
            JSONArray list =JSONArray.parseArray(cube_array);
            System.out.println(cube_array);
            result.put(Constants.DATA_LIST, list);
            putMsg(result, Status.SUCCESS);

        } catch (Exception e) {
            logger.error(GET_CUBE_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     *   获取 某个cube的历史日志
     */
    public Map<String, Object> getStepLog(String id,String stepId) {
        Map<String, Object> result = new HashMap<>(5);
        try {
            KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();

            kylinHttpHelper.login();
            /**
             * GET Cube_List
             */
            String cube_logs = KylinHttpHelper.excute("/jobs/"+id+"/steps/"+stepId+"/output", "GET", null);
            JSONObject list =JSONArray.parseObject(cube_logs);
            System.out.println(cube_logs);
            result.put(Constants.DATA_LIST, list);
            putMsg(result, Status.SUCCESS);

        } catch (Exception e) {
            logger.error(GET_CUBE_INFO_ERROR.getMsg(),e);
            putMsg(result, Status.FAILED);
        }
        return result;
    }
}
