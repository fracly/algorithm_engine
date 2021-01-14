package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Menu;
import cn.escheduler.dao.model.Role;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Role Permission Mapper Interface
 */
@Mapper
public interface RolePermissionMapper {
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "url", column = "url", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "pid", column = "pid", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "operations", column = "operations", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = RolePermissionMapperProvider.class, method = "findPermissionsByRole")
    List<Menu> findPermissionsByRole(@Param("role") Role role);
}
