package com.kaer.more.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.entitiy.AdRemarkData;
import com.kaer.more.entitiy.AdvertisementData;
import com.kaer.more.service.KaerService;
import com.kaer.more.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.liteav.demo.play.SuperPlayerModel;
import com.tencent.liteav.demo.play.SuperPlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scifly.device.Device;

public class MainActivity extends AppCompatActivity {
    private SuperPlayerView mSuperPlayerView;
    private ImageView mIvTextPic;
    private TextView mTvText;
    private HashMap<String, AdRemarkData> mAdRemarkMap = new HashMap<String, AdRemarkData>();//获取新的任务队列的时候清空一次
    private LocationManager locationManager;
    private int nowPosition = 0;
    private static final int GO_AD = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_AD:
                    if (KareApplication.mAdvertisementList.size() != 0) {
                        if (nowPosition == KareApplication.mAdvertisementList.size()) nowPosition = 0;
                        AdvertisementData advertisementData = KareApplication.mAdvertisementList.get(nowPosition);
                        //播放广告
                        if (advertisementData.getMediaType() == 1) {//文本
                            mTvText.setVisibility(View.VISIBLE);
                            mIvTextPic.setVisibility(View.VISIBLE);
                            mSuperPlayerView.setVisibility(View.GONE);
                            mSuperPlayerView.onPause();
                            mTvText.setText(advertisementData.getContent());
                            ImageLoader.getInstance().displayImage(advertisementData.getMedia(), mIvTextPic);
                        } else if (advertisementData.getMediaType() == 2) {//图片
                            mTvText.setVisibility(View.GONE);
                            mIvTextPic.setVisibility(View.VISIBLE);
                            mSuperPlayerView.setVisibility(View.GONE);
                            mSuperPlayerView.onPause();
                            ImageLoader.getInstance().displayImage(advertisementData.getMedia(), mIvTextPic);
                        } else if (advertisementData.getMediaType() == 3) {//广告
                            mTvText.setVisibility(View.GONE);
                            mIvTextPic.setVisibility(View.GONE);
                            mSuperPlayerView.setVisibility(View.VISIBLE);
                            playVideo(advertisementData.getMedia());
                        }

                        if (mAdRemarkMap.containsKey(advertisementData.getAdId())) {
                            AdRemarkData adRemarkData = mAdRemarkMap.get(advertisementData.getAdId());
                            adRemarkData.setAllCount(adRemarkData.getAllCount() + 1);
                            adRemarkData.setAllTime(adRemarkData.getAllTime() + advertisementData.getDuration());
                            mAdRemarkMap.put(advertisementData.getAdId(), adRemarkData);
                        } else {
                            AdRemarkData adRemarkData = new AdRemarkData();
                            adRemarkData.setAdId(advertisementData.getAdId());
                            adRemarkData.setAllCount(1);
                            adRemarkData.setAllTime(advertisementData.getDuration());
                            mAdRemarkMap.put(advertisementData.getAdId(), adRemarkData);
                        }
                        nowPosition = nowPosition + 1;
                        mHandler.sendEmptyMessageDelayed(GO_AD, advertisementData.getDuration() * 1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();// 隐藏ActionBar
        setContentView(R.layout.activity_main);
        //得到当前界面的装饰视图
        View decorView = getWindow().getDecorView();
//      SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);


        //hideBottomUIMenu();//隐藏虚拟按键，否则全屏时虚拟按键会影响视频播放进度条的调整。
        checkPermission();//检查权限
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();

        initList();
        initLocation();

        Intent intent = new Intent(MainActivity.this, KaerService.class);
        startService(intent);

        mMainReceiver = new MainReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KareApplication.ACTION_TUISONG_JSON);
        registerReceiver(mMainReceiver, intentFilter);
    }

    private MainReceiver mMainReceiver;

