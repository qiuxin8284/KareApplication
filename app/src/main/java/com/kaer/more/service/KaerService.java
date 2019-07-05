package com.kaer.more.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.kaer.more.utils.TimeUtil;

import scifly.device.Device;

public class KaerService extends Service {

    private static final int CONNECT_REPEAT_TIME = 1000 * 60 * 30;//半小时
    private static final String STATE_OPEN = "O";
    private static final String STATE_CLOSE = "F";
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
        mNoticeDeviceTask = new NoticeDeviceTask();
        mNoticeDeviceTask.execute(STATE_OPEN);
//        mExcpDeviceTask = new ExcpDeviceTask();
//        mExcpDeviceTask.execute();


        //收到广播-处理推送
        mKaerReceiver = new KaerReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KareApplication.ACTION_TUISONG_JSON);
        registerReceiver(mKaerReceiver, intentFilter);
        //main横屏展示
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNoticeDeviceTask = new NoticeDeviceTask();
        mNoticeDeviceTask.execute(STATE_CLOSE);
        unregisterReceiver(mKaerReceiver);
    }

    private KaerReceiver mKaerReceiver;

    public class KaerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KareApplication.ACTION_TUISONG_JSON)) {
                //解析
                String funtion = intent.getStringExtra("funtion");
                if (funtion.equals("1") || funtion.equals("2") || funtion.equals("3") || funtion.equals("4")) {
                    //funtion 1、2、3、4包含state和value
                    String state = intent.getStringExtra("state");
                    String value = intent.getStringExtra("value");
                    if (funtion.equals("1")) {//推送 1：梯形调整
                        int valueInt = Integer.parseInt(value);
                        if (state.equals("1")) {//1设置自动梯形
                            if(valueInt == 0){
                                Device.setAutoKeyStone(true);
                            }else if(valueInt == 1){
                                Device.setAutoKeyStone(false);
                            }
                        } else if (state.equals("2")) {//2设置垂直手动
                            Device.setManualHorizontalKeyStone(valueInt);
                        } else if (state.equals("3")) {//3设置水平手动
                            Device.setManualVerticalKeyStone(valueInt);
                        }
                    } else if (funtion.equals("2")) {//画面转向
                        int valueInt = Integer.parseInt(value);
                        if (state.equals("1")) {//1设置自动投影方位
                            if(valueInt == 0){
                                Device.setAutoProject(KareApplication.mInstance,true);
                            }else if(valueInt == 1){
                                Device.setAutoProject(KareApplication.mInstance,false);
                            }
                        } else if (state.equals("2")) {//2设置投影方位
                            Device.setProjectorDirect(KareApplication.mInstance,valueInt);
                        }
                    } else if (funtion.equals("3")) {//开关光机（直接or定时）//时间以 8：00格式吧
                        //获取定时时间启动个定时器
                        if(!TextUtils.isEmpty(value)){
                           new TimeUtil(value,state);
                        }else {
                            if (state.equals("1")) {//1设置开机 //1是开 0是关
                                Device.setProjectorLedPower(1);
                            } else if (state.equals("2")) {//2设置关闭
                                Device.setProjectorLedPower(0);
                            }
                        }
                    } else if (funtion.equals("4")) {//机器重启
                        if (state.equals("1")) {//1重启

                        } else if (state.equals("2")) {//2时间

                        }
                    }
                } else {
                    if (funtion.equals("5")) {//图像回传
                        mUploadMediaTask = new UploadMediaTask();
                        mUploadMediaTask.execute();
                    } else if (funtion.equals("6")) {//插播广告
                        mGetAdTask = new GetAdTask();
                        mGetAdTask.execute();
                    } else if (funtion.equals("7")) {//上传定位
                        mLocationDeviceTask = new LocationDeviceTask();
                        mLocationDeviceTask.execute();
                    } else if (funtion.equals("8")) {//暂未定义

                    }
                }
            }
        }
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
                    //更新list
                    //发广播
                    Intent kaerIntent = new Intent();
                    kaerIntent.setAction(KareApplication.ACTION_UPDATE_AD);
                    sendBroadcast(kaerIntent);
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
                    mNowLongitude = "0.00";
                    mNowLatitude = "0.00";
                    //发送定时
                    mHandler.sendEmptyMessageDelayed(TIME_ADD, 1000);
                    //地址监听
                    break;
                case GET_AD_FALSE:
                    break;
                case TIME_ADD:
                    mConnectTime = mConnectTime + 1000;
                    if (mConnectTime >= CONNECT_REPEAT_TIME) {
                        mConnectTime = 0;
                        mGetAdTask = new GetAdTask();
                        mGetAdTask.execute();
                    } else {
                        mHandler.sendEmptyMessageDelayed(TIME_ADD, 1000);
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
            String type = params[0];
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
            String longitude = "0.00";
            String latitude = "0.00";
            String address = "中国";
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
