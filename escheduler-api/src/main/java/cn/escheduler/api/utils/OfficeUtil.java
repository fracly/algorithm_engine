package cn.escheduler.api.utils;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * @Description:
 * @Author ：Marinh
 * @Param: office 相关工具类
 * @retrun:
 * @Creat :2020-06-11-13:47
 **/
public class OfficeUtil {
    /**
     * 日志打印类
     */
    private static Logger logger = Logger.getLogger(OfficeUtil.class.getName());
    /**
     * excel 文件类型
     */
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    /**
     * 中间表头集合
     */
    private static List<List<String>> headList;
    /**
     * 数据导出集合
     */
    private static List<Map<String, Object>> tableDate;
    /**
     * 字段集合
     */
    private static String[] CloumList = new String[]{};
    /**
     * 字段取值下标
     */
    private static int cloumIndex = 0;
    /**
     * 文件路径
     */
    private static String filePath;
    /**
     * excel 对象
     */
    private static HSSFWorkbook wb;



    /**
     * 根据Json 导出表头公共方法
     *
     * @param reportHead 多级表头
     * @return
     */
    public static HSSFWorkbook loadExcelForHeadJson(String[] cloumList, List<Map<String, Object>> tabledate,HSSFWorkbook Wb,List<List<String>> headlist,List<Map<String, Object>> reportHead) {
        CloumList=cloumList;
        tableDate = tabledate;
        wb=Wb;
        headList=headlist;
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFCellStyle contentStyle = createContentCellStyle();
        setCellRange(reportHead, sheet);
        int rowIndex = 0;
        for (List<String> row : headList) {
            Row rows = sheet.createRow(rowIndex);//创建第一行
            int ColIndex = 0;
            for (String col : row) {
                Cell cell = rows.createCell(ColIndex);//创建第一行的第一个单元格
                cell.setCellStyle(contentStyle);
                cell.setCellValue(col);
                ColIndex++;
            }
            rowIndex++;
        }
        for (Map<String, Object> map : tableDate) {
            Row rows = sheet.createRow(rowIndex);//创建第一行
            int ColIndex = 0;
            for (String key : CloumList) {//keySet获取map集合key的集合  然后在遍历key即可
                if(map.containsKey(key)){
                    Cell cell = rows.createCell(ColIndex);//创建第一行的第一个单元格
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue(String.valueOf(map.get(key)));
                    ColIndex++;
                }else{
                    Cell cell = rows.createCell(ColIndex);//创建第一行的第一个单元格
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue("");
                    ColIndex++;
                }
            }
            rowIndex++;
        }
        return wb;
    }

