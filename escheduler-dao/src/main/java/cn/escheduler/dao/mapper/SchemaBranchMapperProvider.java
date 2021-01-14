package cn.escheduler.dao.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class SchemaBranchMapperProvider {
    public static final String TABLE_NAME = "t_escheduler_schema_branch";
    /**
     * insert schema branch
     */
    public String insert(Map<String, Object> parameter){
        return new SQL(){
            {
                INSERT_INTO(TABLE_NAME);
                VALUES("`name`", "#{schemaBranch.name}");
                VALUES("`schema_base`", "#{schemaBranch.base}");
                VALUES("`schema_name`", "#{schemaBranch.schemaName}");
                VALUES("`create_time`", "#{schemaBranch.createTime}");
                VALUES("`desc`", "#{schemaBranch.desc}");
            }
        }.toString();
    }

    /**
     * delete schema branch
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL(){{
            DELETE_FROM(TABLE_NAME);
            WHERE("`schema_id` = #{schemaId}");
        }}.toString();
    }

    /**
     * delete all schema branches
     */
    public String deleteAll(Map<String, Object> parameter) {
        return new SQL(){{
            DELETE_FROM(TABLE_NAME);
        }}.toString();
    }

    /**
     * query schema branch by schema name
     */
    public String queryByKey(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("schema_base = #{layer} and schema_name = #{tableName}");
        }}.toString();
    }

    /**
     * query branch id by name
     */
    public String queryByName(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("id");
            FROM(TABLE_NAME);
            WHERE("schema_base = #{base} and schema_name = #{tableName} and name = #{branchName}");
        }}.toString();
    }

    /**
     * query schema branch by id
     */
    public String queryById(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("id = #{id}");
        }}.toString();
    }
}
