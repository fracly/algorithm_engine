package cn.escheduler.api.utils;

import cn.escheduler.common.enums.DbType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-23-13:31
 **/
public class SqlUtil {

    /**
     * sql 关键字
     */
    private static String[] sqlKey = {" where ", " or ", " and "," group by" ," on "," union "};
    /**
     * 函数匹配规则
     */
    private static String functionKey = "[a-zA-Z_]{1,}\\(.*?\\)";
    /**
     * 匹配()
     */


    private static String unionKey = "\\s\\(.*?\\)\\s";
    /**
     * 参数关键字
     */
    private static String[] parmsKeys = {"(", ")"};
    /**
     * 宏变量正则匹配规则
     */
    private static String hodKey = "\\$\\{[\\s0-9a-zA-Z_.\\-\\s]{1,}\\}";
    /**
     *
     */
    /**
     * 查询sql 唯一实例
     */
    public static String sql;
    /**
     * 查询参数
     */
    public static Map<String, Object> params;
    /**
     * 报表配置查询参数
     */
    private static List<Map> reportParams;



    public static String countSql(String sql){
        sql ="select count(1) as sql_count from ("+sql+") count_SQL";
        return sql;
    }



    /*转换sql 去除宏变量后 转换大小写*/
    private static void toLowerCaseSql(){
        //处理剩下的为传参的宏变量 匹配sql关键字
        List<String> list = new ArrayList<String>();
        Pattern r = Pattern.compile(hodKey);
        // 现在创建 matcher 对象
        Matcher matcher = r.matcher(sql);
        //找到所有宏变量
        while (matcher.find()) {
            String group = matcher.group();
            list.add(group);
        }
        //sql做字母小写转换 防止关键字组合过多
        sql = sql.toLowerCase();
        for(String group : list){
            String groupTemp = group.toLowerCase();
            sql = sql.replaceAll(escapeQueryChars(groupTemp), escapeQueryChars(group));
        }
    }

    /*处理sql特殊符号*/
    private static void handSpecialChar(){
        //将sql 中的换行符替换为空格
        sql = sql.replaceAll("\\n", " ");
        sql = sql.replaceAll("\\t", " ");
    }

