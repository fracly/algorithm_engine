package cn.escheduler.api.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * 自定义报表引擎 通过excel以及sql 解析出来的字段 生成动态表头供
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-11-13:47
 **/
public class reportEngine {
    /**
     * 日志打印类
     */
    private static Logger logger = Logger.getLogger(reportEngine.class.getName());
    /**
     * 中间表头集合
     */
    private List<List<String>> headList;
    /**
     * 字段集合
     */
    private String[] CloumList = new String[]{};
    /**
     * 字段取值下标
     */
    private int cloumIndex;

    /*取值的行列值*/
    private int RowIndex=0;
    private int ColIndex=0;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * excel 对象
     */
    private HSSFWorkbook wb;

    /*报错信息*/
    public String ErrorMsg;

    /*解析状态 0未开始 1开始解析 -1解析报错 2完成表头解析*/
    public int status;


    /*自定义报表引擎初始化*/
    public reportEngine(List<List<String>> headlist, String[] cloumlist) {
        this.headList = headlist;
        this.CloumList = cloumlist;
        this.cloumIndex = 0;
        this.status = 0;
        this.RowIndex=0;
        this.ColIndex=0;
    }

    /**
     * 动态报表表头
     *
     * @return
     */
    public Map<String, Object> parseHead() {
        //验证参数是否正常
        if(!checkEngineParams()){
            this.status=-1;
            return null;
        }
        Map<String, Object> resMap = new HashMap<>();
        //获取第一行对象
        List<Map<String, Object>> maps = parseHead(headList.get(0), 0);
        List<Map<String, Object>> child = findChild(maps, 1);
        needPutCloumInfo(child, 0);
        resMap.put("reportData", child);
        return resMap;
    }

