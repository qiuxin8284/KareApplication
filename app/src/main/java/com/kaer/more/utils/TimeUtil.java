package com.kaer.more.utils;

import android.os.AsyncTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import scifly.device.Device;

public class TimeUtil {
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public static void setAutoTime(String time,String state) {

        Calendar calendar = Calendar.getInstance();

        /*** 定制每日2:00执行方法 ***/

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date date = calendar.getTime(); //第一次执行定时任务的时间
        System.out.println(date);
        System.out.println("before 方法比较：" + date.before(new Date()));
        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
        if (date.before(new Date())) {
            date = addDay(date, 1);
            System.out.println(date);
        }

        Timer timer = new Timer();

//        mSetProjectorLedPowerTask = new SetProjectorLedPowerTask();
//        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
//        timer.schedule(task, date, PERIOD_DAY);
    }

//    private String state = "";
//    private SetProjectorLedPowerTask mSetProjectorLedPowerTask;
//
//    private static class SetProjectorLedPowerTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... params) {
//            if (state.equals("1")) {//1设置开机 //1是开 0是关
//                Device.setProjectorLedPower(1);
//            } else if (state.equals("2")) {//2设置关闭
//                Device.setProjectorLedPower(0);
//            }
//            return null;
//        }
//    }

    // 增加或减少天数
    private static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

}
