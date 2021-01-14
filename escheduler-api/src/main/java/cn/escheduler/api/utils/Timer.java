package cn.escheduler.api.utils;

import cn.escheduler.api.service.HomePageService;
import cn.escheduler.dao.mapper.HomePageMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class Timer {

    @Autowired
    private HomePageMapper homePageMapper;

    @Autowired
    private HomePageService homePageService;


    public void timer() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        Date time = calendar.getTime();

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    homePageService.updateBeforHiveSize();
                    homePageService.getHiveTableSize("0");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行

    }
}
