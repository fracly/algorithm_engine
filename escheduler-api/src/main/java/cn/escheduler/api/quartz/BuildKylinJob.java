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
package cn.escheduler.api.quartz;


import cn.escheduler.api.dto.KylinParam;
import cn.escheduler.api.service.KylinService;
import cn.escheduler.api.utils.KylinHttpHelper;
import cn.escheduler.common.Constants;
import cn.escheduler.dao.ProcessDao;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static cn.escheduler.api.quartz.QuartzExecutors.buildJobGroupName;
import static cn.escheduler.api.quartz.QuartzExecutors.buildJobName;

/**
 * process schedule job
 * <p>
 *  {@link Job}
 * </p>
 */
public class BuildKylinJob implements Job {

    @Autowired
    private KylinService kylinService;

    private static KylinParam kylinParam;

    private static final Logger logger = LoggerFactory.getLogger(BuildKylinJob.class);

    /**
     * {@link ProcessDao}
     */
    private static ProcessDao processDao;


    /**
     * init
     */
    public static void init(ProcessDao processDao) {
        BuildKylinJob.processDao = processDao;
    }
    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * fires that is associated with the <code>Job</code>.
     * </p>
     *
     * <p>
     * The implementation may wish to set a
     * {@link JobExecutionContext#setResult(Object) result} object on the
     * {@link JobExecutionContext} before this method exits.  The result itself
     * is meaningless to Quartz, but may be informative to
     * <code>{@link JobListener}s</code> or
     * <code>{@link TriggerListener}s</code> that are watching the job's
     * execution.
     * </p>
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int scheduleId = dataMap.getInt(Constants.SCHEDULE_ID);
//        logger.info("boy: "+dataMap.getString("boy")+"          Cube: "+dataMap.getString("Cube"));

        String Cube=dataMap.getString("Cube");
        logger.info("/cubes/"+Cube +"/rebuild/body/"+dataMap.getString("body"));

        KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();
        kylinHttpHelper.login();
        if(dataMap.getString("type").equals("M")){
            String e1 = dataMap.getString("e1");
            String s1 = dataMap.getString("s1");
            String dd = dataMap.getString("dd");

            String disable = kylinHttpHelper.excute("/cubes/"+Cube+"/disable", "PUT", "");
            System.out.println("disable  "+disable);
            String delete = kylinHttpHelper.excute("/cubes/"+Cube+"/segs/"+s1+"_"+e1, "DELETE", "");
            System.out.println("delete  "+delete);
            String enable = kylinHttpHelper.excute("/cubes/"+Cube+"/enable", "PUT", "");
            System.out.println("enable  "+enable);
            String cube_array = kylinHttpHelper.excute("/cubes/"+Cube +"/rebuild", "PUT", dataMap.getString("body"));
            logger.info("cube_array: "+cube_array);

        } else if(dataMap.getString("type").equals("LM")){
            String e1 = dataMap.getString("e1");
            String s1 = dataMap.getString("s1");
            String disable = kylinHttpHelper.excute("/cubes/"+Cube+"/disable", "PUT", "");
            System.out.println("disable  "+disable);
            String delete = kylinHttpHelper.excute("/cubes/"+Cube+"/segs/"+s1+"_"+e1, "DELETE", "");
            System.out.println("delete  "+delete);
            String enable = kylinHttpHelper.excute("/cubes/"+Cube+"/enable", "PUT", "");
            System.out.println("enable  "+enable);
            String cube_array = kylinHttpHelper.excute("/cubes/"+Cube +"/rebuild", "PUT", dataMap.getString("body"));
            logger.info("cube_array: "+cube_array);

        }else {
            String cube_array = kylinHttpHelper.excute("/cubes/"+Cube +"/rebuild", "PUT", dataMap.getString("body"));
            logger.info("cube_array: "+cube_array);
        }


        //        String enable = kylinHttpHelper.excute("/cubes/"+name+"/enable", "PUT", "");
//        System.out.println(enable);

    }


    /**
     * delete job
     */
    private void deleteJob(int projectId, int scheduleId) {
        String jobName = buildJobName(scheduleId);
        String jobGroupName = buildJobGroupName(projectId);
        QuartzExecutors.getInstance().deleteJob(jobName, jobGroupName);
    }
}
