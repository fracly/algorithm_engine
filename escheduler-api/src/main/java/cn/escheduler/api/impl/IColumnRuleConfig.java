package cn.escheduler.api.impl;

import java.util.List;
import java.util.Map;

public interface IColumnRuleConfig {

    List<Map<String,String>> getColumnRuleConf(String base, String tblName);
    int getResult(String base, String tblName);
}
