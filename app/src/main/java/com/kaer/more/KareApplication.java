package com.kaer.more;
/**
 * Created by ASUS on 2018/8/24.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.service.autofill.UserData;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kaer.more.activity.MainActivity;
import com.kaer.more.http.Communicator;
import com.kaer.more.http.DeviceUtil;
import com.kaer.more.http.HttpAnalyJsonManager;
import com.kaer.more.http.HttpManager;
import com.kaer.more.http.HttpSendJsonManager;
import com.kaer.more.utils.LogUtil;
import com.kaer.more.utils.ToastUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpUtils;

import cn.jpush.android.api.JPushInterface;

//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechUtility;

public class KareApplication extends Application {

    public static Communicator communicator;
    public static HttpManager httpManager;
    public static String USER_TOKEN = "";
    public static DeviceUtil mDeviceUtil;
    private static Context context;
    /**
     * 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了
     */
    public static KareApplication mInstance;

    private static final int CHECK_DEVICE_SUCCESS = 1;
    private static final int CHECK_DEVICE_FALSE = 2;
    private static final int BIND_DEVICE_SUCCESS = 3;
    private static final int BIND_DEVICE_FALSE = 4;
    public static final int REPEAT_CONNECT_TIME = 30000;//30s重试
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_DEVICE_SUCCESS:
                    mBindDeviceTask = new BindDeviceTask();
                    mBindDeviceTask.execute();
                    break;
                case CHECK_DEVICE_FALSE:
                    break;
                case BIND_DEVICE_SUCCESS:
                    break;
                case BIND_DEVICE_FALSE:
                    mHandler.sendEmptyMessageDelayed(CHECK_DEVICE_SUCCESS,REPEAT_CONNECT_TIME);
                    break;
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(this);
        mInstance = this;
        context = getApplicationContext();
        createSpeech();
        communicator = new Communicator();
        mDeviceUtil = new DeviceUtil(this);
        httpManager = HttpManager.getInstance(this);
        initImageLoader(this);

        initDevice();
    }

    private void initDevice() {
        //获取推送token
        String token = JPushInterface.getRegistrationID(this);
        mDeviceUtil.setToken(token);
        mCheckDeviceTask = new CheckDeviceTask();
        mCheckDeviceTask.execute();
    }

    private CheckDeviceTask mCheckDeviceTask;

    private class CheckDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //读取设备识别号跟常规android系统读取有区别吗？
            String deviceID = mDeviceUtil.getDeviceData().getDeviceId();
            //上传检测是否存在这个设备号
            boolean flag = HttpSendJsonManager.checkDevice(mInstance,deviceID);
            LogUtil.println("checkDevice flag:" + flag);
            if (flag) {
                mHandler.sendEmptyMessage(CHECK_DEVICE_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(CHECK_DEVICE_FALSE);
            }
            return null;
        }
    }

    private BindDeviceTask mBindDeviceTask;

    private class BindDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //读取设备识别号跟常规android系统读取有区别吗？
            String deviceID = mDeviceUtil.getDeviceData().getDeviceId();
            String token = mDeviceUtil.getDeviceData().getToken();
            if(!TextUtils.isEmpty(token)) {
                //上传检测是否存在这个设备号
                boolean flag = HttpSendJsonManager.bindDevice(mInstance, deviceID,token);
                LogUtil.println("bindDevice flag:" + flag);
                if (flag) {
                    mHandler.sendEmptyMessage(CHECK_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(CHECK_DEVICE_FALSE);
                }
            }
            return null;
        }
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void createSpeech() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        //SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + SPEECH_UTILITY_ID);
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获取储存权限
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }


}