    /**
     * 自定义报表合并单元格
     *
     * @param list
     * @param sheet
     */
    public static void setCellRange(List<Map<String, Object>> list, HSSFSheet sheet) {
        for (Map<String, Object> map : list) {
            //keySet获取map集合key的集合  然后在遍历key即可
            int rowEnd = Integer.parseInt(String.valueOf(map.get("rowEnd")));
            int rowStart = Integer.parseInt(String.valueOf(map.get("rowStart")));
            int colStart = Integer.parseInt(String.valueOf(map.get("colStart")));
            int colEnd = Integer.parseInt(String.valueOf(map.get("colEnd")));
            if (colEnd != colStart || rowStart != rowEnd) {
                sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, colStart, colEnd));
            }
            if (map.containsKey("children") && map.get("children") != null) {
                List<Map<String, Object>> reportHead1 = (List<Map<String, Object>>) map.get("children");
                setCellRange(reportHead1, sheet);
            }

        }
    }

    /**
     * 创建内容样式
     *
     * @return
     */
    private static HSSFCellStyle createContentCellStyle() {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        // 生成12号字体
        HSSFFont font = wb.createFont();
        font.setColor((short) 8);
        font.setFontHeightInPoints((short) 8);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 创建标题样式
     *
     * @return
     */
    private static HSSFCellStyle createTitleCellStyle() {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直对齐
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//背景颜色
        HSSFFont headerFont1 = (HSSFFont) wb.createFont(); // 创建字体样式
        headerFont1.setBold(true); //字体加粗
        headerFont1.setFontName("黑体"); // 设置字体类型
        headerFont1.setFontHeightInPoints((short) 15); // 设置字体大小
        cellStyle.setFont(headerFont1); // 为标题样式设置字体样式
        return cellStyle;
    }

    /**
     * 创建表头样式
     *
     * @return
     */
    private static HSSFCellStyle createHeadCellStyle() {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//背景颜色
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        HSSFFont headerFont = (HSSFFont) wb.createFont(); // 创建字体样式
        headerFont.setBold(true); //字体加粗
        headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式

        return cellStyle;
    }

    /**
     * excel 导入根据文件路径
     *
     * @return
     */
    public static List readExcel(String filePath) {
        String fileName = filePath;
        Workbook workbook = null;
        FileInputStream inputStream = null;
        try {
            // 获取Excel后缀名
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            // 获取Excel文件
            File excelFile = new File(fileName);
            if (!excelFile.exists()) {
                logger.warning("指定的Excel文件不存在！");
                return null;
            }
            // 获取Excel工作簿
            inputStream = new FileInputStream(excelFile);
            workbook = getWorkbook(inputStream, fileType);
            List<List<String>> resultDataList = parseExcel(workbook);
            return resultDataList;
        } catch (Exception e) {
            logger.warning("解析Excel失败，文件名：" + fileName + " 错误信息：" + e.getMessage());
            return null;
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.warning("关闭数据流出错！错误信息：" + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 解析Excel数据
     *
     * @param wb Excel工作簿对象
     * @return 解析结果
     */
    private static List<List<String>> parseExcel(Workbook wb) {
        List resultDataList = new ArrayList<List<String>>();
        Sheet sheet = wb.getSheetAt(0);    //获得第一个表单

        //报表总列数和总行数 要求报表必须符合格式要求 不能第一行留空
        int rowCount = sheet.getLastRowNum() + 1;
        int lastRowNum = sheet.getLastRowNum();
        int physicalNumberOfCells = sheet.getRow(0).getPhysicalNumberOfCells();

        for (int i = 0; i < rowCount; i++) {
            Row row;
            String[] res = new String[physicalNumberOfCells];
            for (int j = 0; j < physicalNumberOfCells; j++) {
                String mergedRegionValue = "";
                if (isMergedRegion(sheet, i, j)) {
                    mergedRegionValue = getMergedRegionValue(sheet, i, j);
                    res[j] = mergedRegionValue;
                } else {
                    mergedRegionValue = getCellValue(sheet.getRow(i).getCell(j));
                }
                res[j] = mergedRegionValue;
            }
            List<String> strings = Arrays.asList(res);
            resultDataList.add(strings);
        }
        return resultDataList;
    }


    /**
     * 合并报表内容
     *
     * @return
     */
    public static Map<String, Object> parseHead(List<List<String>> headlist, String[] cloumlist) {
        headList=headlist;
        CloumList=cloumlist;
        cloumIndex=0;
        Map<String, Object> resMap = new HashMap<>();
        //获取第一行对象
        List<Map<String, Object>> maps = parseHead(headList.get(0), 0);
        List<Map<String, Object>> child = findChild(maps, 1);
        needPutCloumInfo(child, 0);
        resMap.put("reportData", child);
        return resMap;
    }

    /**
     * 给解析好的表头 设置字段
     *
     * @param list
     * @param rowIndex
     */
    public static void needPutCloumInfo(List<Map<String, Object>> list, int rowIndex) {
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
                if (CloumList.length <= (cloumIndex ) || map.containsKey("dataIndex")) {
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
    private static List<Map<String, Object>> findChild(List<Map<String, Object>> maps, int row) {
        if (row >= headList.size()) {
            return null;
        }
        List<Map<String, Object>> maps1 = parseHead(headList.get(row), row);
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
                //如果父集标题和子集标题一直  则跨列 无需新增child row加一
                if (pTitle.equals(title) && colStart >= pcolStart && colEnd <= pcolEnd) {
                    prowEnd++;
                    map.put("rowEnd", prowEnd);
                    int i = row + 1;
                    if (i < headList.size()) {
                        mergeRow(map, headList, i);
                    }
                }
                if (!pTitle.equals(title) && colStart >= pcolStart && colEnd <= pcolEnd) {
                    children.add(item);
                }
            }
            map.put("children", children);
            //递归调用
            if (row + 1 <= headList.size()) {
                int i = row + 1;
                findChild(children, i);
            }
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
    private static void mergeRow(Map<String, Object> map, List<List<String>> headList, int row) {
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
    private static List<Map<String, Object>> parseHead(List<String> res, int row) {
        List<Map<String, Object>> list = new ArrayList<>();
        //去重
        String resValue = "";
        int colStart = 0;
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
    public static int getNumForCol(List<String> res, String colName, int index) {
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

    /**
     * 测试方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        filePath = "G:\\tmp\\11 月份“百日会战”v1.0.xls";
        /*获取中间表头 参数为导入的文件路径*/
        headList = readExcel(filePath);
        String s1 = JSON.toJSONString(headList);
        /**
         * 获取报表表头 传参为中间表头和字段集合
         */
        Map<String, Object> objectMap = parseHead(headList,CloumList);

        String s = JSON.toJSONString(objectMap);
        //创建HSSFWorkbook工作薄对象
        wb = new HSSFWorkbook();
        //wb = loadExcelForHeadJson(objectMap);
        //文档输出
        FileOutputStream out = new FileOutputStream("E:\\" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString() + ".xls");
        wb.write(out);
    }

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param fileType    文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * 将单元格内容转换为字符串
     *
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                Double doubleValue = cell.getNumericCellValue();

                // 格式化科学计数法，取一位整数
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:     // 空值
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case ERROR:     // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }


    /**
     * 合并单元格处理,获取合并行
     *
     * @param sheet
     * @return List<CellRangeAddress>
     */
    public List<CellRangeAddress> getCombineCell(Sheet sheet) {
        List<CellRangeAddress> list = new ArrayList<>();
        //获得一个 sheet 中合并单元格的数量
        int sheetmergerCount = sheet.getNumMergedRegions();
        //遍历所有的合并单元格
        for (int i = 0; i < sheetmergerCount; i++) {
            //获得合并单元格保存进list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }

    private int getRowNum(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) {
        int xr = 0;
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        for (CellRangeAddress ca : listCombineCell) {
            //获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR) {
                if (cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC) {
                    xr = lastR;
                }
            }

        }
        return xr;

    }

    /**
     * 判断单元格是否为合并单元格，是的话则将单元格的值返回
     *
     * @param listCombineCell 存放合并单元格的list
     * @param cell            需要判断的单元格
     * @param sheet           sheet
     * @return
     */
    public String isCombineCell(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet)
            throws Exception {
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        String cellValue = null;
        for (CellRangeAddress ca : listCombineCell) {
            //获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR) {
                if (cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC) {
                    Row fRow = sheet.getRow(firstR);
                    Cell fCell = fRow.getCell(firstC);
                    cellValue = getCellValue(fCell);
                    break;
                }
            } else {
                cellValue = "";
            }
        }
        return cellValue;
    }


    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }

        return null;
    }


    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }


}
