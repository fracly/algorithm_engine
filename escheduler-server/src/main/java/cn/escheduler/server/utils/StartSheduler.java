package cn.escheduler.server.utils;

import cn.escheduler.api.quartz.QuartzExecutors;
import org.quartz.SchedulerException;

public class StartSheduler {
    public static void main(String[] args) throws SchedulerException {
        QuartzExecutors.getInstance().start();

    }
}
