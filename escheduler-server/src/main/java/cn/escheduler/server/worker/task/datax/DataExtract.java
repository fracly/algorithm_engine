package cn.escheduler.server.worker.task.datax;

import java.io.IOException;

/**
 *  通过datax工具实现数据抽取，调用datax.py shell脚本 加xx.json配置文件
 *  要配置好DATAX_HOME 信息
 */
public class DataExtract {


    public static void main(String[] args) {

        String dataxPath = "${DATAX_HOME}/bin/datax.py";
        Process process = null;

        String command1 = "chmod 777" + dataxPath;
        try
        {
            Runtime.getRuntime().exec(command1).waitFor();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }




}
