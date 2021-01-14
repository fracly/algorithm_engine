package cn.escheduler.dao.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class EnumMapperProvider {
    public static final String LAYER_TABLE = "t_escheduler_model_basename";
    public static final String CATALOG_TABLE = "t_escheduler_model_catalog";

    public static final String DATAASSET_TYPE_TABLE = "t_escheduler_dataasset_type";
    public static final String DATAASSET_LABEL_TABLE = "t_escheduler_dataasset_label";
    public static final String DATAASSET_STATE_TABLE = "t_escheduler_dataasset_state";
    public static final String DATAASSET_DESTROY_METHOD_TABLE = "t_escheduler_dataasset_destroy_method";


    public String getAllSchemaResources() {
        return new SQL() {{
            SELECT("base as layer, name");
            FROM(LAYER_TABLE);
        }}.toString();
    }

    public String getSchemaBusinessTopic(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("layer as topic, layer as name");
            FROM(CATALOG_TABLE);
            WHERE("`base` = #{layer}");
        }}.toString();
    }

    public String getDataAssetType(){
        return new SQL() {{
            SELECT("name, value");
            FROM(DATAASSET_TYPE_TABLE);
        }}.toString();
    }

    public String getDataAssetLabel() {
        return new SQL() {{
            SELECT("name, cast(id as char) as value");
            FROM(DATAASSET_LABEL_TABLE);
        }}.toString();
    }

    public String getDataAssetState() {
        return new SQL() {{
            SELECT("name, cast(id as char) as value");
            FROM(DATAASSET_STATE_TABLE);
        }}.toString();
    }

    public String getDataAssetDestroyMethod() {
        return new SQL() {{
            SELECT("name, cast(id as char) as value");
            FROM(DATAASSET_DESTROY_METHOD_TABLE);
        }}.toString();
    }
}
