package cn.escheduler.api.utils;
import cn.escheduler.api.configuration.ConfigurationManager;
import cn.escheduler.api.enums.Status;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/9
 * @version:
 */
public class KylinHttpHelper {
    private static String encoding;

    private static final String baseURL = ConfigurationManager.getProperty("kylin_ip");

    public static String login() {
        String user= ConfigurationManager.getProperty("kylin_user");
        String password= ConfigurationManager.getProperty("kylin_password");

        String auth = user + ":" + password;

        @SuppressWarnings("restriction")

        String code = new sun.misc.BASE64Encoder() {

            @Override
            protected int bytesPerLine() {

                return 9999;

            }

        }.encode(new String(auth).getBytes()) + "'";

        System.out.println("CODE:" + code);


        String method = "POST";

        String para = "/user/authentication";

        byte[] key = (user + ":" + password).getBytes();

        encoding = code;

        return excute(para, method, null);

    }


    public static String query(String body) {

        String method = "POST";

        String para = "/query";


        return excute(para, method, body);

    }


    public static String excute(String para, String method, String body) {


        StringBuilder out = new StringBuilder();

        try {

            URL url = new URL(baseURL + para);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);

            connection.setDoOutput(true);

            connection.setRequestProperty("Authorization", "Basic " + encoding);

            connection.setRequestProperty("Content-Type", "application/json");

            if (body != null) {

                byte[] outputInBytes = body.getBytes("UTF-8");

                OutputStream os = connection.getOutputStream();

                os.write(outputInBytes);

                os.close();

            }

            InputStream content = (InputStream) connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            String line;

            while ((line = in.readLine()) != null) {

                out.append(line);
            }

            in.close();

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return out.toString();

    }

    public static void main(String[] args) {

        String name="traffic_exam_cube4";
        String body="{\"buildType\":\"BUILD\"}";
        KylinHttpHelper kylinHttpHelper = new KylinHttpHelper();
        kylinHttpHelper.login();
//        String disable = kylinHttpHelper.excute("/cubes/"+name+"/disable", "PUT", "");
//        System.out.println(disable);
//        String delete = kylinHttpHelper.excute("/cubes/"+name+"/segs/20201216160000_20201217000000", "DELETE", "");
//        System.out.println(delete);
//        String enable = kylinHttpHelper.excute("/cubes/"+name+"/enable", "PUT", "");
//        System.out.println(enable);


    }

}
