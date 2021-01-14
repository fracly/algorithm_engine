package cn.escheduler.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

public interface EnumMapper {
    @SelectProvider(type = EnumMapperProvider.class, method = "getAllSchemaResources")
    List<Map<String, Object>> getAllSchemaResources();

    @SelectProvider(type = EnumMapperProvider.class, method = "getSchemaBusinessTopic")
    List<Map<String, Object>> getSchemaBusinessTopic(@Param("layer") String layer);

    @SelectProvider(type = EnumMapperProvider.class, method = "getDataAssetType")
    List<Map<String, Object>> getDataAssetType();

    @SelectProvider(type = EnumMapperProvider.class, method = "getDataAssetLabel")
    List<Map<String, Object>> getDataAssetLabel();

    @SelectProvider(type = EnumMapperProvider.class, method = "getDataAssetState")
    List<Map<String, Object>> getDataAssetState();

    @SelectProvider(type = EnumMapperProvider.class, method = "getDataAssetDestroyMethod")
    List<Map<String, Object>> getDataAssetDestroyMethod();

}
