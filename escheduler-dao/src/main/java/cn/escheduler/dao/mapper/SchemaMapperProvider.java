package cn.escheduler.dao.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * Schema mapper provider
 * the core of schema management
 */
public class SchemaMapperProvider {
    public static final String TABLE_MODEL_NAME = "t_escheduler_model_design";
    public static final String TABLE_MODEL_BASENAME = "t_escheduler_model_basename";

    /**
     * query schema id by name
     */
    public String queryByName(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("id");
            FROM(TABLE_MODEL_NAME );
            WHERE("name = #{name}");
        }}.toString();
    }

    /**
     * query schema by id
     */
    public String queryByKey(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("base as layer, layer as topic, tablename, tabledescribe, area as tabledetail, " +
                    "type as resource, create_time as createTime, username as createUser");
            FROM(TABLE_MODEL_NAME );
            WHERE("base = #{layer} and tablename = #{schemaName}");
        }}.toString();
    }

    /**
     * query project list paging
     * @param parameter
     * @return
     */
    public String querySchemas(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("a.base as layer, a.layer as topic, a.tablename, a.tabledescribe, a.area as tabledetail, " +
                    "a.type as resource, a.create_time as createTime, a.username as createUser, b.name as CNLayer");
            FROM(TABLE_MODEL_NAME + " a");
            INNER_JOIN(TABLE_MODEL_BASENAME + " b on a.base = b.base");
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
                WHERE("a.tabledescribe like concat('%', '" + parameter.get("searchVal").toString() + "', '%')");
            }
            Object layer = parameter.get("layer");
            if(layer != null && StringUtils.isNotEmpty(layer.toString()) && !layer.equals("all")) {
                String[] list = layer.toString().split(",");
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < list.length; i ++) {
                    if(i == list.length - 1) {
                        sb.append("'" + list[i] + "'");
                    } else {
                        sb.append("'" + list[i] + "'" + ",");
                    }
                }
                WHERE( " a.base in (" + sb.toString() + ")");
            }
            Object topic = parameter.get("topic");
            if(topic != null && StringUtils.isNotEmpty(topic.toString()) && !topic.equals("all")) {
                WHERE( " a.layer = #{topic}");
            }
            ORDER_BY("a.create_time desc limit #{offset}, #{pageSize}");
        }}.toString();
    }


    /**
     * query project list paging
     * @param parameter
     * @return
     */
    public String getTotalSize(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1)");
            FROM(TABLE_MODEL_NAME);
            Object searchVal = parameter.get("searchVal");
            if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
                WHERE("tabledescribe like concat('%', '" + parameter.get("searchVal").toString() + "', '%')");
            }
            Object layer = parameter.get("layer");
            if(layer != null && StringUtils.isNotEmpty(layer.toString()) && !layer.equals("all")) {
                String[] list = layer.toString().split(",");
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < list.length; i ++) {
                    if(i == list.length - 1) {
                        sb.append("'" + list[i] + "'");
                    } else {
                        sb.append("'" + list[i] + "'" + ",");
                    }
                }
                WHERE( " base in (" + sb.toString() + ")");
            }
            Object topic = parameter.get("topic");
            if(topic != null && StringUtils.isNotEmpty(topic.toString()) && !topic.equals("all")) {
                WHERE( " layer = #{topic}");
            }
        }}.toString();
    }

    /**
     * get all schemas
     */
    public String getAllSchemas(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("base as layer, layer as topic, tablename, tabledescribe, area as tabledetail," +
                    "type as resource, create_time as createTime, username as createUser");
            FROM(TABLE_MODEL_NAME);
        }}.toString();
    }

    /**
     * verify schema name whether already exists
     */
    public String verifyName(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("count(1)");
            FROM(TABLE_MODEL_NAME);
            WHERE(" name = #{name}");
        }}.toString();
    }

}
