package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Schema;
import cn.escheduler.dao.model.SchemaBranch;
import cn.escheduler.dao.model.SchemaVersion;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * schema version mapper
 */
public interface SchemaVersionMapper {

    /**
     * insert schema version
     */
    @InsertProvider(type = SchemaVersionMapperProvider.class, method = "insert")
    @Options(useGeneratedKeys = true,keyProperty = "schemaVersion.id")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "schemaVersion.id", before = false, resultType = int.class)
    int insert(@Param("schemaVersion") SchemaVersion schemaVersion,
               @Param("schema") Schema schema, @Param("schemaBranch") SchemaBranch schemaBranch);

    /**
     * delete all schema version
     */
    @DeleteProvider(type = SchemaVersionMapperProvider.class, method = "deleteAll")
    int deleteAll();

    /**
     * delete schema version by schema name
     */
    @DeleteProvider(type = SchemaVersionMapperProvider.class, method = "delete")
    int delete(@Param("schemaId") int schemaId);

    /**
     * query all schema versions by primary key
     */
    @Results(value = {
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "schemaText", column = "schema_text", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "state", column = "state", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "schemaId", column = "schema_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "branchId", column = "branch_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = SchemaVersionMapperProvider.class, method = "queryByKey")
    List<SchemaVersion> queryByKey(@Param("layer") String layer, @Param("tableName") String tableName);


    /**
     * query lasted version of the schema by id
     */
    @Results(value = {
            @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "version", column = "version", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "schemaText", column = "schema_text", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "state", column = "state", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "schemaId", column = "schema_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "branchId", column = "branch_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = SchemaVersionMapperProvider.class, method = "queryLastestVersionSchema")
    SchemaVersion queryLastestVersionSchema(@Param("layer") String layer, @Param("schemaName") String schemaName,
                                            @Param("branchName") String branchName);

}
