package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Role;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * User Role Mapper Interface
 */
@Mapper
public interface UserRoleMapper {
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "cn_name", column = "cn_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = UserRoleMapperProvider.class, method = "findRolesByUser")
    List<Role> findRolesByUser(@Param("id") Integer id);

}
