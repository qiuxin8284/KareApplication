package com.kaer.more.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.jordan.httplibrary.utils.CommonUtils;
import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.entitiy.AdRemarkData;
import com.kaer.more.entitiy.AdvertisementListData;
import com.kaer.more.entitiy.UploadData;
import com.kaer.more.http.HttpSendJsonManager;
import com.kaer.more.utils.LogUtil;
import com.kaer.more.utils.TimeUtil;
import com.kaer.more.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import scifly.device.Device;
import scifly.util.LogUtils;

public class KaerService extends Service {

    //private static final int CONNECT_REPEAT_TIME = 1000 * 60 * 30;//半小时
    private static final int CONNECT_REPEAT_TIME = 1000 * 90;//1.5分钟
    private static final String STATE_OPEN = "O";
    private static final String STATE_CLOSE = "F";
    private int mConnectTime = 0;
//    private String mNowLongitude = "";
//    private String mNowLatitude = "";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.println("KaerService onCreate");
        //开启定时服务
//        //第一次触发广告
        LogUtil.println("adSearch onCreate mGetFristAdTask");
        mGetFristAdTask = new GetFristAdTask();
        mGetFristAdTask.execute();

        //发送接口
//        mNoticeDeviceTask = new NoticeDeviceTask();
//        mNoticeDeviceTask.execute(STATE_OPEN);
//        mExcpDeviceTask = new ExcpDeviceTask();
//        mExcpDeviceTask.execute();


        //收到广播-处理推送
        mKaerReceiver = new KaerReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KareApplication.ACTION_TUISONG_JSON);
        intentFilter.addAction(KareApplication.ACTION_IMAGE_UPLOAD_SUCESS);
        registerReceiver(mKaerReceiver, intentFilter);
        //main横屏展示
        LogUtil.println("NewTest 开启检查服务");
