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
import com.kaer.more.entitiy.UploadData;
import com.kaer.more.http.HttpSendJsonManager;
import com.kaer.more.utils.LogUtil;

public class KaerService extends Service {

    private static final int CONNECT_REPEAT_TIME = 1000 * 60 * 30;//半小时
    private int mConnectTime = 0;
    private String mNowLongitude = "";
    private String mNowLatitude = "";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.println("KaerService onCreate");
        //开启定时服务
//        //第一次触发广告
//        mGetAdTask = new GetAdTask();
//        mGetAdTask.execute();

        //发送接口
//        mNoticeDeviceTask = new NoticeDeviceTask();
//        mNoticeDeviceTask.execute();
//        mLocationDeviceTask = new LocationDeviceTask();
//        mLocationDeviceTask.execute();
//        mExcpDeviceTask = new ExcpDeviceTask();
//        mExcpDeviceTask.execute();
//        mUploadMediaTask = new UploadMediaTask();
//        mUploadMediaTask.execute();


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

    private static final int IMG_DEVICE_SUCCESS = 7;
    private static final int IMG_DEVICE_FALSE = 8;
    private static final int UPLOAD_IMG_SUCCESS = 9;
    private static final int UPLOAD_IMG_FALSE = 10;
    private static final int GET_AD_SUCCESS = 11;
    private static final int GET_AD_FALSE = 12;
    private static final int TIME_ADD = 13;
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
                case IMG_DEVICE_SUCCESS:
                    break;
                case IMG_DEVICE_FALSE:
                    break;
                case UPLOAD_IMG_SUCCESS:
                    if (uploadData != null && TextUtils.isEmpty(uploadData.getUrl())) {
                        mImgDeviceTask = new ImgDeviceTask();
                        mImgDeviceTask.execute(uploadData.getUrl());
                    }
                    break;
                case UPLOAD_IMG_FALSE:
                    break;
                case GET_AD_SUCCESS:
                    //记录当前地理位置&&记得请求成功的时间开启定时器清0
                    mConnectTime = 0;
                    mNowLongitude = "100.00";
                    mNowLatitude = "100.00";
                    //发送定时
                    mHandler.sendEmptyMessageDelayed(TIME_ADD,1000);
                    //地址监听
                    break;
                case GET_AD_FALSE:
                    break;
                case TIME_ADD:
                    mConnectTime = mConnectTime + 1000;
                    if(mConnectTime>=CONNECT_REPEAT_TIME){
                        mConnectTime = 0;
                        mGetAdTask = new GetAdTask();
                        mGetAdTask.execute();
                    }else{
                        mHandler.sendEmptyMessageDelayed(TIME_ADD,1000);
                    }
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
            if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.noticeDevice(KareApplication.mInstance, type, deviceID);
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
            String address = "深圳市南山区";
            LogUtil.println("locationDevice longitude:" + longitude);
            LogUtil.println("locationDevice latitude:" + latitude);
            LogUtil.println("locationDevice address:" + address);
            if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(deviceID) && !TextUtils.isEmpty(address)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.locationDevice(KareApplication.mInstance, longitude, latitude, deviceID, address);
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
            if (!TextUtils.isEmpty(desp) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.excpDevice(KareApplication.mInstance, desp, content, deviceID);
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

    //图像回传
    private ImgDeviceTask mImgDeviceTask;

    private class ImgDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String deviceID = "0bebf5bfc9554";
            String img = params[0];
            LogUtil.println("imgDevice img:" + img);
            if (!TextUtils.isEmpty(img) && !TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.imgDevice(KareApplication.mInstance, img, deviceID);
                LogUtil.println("imgDevice flag:" + flag);
                if (flag) {
                    mHandler.sendEmptyMessage(IMG_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(IMG_DEVICE_FALSE);
                }
            }
            return null;
        }
    }

    //上传图片
    private UploadMediaTask mUploadMediaTask;
    private UploadData uploadData;

    private class UploadMediaTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            int type = 0;
            String name = "";
            String file = "";
            String time = "";
            LogUtil.println("uploadMedia type:" + type);
            LogUtil.println("uploadMedia name:" + name);
            LogUtil.println("uploadMedia file:" + file);
            LogUtil.println("uploadMedia time:" + time);
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(file) && !TextUtils.isEmpty(time)) {
                //上传检测是否存在这个设备号
                uploadData = HttpSendJsonManager.uploadMedia(KareApplication.mInstance, type, name, file, time);
                LogUtil.println("excpDevice flag:" + uploadData.isOK());
                if (uploadData.isOK()) {
                    mHandler.sendEmptyMessage(UPLOAD_IMG_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(UPLOAD_IMG_FALSE);
                }
            }
            return null;
        }
    }

    private GetAdTask mGetAdTask;

    private class GetAdTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mHandler.sendEmptyMessage(IMG_DEVICE_SUCCESS);
            return null;
        }
    }
}
