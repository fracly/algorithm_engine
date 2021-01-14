package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Schema;
import cn.escheduler.dao.model.SchemaBranch;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * Schema Branch Mapper
 */
public interface SchemaBranchMapper {

    /**
     * insert schema branch
     */
    @InsertProvider(type = SchemaBranchMapperProvider.class, method = "insert")
    @Options(useGeneratedKeys = true,keyProperty = "schemaBranch.id")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "schemaBranch.id", before = false, resultType = int.class)
    int insert(@Param("schema") Schema schema, @Param("schemaBranch") SchemaBranch schemaBranch);

    /**
     * delete all schema branches
     */
    @DeleteProvider(type = SchemaBranchMapperProvider.class, method = "deleteAll")
    int deleteAll();

    /**
     * delete schema branch
     */
    @DeleteProvider(type = SchemaBranchMapperProvider.class, method = "delete")
    int delete(@Param("schemaId") Integer schemaId);

    /**
     * query branches by primary key
     */
    @Results(value = {
        @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
        @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
        @Result(property = "base", column = "schema_base", javaType = String.class, jdbcType = JdbcType.VARCHAR),
        @Result(property = "schemaName", column = "schema_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
        @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
        @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = SchemaBranchMapperProvider.class, method = "queryByKey")
    List<SchemaBranch> queryByKey(@Param("layer") String layer, @Param("tableName") String tableName);

    /**
     * query branch by id
     */
    @Results(value = {
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "schemaId", column = "schema_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = SchemaBranchMapperProvider.class, method = "queryById")
    SchemaBranch queryById(@Param("id") Integer id);

    /**
     * query branchId by name
     */
    @SelectProvider(type = SchemaBranchMapperProvider.class, method = "queryByName")
    Integer queryByName(@Param("base") String base, @Param("tableName") String tableName, @Param("branchName") String branchName);
}
