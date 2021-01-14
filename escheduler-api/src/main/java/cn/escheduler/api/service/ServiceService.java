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
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.PageInfo;
import cn.escheduler.dao.mapper.ServiceMapper;
import cn.escheduler.dao.model.Application;
import cn.escheduler.dao.model.ProcessData;
import cn.escheduler.dao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * server service
 */
@Service
public class ServiceService extends BaseService{


    @Autowired
    ServiceMapper serviceMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * query application list
     *
     * @return
     */
    public List<Application> applicationList(Integer pageNo, Integer pageSize, String searchVal) {
        Integer offset = (pageNo - 1) * pageSize;
        return serviceMapper.applicationList(offset, pageSize, searchVal);
    }

    public Integer applicationListTotal(String searchVal) {
        return serviceMapper.applicationListTotal(searchVal);
    }

    /**
     * query interface list
     *
     * @return
     * @param applicationId
     */
    public List<Map<String, Object>> interfaceList(String applicationId,String key) {
        return serviceMapper.interfaceList(applicationId,key);
    }


    public int applicationCreate(User loginUser, String name, String type, String desc, Integer status, String interfaces) {
        String apiKey = UUID.randomUUID().toString();
        String secretKey = UUID.randomUUID().toString();
        Application app = new Application();
        app.setCreateTime(new Date());
        app.setCreateUser(loginUser.getUserName());
        app.setAPIKey(apiKey);
        app.setSecretKey(secretKey);
        app.setDesc(desc);
        app.setName(name);
        app.setType(type);
        app.setStatus(status); // 1表示初始状态，0表示禁用状态

        int id = serviceMapper.queryMaxId();
        app.setId(id + 1);
        // 插入应用记录
        int insertAppCount = serviceMapper.applicationCreate(app);
        if(insertAppCount > 0) {
            String[] interfaceArray = interfaces.split(",");
            for(String interf : interfaceArray) {
                // 插入应用与服务的关联记录
                int insertRelCount = serviceMapper.insertRelation(interf, id + 1);
                if(insertRelCount == 0) return 0;
            }
            // 插入客户端调用记录
            int insertOauthCount = serviceMapper.insertOauthRelation(apiKey, passwordEncoder.encode(secretKey));
            if(insertOauthCount == 0) return 0;
        } else {
            return 0;
        }
        return 1;
    }

    public int applicationUpdate(Integer id, String name, String type, String desc, Integer status, String interfaces) {

        Application app = serviceMapper.applicationDetail(id);
        app.setDesc(desc);
        app.setName(name);
        app.setType(type);
        app.setStatus(status); // 1表示初始状态，0表示禁用状态
        // 插入应用记录
        int updateAppCount = serviceMapper.applicationUpdate(app);
        if(updateAppCount > 0) {
            // 先清理关系
            serviceMapper.deleteRelation(app.getId());

            // 再重新插入
            String[] interfaceArray = interfaces.split(",");
            for(String interf : interfaceArray) {
                // 插入应用与服务的关联记录
                int insertRelCount = serviceMapper.insertRelation(interf, app.getId());
                if(insertRelCount == 0) return 0;
            }
        } else {
            return 0;
        }
        return 1;
    }

    public int applicationDelete(Long applicationId) {
        return serviceMapper.applicationDelete(applicationId);
    }

    public Application applicationDetail(Integer applicationId) {
        Application application = serviceMapper.applicationDetail(applicationId);
        String interfaceIds = serviceMapper.applicationInterfaces(applicationId);
        application.setInterfaces(interfaceIds);
        return application;
    }

    public List<Map<String, Object>> interfaceInvoke(String searchVal, Integer pageNo, Integer pageSize) {
        Integer offset = (pageNo - 1) * pageSize;
        List<Map<String, Object>> list = serviceMapper.interfaceInvoke(searchVal, offset, pageSize);
        return list;
    }

    public Integer interfaceInvokeTotal(String searchVal) {
        return serviceMapper.interfaceListTotal(searchVal);
    }


    /**
     * callStaticList
     * @return
     */
    public Map<String, Object>  callStatList(Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>(5);

        Integer count = serviceMapper.countCallStatList();

        PageInfo pageInfo = new PageInfo<ProcessData>(pageNo, pageSize);
        List<Map> resourceList = serviceMapper.getCallStatList(pageInfo.getStart(),pageSize);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(resourceList);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> getApplicationInfo(String applicationName, String serviceName, String status, String time, String mode) {
        Map<String, Object> result = new HashMap<>(5);
        if(time.split(",").length!=2){
            putMsg(result, Status.TIME_FORMAT_ERROR);
            return result;
        }

        if(mode.equals("H")){
            mode="%Y-%m-%d %h";
        }
        else {
            mode="%Y-%m-%d";
        }

        String startTime=time.split(",")[0];
        String endTime=time.split(",")[1];

        List<Map> applicationInfo;

        Map<String,List<Map>> ml=new HashMap<>();
        List <Map> lists=new ArrayList<>();
        List <Map> listf=new ArrayList<>();
        List<String> serviceList=null;
        if(serviceName!=null && !serviceName.equals("")){
            serviceList=Arrays.asList(serviceName.replace(",", "','"));
        }

        applicationInfo = serviceMapper.getApplicationInfo(applicationName, serviceList, status, mode, startTime, endTime);;

        for (Map m:applicationInfo) {
            Object fail = m.get("fail");
            Object succ = m.get("succ");
            Object statTime = m.get("statTime");
            Map ms=new HashMap();
            Map mf=new HashMap();
            ms.put("name",statTime);
            ms.put("value",succ);
            mf.put("name",statTime);
            mf.put("value",fail);
            lists.add(ms);
            listf.add(mf);
        }
        if(applicationInfo.size()!=0){
            if(lists.get(0).get("value")!=null){
                ml.put("succ",lists);
            }
            if(listf.get(0).get("value")!=null){
                ml.put("fail",listf);
            }
        }

        result.put(Constants.DATA_LIST,ml);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    public Map<String, Object> getApplicationFailInfo(String applicationName, String serviceName, String status, String time, String mode, Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>(5);

        Integer count;
        PageInfo pageInfo = new PageInfo<ProcessData>(pageNo, pageSize);

        if(time.split(",").length!=2){
            putMsg(result, Status.TIME_FORMAT_ERROR);
            return result;
        }
        if(mode.equals("H")){
            mode="%Y-%m-%d %h";
        }
        else {
            mode="%Y-%m-%d";
        }
        String startTime=time.split(",")[0];
        String endTime=time.split(",")[1];


        List<String> serviceList=null;
        if(serviceName!=null && !serviceName.equals("")){
            serviceList=Arrays.asList(serviceName.replace(",", "','"));
        }

        count=serviceMapper.countApplicationFailInfo(applicationName, serviceList, status, mode, startTime, endTime);
        List<Map> applicationInfo = serviceMapper.getApplicationFailInfo(applicationName, serviceList, status, mode, startTime, endTime,pageInfo.getStart(),pageSize);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(applicationInfo);

        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    public   Map<String, Object> getTypeList(String code){
        Map<String, Object> result = new HashMap<>(5);
        List<Map> resourceList = serviceMapper.getTypeList(code);
        result.put(Constants.DATA_LIST, resourceList);
        putMsg(result, Status.SUCCESS);
        return result;

    }

}
