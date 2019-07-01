package com.kaer.more.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.kaer.more.KareApplication;
import com.kaer.more.http.HttpSendJsonManager;
import com.kaer.more.utils.LogUtil;

public class KaerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.println("KaerService onCreate");
        //开启定时服务

        //发送接口
//        mNoticeDeviceTask = new NoticeDeviceTask();
//        mNoticeDeviceTask.execute();
//        mLocationDeviceTask = new LocationDeviceTask();
//        mLocationDeviceTask.execute();
        mExcpDeviceTask = new ExcpDeviceTask();
        mExcpDeviceTask.execute();


        //收到广播-处理推送

        //main横屏展示
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static final int NOTICE_DEVICE_SUCCESS = 1;
    private static final int NOTICE_DEVICE_FALSE = 2;
    private static final int LOCATION_DEVICE_SUCCESS = 3;
    private static final int LOCATION_DEVICE_FALSE = 4;
    private static final int EXCP_DEVICE_SUCCESS = 5;
    private static final int EXCP_DEVICE_FALSE = 6;
    public static final int REPEAT_CONNECT_TIME = 30000;//30s重试
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOTICE_DEVICE_SUCCESS:
                    break;
                case NOTICE_DEVICE_FALSE:
                    break;
                case LOCATION_DEVICE_SUCCESS:
                    break;
                case LOCATION_DEVICE_FALSE:
                    break;
                case EXCP_DEVICE_SUCCESS:
                    break;
                case EXCP_DEVICE_FALSE:
                    break;
            }
        }
    };

    //上传状态
    private NoticeDeviceTask mNoticeDeviceTask;
    private class NoticeDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //读取设备识别号跟常规android系统读取有区别吗？
            String deviceID = "0bebf5bfc9554";
            String type = "O";
            LogUtil.println("noticeDevice type:" + type);
            if(!TextUtils.isEmpty(type)&&!TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.noticeDevice(KareApplication.mInstance,type,deviceID);
                LogUtil.println("noticeDevice flag:" + flag);
                if (flag) {
                    mHandler.sendEmptyMessage(NOTICE_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(NOTICE_DEVICE_FALSE);
                }
            }
            return null;
        }
    }

    //上传定位
    private LocationDeviceTask mLocationDeviceTask;
    private class LocationDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String deviceID = "0bebf5bfc9554";
            String longitude = "100.00";
            String latitude = "100.00";
            LogUtil.println("locationDevice longitude:" + longitude);
            LogUtil.println("locationDevice latitude:" + latitude);
            if(!TextUtils.isEmpty(longitude)&&!TextUtils.isEmpty(latitude)&&!TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.locationDevice(KareApplication.mInstance, longitude, latitude,deviceID);
                LogUtil.println("locationDevice flag:" + flag);
                if (flag) {
                    mHandler.sendEmptyMessage(LOCATION_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(LOCATION_DEVICE_FALSE);
                }
            }
            return null;
        }
    }

    //上传预警
    private ExcpDeviceTask mExcpDeviceTask;
    private class ExcpDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String deviceID = "0bebf5bfc9554";
            String desp = "APP预警";
            String content = "已经长时间没有启动运行了";
            LogUtil.println("excpDevice desp:" + desp);
            LogUtil.println("excpDevice content:" + content);
            if(!TextUtils.isEmpty(desp)&&!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.excpDevice(KareApplication.mInstance,desp,content,deviceID);
                LogUtil.println("excpDevice flag:" + flag);
                if (flag) {
                    mHandler.sendEmptyMessage(EXCP_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(EXCP_DEVICE_FALSE);
                }
            }
            return null;
        }
    }
}
