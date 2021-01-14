package cn.escheduler.api.utils;



import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wzp on 2017/6/27.
 */
public class DateUtils {
    public static String dateFormat = "yyyy-MM-dd";
    public static String timeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String timeFormat2 = "yyyyMMddHHmmss";

    public static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATEKEY_FORMAT =
            new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setRequestFactory(new HttpComponentsClientRestfulHttpRequestFactory());
        String url = "http://bd3.bcht:8088/ws/v1/cluster/apps/application_1605682285641_9999";
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String body = responseEntity.getBody();
            JSONObject js=JSONObject.parseObject(body);
            Object app = js.get("app");
            System.out.println(body);
        }catch (Exception e){
            System.out.println("===========");
        }





    }

    public  static Map getKylinTime(String h,int n){
        String currentDateTime = DateUtils.getCurrentDateTime("yyyy-MM-dd");
        Date date = DateUtils.stringToDate(currentDateTime, "yyyy-MM-dd");
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        String e = DateUtils.dateToString(cd.getTime(), "yyyy-MM-dd")+h;
        cd.add(Calendar.DAY_OF_MONTH,-n );
        String s = DateUtils.dateToString(cd.getTime(), "yyyy-MM-dd")+h;
        long timeStart = DateUtils.stringToDate(s, "yyyy-MM-dd HH:mm:ss").getTime();
//        System.out.println("time================================="+timeStart+"================================="+s);
        long timeEnd = DateUtils.stringToDate(e, "yyyy-MM-dd HH:mm:ss").getTime();
//        System.out.println("time================================="+timeEnd+"================================="+e);
        Map map=new HashMap();
        map.put("startTime",timeStart);
        map.put("endTime",timeEnd);
        return map;
    }


    public static Map getLM() {

        String currentDateTime = DateUtils.getCurrentDateTime(dateFormat);
        Date date = DateUtils.stringToDate(currentDateTime, dateFormat);
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);

        cd.add(Calendar.MONTH,-1 );
        cd.set(Calendar.DAY_OF_MONTH,1);
        String s = DateUtils.dateToString(cd.getTime(), dateFormat)+" 00:00:00";
        String s1= DateUtils.dateToString(cd.getTime(), "yyyyMMddHHmmss");
        long timeStart = DateUtils.stringToDate(s, "yyyy-MM-dd HH:mm:ss").getTime();
        cd.add(Calendar.MONTH,1 );
        cd.set(Calendar.DAY_OF_MONTH,0);
        String e = DateUtils.dateToString(cd.getTime(), dateFormat)+" 00:00:00";
        String e1=DateUtils.dateToString(cd.getTime(), "yyyyMMddHHmmss");
        long timeEnd = DateUtils.stringToDate(e, "yyyy-MM-dd HH:mm:ss").getTime();
        Map map=new HashMap();
        map.put("s",timeStart);
        map.put("e",timeEnd);
        map.put("s1",s1);
        map.put("e1",e1);

        return map;
    }

    public static Map getM() {

        String currentDateTime = DateUtils.getCurrentDateTime(dateFormat);
        Date date = DateUtils.stringToDate(currentDateTime, dateFormat);
        Calendar cd = Calendar.getInstance();
        String dd = DateUtils.dateToString(cd.getTime(), "dd");

        Calendar cd1 = Calendar.getInstance();
        String e = DateUtils.dateToString(cd.getTime(), dateFormat);
        System.out.println(e);
        long timeEnd = DateUtils.stringToDate(e+" 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();

        cd1.setTime(date);
        cd1.add(Calendar.DAY_OF_MONTH,-1);
        String e1 = DateUtils.dateToString(cd1.getTime(), "yyyyMMdd")+"000000";

        cd.setTime(date);
        cd.add(Calendar.MONTH,0 );
        cd.set(Calendar.DAY_OF_MONTH,1);
        String s = DateUtils.dateToString(cd.getTime(), dateFormat)+" 00:00:00";
        String s1 = DateUtils.dateToString(cd.getTime(),"yyyyMMdd")+"000000";
        System.out.println(s);

        long timeStart = DateUtils.stringToDate(s, "yyyy-MM-dd HH:mm:ss").getTime();
        Map map=new HashMap();
        map.put("s",timeStart);
        map.put("e",timeEnd);
        map.put("e1",e1);
        map.put("s1",s1);
        map.put("dd",dd);
        return map;
    }

    public static String getLMEnd() {
        return dateFormat;
    }

    public static String getMFirst() {
        return dateFormat;
    }

    /**
     * 时间转字符
     *
     * @param date 时间 yyyy-MM-dd
     * @return
     */
    public static String toDateStr(Date date) {
        return toStr(date, dateFormat);
    }

    /**
     * 时间转字符
     *
     * @param date 时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String toTimeStr(Date date) {
        return toStr(date, timeFormat);
    }

    public static String toTimeStr2(Date date) {
        return toStr(date, timeFormat2);
    }

    /**
     * 时间转字符
     *
     * @param date   时间
     * @param format 时间格式
     * @return
     */
    public static String toStr(Date date, String format) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return "";
    }

    /**
     * 字符转时间
     *
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public static Date toDate(String dateStr) {
        return toDate(dateStr, dateFormat);
    }

    /**
     * 字符转时间
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date toTime(String dateStr) {
        return toDate(dateStr, timeFormat);
    }

    /**
     * 字符转时间
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date toDate(String dateStr, String format) {
        if (StringUtils.isNotEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取用户当前登录时间
     */
    public static Timestamp getlocaltime() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        return time;
    }

    /**
     * 转换为时间戳
     *
     * @param date
     * @return
     */
    public static Timestamp toTimestamp(Date date) {
        Timestamp time = new Timestamp(date.getTime());
        return time;
    }

    /**
     * 获取当前时间
     * 字符串格式
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String DateToString() {
        return toStr(new Date(), timeFormat);
    }

    /**
     * 获取指定时间的当天开始时间
     *
     * @param date 指定时间
     * @return 当天的开始时间
     */
    public static Date getBeginOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);
        return cal.getTime();
    }

    /**
     * 当前日期+/-days后的日期
     *
     * @param days
     * @return
     */
    public static Date getDateByDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 获取本周的结束时间
     */
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取本周的开始时间
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * 判断日期大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    /**
     * 时间段内的天数
     *
     * @param cntDateBeg 开始时间
     * @param cntDateEnd 结束时间
     * @return
     */
    public static Integer getDays(String cntDateBeg, String cntDateEnd) {
        Integer day = 0;
        //拆分成数组
        String[] dateBegs = cntDateBeg.split("-");
        String[] dateEnds = cntDateEnd.split("-");
        //开始时间转换成时间戳
        Calendar start = Calendar.getInstance();
        start.set(Integer.valueOf(dateBegs[0]), Integer.valueOf(dateBegs[1]) - 1, Integer.valueOf(dateBegs[2]));
        Long startTIme = start.getTimeInMillis();
        //结束时间转换成时间戳
        Calendar end = Calendar.getInstance();
        end.set(Integer.valueOf(dateEnds[0]), Integer.valueOf(dateEnds[1]) - 1, Integer.valueOf(dateEnds[2]));
        Long endTime = end.getTimeInMillis();
        //定义一个一天的时间戳时长
        Long oneDay = 1000 * 60 * 60 * 24l;
        Long time = startTIme;
        //循环得出
        while (time <= endTime) {
            day++;
            time += oneDay;
        }
        return day;
    }

    /**
     * 时间段内的周数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getWeeks(String startDate, String endDate) {
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            Date start = toDate(startDate);
            Date end = toDate(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            long time1 = cal.getTimeInMillis();
            cal.setTime(end);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            Double days = Double.parseDouble(String.valueOf(between_days));
            if ((days / 7) > 0 && (days / 7) <= 1) {
                //不满一周的按一周算
                return 1;
            } else if (days / 7 > 1) {
                int day = days.intValue();
                if (day % 7 > 0) {
                    return day / 7 + 1;
                } else {
                    return day / 7;
                }
            } else if ((days / 7) == 0) {
                return 0;
            } else {
                //负数返还null
                return null;
            }
        }
        return null;
    }


    public static String getWeekBegin(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time = sdf.parse(date);
        cal.setTime(time);

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        String getWeekBegin = sdf.format(cal.getTime());
        return getWeekBegin;

    }

    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin  dateEnd
     * @author liuliang
     * 根据参数获取获取前n天对应的时间段
     */
    public static Map getNumDay(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addDays(dateBegin, num));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addDays(dateEnd, num));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param Time yyyy-MM-dd HH:mm
     * @param num
     * @return
     */
    public static String getNumMinute(String Time, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateMinute ="";
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(Time);
             dateMinute = sdf.format(org.apache.commons.lang3.time.DateUtils.addMinutes(dateBegin, num));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateMinute;
    }



    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin  dateEnd
     * @author liuliang
     * 根据参数获取获取前n天对应的时间段
     */
    public static Map getNumDayTwo(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addDays(dateBegin, num));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addDays(dateEnd, num));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin  dateEnd
     * @author liuliang
     * 根据参数获取获取前n周同一天对应的时间段
     */
    public static Map getNumWeekDay(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addWeeks(dateBegin, num));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addWeeks(dateEnd, num));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin    dateEnd
     * @author liuliang
     * 根据参数获取获取前n月同一天对应的时间段
     */
    public static Map getNumMonthDay(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addMonths(dateBegin, num));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addMonths(dateEnd, num));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取某个时间段内所有月份
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetween(String startDate, String endDate) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(startDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(endDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }



    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin    dateEnd
     * @author liuliang
     * 根据参数获取获取前n年同一天对应的时间段
     */
    public static Map getNumYearhDay(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addYears(dateBegin, num));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addYears(dateEnd, num));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param startTime yyyy-MM-dd hh:mm:ss
     * @param endTime   yyyy-MM-dd hh:mm:ss
     * @param num
     * @return dateBegin    dateEnd
     * @author liuliang
     * 根据参数获取获取前n季度同一天对应的时间段
     */
    public static Map getNumJdDay(String startTime, String endTime, int num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        try {
            Date dateBegin = sdf.parse(startTime);
            Date dateEnd = sdf.parse(endTime);
            String dateBegin1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addMonths(dateBegin, num * 3));
            String dateend1 = sdf.format(org.apache.commons.lang3.time.DateUtils.addMonths(dateEnd, num * 3));
            map.put("dateBegin", dateBegin1);
            map.put("dateEnd", dateend1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @author wuyanfei
     * @date 2019-09-23
     * 获取一年前的同一天
     */
    public static String getOneYearAgo(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, -1);
            Date after = calendar.getTime();
            return sdf.format(after);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @author wuyanfei
     * @date 2019-09-23
     * 获取一个季度前的同一天
     */
    public static String getOneQuarterAgo(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -3);
            Date after = calendar.getTime();
            return sdf.format(after);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @author wuyanfei
     * @date 2019-09-23
     * 获取一个月前的同一天
     */
    public static String getOneMonthAgo(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);
            Date after = calendar.getTime();
            return sdf.format(after);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @author wuyanfei
     * @date 2019-09-23
     * 获取一周前的同一天
     */
    public static String getOneWeekAgo(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            Date after = calendar.getTime();
            return sdf.format(after);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @author wuyanfei
     * @date 2019-09-23
     * 获取前一天
     */
    public static String getOneDayAgo(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date after = calendar.getTime();
            return sdf.format(after);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    /**
     * @author wuyanfei
     * @date 2019-09-23
     * <p>
     * 获取不同时间粒度下的起始时间
     */
    public static String getStartTimeOfDay(String yearMonthDay) {
        return yearMonthDay + " 00:00:00";
    }

    public static String getEndTimeOfDay(String yearMonthDay) {
        return yearMonthDay + " 23:59:59";
    }

    public static String getStartTimeOfWeek(String yearWeek) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearWeek.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(params[1]));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return sdf.format(cal.getTime()) + " 00:00:00";
    }

    public static String getEndTimeOfWeek(String yearWeek) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearWeek.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(params[1]) + 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return sdf.format(cal.getTime()) + " 23:59:59";
    }

    /**
     * @Param 2020-01
     * @return一个月开始的时间
     **/

    public static String getStartTimeOfMonth(String yearMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearMonth.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(params[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(cal.getTime()) + " 00:00:00";
    }

    public static String getEndTimeOfMonth(String yearMonth) {
        SimpleDateFormat ss = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearMonth.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(params[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DATE, -1);
        return ss.format(cal.getTime()) + " 23:59:59";
    }

    public static String getStartTimeOfQuarter(String yearnQuarter) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearnQuarter.split("-");
        if (params.length < 2) {
            throw new RuntimeException("参数不正确");
        }
        int quarter = Integer.parseInt(params[1]);
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if (quarter == 1) {
            cal.set(Calendar.MONTH, Calendar.JANUARY);
        } else if (quarter == 2) {
            cal.set(Calendar.MONTH, Calendar.APRIL);
        } else if (quarter == 3) {
            cal.set(Calendar.MONTH, Calendar.JULY);
        } else if (quarter == 4) {
            cal.set(Calendar.MONTH, Calendar.OCTOBER);
        }
        return sdf.format(cal.getTime()) + " 00:00:00";
    }

    public static String getEndTimeOfQuarter(String yearnQuarter) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        String[] params = yearnQuarter.split("-");
        int quarter = Integer.parseInt(params[1]);
        cal.set(Calendar.YEAR, Integer.parseInt(params[0]));
        if (quarter == 1) {
            cal.set(Calendar.MONTH, Calendar.MARCH);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, num);
        } else if (quarter == 2) {
            cal.set(Calendar.MONTH, Calendar.JUNE);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, num);
        } else if (quarter == 3) {
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, num);
        } else if (quarter == 4) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, num);
        }
        return sdf.format(cal.getTime()) + " 23:59:59";
    }

    public static String getStartTimeOfYear(String year) {
        return year + "-01-01 00:00:00";
    }

    public static String getEndTimeOfYear(String year) {
        return year + "-12-31 23:59:59";
    }

    public static String fixToTwoDigitStr(Integer param) {
        if (param < 10) {
            return "0" + param;
        } else {
            return param + "";
        }
    }

    /**
     * @Author liuliang
     * 获取某一个周对应的开始日期和结束日期
     */

    public static Map getBeginAndEndTimeOfWeek(int year, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        int dayOfWeek = 7 - calendar.get(Calendar.DAY_OF_WEEK) + 2;
        week = week - 2;//周数减去第一周再减去要得到的周
        calendar.add(Calendar.DAY_OF_YEAR, week * 7 + dayOfWeek);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //周一

        String dateStart = sf.format(calendar.getTime());
        //System.out.println(dateStart);
        calendar.add(Calendar.DAY_OF_YEAR, 6);

        //周日
        String dateEnd = sf.format(calendar.getTime());
        //System.out.println(dateEnd);

        Map<String, String> map = new HashMap<>();
        map.put("startDate", dateStart);
        map.put("dateEnd", dateEnd);
        return map;
    }

    public static int getDayNumBetween(Date date1, Date date2) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //不在同一年
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {//同一年
            System.out.println("判断day2 - day1 : " + (day2 - day1));
            System.out.println(day2 - day1);
            return day2 - day1;
        }
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
     * 将字符串日期转换为日期格式
     * 自定義格式
     *
     * @param datestr
     * @return
     *
     */
    public static Date stringToDateUTC(String datestr, String dateformat) {
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
    public static String dateToStringUTC(Date date, String dateformat) {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        datestr = df.format(date);
        return datestr;
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
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        datestr = df.format(new Date());
        return datestr;
    }

    /**
     * 获取当前日期时间
     *
     *
     * @return
     *
     */
    public static String getCurrentDateTimeUTC(String Dateformat) {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(Dateformat);
        datestr = df.format(new Date());
        return datestr;
    }



    /**
     * String格式日期加1秒（分钟或小时）
     *
     * @param str
     * @param n   秒
     * @return
     */
    public static String getAddTime(String str, Integer n) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setTime(date.getTime() + n * 1000);
        return df.format(date);

    }


    /**
     * 获取当前是本年第几周
     *
     * @return int
     * @Author liuliang
     * @Description
     * @Date 获取当前是本年第几周
     * @Param [str, n]
     **/
    public static int getNowWeekOfYear() {
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.WEEK_OF_YEAR);
        return i;
    }

    /**
     * 判断一个时间是否在另一个时间之前个时间是否在另一个时间之前
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean before(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);

            if (dateTime1.before(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(time1);
            System.out.println(time2);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断一个时间是否在另一个时间之后
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean after(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);

            if (dateTime1.after(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算时间差值（单位为秒）
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return 差值
     */
    public static int minus(String time1, String time2) {
        try {
            Date datetime1 = TIME_FORMAT.parse(time1);
            Date datetime2 = TIME_FORMAT.parse(time2);

            long millisecond = datetime1.getTime() - datetime2.getTime();

            return Integer.valueOf(String.valueOf(millisecond / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取年月日和小时
     *
     * @param datetime 时间（yyyy-MM-dd HH:mm:ss）
     * @return 结果（yyyy-MM-dd_HH）
     */
    public static String getDateHour(String datetime) {
        String date = datetime.split(" ")[0];
        String hourMinuteSecond = datetime.split(" ")[1];
        String hour = hourMinuteSecond.split(":")[0];
        return date + "_" + hour;
    }

    /**
     * 获取当天日期（yyyy-MM-dd）
     *
     * @return 当天日期
     */
    public static String getTodayDate() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取当天日期（yyyy-MM-dd HH:mm:ss）
     *
     * @return 当天时间
     */
    public static String getTodayTime() {
        return TIME_FORMAT.format(new Date());
    }

    /**
     * 获取昨天的日期（yyyy-MM-dd）
     *
     * @return 昨天的日期
     */
    public static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);

        Date date = cal.getTime();

        return DATE_FORMAT.format(date);
    }

    /**
     * 获取明天的日期（yyyy-MM-dd）
     *
     * @return 明天的日期
     */
    public static String getMingday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);

        Date date = cal.getTime();

        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化日期（yyyy-MM-dd）
     *
     * @param date Date对象
     * @return 格式化后的日期
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param date Date对象
     * @return 格式化后的时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * 解析时间字符串
     *
     * @param time 时间字符串
     * @return Date
     */
    public static Date parseTime(String time) {
        try {
            return TIME_FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化日期key
     *
     * @param date
     * @return
     */
    public static String formatDateKey(Date date) {
        return DATEKEY_FORMAT.format(date);
    }

    /**
     * 格式化日期key
     *
     * @param datekey
     * @return
     */
    public static Date parseDateKey(String datekey) {
        try {
            return DATEKEY_FORMAT.parse(datekey);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间，保留到分钟级别
     * yyyyMMddHHmm
     *
     * @param date
     * @return
     */
    public static String formatTimeMinute(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return sdf.format(date);
    }

    /**
     * 格式化时间，保留到M秒级别
     * yyyyMMddHHmm
     *
     * @param date
     * @return
     */

    public static String formatTimeMillis(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    /**
     * 获取给定日期的前一分钟
     */
    public static String getYestoMinute(String dateMin) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        Date parse = null;
        try {
            parse = sdf.parse(dateMin);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        cal.add(Calendar.MINUTE, -1); // 设置为前一分钟
        Date time = cal.getTime();
        String format = sdf.format(time);
        return format;
    }

    /**
     * 获取给定日期的前一小时
     */
    public static String getYestoHour(String dateMin) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        Date parse = null;
        try {
            parse = sdf.parse(dateMin);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        cal.add(Calendar.HOUR, -1); // 设置为前一分钟
        Date time = cal.getTime();
        String format = sdf.format(time);
        return format;
    }

    public static String getRangeTime(String dateTime) {
        //2018-09-13 18:02:57  dateTime
        String date = dateTime.split(" ")[0];
        String hour = dateTime.split(" ")[1].split(":")[0];

        int minute = StringUtils.convertStringtoInt(dateTime.split(" ")[1].split(":")[1]);

//		 String second = dateTime.split(" ")[1].split(":")[2];
        if (minute + (5 - minute % 5) == 60) {
            return date + " " + hour + ":" + StringUtils.fulfuill((minute - (minute % 5)) + "") + "~" + date + " " + StringUtils.fulfuill((Integer.parseInt(hour) + 1) + "") + ":00";
        }
        return date + " " + hour + ":" + StringUtils.fulfuill((minute - (minute % 5)) + "") + "~" + date + " " + hour + ":" + StringUtils.fulfuill((minute + (5 - minute % 5)) + "");
    }

    /**
     * 获取给定时间段内所有天数
     */
    public static List<String> findDaysStr(String begintTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(begintTime);
            dEnd = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> daysStrList = new ArrayList<String>();
        daysStrList.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (dEnd.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            String dayStr = sdf.format(calBegin.getTime());
            daysStrList.add(dayStr);
        }
        return daysStrList;
    }





}
