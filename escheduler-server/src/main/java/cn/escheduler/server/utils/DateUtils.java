package cn.escheduler.server.utils;

import cn.escheduler.common.process.Property;
import com.alibaba.fastjson.JSONArray;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述:公共日期工具类
 */
public class DateUtils {


    /**
     * 获取自定义参数系统时间
     *
     *
     * @return
     *
     */
    public static List<Property> getPropTime(List<Property> localParams) {

        if(localParams.size()>0){
            for(int i=0;i<localParams.size();i++){
                Property property = localParams.get(i);

                int n =0;
                if(property.getN()!=null&&!"".equals(property.getN())){
                    n = Integer.parseInt(property.getN());
                }

                String time = property.getTime();
                String value = property.getValue();

                String currentDateTime = DateUtils.getCurrentDateTime(time);
                Date date = DateUtils.stringToDate(currentDateTime, time);

                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                switch(time){
                    case "yyyy-MM-dd":
                        cd.add(Calendar.DAY_OF_MONTH,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    case "yyyyMMdd":
                        cd.add(Calendar.DAY_OF_MONTH,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    case "yyyy-MM":
                        cd.add(Calendar.MONTH,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    case "yyyyMM":
                        cd.add(Calendar.MONTH,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    case "yyyy":
                        cd.add(Calendar.YEAR,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    case "yyyy-MM-dd HH:mm:ss":
                        cd.add(Calendar.SECOND,-n );
                        value=DateUtils.dateToString(cd.getTime(),time);
                        break;
                    default:
                        value="0";
                        break;
                }
                property.setValue(value);
                localParams.set(i,property);
            }
        }
        return localParams;
    }




    /**
     * @param args
     */
    public static void main(String[] args)   {

        String h=" 08:00:00";
        int n=3;






    }


    /**
     * 获取当前日期时间
     *
     *
     * @return
     *
     */
    public static String getCurrentDateTime(String Dateformat) {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(Dateformat);
        datestr = df.format(new Date());
        return datestr;
    }


    /**
     * 将字符串日期转换为日期格式
     * 自定義格式
     *
     * @param datestr
     * @return
     *
     */
    public static Date stringToDate(String datestr, String dateformat) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        try {
            date = df.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 将日期格式日期转换为字符串格式 自定義格式
     *
     * @param date
     * @param dateformat
     * @return
     */
    public static String dateToString(Date date, String dateformat) {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        datestr = df.format(date);
        return datestr;
    }

    /**
     * 获取日期的DAY值
     *
     *
     * @param date
     *      输入日期
     * @return
     *
     */
    public static int getDayOfDate(Date date) {
        int d = 0;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        d = cd.get(Calendar.DAY_OF_MONTH);
        return d;
    }

    /**
     * 获取日期的MONTH值
     *
     *
     * @param date
     *      输入日期
     * @return
     *
     */
    public static int getMonthOfDate(Date date) {
        int m = 0;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        m = cd.get(Calendar.MONTH) + 1;
        return m;
    }

    /**
     * 获取日期的YEAR值
     *
     *
     * @param date
     *      输入日期
     * @return
     *
     */
    public static int getYearOfDate(Date date) {
        int y = 0;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        y = cd.get(Calendar.YEAR);
        return y;
    }

    /**
     * 获取星期几
     *
     *
     * @param date
     *      输入日期
     * @return
     *
     */
    public static int getWeekOfDate(Date date) {
        int wd = 0;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        wd = cd.get(Calendar.DAY_OF_WEEK) - 1;
        return wd;
    }

    /**
     * 获取输入日期的当月第一天
     *
     *
     * @param date
     *      输入日期
     * @return
     *
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.set(Calendar.DAY_OF_MONTH, 1);
        return cd.getTime();
    }

    /**
     * 获得输入日期的当月最后一天
     *
     * @param date
     */
    public static Date getLastDayOfMonth(Date date) {
        return DateUtils.addDay(DateUtils.getFirstDayOfMonth(DateUtils.addMonth(date, 1)), -1);
    }

    /**
     * 根据整型数表示的年月日，生成日期类型格式
     *
     * @param year
     *      年
     * @param month
     *      月
     * @param day
     *      日
     * @return
     *
     */
    public static Date getDateByYMD(int year, int month, int day) {
        Calendar cd = Calendar.getInstance();
        cd.set(year, month-1, day);
        return cd.getTime();
    }


    /**
     * 计算 fromDate 到 toDate 相差多少年
     *
     * @param fromDate
     * @param toDate
     * @return 年数
     *
     */
    public static int getYearByMinusDate(Date fromDate, Date toDate) {
        Calendar df=Calendar.getInstance();
        df.setTime(fromDate);

        Calendar dt=Calendar.getInstance();
        dt.setTime(toDate);

        return dt.get(Calendar.YEAR)-df.get(Calendar.YEAR);
    }

    /**
     * 计算 fromDate 到 toDate 相差多少个月
     *
     * @param fromDate
     * @param toDate
     * @return 月数
     *
     */
    public static int getMonthByMinusDate(Date fromDate, Date toDate) {
        Calendar df=Calendar.getInstance();
        df.setTime(fromDate);

        Calendar dt=Calendar.getInstance();
        dt.setTime(toDate);

        return dt.get(Calendar.YEAR)*12+dt.get(Calendar.MONTH)-
                (df.get(Calendar.YEAR)*12+df.get(Calendar.MONTH));
    }


    /**
     * 在输入日期上增加（+）或减去（-）天数
     *
     * @param date
     *      输入日期
     * @param iday
     *      要增加或减少的天数
     */
    public static Date addDay(Date date, int iday) {
        Calendar cd = Calendar.getInstance();

        cd.setTime(date);

        cd.add(Calendar.DAY_OF_MONTH, iday);

        return cd.getTime();
    }

    /**
     * 在输入日期上增加（+）或减去（-）月份
     *
     * @param date
     *      输入日期
     * @param imonth
     *      要增加或减少的月分数
     */
    public static Date addMonth(Date date, int imonth) {
        Calendar cd = Calendar.getInstance();

        cd.setTime(date);

        cd.add(Calendar.MONTH, imonth);

        return cd.getTime();
    }

    /**
     * 在输入日期上增加（+）或减去（-）年份
     *
     * @param date
     *      输入日期
     * @param iyear
     *      要增加或减少的年数
     */
    public static Date addYear(Date date, int iyear) {
        Calendar cd = Calendar.getInstance();

        cd.setTime(date);

        cd.add(Calendar.YEAR, iyear);

        return cd.getTime();
    }








}