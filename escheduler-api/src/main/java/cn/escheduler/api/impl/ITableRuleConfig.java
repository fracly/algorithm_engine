package cn.escheduler.api.impl;

import java.util.Map;

public interface ITableRuleConfig {

     Map<String,String> getTableRuleConf(String base, String tableName);

}
