package cn.escheduler.dao.mapper;


import cn.escheduler.common.enums.UserType;
import cn.escheduler.dao.model.Menu;
import cn.escheduler.dao.model.Role;
import cn.escheduler.dao.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Mapper Interface
 */
@Mapper
public interface SystemMapper {

    /**
     * menu actions
     */
    @InsertProvider(type = SystemMapperProvider.class, method = "insertMenu")
    int insertMenu(@Param("menu") Menu menu);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteMenu")
    int deleteMenu(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "disableMenu")
    int disableMenu(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enableMenu")
    int enableMenu(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "updateMenu")
    int updateMenu(@Param("id") Integer id, @Param("name") String name, @Param("code") String code, @Param("url") String url, @Param("pid") Integer pid, @Param("sort") Integer sort);

    @SelectProvider(type = SystemMapperProvider.class, method = "searchMenu")
    List<Menu> searchMenu(@Param("name") String name, @Param("status") int status);

    /**
     * role actions
     */
    @InsertProvider(type = SystemMapperProvider.class, method = "insertRole")
    int insertRole(@Param("role") Role role);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteRole")
    int deleteRole(@Param("id") int id);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteRoleMenuRelation")
    int deleteRoleMenuRelation(@Param("roleId") int roleId);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertRoleMenuRelation")
    int insertRoleMenuRelation(@Param("roleId") int roleId, @Param("menuId") int menuId);

    @UpdateProvider(type = SystemMapperProvider.class, method = "disableRole")
    int disableRole(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enableRole")
    int enableRole(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "updateRole")
    int updateRole(@Param("name") String name, @Param("code") String code, @Param("id") Integer id, @Param("desc") String desc);

    @Results(value = {@Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)})
    @SelectProvider(type = SystemMapperProvider.class, method = "searchRole")
    List<Role> searchRole(@Param("name") String name, @Param("status") int status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @SelectProvider(type = SystemMapperProvider.class, method = "countRole")
    Integer countRole(@Param("name") String name, @Param("status") int status);

    @SelectProvider(type = SystemMapperProvider.class, method = "roleMenuList")
    List<Integer> roleMenuList(@Param("roleId") Integer roleId);

    /**
     * user actions
     */
    @InsertProvider(type = SystemMapperProvider.class, method = "insertUser")
    int insertUser(@Param("user") User user);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteUser")
    int deleteUser(@Param("id") int id);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteUserRoleRelation")
    int deleteUserRoleRelation(@Param("userId") int userId);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertUserRoleRelation")
    int insertUserRoleRelation(@Param("userId") int userId, @Param("roleId") int roleId);

    @UpdateProvider(type = SystemMapperProvider.class, method = "disableUser")
    int disableUser(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enableUser")
    int enableUser(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "updateUser")
    int updateUser(@Param("id") Integer id, @Param("userCNName") String userCNName, @Param("userName") String userName, @Param("userPassword") String userPassword, @Param("phone") String phone, @Param("email") String email, @Param("desc") String desc, @Param("tenantId") Integer tenantId, @Param("queue") String queue);

    @UpdateProvider(type = SystemMapperProvider.class, method = "updateUserLoginInfo")
    int updateUserLoginInfo(@Param("id") Integer id, @Param("lastLoginTime") Date lastLoginTime, @Param("lastLoginIp") String lastLoginIp);

    @UpdateProvider(type = SystemMapperProvider.class, method = "userModifyPassword")
    int userModifyPassword(@Param("id") Integer id, @Param("password") String password);

    @SelectProvider(type = SystemMapperProvider.class, method = "userRoleList")
    List<Integer> userRoleList(@Param("userId") Integer userId);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userCNName", column = "user_cn_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = SystemMapperProvider.class, method = "searchUser")
    List<User> searchUser(@Param("name") String name, @Param("status") int status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @SelectProvider(type = SystemMapperProvider.class, method = "countUser")
    Integer countUser(@Param("name") String name, @Param("status") int status);

    @SelectProvider(type = SystemMapperProvider.class, method = "queryUserByName")
    Integer queryUserByName(@Param("userName") String userName);

    @SelectProvider(type = SystemMapperProvider.class, method = "queryRoleByCode")
    Integer queryRoleByCode(@Param("code") String code);

    @SelectProvider(type = SystemMapperProvider.class, method = "getConf")
    List<Map> getConf(@Param("key") String key);
}
