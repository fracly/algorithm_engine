package cn.escheduler.api.impl;

import java.util.List;
import java.util.Map;

public interface ITableDetail {

    /**
     *  获取表的详细信息
     * @param base
     * @param tblName
     * @return
     */
     List<Map<String, String>> getTableDetailInfo(String base, String tblName);
}