    public static List resultSetToList(ResultSet rs) throws java.sql.SQLException {
        if (rs == null) {
            return Collections.EMPTY_LIST;
        }
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
        List list = new ArrayList();
        Map rowData = new HashMap();
        while (rs.next()) {
            rowData = new HashMap(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }


    public static String Pagination(String sql, Integer pageSize, Integer pageNum, DbType dbType){
        switch (dbType) {
            case POSTGRESQL:

                break;
            case MYSQL:
                sql =sql + " limit " +(pageNum-1)*pageSize +","+pageSize;
                break;
            case HIVE:
                sql =sql + " limit 10000";
                break;
            case SPARK:

                break;
            case CLICKHOUSE:

                break;
            case ORACLE:

                break;
            case SQLSERVER:

                break;
            case KAFKA:

                break;
            case ELASTICSEARCH:
                sql =sql + " limit " +(pageNum-1)*pageSize +","+pageSize;
                break;
            case KYLIN:
                sql =sql + " limit " +(pageNum-1)*pageSize +","+pageSize;
                break;
            default:
                break;
        }
        return sql;
    }

    /**
     * 处理sql 统一入口
     * @param sqls sql 必须不为空
     * @param param
     * @param reportParam
     * @return
     */
    public static String processSql(String sqls, Map<String, Object> param, List<Map> reportParam) {
        if(StringUtils.isEmpty(sqls)){
            return "sql is not allow null";
        }
        //参数初始化
        sql = sqls;
        params = param;
        reportParams = reportParam;
        //sql 特殊符号处理
        handSpecialChar();
        //sql字段小写处理
        toLowerCaseSql();
        //如果参数不为空 则直接将宏变量参数进行替换
        if (params != null) {
            sql = repalceParams();
        }
        //处理剩下的为传参的宏变量 匹配sql关键字
        Pattern r = Pattern.compile(hodKey);
        // 现在创建 matcher 对象
        Matcher matcher = r.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();
            processMacro(group);
        }
        return sql;
    }

    /**
     * 处理宏变量
     *
     * @return
     */
    private static String processMacro(String macroValue) {
        //特殊符号需要转义才能使用
        String newmacroValue = escapeQueryChars(macroValue);
        String[] split = sql.split(newmacroValue);
        String sqlFirst = split[0];
        String firstKeyWord = "";
        int firstIndex = 0;
        String LastKeyWord = "";
        int LastIndex = 0;
        //处理前置关键字
        for (String key : sqlKey) {
            int i1 = sqlFirst.lastIndexOf(key);
            if (i1 != -1 && firstIndex < i1) {
                firstIndex = i1;
                firstKeyWord = key;
            }
        }
        //如果宏变量已被替换 则不再进行替换
        if(sql.indexOf(macroValue)==-1){
            return sql;
        }
        if (split.length < 2) {
            String Firstsubstring = sql.substring(firstIndex, sqlFirst.length() + macroValue.length());
            chooseSql(firstKeyWord, "", Firstsubstring);
            return sql;
        }
        //处理后置关键字
        String sqlLast = split[1];
        LastIndex = sqlLast.length();
        for (String key : sqlKey) {
            int i1 = sqlLast.indexOf(key);
            if (i1 != -1 && LastIndex > i1) {
                LastIndex = i1 + key.length();
                LastKeyWord = key;
            }
        }
        LastIndex = sqlFirst.length() + macroValue.length() + LastIndex;
        //包含后置字段的字符
        String Lastsubstring = sql.substring(firstIndex, LastIndex);
        //不包含后置字段的字符
        String Firstsubstring = sql.substring(firstIndex, sqlFirst.length() + macroValue.length());
        chooseSql(firstKeyWord, LastKeyWord, Lastsubstring);
        return sql;
    }

    /**
     * @param firstKeyWord  前置关键字
     * @param LastKeyWord   后置关键字
     * @param Lastsubstring 包含后置字段的字符
     * @return
     */
    private static void chooseSql(String firstKeyWord, String LastKeyWord, String Lastsubstring) {
        //是否有函数
        boolean hasFunction = false;
        //匹配是否有（）参数在宏变量种
        boolean hasUnion = false;
        //是否有括号
        boolean hasBracket = false;
        //先匹配 是否有函数在参数宏变量内
        hasFunction = Pattern.compile(functionKey).matcher(Lastsubstring).find();
        hasUnion = Pattern.compile(unionKey).matcher(Lastsubstring).find();
        if (Lastsubstring.indexOf("(") == -1 && Lastsubstring.indexOf(")") == -1) {
            hasBracket = true;
        }
        if (hasFunction || hasUnion || hasBracket) {
            sql = sql.replaceAll(escapeQueryChars(Lastsubstring), firstKeyWord + " 1=1 " + LastKeyWord);
        } else {
            chooseParmaSql(firstKeyWord, LastKeyWord, Lastsubstring);
        }
    }


    /**
     * 处理参数带括号的宏变量
     *
     * @param firstKeyWord
     * @param LastKeyWord
     * @param Lastsubstring
     */
    private static void chooseParmaSql(String firstKeyWord, String LastKeyWord, String Lastsubstring) {
        //宏变量的位置信息
        int[] res=new int[]{0,0};
        Matcher matcher = Pattern.compile(hodKey).matcher(Lastsubstring);
        //找到所有宏变量
        while (matcher.find()) {
            res[0]= Lastsubstring.indexOf(matcher.group());
            res[1]= Lastsubstring.indexOf(matcher.group())+matcher.group().length();
        }
        int keyMinIndex=0;
        String key="";
        //获取离得最近的参数关键字
        for (String keys : parmsKeys) {
            int indexTemp = Lastsubstring.indexOf(keys);
            if(indexTemp!=-1){
                //获取key 的位置
                int temp1 = Math.abs(indexTemp - res[0])>Math.abs(indexTemp - res[1])?Math.abs(indexTemp - res[1]):Math.abs(indexTemp - res[0]);
                if(keyMinIndex==0){
                    keyMinIndex = temp1;
                    key=keys;
                }
                if(keyMinIndex!=0 && temp1<keyMinIndex){
                    key=keys;
                }
            }
        }
        //根据参数关键字对sql 进行再一次加工
        if (Lastsubstring.indexOf(key) != -1) {
            String[] split = Lastsubstring.split(escapeQueryChars(key));
            if (split.length <= 1) {
                sql = sql.replaceAll(escapeQueryChars(Lastsubstring), escapeQueryChars(firstKeyWord) + " 1=1 " + key);
            } else {
                String tempKey = "";
                String repalceS = "";
                if (Pattern.compile(hodKey).matcher(split[0]).find()) {
                    tempKey = split[0]  + key;
                    repalceS = firstKeyWord + " 1=1 " + key;
                }
                if (Pattern.compile(hodKey).matcher(split[1]).find()) {
                    tempKey = key + split[1];
                    repalceS = key + " 1=1 " + LastKeyWord;
                }
                sql = sql.replaceAll(escapeQueryChars(tempKey), escapeQueryChars(repalceS));
                System.out.println("替换的参数为：" + tempKey + " repacle:" + repalceS);
            }
        }
    }

    /**
     * 处理特殊字符
     */
    public static String escapeQueryChars(String s) {
        if (!StringUtils.isNotEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')'
                    || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"'
                    || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                    || c == '|' || c == '&' || c == ';' || c == '/' || c == '.'
                    || c == '$' || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * 替换参数
     * @param param 替换值
     * @param paramType 参数类型
     * @param paramName 参数名
     * @param groupString 正则匹配的字符串
     * @return
     */
    private static String chooseParam(Object param, String paramType, String paramName,String groupString) {
        String S_param = "";//参数替换
        switch (paramType) {
            case "select":
            case "input":
            case "carplate":
                S_param = String.valueOf(param);
                break;
            case "time1":
                String[] split2 = groupString.split("=");
                if(split2.length==0){
                    return "";
                }
                if (param instanceof List && ((List) param).size() != 0) {
                    List param1 = (List) param;
                    S_param=("'"+String.valueOf(param1.get(0))+"'");
                }

                break;
            case "time":
                //如果是时间类型 则需要获取sql字段名称进行凭借
                String[] split = groupString.split("=");
                if(split.length==0){
                    return "";
                }
                if (param instanceof List && ((List) param).size() != 0) {
                    List param1 = (List) param;
                    //如果只传了一个时间参数 则默认结束时间参数和开始时间参数一样
                    S_param += " (" + split[0] + " >='" + param1.get(0).toString() + "' and " + split[0] + "<='" + param1.get(1).toString() + "') ";
                }
                break;
            case "tree":
            case "department":
            case "s-select":
                //如果是时间类型 则需要获取sql字段名称进行凭借
                String[] split1 = groupString.split("=");
                if(split1.length==1){
                    return "";
                }
                if (param instanceof List && ((List) param).size() != 0) {
                    List<Object> param1 = (List<Object>) param;
                    S_param += (" " + split1[0] + " in (");
                    for (Object res : param1) {
                        if (res instanceof String) {
                            S_param += ("'" + res + "',");
                        } else {
                            S_param += (String.valueOf(res) + ",");
                        }
                    }
                    //去除最后一个逗号
                    S_param = S_param.substring(0, S_param.length() - 1);
                    S_param += ")";
                }
                break;
        }
        return S_param;
    }

    /**
     * 替換sql 中宏变量的参数
     *
     * @return
     */
    private static String repalceParams() {
        //
        for (Map<String, Object> item : reportParams) {
            String S_param = "";
            //参数名
            String paramName = String.valueOf(item.get("paramName"));
            //参数类型
            String paramType = String.valueOf(item.get("paramType"));

            //当查询条件为tree 等时 需要将查询字段一起进行替换 否则只替换宏变量即可
            String macroKey = "";
            if ("tree".equals(paramType) || "s-select".equals(paramType) || "department".equals(paramType) || "time".equals(paramType)) {
                macroKey =  "[a-zA-Z0-9._\\-]{0,}[\\s]{0,}[=<>]{0,}[\\s]{0,}\\$\\{[\\s]{0,}" + paramName + "[\\s]{0,}\\}";
            } else {
                macroKey = "\\$\\{[\\s]{0,}" + paramName + "[\\s]{0,}\\}";
            }
            //做剩余未传参的参数进行替换
            Pattern r = Pattern.compile(macroKey);
            // 现在创建 matcher 对象
            Matcher matcher = r.matcher(sql);
            while (matcher.find()) {
                String group = matcher.group();
                //判断传入参数类型
                Object param = params.get(paramName);
                if (param != null && sql.indexOf(group)!=-1) {
                    //如果时间参数是> < 时间判断 则不进行区间判断
                    if("time".equals(paramType) && (group.indexOf("<")!=-1 || group.indexOf(">")!=-1)){
                       String timemacroKey = "\\$\\{[\\s]{0,}" + paramName + "[\\s]{0,}\\}";
                        Matcher matcher1 = Pattern.compile(timemacroKey).matcher(group);
                        while (matcher1.find()) {
                            group = matcher1.group();
                            S_param = chooseParam(param, paramType+"1", paramName,group);
                        }
                    }else{
                        S_param = chooseParam(param, paramType, paramName,group);
                    }
                }
                //只有找到替换参数具体指后 才进行参数替换 否则进行默认逻辑
                if (StringUtils.isNotEmpty(S_param) && sql.indexOf(group)!=-1) {
                    group = escapeQueryChars(group);
                    sql = sql.replaceAll(group, S_param);
                }
            }
        }

        //循环所有参数 并在处理宏变量之前 提前处理Sql
       /* for(String key:params.keySet()){
            String S_param="";//参数替换
            String macroKey="\\$\\{[\\s]{0,}"+key+"[\\s]{0,}\\}";
            //判断传入参数类型
            Object param = params.get(key);
            //参数为空时 跳出循环
            if(param==null){
                continue;
            }

        }*/
        return sql;
    }

    ;


    /**
     * 处理查询条件的传参
     * 时间参数的封装
     * @param res
     */
    public static void processParms(Map<String, Object> res) {

        //循环所有参数 看是否有参数需要处理
        for (String key : res.keySet()) {
            Object o = res.get(key);
            //如果参数有对应的时间格式 和 时间粒度参数  则代表此参数为时间查询参数 需要根据时间格式对参数进行填充
            if (res.containsKey(key + "$timeFormat") && res.containsKey(key + "$Grain")) {
                try {
                    List newArr = new ArrayList<>();
                    //时间参数数组
                    List timeArray = (List) res.get(key);
                    String timeFormat = String.valueOf(res.get(key + "$timeFormat"));
                    String Grain = String.valueOf(res.get(key + "$Grain"));
                    //如果没有传参数 则不处理参数
                    if (timeArray.size() == 0) {
                        continue;
                    }
                    //如果类型为单个时间查询 且第一个参数没有输入 则直接返回
                    if (timeArray.size() == 2 && "1".equals(Grain.substring(0, 1)) && timeArray.get(0)==null) {
                        continue;
                    }
                    //如果没有选择时间格式，不进行时间查询
                    if(StringUtils.isEmpty(Grain) || "null ".equals(Grain)){
                        timeArray.clear();
                        continue;
                    }
                    //默认填充时间
                    String nowDate = getDefaultTime(timeFormat,1);
                    String beforeDate =  getDefaultTime(timeFormat,0);
                    //如果第一个时间参数没传 格局时间格式默认填充197001010000000
                    if(timeArray.get(0)==null && "2".equals(Grain.substring(0, 1)) ){
                        timeArray.remove(0);
                        timeArray.add(0,beforeDate);
                    }
                    //如果只传入了一个参数 切需要传两个参数 且后一个参数没传 根据时间格式默认填充当前时间
                    if(timeArray.size()==1 && "2".equals(Grain.substring(0, 1)) ){
                        timeArray.add(nowDate);
                    }
                    //此判断代表查询粒度为单个时间
                    if ("1".equals(Grain.substring(0, 1))) {
                        newArr.add(dataFormatCompletForStart(String.valueOf(timeArray.get(0)), timeFormat));
                        newArr.add(dataFormatCompletForEnd(String.valueOf(timeArray.get(0)), timeFormat));
                    } else {
                        newArr.add(dataFormatCompletForStart(String.valueOf(timeArray.get(0)), timeFormat));
                        newArr.add(dataFormatCompletForEnd(String.valueOf(timeArray.get(1)), timeFormat));
                    }
                    res.put(key, newArr);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private static String getDefaultTime(String timeFormat,int type){
        String time="";
        if (timeFormat.indexOf("-") == -1 && timeFormat.indexOf(":") == -1) {
            //获取当前时间
            if(type==1){
                time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            }else{
                time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(0));
            }
        } else {
            if(type==1){
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }else{
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(0));
            }
        }
        time = time.substring(0, timeFormat.length());
        return time;
    }

    /**
     * 开始时间自动补全
     *
     * @param time
     * @param timeFormat
     * @return
     */
    private static String dataFormatCompletForStart(String time, String timeFormat) {
        String Temptime = "19700101 00:00:00";
        if (timeFormat.indexOf("-") == -1 && timeFormat.indexOf(":") == -1) {
            Temptime = "19700101000000";
        } else {
            Temptime = "1970-01-01 00:00:00";
        }
        //如果时间为空 直接根据日期格式返回当前时间 防止查询报错
        if (time == null || !StringUtils.isNotEmpty(time)) {
            return new SimpleDateFormat(timeFormat).format(new Date());
        }
        //如果日志比格式短 则代表日期需要补全
        if (time.length() < timeFormat.length()) {
            int i = timeFormat.length() - time.length();
            String substring = Temptime.substring(time.length(), time.length() + i);
            time += substring;
        }
        return time;
    }

    /**
     * 结束时间自动补全
     *
     * @param time
     * @param timeFormat
     * @return
     */
    private static String dataFormatCompletForEnd(String time, String timeFormat) {
        //目前只支持这两个格式
        StringBuffer Temptime = new StringBuffer();
        if (timeFormat.indexOf("-") == -1 && timeFormat.indexOf(":") == -1) {
            Temptime.append("29991231235959");
        } else {
            Temptime.append("2999-12-31 23:59:59");
        }
        //如果时间为空 直接根据日期格式返回当前时间 防止查询报错
        if (time == null || !StringUtils.isNotEmpty(time)) {
            return new SimpleDateFormat(timeFormat).format(new Date());
        }
        //如果日志比格式短 则代表日期需要补全
        if (time.length() < timeFormat.length()) {
            //当需要补全时间从日开始的时候 需要根据年月进行判断
            if (time.length() == 6) {
                int lastDayOfMonth = getLastDayOfMonth(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(4, 6)));
                Temptime = Temptime.replace(6, 8, lastDayOfMonth + "");
            } else if (time.length() == 7) {
                int lastDayOfMonth = getLastDayOfMonth(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)));
                Temptime = Temptime.replace(8, 10, lastDayOfMonth + "");
            }
            int i = timeFormat.length() - time.length();
            String substring = Temptime.substring(time.length(), time.length() + i);
            time += substring;
        }


        return time;
    }


    //获取年月的最后一天
    public static int getLastDayOfMonth(int year, int month) {
        int date = new Date(year, month, 0).getDate();
        return date;
    }


    public static void main(String[] args) {
        String time="SELECT b.xzqhmc as xzqh, cdd_dxkc, cdd_zxkc, cdd_xxkc, cdd_wxkc\n" +
                "\t, cdd_kc_total, cdd_dxhc, cdd_zxhc, cdd_xxhc, cdd_wxhc\n" +
                "\t, cdd_hc_total, cdd_kc_total + cdd_hc_total AS cdd_total, cdshd_dxkc, cdshd_zxkc\n" +
                "\t, cdshd_xxkc, cdshd_wxkc, cdshd_kc_total, cdshd_dxhc, cdshd_zxhc\n" +
                "\t, cdshd_xxhc, cdshd_wxhc, cdshd_hc_total, cdshd_kc_total + cdshd_hc_total AS cdshd_total\n" +
                "\t, rldc_dxkc, rldc_zxkc, rldc_xxkc, rldc_wxkc, rldc_kc_total\n" +
                "\t, rldc_dxhc, rldc_zxhc, rldc_xxhc, rldc_wxhc, rldc_hc_total\n" +
                "\t, rldc_kc_total + rldc_hc_total AS rldc_total\n" +
                "FROM bcht_bd_dsjmh.app_veh_xnycx_tj_mon a \n" +
                "inner join bcht_bd_dsjmh.bbzx_bmmc b on a.xzqh = b.xzqh \n" +
                "WHERE month_id = ${month} and 1=1 \n" +
                "UNION ALL\n" +
                "SELECT 'ȫʡ' AS xzqh, SUM(a.cdd_dxkc) AS cdd_dxkc, SUM(a.cdd_zxkc) AS cdd_zxkc\n" +
                "\t, SUM(a.cdd_xxkc) AS cdd_xxkc, SUM(a.cdd_wxkc) AS cdd_wxkc\n" +
                "\t, SUM(a.cdd_kc_total) AS cdd_kc_total, SUM(a.cdd_dxhc) AS cdd_dxhc\n" +
                "\t, SUM(a.cdd_zxhc) AS cdd_zxhc, SUM(a.cdd_xxhc) AS cdd_xxhc\n" +
                "\t, SUM(a.cdd_wxhc) AS cdd_wxhc, SUM(a.cdd_hc_total) AS cdd_hc_total\n" +
                "\t, SUM(a.cdd_total) AS cdd_total, SUM(a.cdshd_dxkc) AS cdshd_dxkc\n" +
                "\t, SUM(a.cdshd_zxkc) AS cdshd_zxkc, SUM(a.cdshd_xxkc) AS cdshd_xxkc\n" +
                "\t, SUM(a.cdshd_wxkc) AS cdshd_wxkc, SUM(a.cdshd_kc_total) AS cdshd_kc_total\n" +
                "\t, SUM(a.cdshd_dxhc) AS cdshd_dxhc, SUM(a.cdshd_zxhc) AS cdshd_zxhc\n" +
                "\t, SUM(a.cdshd_xxhc) AS cdshd_xxhc, SUM(a.cdshd_wxhc) AS cdshd_wxhc\n" +
                "\t, SUM(a.cdshd_hc_total) AS cdshd_hc_total, SUM(a.cdshd_total) AS cdshd_total\n" +
                "\t, SUM(a.rldc_dxkc) AS rldc_dxkc, SUM(a.rldc_zxkc) AS rldc_zxkc\n" +
                "\t, SUM(a.rldc_xxkc) AS rldc_xxkc, SUM(a.rldc_wxkc) AS rldc_wxkc\n" +
                "\t, SUM(a.rldc_kc_total) AS rldc_kc_total, SUM(a.rldc_dxhc) AS rldc_dxhc\n" +
                "\t, SUM(a.rldc_zxhc) AS rldc_zxhc, SUM(a.rldc_xxhc) AS rldc_xxhc\n" +
                "\t, SUM(a.rldc_wxhc) AS rldc_wxhc, SUM(a.rldc_hc_total) AS rldc_hc_total\n" +
                "\t, SUM(a.rldc_total) AS rldc_total\n" +
                "FROM (\n" +
                "\tSELECT xzqh, cdd_dxkc, cdd_zxkc, cdd_xxkc, cdd_wxkc\n" +
                "\t\t, cdd_kc_total, cdd_dxhc, cdd_zxhc, cdd_xxhc, cdd_wxhc\n" +
                "\t\t, cdd_hc_total, cdd_kc_total + cdd_hc_total AS cdd_total, cdshd_dxkc, cdshd_zxkc\n" +
                "\t\t, cdshd_xxkc, cdshd_wxkc, cdshd_kc_total, cdshd_dxhc, cdshd_zxhc\n" +
                "\t\t, cdshd_xxhc, cdshd_wxhc, cdshd_hc_total, cdshd_kc_total + cdshd_hc_total AS cdshd_total\n" +
                "\t\t, rldc_dxkc, rldc_zxkc, rldc_xxkc, rldc_wxkc, rldc_kc_total\n" +
                "\t\t, rldc_dxhc, rldc_zxhc, rldc_xxhc, rldc_wxhc, rldc_hc_total\n" +
                "\t\t, rldc_kc_total + rldc_hc_total AS rldc_total\n" +
                "\tFROM bcht_bd_dsjmh.app_veh_xnycx_tj_mon\n" +
                " WHERE month_id = ${month} and 1=1 ) a";
         sql=time;
        toLowerCaseSql();


        /*String  macroKey =  "[\\s(]{0,1}[a-zA-Z0-9._\\-]{0,}\\$\\{[\\s]{0,}" + paramName + "[\\s]{0,}\\}";

        //做剩余未传参的参数进行替换
        Pattern r = Pattern.compile(macroKey);
        // 现在创建 matcher 对象
        Matcher matcher = r.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();

        }*/

       /* List<String> objects = new ArrayList<String>();
        objects.add("111");
        objects.add("211");*/
        List<String> timeArr = new ArrayList<String>();
        timeArr.add("20190201");
        timeArr.add("20190202");
        HashMap<String, Object> map = new HashMap<>();
        //map.put("name", "张三");
        //map.put("id", objects);
        map.put("p_day_id", timeArr);
        map.put("p_day_id$Grain", "13");
        map.put("p_day_id$timeFormat", "YYYYMMDD");
         /*HashMap<String, Object> param11 = new HashMap<>();
        param11.put("paramName", "name");
        param11.put("paramType", "input");;*/


        HashMap<String, Object> param33 = new HashMap<>();
        param33.put("paramName", "p_day_id");
        param33.put("paramType", "time");
        param33.put("attributeValue", "{\"dataFormat\":\"YYYYMMDD\",\"queryGrain\":\"11|21|12|22|13|23|14|24|15|25|16|26\"}");


        List<Map> object22 = new ArrayList<>();

        object22.add(param33);
        SqlUtil.processParms(map);
        String s =  processSql(time, null, null);
        System.out.println(s);
    }
}
