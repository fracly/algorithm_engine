package cn.escheduler.api.utils;

import cn.escheduler.api.configuration.ConfigurationManager;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class FtpUtils {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);
    public static void main(String[] args) {
        put("D:/sjzl.xlsx","/home/yqq");
    }

    public static void put(String fromPath,String toPath){
        try {
            File fromFile = new File(fromPath);

            String[] split = fromPath.split("/");
            String fileName=split[split.length-1];

            File toFileName = new File(fileName);

            //创建ftp客户端  
            FTPClient ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            String hostname = ConfigurationManager.getProperty(Constants.EXCLEHOST);
            int port = 21;
            String username = ConfigurationManager.getProperty(Constants.EXCLEUSERNAME);
            String password = ConfigurationManager.getProperty(Constants.EXCLEPASSWORD);
            try {
                //链接ftp服务器  
                ftpClient.connect(hostname, port);
                //登录ft
                ftpClient.login(username, password);
                int  reply = ftpClient.getReplyCode();
                logger.info("reply : "+reply);
                //如果reply返回230就算成功了，如果返回530密码用户名错误或当前用户无权限下面有详细的解释。  
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    return ;
                }
                //服务器之间传输需要加下列语句
//            ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(toPath);
                String remoteFileName = ""+toFileName;
                InputStream input = new FileInputStream(fromFile);
                ftpClient.storeFile(remoteFileName, input);//文件你若是不指定就会上传到root目录下  
                System.out.println("---文件复制完成---");
                input.close();
                ftpClient.logout();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally
            {
                if (ftpClient.isConnected())
                {
                    try
                    {
                        ftpClient.disconnect();
                    } catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
