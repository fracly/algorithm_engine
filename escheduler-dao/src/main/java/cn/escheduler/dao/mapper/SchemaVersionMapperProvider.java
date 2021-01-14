package cn.escheduler.dao.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * schema version mapper provider
 */
public class SchemaVersionMapperProvider {

    public static final String VERSION_TABLE_NAME = "t_escheduler_schema_version";

    /**
     * insert schema version
     */
    public String insert(Map<String, Object> parameter){
        return new SQL(){{
            INSERT_INTO(VERSION_TABLE_NAME);
            VALUES("`name`", "#{schemaVersion.name}");
            VALUES("`version`", "#{schemaVersion.version}");
            VALUES("`schema_text`","#{schemaVersion.schemaText}");
            VALUES("`desc`", "#{schemaVersion.desc}");
            VALUES("`state`", "#{schemaVersion.state}");
            VALUES("`create_time`", "#{schemaVersion.createTime}");
            VALUES("`update_time`", "#{schemaVersion.updateTime}");
            VALUES("`schema_base`", "#{schema.layer}");
            VALUES("`schema_tablename`", "#{schemaVersion.schemaTableName}");
            VALUES("`branch_id`", "#{schemaBranch.id}");
        }}.toString();
    }

    /**
     * delete schema version by schemaName
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL(){{
            DELETE_FROM(VERSION_TABLE_NAME);
            WHERE("`schema_id`=#{schemaId}");
        }}.toString();
    }

    /**
     * delete schema version by schemaName
     */
    public String deleteAll(Map<String, Object> parameter) {
        return new SQL(){{
            DELETE_FROM(VERSION_TABLE_NAME);
        }}.toString();
    }

    /**
     * query schema branch by schema name
     */
    public String queryByKey(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("*");
            FROM(VERSION_TABLE_NAME );
            WHERE("schema_base = #{layer} and schema_tablename = #{tableName}");
            ORDER_BY("version");
        }}.toString();
    }

    /**
     * query lastest version
     */
    public String queryLastestVersionSchema(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("a.*");
            FROM("t_escheduler_schema_version a");
            JOIN("t_escheduler_schema_branch b on a.branch_id=b.id");
            WHERE("a.schema_base = #{layer} and a.schema_tablename=#{schemaName} and b.name=#{branchName}");
            ORDER_BY("version desc limit 1");
        }}.toString();
    }

}
