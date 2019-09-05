package com.kaer.more.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

import com.kaer.more.activity.MainActivity;
import com.kaer.more.service.KaerService;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import scifly.device.Device;

public class TimeUtil {
    //时间间隔
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    //private static final long PERIOD_DAY = 10 * 1000;
    private String mState;

    public TimeUtil(String time , String state, Handler handler) {
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute =  Integer.parseInt(times[1]);
        int second =  Integer.parseInt(times[2]);
        this.mState = state;
        long delayTime = 1000;
        Calendar calendar = Calendar.getInstance();

        /*** 定制每日执行方法 ***/
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Date date = calendar.getTime(); //第一次执行定时任务的时间
        System.out.println(date);
        System.out.println("before 开始时间：" + date.toString());
        System.out.println("before 当前时间：" + new Date().toString());
        System.out.println("before 方法比较：" + date.before(new Date()));
        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
        if (date.before(new Date())) {
            delayTime = date.getTime()+ PERIOD_DAY- new Date().getTime();
//            date = this.addDay(date, 1);
//            System.out.println(date);
        }else{
            delayTime = date.getTime() - new Date().getTime();
        }
        System.out.println("before delayTime：" + delayTime);

        final Timer timer = new Timer();

//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                if (mState.equals("1")) {//1设置开机 //1是开 0是关
//                    Device.setProjectorLedPower(1);
//                } else if (mState.equals("2")) {//2设置关闭
//                    Device.setProjectorLedPower(0);
//                }
//                timer.cancel();
//            }
//        };
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        System.out.println("before 最终开始时间：" + date.toString());
        System.out.println("before 延迟时间：" + PERIOD_DAY);
        //timer.schedule(task, date, PERIOD_DAY);
        Message message = new Message();
        message.obj = state;
        message.what = KaerService.TIME_DELAY;
        handler.sendMessageDelayed(message,delayTime);
    }

    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;// 结果
        final long aDayInMillis = 1000 * 60 * 60 * 24;// 一天的全部毫秒数
        final long currentTimeMillis = System.currentTimeMillis();// 当前时间+1分钟，方便触发刷新

        Time now = new Time();// 注意这里导入的时候选择android.text.format.Time类,而不是java.sql.Time类
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis - 1000 * 60);
        endTime.hour = endHour;
        endTime.minute = endMin;

        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;
    }
    public TimeUtil(String time , Handler handler) {
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute =  Integer.parseInt(times[1]);
        int second =  0;
        long delayTime = 1000;
        Calendar calendar = Calendar.getInstance();

        /*** 定制每日执行方法 ***/
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Date date = calendar.getTime(); //第一次执行定时任务的时间
        System.out.println(date);
        System.out.println("operateDevice before 开始时间：" + date.toString());
        System.out.println("operateDevice before 当前时间：" + new Date().toString());
        System.out.println("operateDevice before 方法比较：" + date.before(new Date()));
        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
        if (date.before(new Date())) {
            delayTime = date.getTime()+ PERIOD_DAY- new Date().getTime();
        }else{
            delayTime = date.getTime() - new Date().getTime();
        }
        System.out.println("operateDevice before delayTime：" + delayTime);

        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        System.out.println("operateDevice before 最终开始时间：" + date.toString());
        System.out.println("operateDevice before 延迟时间：" + PERIOD_DAY);
        //timer.schedule(task, date, PERIOD_DAY);
        handler.sendEmptyMessageDelayed(MainActivity.REPEAT_OPT,delayTime);
    }
}
