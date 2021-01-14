package cn.escheduler.dao.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * Schema mapper provider
 * the core of schema management
 */
public class ServiceMapperProvider {
    public static final String TABLE_APPLICATION = "t_ms_application";

    public static final String TABLE_APPLICATION_INTERFACE_RELATION = "t_ms_app_service_rel";

    public static final String TABLE_OAUTH_CLIENT_DETAILS = "oauth_client_details";

    public static final String TABLE_ATOMIC_SERVICE = "t_ms_atomic_service";

    public static final String TABLE_SERVICE_INVOKE_LOG = "t_ms_service_log";

    public static final String TABLE_CODE = "t_base_code";

    public static final String TABLE_MODEL_DESIGN = "t_escheduler_model_design";

    public String applicationList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_APPLICATION);
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " application_name like concat('%', #{searchVal}, '%') ");
            }
            WHERE("`application_status` = 1");
            ORDER_BY("create_time desc limit #{offset},#{pageSize}");
        }}.toString();
    }
    public String applicationListTotal(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("count(1)");
            FROM(TABLE_APPLICATION);
            WHERE("`application_status` = 1");
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " application_name like concat('%', #{searchVal}, '%') ");
            }
        }}.toString();
    }

    public String applicationDetail(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_APPLICATION);
            WHERE("`application_id` = #{applicationId}");
        }}.toString();
    }

    public String applicationInterfaces(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("group_concat(service_id)");
            FROM(TABLE_APPLICATION_INTERFACE_RELATION);
            WHERE("`application_id` = #{applicationId}");
        }}.toString();
    }

    public String interfaceList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_ATOMIC_SERVICE);
            WHERE("`status` = 1");
            Object applicationId = parameter.get("applicationId");
            if(applicationId != null && StringUtils.isNotEmpty(applicationId.toString())){
                WHERE( " service_id in (select service_id from t_ms_app_service_rel where application_id = #{applicationId}) ");
            }
            Object key = parameter.get("key");
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                WHERE( " service_id in (select service_id from t_ms_app_service_rel where application_id in " +
                        "(select application_id from t_ms_application where API_Key=#{key})) ");
            }
            ORDER_BY("status desc,create_time desc");
        }}.toString();
    }

    public String interfaceInvoke(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("a.service_name, count(b.log_id) as invokeTotal, count(c.log_id) as successTotal");
            FROM(TABLE_ATOMIC_SERVICE  + " a");
            LEFT_OUTER_JOIN(TABLE_SERVICE_INVOKE_LOG + " b on a.service_id = b.service_id");
            LEFT_OUTER_JOIN(" (select * from t_ms_service_log where log_status=2)c on a.service_id=c.service_id ");
            GROUP_BY("a.service_name");
        }}.toString();
    }

    public String interfaceListTotal(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("count(1)");
            FROM(TABLE_ATOMIC_SERVICE);
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                WHERE( " service_name like concat('%', #{searchVal}, '%') ");
            }
        }}.toString();
    }

    public String applicationCreate(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_APPLICATION);
                VALUES("`application_name`", "#{application.name}");
                VALUES("`application_type`", "#{application.type}");
                VALUES("`desc`", "#{application.desc}");
                VALUES("`API_Key`", "#{application.APIKey}");
                VALUES("`Secret_Key`", "#{application.SecretKey}");
                VALUES("`create_user`", "#{application.createUser}");
                VALUES("`create_time`", "#{application.createTime}");
                VALUES("`application_status`", "#{application.status}");
            }
        }.toString();
    }

    public String insertRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_APPLICATION_INTERFACE_RELATION);
                VALUES("`application_id`", "#{applicationId}");
                VALUES("`service_id`", "#{serviceId}");
            }
        }.toString();
    }

    public String deleteRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_APPLICATION_INTERFACE_RELATION);
                WHERE("`application_id` = #{id}");
            }
        }.toString();
    }

    public String insertOauthRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_OAUTH_CLIENT_DETAILS);
                VALUES("`client_id`", "#{apiKey}");
                VALUES("`client_secret`", "#{encryptKey}");
                VALUES("`resource_ids`", "'product_api'");
                VALUES("`scope`", "'read,write'");
                VALUES("`authorized_grant_types`", "'client_credentials'");
                VALUES("`web_server_redirect_uri`", "'http://127.0.0.1'");
                VALUES("`authorities`", "'ROLE_ADMIN'");
                VALUES("`access_token_validity`", "7200");
                VALUES("`refresh_token_validity`", "0");
                VALUES("`autoapprove`", "'true'");
            }
        }.toString();
    }

    public String applicationDelete(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_APPLICATION);
                SET("`application_status` = 0");
                WHERE("`application_id` = #{applicationId}");
            }
        }.toString();
    }

    public String applicationUpdate(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(TABLE_APPLICATION);
                SET("`application_name` = #{application.name}");
                SET("`application_type` = #{application.type}");
                SET("`desc` = #{application.desc}");
                SET("`application_status` = #{application.status}");
                WHERE("`application_id` = #{application.id}");
            }
        }.toString();
    }


    public String queryMaxId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("IFNULL(max(application_id),0)");
                FROM(TABLE_APPLICATION);
            }
        }.toString();
    }

    /**
     * countServerList
     * @param parameter
     * @return
     */
    public String countCallStatList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" count(0) from t_ms_atomic_service a inner join  " +
                    "(select service_id,count(0) total,sum(case log_status when 2 then 1 ELSE 0 end) fail, " +
                    "ROUND(sum(case log_status when 2 then 1 ELSE 0 end)/count(0),4) rate from t_ms_service_log group by service_id) b " +
                    "on a.service_id=b.service_id  where a.status=1");
        }}.toString();
    }


    /**
     * getServerList
     * @param parameter
     * @return
     */
    public String getCallStatList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT(" a.service_name,b.service_id,b.total,b.fail,b.rate  from t_ms_atomic_service a inner join " +
                    "(select service_id,count(0) total,sum(case log_status when 2 then 0 ELSE 1 end) fail," +
                    "ROUND(sum(case log_status when 2 then 0 ELSE 1 end)/count(0),4) rate from t_ms_service_log group by service_id) b " +
                    "on a.service_id=b.service_id  where a.status=1 order by total desc limit #{start},#{pageSize}  ");
        }}.toString();
    }

    /**
     * getApplicationInfo
     * @param parameter
     * @return
     */
    public String getApplicationInfo(Map<String, Object> parameter) {
        List<String> serviceName = (List<String>) parameter.get("serviceName");
        Object applicationName = parameter.get("applicationName");
        return new SQL() {{
            //失败 正常 同时统计
            if(parameter.get("status").toString().split(",").length==2){
                SELECT("DATE_FORMAT(begin_time,#{mode}) statTime,sum(case log_status when 2 then 1 else 0 end) succ," +
                        "sum(case log_status when 2 then 0 else 1 end) fail  from t_ms_service_log ");
                WHERE("begin_time>STR_TO_DATE(#{startTime},'%Y-%m-%d %H:%i:%s') and begin_time<STR_TO_DATE(#{endTime},'%Y-%m-%d %H:%i:%s')");
                if(applicationName!=null&& !applicationName.equals("")){
                    WHERE("application_id=#{applicationName}");
                }
                if(serviceName!=null){
                    WHERE("`service_id` in ('" + String.join(",", serviceName) + "')");
                }
                GROUP_BY("DATE_FORMAT(begin_time,#{mode})");

                //值统计正常
            }else if(parameter.get("status").toString().equals("2")){
                SELECT("DATE_FORMAT(begin_time,#{mode}) statTime,sum(case log_status when 2 then 1 else 0 end) succ from t_ms_service_log ");
                WHERE("begin_time>STR_TO_DATE(#{startTime},'%Y-%m-%d %H:%i:%s') and begin_time<STR_TO_DATE(#{endTime},'%Y-%m-%d %H:%i:%s')");
                if(applicationName!=null&& !applicationName.equals("")){
                    WHERE("application_id=#{applicationName}");
                }
                if(serviceName!=null){
                    WHERE("`service_id` in ('" + String.join(",", serviceName) + "')");
                }

                GROUP_BY("DATE_FORMAT(begin_time,#{mode})");

                //只统计失败
            }else {
                SELECT("DATE_FORMAT(begin_time,#{mode}) statTime,sum(case log_status when 2 then 0 else 1 end) fail  from t_ms_service_log ");
                WHERE("begin_time>STR_TO_DATE(#{startTime},'%Y-%m-%d %H:%i:%s') and begin_time<STR_TO_DATE(#{endTime},'%Y-%m-%d %H:%i:%s')");
                if(applicationName!=null&& !applicationName.equals("")){
                    WHERE("application_id=#{applicationName}");
                }
                if(serviceName!=null){
                    WHERE("`service_id` in ('" + String.join(",", serviceName) + "')");
                }

                GROUP_BY("DATE_FORMAT(begin_time,#{mode})");
            }
        }}.toString();
    }


    /**
     * getApplicationFailInfo
     * @param parameter
     * @return
     */
    public String getApplicationFailInfo(Map<String, Object> parameter) {
        List<String> serviceName = (List<String>) parameter.get("serviceName");
        Object applicationName = parameter.get("applicationName");
        return new SQL() {{

            SELECT(" a.log_id,a.application_id,b.application_name,a.service_id,c.service_name,a.begin_time,a.log_msg " +
                    "FROM t_ms_service_log a LEFT JOIN t_ms_application b on a.application_id = b.API_Key " +
                    "LEFT JOIN t_ms_atomic_service c  on a.service_id = c.service_id  ");
            WHERE("a.log_status!=2");
            WHERE("a.begin_time>STR_TO_DATE(#{startTime},'%Y-%m-%d %H:%i:%s') and a.begin_time<STR_TO_DATE(#{endTime},'%Y-%m-%d %H:%i:%s')");

            if(applicationName!=null&& !applicationName.equals("")){
                WHERE("a.application_id=#{applicationName}");
            }
            if(serviceName!=null){
                WHERE("a.service_id in ('" + String.join(",", serviceName) + "')");
            }
            ORDER_BY("a.begin_time desc limit #{start},#{pageSize}");
        }}.toString();
    }


    /**
     * getApplicationFailInfo
     * @param parameter
     * @return
     */
    public String countApplicationFailInfo(Map<String, Object> parameter) {
        List<String> serviceName = (List<String>) parameter.get("serviceName");
        Object applicationName = parameter.get("applicationName");
        return new SQL() {{

            SELECT(" count(0) " +
                    "FROM t_ms_service_log a LEFT JOIN t_ms_application b on a.application_id = b.API_Key " +
                    "LEFT JOIN t_ms_atomic_service c  on a.service_id = c.service_id  ");
            WHERE("a.log_status!=2");
            WHERE("a.begin_time>STR_TO_DATE(#{startTime},'%Y-%m-%d %H:%i:%s') and a.begin_time<STR_TO_DATE(#{endTime},'%Y-%m-%d %H:%i:%s')");
            if(applicationName!=null&& !applicationName.equals("")){
                WHERE("a.application_id=#{applicationName}");
            }
            if(serviceName!=null){
                WHERE("a.service_id in ('" + String.join(",", serviceName) + "')");
            }
        }}.toString();
    }

    public String countService(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1)");
            FROM(TABLE_ATOMIC_SERVICE);
        }}.toString();
    }

    public String serviceInvokeTimes(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1)");
            FROM(TABLE_SERVICE_INVOKE_LOG);
        }}.toString();
    }

    public String serviceInvokeTimesByDay(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("date(begin_time) as x, count(1) as y");
            FROM(TABLE_SERVICE_INVOKE_LOG);
            WHERE("begin_time >= #{startTime} and begin_time <= #{endTime}");
            GROUP_BY("date(begin_time)");
        }}.toString();
    }

    public String serviceAvgInvokeTimes(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1)");
            FROM(TABLE_SERVICE_INVOKE_LOG);
            WHERE("begin_time >= #{startTime} and begin_time <= #{endTime}");
        }}.toString();
    }

    public String getTypeList(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(TABLE_CODE);
            WHERE("valid_flag = 1 and  code_type=#{code} ");
            ORDER_BY("order_no");
        }}.toString();
    }

    public String queryTableNameMap(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("CONCAT(base,'-',tablename) as k, tabledescribe as v");
            FROM(TABLE_MODEL_DESIGN);
        }}.toString();
    }

    public String queryTableTotal(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(0) as total");
            FROM(TABLE_MODEL_DESIGN);
        }}.toString();
    }
}
