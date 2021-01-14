package cn.escheduler.server.worker.task.datax;

import java.util.ArrayList;

public class DataXJob {

    public  static String DATAX_HOME = "/home/bigdata/datax3-phoenix-4.11/datax3";
    public static String g_confPath =  DATAX_HOME + "/jsonConf/";

    /**
     *  获取配置json的信息
     * @param confFilename
     * @return
     */
    public String getJsonConf(String confFilename)
    {
        return null;
    }


    /**
     *  保存配置文件信息
     * @param confFilename
     */
    public static int saveJsonConfFile(String confFilename) throws Exception {
        String jsonParams = " reader and  writer";
        RemoteShellExecutorTask exe = new RemoteShellExecutorTask();
        String jsonParamsPath = g_confPath + confFilename;
        String execStr = "echo " + jsonParams + ">>" + jsonParamsPath;
        // 执行myTest.sh 参数为java Know dummy
        int ret = exe.exec(execStr);
        System.out.println(ret );
        return ret;
    }


    /**
     *  获取配置信息列表
     * @return
     */
    public ArrayList<String> getConfFileList()
    {

        return null;
    }


    /**
     *  保存job信息，保存到MySQL
     *
     * @param jobInfo
     * @return
     */
    public boolean  saveJobInfo(String jobInfo)
    {

        return false;
    }

    /**
     *  获取datax任务列表，job字段{id,confFilename,source,dest,state,runTime}
     * @return
     */
    public ArrayList<String> getJobInfoList()
    {
        // 获取job信息
        return null;

    }


    /**]
     *  执行数据导入任务
     * @param jsonConfFile 配置的json文件
     * @return
     */
    public static int runDataxJob(String jsonConfFile)
    {
        RemoteShellExecutorTask exe = new RemoteShellExecutorTask("10.20.10.171","root","mima");
        String jsonParamsPath = g_confPath + jsonConfFile;
        String dataxShell = DATAX_HOME +"/bin/datax.py ";
        String execStr = dataxShell + jsonParamsPath ;
        // 执行myTest.sh 参数为java Know dummy
        int ret = 0;
        try {
            ret = exe.exec(execStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ret );
        return ret;
    }

    public static void main(String args[]) throws Exception {

        //saveJsonConfFile("hive2es");
  //       RemoteShellExecutorTask task = new RemoteShellExecutorTask();

            runDataxJob("sql2sql.json");
    }

}