//        timerMail.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                LogUtil.println("NewTest 发送消息");
//                mHandler.sendEmptyMessage(APP_DEAD);
//            }
//        }, 15000, 5000);
//        super.onCreate();

        mHandler.sendEmptyMessageDelayed(DEVICE_UPLOAD,DEVICE_UPLOAD_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logout();
//        if (timerMail!= null){
//            timerMail.cancel();
//        }
//        LogUtil.println("NewTest 销毁服务");
        unregisterReceiver(mKaerReceiver);
    }

    private KaerReceiver mKaerReceiver;

    public class KaerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("KaerReceiver", "KaerReceiver");
            String action = intent.getAction();
            if (action.equals(KareApplication.ACTION_TUISONG_JSON)) {
                //解析
                String function = intent.getStringExtra("function");
                try {
                    if (function.equals("1") || function.equals("2") || function.equals("3") || function.equals("4")) {
                        //function 1、2、3、4包含state和value
                        String state = intent.getStringExtra("state");
                        String value = intent.getStringExtra("value");
                        if (function.equals("1")) {//推送 1：梯形调整
                            LogUtil.i("KaerReceiver", "梯形调整");
                            int valueInt = Integer.parseInt(value);
                            if (state.equals("1")) {//1设置自动梯形
                                LogUtil.i("KaerReceiver", "1设置自动梯形");
                                if (valueInt == 0) {
                                    LogUtil.i("KaerReceiver", "setAutoKeyStone true");
                                    Device.setAutoKeyStone(true);
                                } else if (valueInt == 1) {
                                    Device.setAutoKeyStone(false);
                                }
                            } else if (state.equals("2")) {//2设置垂直手动
                                LogUtil.i("KaerReceiver", "2设置垂直手动");
                                Device.setManualHorizontalKeyStone(valueInt);
                            } else if (state.equals("3")) {//3设置水平手动
                                LogUtil.i("KaerReceiver", "3设置水平手动");
                                Device.setManualVerticalKeyStone(valueInt);
                            }
                        } else if (function.equals("2")) {//画面转向
                            LogUtil.i("KaerReceiver", "画面转向");
                            int valueInt = Integer.parseInt(value);
                            if (state.equals("1")) {//1设置自动投影方位
                                //缺少android.permission.WRITE_SETTINGS.
                                if (valueInt == 0) {
                                    LogUtil.i("KaerReceiver", "setAutoProject 0");
                                    Device.setAutoProject(KareApplication.mInstance, true);
                                } else if (valueInt == 1) {
                                    Device.setAutoProject(KareApplication.mInstance, false);
                                }
                            } else if (state.equals("2")) {//2设置投影方位
                                LogUtil.i("KaerReceiver", "2设置投影方位");
                                //缺少android.permission.WRITE_SETTINGS.
                                Device.setProjectorDirect(KareApplication.mInstance, valueInt);
                            }
                        } else if (function.equals("3")) {//开关光机（直接or定时）//时间以 8：00格式吧
                            LogUtil.i("KaerReceiver", "开关光机");
                            //获取定时时间启动个定时器
                            if (!TextUtils.isEmpty(value)) {
                                new TimeUtil(value, state, mHandler);
                            } else {
                                if (state.equals("1")) {//1设置开机 //1是开 0是关
                                    LogUtil.i("KaerReceiver", "setProjectorLedPower 1");
                                    Device.setProjectorLedPower(1);
                                } else if (state.equals("2")) {//2设置关闭
                                    Device.setProjectorLedPower(0);
                                }
                            }
                        } else if (function.equals("4")) {//机器重启
                            LogUtil.i("KaerReceiver", "机器重启");
                            if (state.equals("1")) {//1重启
                                LogUtil.i("KaerReceiver", "机器重启 1");

                            } else if (state.equals("2")) {//2时间

                            }
                        }
                    } else {
                        if (function.equals("5")) {//图像回传
                            LogUtil.i("KaerReceiver", "图像回传");
                            Intent kaerIntent = new Intent();
                            kaerIntent.setAction(KareApplication.ACTION_IMAGE_UPLOAD);
                            sendBroadcast(kaerIntent);
                        } else if (function.equals("6")) {//插播广告
                            LogUtil.i("KaerReceiver", "插播广告");
                            mGetAdTask = new GetAdTask();
                            mGetAdTask.execute();
                        } else if (function.equals("7")) {//上传定位
                            LogUtil.i("KaerReceiver", "上传定位");
                            mLocationDeviceTask = new LocationDeviceTask();
                            mLocationDeviceTask.execute();
                        } else if (function.equals("8")) {//暂未定义
                            LogUtil.i("KaerReceiver", "暂未定义");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals(KareApplication.ACTION_IMAGE_UPLOAD_SUCESS)) {
                mUploadMediaTask = new UploadMediaTask();
                mUploadMediaTask.execute();
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
    private static final int GET_AD_FRIST_SUCCESS = 13;
    private static final int GET_AD_FRIST_FALSE = 14;
    private static final int TIME_ADD = 15;
    public static final int TIME_DELAY = 16;
    public static final int APP_DEAD = 17;
    public static final int DEVICE_UPLOAD = 18;
    public static final int DEVICE_UPLOAD_TIME = 2*60*1000;//30s重试
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
                case GET_AD_FRIST_SUCCESS:
                    //记录当前地理位置&&记得请求成功的时间开启定时器清0
                    mConnectTime = 0;
//                    mNowLongitude = "0.00";
//                    mNowLatitude = "0.00";
                    //发送定时
                    mHandler.sendEmptyMessageDelayed(TIME_ADD, 1000);
                    ToastUtils.shortToast(KaerService.this,"收到广播更新");
                    //地址监听
                    break;
                case GET_AD_FRIST_FALSE:
                    mGetFristAdTask = new GetFristAdTask();
                    mGetFristAdTask.execute();
                    break;
                case GET_AD_SUCCESS:
                    //记录当前地理位置&&记得请求成功的时间开启定时器清0
                    mConnectTime = 0;
//                    mNowLongitude = "0.00";
//                    mNowLatitude = "0.00";
                    //发送定时
                    //mHandler.sendEmptyMessageDelayed(TIME_ADD, 1000);
                    ToastUtils.shortToast(KaerService.this,"收到广播更新");
                    //地址监听
                    break;
                case GET_AD_FALSE:
                    mGetAdTask = new GetAdTask();
                    mGetAdTask.execute();
                    break;
                case TIME_ADD:
                    LogUtil.println("adSearch TIME_ADD mConnectTime:" + mConnectTime + "|CONNECT_REPEAT_TIME:" + CONNECT_REPEAT_TIME);
                    mConnectTime = mConnectTime + 1000;
                    if (mConnectTime >= CONNECT_REPEAT_TIME) {
                        mConnectTime = 0;
                        mGetFristAdTask = new GetFristAdTask();
                        mGetFristAdTask.execute();
                    } else {
                        mHandler.sendEmptyMessageDelayed(TIME_ADD, 1000);
                    }
                    break;
                case TIME_DELAY:
                    System.out.println("before TIME_DELAY：");
                    String state = (String) msg.obj;
                    System.out.println("before TIME_DELAY state：" + state);
                    if (state.equals("1")) {//1设置开机 //1是开 0是关
                        Device.setProjectorLedPower(1);
                    } else if (state.equals("2")) {//2设置关闭
                        Device.setProjectorLedPower(0);
                    }
                    break;
                case APP_DEAD:
                    try {
                        if(!isBackgroundRunning()){
                            LogUtil.println("NewTest  退出操作");
                            logout();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DEVICE_UPLOAD:
                    LogUtil.println("deviceUpload DEVICE_UPLOAD");
                    mDeviceUploadTask = new DeviceUploadTask();
                    mDeviceUploadTask.execute();
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
            String deviceID = KareApplication.default_imei;
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
            String deviceID = KareApplication.default_imei;
            String address = "中国";
            LogUtil.println("locationDevice longitude:" + KareApplication.mLongitude);
            LogUtil.println("locationDevice latitude:" + KareApplication.mLatitude);
            LogUtil.println("locationDevice address:" + address);
            if (!TextUtils.isEmpty(KareApplication.mLongitude) && !TextUtils.isEmpty(KareApplication.mLatitude) && !TextUtils.isEmpty(deviceID) && !TextUtils.isEmpty(address)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.locationDevice(KareApplication.mInstance, KareApplication.mLongitude, KareApplication.mLatitude, deviceID, address);
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
            String deviceID = KareApplication.default_imei;
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
            String deviceID = KareApplication.default_imei;
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
            String name = "kaer.jpg";
            String file = CommonUtils.encodeToBase64(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + name);
            String time = "0";
            LogUtil.println("uploadMedia type:" + type);
            LogUtil.println("uploadMedia name:" + name);
            LogUtil.println("uploadMedia file:" + file);
            LogUtil.println("uploadMedia time:" + time);
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(file) && !TextUtils.isEmpty(time)) {
                //上传检测是否存在这个设备号
                uploadData = HttpSendJsonManager.uploadMedia(KareApplication.mInstance, type, name, file, time);
                LogUtil.println("UploadMediaTask flag:" + uploadData.isOK());
                if (uploadData.isOK()) {
                    mHandler.sendEmptyMessage(UPLOAD_IMG_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(UPLOAD_IMG_FALSE);
                }
            }
            return null;
        }
    }

    private GetFristAdTask mGetFristAdTask;

    private class GetFristAdTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            LogUtil.println("adSearch mGetAdTask KareApplication.mGetAd："+KareApplication.mGetAd);
            if(KareApplication.mGetAd) {
                String deviceID = KareApplication.default_imei;
                ArrayList<AdRemarkData> list = new ArrayList<AdRemarkData>();
                for (String key : KareApplication.mAdRemarkMap.keySet()) {
                    AdRemarkData adRemarkData = KareApplication.mAdRemarkMap.get(key);
                    list.add(adRemarkData);
                }
                LogUtil.println("adSearch GetFristAdTask list:" + list.toString());
                AdvertisementListData advertisementListData = HttpSendJsonManager.adSearch(KareApplication.mInstance, deviceID, KareApplication.mLongitude, KareApplication.mLatitude, list);
                LogUtil.println("adSearch GetFristAdTask advertisementListData:" + advertisementListData.toString());
                //如果失败重新获取
                //如果成功那么推送刷新广告
                if (advertisementListData.isOK()) {
                    LogUtil.println("adSearch GetFristAdTask GET_AD_FRIST_SUCCESS");
                    KareApplication.mAdvertisementList = advertisementListData.getAdList();
                    KareApplication.mAdRemarkMap = new HashMap<String, AdRemarkData>();
                    Intent intent = new Intent();
                    intent.setAction(KareApplication.ACTION_UPDATE_AD);
                    sendBroadcast(intent);
                    mHandler.sendEmptyMessage(GET_AD_FRIST_SUCCESS);
                } else {
                    LogUtil.println("adSearch GetFristAdTask GET_AD_FRIST_FALSE");
                    mHandler.sendEmptyMessageDelayed(GET_AD_FRIST_FALSE, 30000);
                }
            }else{
                mHandler.sendEmptyMessageDelayed(GET_AD_FRIST_FALSE, 30000);
            }
            return null;
        }
    }

    private GetAdTask mGetAdTask;

    private class GetAdTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            LogUtil.println("adSearch mGetAdTask KareApplication.mGetAd："+KareApplication.mGetAd);
            if(KareApplication.mGetAd) {
                String deviceID = KareApplication.default_imei;
                ArrayList<AdRemarkData> list = new ArrayList<AdRemarkData>();
                for (String key : KareApplication.mAdRemarkMap.keySet()) {
                    AdRemarkData adRemarkData = KareApplication.mAdRemarkMap.get(key);
                    list.add(adRemarkData);
                }
                LogUtil.println("adSearch GetAdTask list:" + list.toString());
                AdvertisementListData advertisementListData = HttpSendJsonManager.adSearch(KareApplication.mInstance, deviceID, KareApplication.mLongitude, KareApplication.mLatitude, list);
                LogUtil.println("adSearch GetAdTask advertisementListData:" + advertisementListData.toString());
                //如果失败重新获取
                //如果成功那么推送刷新广告
                if (advertisementListData.isOK()) {
                    LogUtil.println("adSearch GetAdTask GET_AD_SUCCESS");
                    KareApplication.mAdvertisementList = advertisementListData.getAdList();
                    KareApplication.mAdRemarkMap = new HashMap<String, AdRemarkData>();
                    Intent intent = new Intent();
                    intent.setAction(KareApplication.ACTION_UPDATE_AD);
                    sendBroadcast(intent);
                    mHandler.sendEmptyMessage(GET_AD_SUCCESS);
                } else {
                    LogUtil.println("adSearch GetAdTask GET_AD_FALSE");
                    mHandler.sendEmptyMessageDelayed(GET_AD_FALSE, 30000);
                }
            }else{
                mHandler.sendEmptyMessageDelayed(GET_AD_FALSE, 30000);
            }
            return null;
        }
    }

    private DeviceUploadTask mDeviceUploadTask;

    private class DeviceUploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String deviceID = KareApplication.default_imei;
            LogUtil.println("deviceUpload doInBackground");

            ArrayList<AdRemarkData> list = new ArrayList<AdRemarkData>();
            LogUtil.println("deviceUpload KareApplication.mAdRemarkMap.size()："+KareApplication.mAdRemarkMap.size());
            if(KareApplication.mAdRemarkMap!=null&&KareApplication.mAdRemarkMap.size()!=0) {
                for (String key : KareApplication.mAdRemarkMap.keySet()) {
                    AdRemarkData adRemarkData = KareApplication.mAdRemarkMap.get(key);
                    list.add(adRemarkData);
                }
                LogUtil.println("deviceUpload list:" + list.toString());
                LogUtil.println("deviceUpload deviceID:" + deviceID);
                if (HttpSendJsonManager.deviceUpload(KareApplication.mInstance, deviceID, list)) {
                    LogUtil.println("deviceUpload OK");
                    KareApplication.mAdRemarkMap = new HashMap<String, AdRemarkData>();
                }
            }
            mHandler.sendEmptyMessageDelayed(DEVICE_UPLOAD, DEVICE_UPLOAD_TIME);
            return null;
        }
    }

    private static final String PackageName = "com.kaer.more";
    private final Timer timerMail = new Timer();
    private ActivityManager activityManager=null;

    private boolean isBackgroundRunning() {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> processList = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : processList) {
            if (info.baseActivity.getPackageName().startsWith(PackageName)) {
                return true;
            }
        }
        return false;
    }
    public void logout() {
        mNoticeDeviceTask = new NoticeDeviceTask();
        mNoticeDeviceTask.execute(STATE_CLOSE);
        mDeviceUploadTask = new DeviceUploadTask();
        mDeviceUploadTask.execute();
    }
}
