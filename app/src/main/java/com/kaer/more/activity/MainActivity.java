package com.kaer.more.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.entitiy.AdRemarkData;
import com.kaer.more.entitiy.AdvertisementData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.liteav.demo.play.SuperPlayerModel;
import com.tencent.liteav.demo.play.SuperPlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SuperPlayerView mSuperPlayerView;
    private ImageView mIvTextPic;
    private TextView mTvText;
    private ArrayList<AdvertisementData> list = new ArrayList<AdvertisementData>();
    private HashMap<String, AdRemarkData> mAdRemarkMap = new HashMap<String, AdRemarkData>();//获取新的任务队列的时候清空一次
    private int nowPosition = 0;
    private static final int GO_AD = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_AD:
                    if (list.size() != 0) {
                        if (nowPosition == list.size()) nowPosition = 0;
                        AdvertisementData advertisementData = list.get(nowPosition);
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

                        if(mAdRemarkMap.containsKey(advertisementData.getAdId())) {
                            AdRemarkData adRemarkData = mAdRemarkMap.get(advertisementData.getAdId());
                            adRemarkData.setAllCount(adRemarkData.getAllCount()+1);
                            adRemarkData.setAllTime(adRemarkData.getAllTime()+advertisementData.getDuration());
                            mAdRemarkMap.put(advertisementData.getAdId(),adRemarkData);
                        }else{
                            AdRemarkData adRemarkData = new AdRemarkData();
                            adRemarkData.setAdId(advertisementData.getAdId());
                            adRemarkData.setAllCount(1);
                            adRemarkData.setAllTime(advertisementData.getDuration());
                            mAdRemarkMap.put(advertisementData.getAdId(),adRemarkData);
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
    }

    private void initList() {
        //获取网络数据
        //先模拟数据
        list = new ArrayList<AdvertisementData>();
        AdvertisementData advertisementData = new AdvertisementData();//模拟文本
        advertisementData.setMediaType(1);
        advertisementData.setContent("广告测试文本1");
        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396474168&di=f209408b5f8bcbfdcd27dec8ff9a7c9a&imgtype=0&src=http%3A%2F%2Fpic25.nipic.com%2F20121112%2F9252150_150552938000_2.jpg");
        advertisementData.setDuration(10);
        list.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟视频
        advertisementData.setMediaType(3);
        advertisementData.setDuration(30);
        advertisementData.setMedia("http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4");
        list.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟图片
        advertisementData.setMediaType(2);
        advertisementData.setDuration(10);
        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396439119&di=b52f8459a8c209324c638503b4c9005c&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20141219%2Fzhongguofengdaodeliyizhanbanzhijing_3744115.jpg");
        list.add(advertisementData);
        advertisementData = new AdvertisementData();//模拟视频
        advertisementData.setMediaType(3);
        advertisementData.setDuration(30);
        advertisementData.setMedia("http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4");
        list.add(advertisementData);
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
        mIvTextPic = (ImageView) findViewById(R.id.iv_text_and_pic);
        mTvText = (TextView) findViewById(R.id.tv_text);
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
        mSuperPlayerView.resetPlayer();
    }
}