    public class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KareApplication.ACTION_UPDATE_AD)) {
                //开始执行第一条
                nowPosition = 0;
            }
        }
    }
    private void initList() {
        //获取网络数据
        //先模拟数据-默认广告
        KareApplication.mAdvertisementList = new ArrayList<AdvertisementData>();
        AdvertisementData advertisementData = new AdvertisementData();//模拟文本
        advertisementData.setMediaType(1);
        advertisementData.setContent("广告测试文本1");
        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396474168&di=f209408b5f8bcbfdcd27dec8ff9a7c9a&imgtype=0&src=http%3A%2F%2Fpic25.nipic.com%2F20121112%2F9252150_150552938000_2.jpg");
        advertisementData.setDuration(10);
        KareApplication.mAdvertisementList.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟视频
        advertisementData.setMediaType(3);
        advertisementData.setDuration(30);
        advertisementData.setMedia("http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4");
        KareApplication.mAdvertisementList.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟图片
        advertisementData.setMediaType(2);
        advertisementData.setDuration(10);
        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396439119&di=b52f8459a8c209324c638503b4c9005c&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20141219%2Fzhongguofengdaodeliyizhanbanzhijing_3744115.jpg");
        KareApplication.mAdvertisementList.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟视频
        advertisementData.setMediaType(3);
        advertisementData.setDuration(30);
        advertisementData.setMedia("http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4");
        KareApplication.mAdvertisementList.add(advertisementData);
        //开始执行第一条
        nowPosition = 0;
        mHandler.sendEmptyMessage(GO_AD);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        //String videoUrl = "http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4";
        mSuperPlayerView = (SuperPlayerView) findViewById(R.id.video_player_item);
        LogUtil.println("initView mSuperPlayerViewId:"+(mSuperPlayerView.getId()));
        mIvTextPic = (ImageView) findViewById(R.id.iv_text_and_pic);
        LogUtil.println("initView mIvTextPicId:"+(mIvTextPic.getId()));
        mTvText = (TextView) this.findViewById(R.id.tv_text);
        LogUtil.println("initView mTvTextgetId:"+(mTvText.getId()));

    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }

    /**
     * 播放视频
     */
    private void playVideo(String url) {
        //url = "http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4";
        SuperPlayerModel model = new SuperPlayerModel();
        model.videoURL = url;
        mSuperPlayerView.playWithMode(model);
    }

    private void stopVideo() {
        //mSuperPlayerView.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMainReceiver);
        mSuperPlayerView.resetPlayer();
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        LogUtil.println("initLocation");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        String locationProvider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
//            Intent i = new Intent();
//            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(i);
        }
        //获取Location
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(locationProvider);
//        String string = "纬度为：" + location.getLatitude() + ",经度为："
//                + location.getLongitude();
//        LogUtil.println("location:" + string);
//        //监视地理位置变化
//        locationManager.requestLocationUpdates(locationProvider, 1000, 100, locationListener);
    }


    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            String string = "纬度为：" + location.getLatitude() + ",经度为："
                    + location.getLongitude();
            LogUtil.println("locationListener:" + string);
            //遍历广告中有经纬度定位的广告
            for(int i = 0;i<KareApplication.mAdvertisementList.size();i++){
                AdvertisementData advertisementData = KareApplication.mAdvertisementList.get(i);
                if(!TextUtils.isEmpty(advertisementData.getLocation())){
                    //拆分advertisementData.getLocation();
                    double latitude = 0.0;
                    double longitude = 0.0;
                    //判断上下距离 1km等于经纬度多少。1/111 1/111 0.009
                    double laDistance = location.getLatitude() - latitude;
                    double lgDistance = location.getLongitude() - longitude;
                    if(laDistance<-0.009*Double.valueOf(advertisementData.getLimits())&&laDistance>0.009*Double.valueOf(advertisementData.getLimits())){
                        //重新获取新的广告
                    }
                    if(longitude<-0.009*Double.valueOf(advertisementData.getLimits())&&longitude>0.009*Double.valueOf(advertisementData.getLimits())){
                        //重新获取新的广告
                    }
                }
            }
            //如果有判断是否符合在距离之内，否则发送广播请求新的广告
        }
    };
}
