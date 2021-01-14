package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Application;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * service mapper
 */
public interface ServiceMapper {
    @Results(value = {@Result(property = "id", column = "application_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "application_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "application_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "APIKey", column = "API_Key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "SecretKey", column = "Secret_Key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "status", column = "application_status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "applicationList")
    List<Application> applicationList(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("searchVal") String searchVal);

    @SelectProvider(type = ServiceMapperProvider.class, method = "applicationListTotal")
    Integer applicationListTotal(@Param("searchVal") String searchVal);

    @Results(value = {@Result(property = "id", column = "application_id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "application_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "application_type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "APIKey", column = "API_Key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "SecretKey", column = "Secret_Key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createUser", column = "create_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "status", column = "application_status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "applicationDetail")
    Application applicationDetail(@Param("applicationId") Integer applicationId);

    @SelectProvider(type = ServiceMapperProvider.class, method = "interfaceList")
    List<Map<String, Object>> interfaceList(@Param("applicationId") String applicationId,@Param("key") String key);

    @SelectProvider(type = ServiceMapperProvider.class, method = "interfaceListTotal")
    Integer interfaceListTotal(@Param("searchVal") String searchVal);

    @InsertProvider(type = ServiceMapperProvider.class, method = "applicationCreate")
    int applicationCreate(@Param("application") Application application);

    @InsertProvider(type = ServiceMapperProvider.class, method = "insertRelation")
    int insertRelation(@Param("serviceId") String serviceId, @Param("applicationId") Integer applicationId);

    @DeleteProvider(type = ServiceMapperProvider.class, method = "deleteRelation")
    int deleteRelation(@Param("id") Integer id);


    @InsertProvider(type = ServiceMapperProvider.class, method = "insertOauthRelation")
    int insertOauthRelation(@Param("apiKey") String apiKey, @Param("encryptKey") String encryptKey);

    @UpdateProvider(type = ServiceMapperProvider.class, method = "applicationUpdate")
    int applicationUpdate(@Param("application") Application application);

    @UpdateProvider(type = ServiceMapperProvider.class, method = "applicationDelete")
    int applicationDelete(@Param("applicationId") Long applicationId);

    @SelectProvider(type = ServiceMapperProvider.class, method = "applicationInterfaces")
    String applicationInterfaces(@Param("applicationId") Integer applicationId);

    @SelectProvider(type = ServiceMapperProvider.class, method = "interfaceInvoke")
    List<Map<String, Object>> interfaceInvoke(@Param("searchVal") String searchVal, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @SelectProvider(type = ServiceMapperProvider.class, method = "queryMaxId")
    int queryMaxId();

    /**
     * countCallStatList
     * @return
     */
    @SelectProvider(type = ServiceMapperProvider.class, method = "countCallStatList")
    Integer countCallStatList();


    /**
     * getCallStatList
     * @param pageSize
     * @return
     */
    @Results(value = {@Result(property = "serviceName", column = "service_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                      @Result(property = "service_id", column = "service_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                      @Result(property = "total", column = "total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                      @Result(property = "fail", column = "fail", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                      @Result(property = "rate", column = "rate",  javaType = Float.class, jdbcType = JdbcType.FLOAT)
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "getCallStatList")
    List<Map> getCallStatList(
                            @Param("start") Integer start,
                            @Param("pageSize") Integer pageSize);

    /**
     * getApplicationInfo
     * @return
     */
    @Results(value = {@Result(property = "statTime", column = "statTime", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "succ", column = "succ", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "fail", column = "fail", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "getApplicationInfo")
    List<Map> getApplicationInfo(@Param("applicationName") String applicationName,
                                 @Param("serviceName") List<String>  serviceName,
                                 @Param("status") String status,
                                 @Param("mode") String mode,
                                 @Param("startTime") String startTime,
                                 @Param("endTime") String endTime);

    /**
     * getApplicationInfo
     * @return
     */
    @Results(value = {@Result(property = "statTime", column = "statTime", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "succ", column = "succ", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "fail", column = "fail", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "getApplicationFailInfo")
    List<Map> getApplicationFailInfo(@Param("applicationName") String applicationName,
                                     @Param("serviceName") List<String> serviceName,
                                     @Param("status") String status,
                                     @Param("mode") String mode,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime,
                                     @Param("start") Integer start,
                                     @Param("pageSize") Integer pageSize);

    /**
     * countApplicationFailInfo
     * @return
     */
    @SelectProvider(type = ServiceMapperProvider.class, method = "countApplicationFailInfo")
    Integer countApplicationFailInfo(@Param("applicationName") String applicationName,
                                     @Param("serviceName") List<String> serviceName,
                                     @Param("status") String status,
                                     @Param("mode") String mode,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);


    @SelectProvider(type = ServiceMapperProvider.class, method = "countService")
    Integer countService();

    @SelectProvider(type = ServiceMapperProvider.class, method = "serviceInvokeTimes")
    Integer serviceInvokeTimes();

    @SelectProvider(type = ServiceMapperProvider.class, method = "serviceInvokeTimesByDay")
    List<Map<String, Object>> serviceInvokeTimesByDay(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @SelectProvider(type = ServiceMapperProvider.class, method = "serviceAvgInvokeTimes")
    Integer serviceAvgInvokeTimes(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Results(value = {@Result(property = "id", column = "id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = ServiceMapperProvider.class, method = "getTypeList")
    List<Map> getTypeList(@Param("code") String code);

    @SelectProvider(type = ServiceMapperProvider.class, method = "queryTableNameMap")
    List<Map> queryTableNameMap();

    @SelectProvider(type = ServiceMapperProvider.class, method = "queryTableTotal")
    Integer queryTableTotal();
}
