package cn.escheduler.dao.mapper;

import cn.escheduler.dao.model.Schema;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

/**
 * schema mapper
 */
public interface SchemaMapper {
    /**
     * query schema by name
     * @param name
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = SchemaMapperProvider.class, method = "queryByName")
    Schema queryByName(@Param("name") String name);

    /**
     * query schema by primary key
     * @param layer
     * @param schemaName
     * @return
     */
    @SelectProvider(type = SchemaMapperProvider.class, method = "queryByKey")
    Schema queryByKey(@Param("layer") String layer, @Param("schemaName") String schemaName);

    /**
     * query project list paging
     *
     * @return
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "schemaGroup", column = "schema_group", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "validateLevel", column = "validate_level", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "compatibility", column = "compatibility", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = SchemaMapperProvider.class, method = "querySchemas")
    List<Schema> querySchemas(@Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("layer") String layer,
                                           @Param("topic") String topic,
                                           @Param("searchVal")  String searchVal);


    @SelectProvider(type = SchemaMapperProvider.class, method = "getAllSchemas")
    List<Schema> getAllSchemas();

    @SelectProvider(type = SchemaMapperProvider.class, method = "getTotalSize")
    int getTotalSize(@Param("layer") String layer,
                     @Param("topic") String topic,
                     @Param("searchVal") String searchVal);

    /**
     * verify schema name whether already exists
     */
    @SelectProvider(type = SchemaMapperProvider.class, method = "verifyName")
    int verifyName(@Param("name") String name);
}