    /*自定义报表引擎初始化校验*/
    private boolean checkEngineParams(){
        if(headList.size()==0){
            ErrorMsg="解析excel报错，excel内容读取为空，请检查excel内容";
            return false;
        }
        if(CloumList.length==0){
            ErrorMsg="sql引擎解析字段报错，解析字段为空，请检查Sql语句是否正确";
            return false;
        }
        int i=0;
        for (List head : headList){
            if(head.size()<CloumList.length){
                ErrorMsg="excel设置出错，请检查第"+(i+1)+"行，此行单元格数量小于sql字段数量，无法正常解析，请检查是否有空单元格";
                return false;
            }
            if(head.size()>CloumList.length){
                ErrorMsg="excel设置出错，请检查第"+(i+1)+"行，此行单元格数量大于sql字段数量，无法正常解析，请检查是否有多余单元格";
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * 给解析好的表头 设置字段
     *
     * @param list
     * @param rowIndex
     */
    public  void needPutCloumInfo(List<Map<String, Object>> list, int rowIndex) {
        HashMap<Object, Object> map = new HashMap<>();
        /*循环所有数据*/
        for (Map<String, Object> item : list) {
            List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
            if (item.containsKey("children")) {
                children = (List<Map<String, Object>>) item.get("children");
            }
            if (children.size() > 0) {
                needPutCloumInfo(children, rowIndex + 1);
            } else {
                item.remove("children");
                if (CloumList.length <= (cloumIndex) || map.containsKey("dataIndex")) {
                    System.out.println("字段信息有误，请检查参数后重试");
                    return;
                }
                item.put("dataIndex", CloumList[cloumIndex]);
                item.put("key", CloumList[cloumIndex]);
                item.put("align", "center");
                if (cloumIndex == 0) {
                    item.put("width", 200);
                    String title = String.valueOf(item.get("title"));
                    //如果第一个标题 含有| 则特殊处理表头
                    if (title.indexOf("|") != -1) {
                        item.remove("title");
                        item.put("fixed", 200);
                        Map<Object, Object> solts = new HashMap<>();
                        solts.put("title", "customTitle");
                        item.put("slots", solts);
                        Map<Object, Object> scopedSlots = new HashMap<>();
                        scopedSlots.put("customRender", "name");
                        item.put("scopedSlots", scopedSlots);
                        String[] split = title.split("\\|");
                        for (int i = 0; i < split.length; i++) {
                            item.put("headName" + (i + 1), split[i]);
                        }
                    }
                } else {
                    item.put("width", 100);
                }
                cloumIndex++;
            }
        }
    }

    /**
     * 根据表头长度 递归查询表头
     *
     * @param maps
     * @param row
     * @return
     */
    private List<Map<String, Object>> findChild(List<Map<String, Object>> maps, int row) {
        if (row >= headList.size()) {
            return maps;
        }
        List<Map<String, Object>> maps1 = parseHead(headList.get(row), row);
        try {
            for (Map map : maps) {
                List children = new ArrayList<>();
                for (Map<String, Object> item : maps1) {
                    String pTitle = String.valueOf(map.get("title"));

                    String title = String.valueOf(item.get("title"));
                    int pcolStart = Integer.parseInt(String.valueOf(map.get("colStart")));
                    int pcolEnd = Integer.parseInt(String.valueOf(map.get("colEnd")));
                    int prowEnd = Integer.parseInt(String.valueOf(map.get("rowEnd")));
                    int colStart = Integer.parseInt(String.valueOf(item.get("colStart")));
                    int colEnd = Integer.parseInt(String.valueOf(item.get("colEnd")));
                    //如果父集标题和子集标题一直  则跨列 无需新增child row加1
                    if (pTitle.equals(title) && colStart >= pcolStart && colEnd <= pcolEnd) {
                        prowEnd++;
                        if(prowEnd>=headList.size()){
                            continue;
                        }
                        map.put("rowEnd", prowEnd);
                        int i = row + 1;
                        if (i < headList.size()) {
                            mergeRow(map, headList, i);
                        }
                    }
                    /*如果子列和父列值不同，且行在父列行范围内 则判断属于父级的child*/
                    if (!pTitle.equals(title) && colStart >= pcolStart && colEnd <= pcolEnd) {
                        children.add(item);
                    }
                }
                map.put("children", children);

                //递归调用
                if (row + 1 <= headList.size()) {
                    int i = row + 1;
                    //如果没有子项,且自己跨行，则进行自己的下一步判断
                    if(children.size()==0 && Integer.parseInt(map.get("rowEnd").toString())!=Integer.parseInt(map.get("rowStart").toString())){
                        List objects = new ArrayList<>();
                        objects.add(map);
                        findChild(objects, i);
                    }else{
                        findChild(children, i);
                    }
                }
            }
        }catch (Exception e){
            logger.info("Excel解析出错：行："+(row+1)+" 解析出现错误,报错信息为："+e);
            status=-1;
            ErrorMsg="Excel解析出错：行："+(row+1)+" 解析出现错误,报错信息为："+e;
        }

        return maps;
    }

    /**
     * 当子列和父列值相同时，自动合并到下级
     *
     * @param map
     * @param headList
     * @param row
     */
    private void mergeRow(Map<String, Object> map, List<List<String>> headList, int row) {
        List<String> strings = headList.get(row);
        List<Map<String, Object>> maps1 = parseHead(strings, row);
        for (Map<String, Object> item : maps1) {
            String pTitle = String.valueOf(map.get("title"));
            String title = String.valueOf(item.get("title"));
            int pcolStart = Integer.parseInt(String.valueOf(map.get("colStart")));
            int pcolEnd = Integer.parseInt(String.valueOf(map.get("colEnd")));
            int prowEnd = Integer.parseInt(String.valueOf(map.get("rowEnd")));
            int colStart = Integer.parseInt(String.valueOf(item.get("colStart")));
            int colEnd = Integer.parseInt(String.valueOf(item.get("colEnd")));
            //如果父集标题和子集标题一直  则跨列 无需新增child row加一
            if (pTitle.equals(title) && colStart >= pcolStart && colEnd <= pcolEnd) {
                prowEnd++;
                map.put("rowEnd", prowEnd);
                int i = row + 1;
                if (i < headList.size()) {
                    mergeRow(map, headList, i);
                }
            }
        }
    }

    /**
     * 获取报表各字段的地址
     *
     * @param res
     * @param row
     * @return
     */
    private List<Map<String, Object>> parseHead(List<String> res, int row) {
        List<Map<String, Object>> list = new ArrayList<>();
        //去重
        String resValue = "";
        int colStart = 0;
        try {
            for (String resValueTemp : res) {
                if (!resValue.equals(resValueTemp)) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", resValueTemp);
                    map.put("rowStart", row);
                    map.put("rowEnd", row);
                    map.put("colStart", colStart);
                    map.put("colEnd", colStart + getNumForCol(res, resValueTemp, colStart) - 1);
                    list.add(map);
                }
                colStart++;
                resValue = resValueTemp;
            }
        }catch (Exception e){
            status=-1;
            ErrorMsg="Excel解析出错：行："+(row+1)+"列："+colStart+" 解析出现错误,报错信息为："+e;
        }
        return list;
    }

    /**
     * 获取同行相同列的数量
     *
     * @param res
     * @param colName
     * @param index
     * @return
     */
    public int getNumForCol(List<String> res, String colName, int index) {
        int i = 0;
        int colEnd = 0;
        for (String item : res) {
            if (i < index) {
                i++;
                continue;
            }
            if (colName.equals(item)) {
                colEnd++;
            } else {
                break;
            }
        }
        return colEnd;
    }
}
