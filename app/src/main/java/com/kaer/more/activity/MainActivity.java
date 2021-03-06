package com.kaer.more.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.IPackageInstallListener;
import android.app.PackageManagerExtra;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.entitiy.AdRemarkData;
import com.kaer.more.entitiy.AdvertisementData;
import com.kaer.more.entitiy.OperateData;
import com.kaer.more.entitiy.RenewData;
import com.kaer.more.http.HttpAnalyJsonManager;
import com.kaer.more.http.HttpSendJsonManager;
import com.kaer.more.service.KaerService;
import com.kaer.more.utils.APPUtil;
import com.kaer.more.utils.LogUtil;
import com.kaer.more.utils.SettingSharedPerferencesUtil;
import com.kaer.more.utils.SilentInstall;
import com.kaer.more.utils.TimeUtil;
import com.kaer.more.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.liteav.demo.play.SuperPlayerModel;
import com.tencent.liteav.demo.play.SuperPlayerView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import scifly.device.Device;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mRLMainBG;
    private SuperPlayerView mSuperPlayerView;
    private ImageView mIvTextPic;
    private ImageView mIvTimeAd;
    private TextView mTvText;
    //private LocationManager locationManager;
    private int nowPosition = 0;
    private static final int GO_AD = 0;
    private static final int GET_PIC = 1;
    private static final int VERSION_SUCCESS = 2;
    private static final int VERSION_FALSE = 3;
    private static final int VERSION_SHOW = 4;
    private static final int NOTICE_DEVICE_SUCCESS = 5;
    private static final int NOTICE_DEVICE_FALSE = 6;
    public static final int NOTICE_DEVICE = 7;
    private static final int UPDATE_SHOW = 8;
    private static final int OPT_DEVICE_SUCCESS = 9;
    private static final int OPT_DEVICE_FALSE = 10;
    public static final int REPEAT_OPT = 11;
    public static final int SHOW_TIME_AD = 12;
    public static final int SHOW_TIME_AD_END = 13;
    public static final int NOTICE_DEVICE_TIME = 2*60*1000;//30s重试
    private TextView mTvDeviceID;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_AD:
                    if (KareApplication.mAdvertisementList.size() != 0) {
                        if (nowPosition >= KareApplication.mAdvertisementList.size())
                            nowPosition = 0;
                        AdvertisementData advertisementData = KareApplication.mAdvertisementList.get(nowPosition);
                        //播放广告
                        if (advertisementData.getMediaType() == 1) {//文本
                            mTvText.setVisibility(View.VISIBLE);
                            mIvTextPic.setVisibility(View.VISIBLE);
                            mSuperPlayerView.setVisibility(View.GONE);
                            mSuperPlayerView.onPause();
                            mTvText.setText(advertisementData.getContent());
                            if(advertisementData.getMedia().contains("assets://")){
                                LogUtil.println("assets advertisementData.getMedia():" + advertisementData.getMedia());
                                LogUtil.println("assets advertisementData.getMedia() substring:" + advertisementData.
                                        getMedia().substring(advertisementData.getMedia().indexOf("//")));
//                                    Glide.with(MainActivity.this).load(getResources().getAssets().
//                                            open(advertisementData.getMedia().substring(advertisementData.getMedia().indexOf("//"))))
//                                            .into(mIvTextPic); //加载assets图片
                                ImageLoader.getInstance().displayImage(advertisementData.getMedia(),mIvTextPic);
                            }else{
                                RequestOptions option = new RequestOptions();
                                //option.centerCrop();
                                //option.placeholder(R.mipmap.ad_001); //或者
                                option.fallback(R.mipmap.ad_001);
                                option.placeholder(new ColorDrawable(Color.BLACK));
                                option.error(R.mipmap.ad_001);
                                Glide.with(getApplicationContext()) .load(advertisementData.getMedia()) .apply(option) .into(mIvTextPic);
                            //ImageLoader.getInstance().displayImage(advertisementData.getMedia(), mIvTextPic,options);
//                            Glide.with(MainActivity.this).load(advertisementData.getMedia()).listener(new RequestListener<Drawable>() {
//                                @Override
//                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                    //图片加载失败调用
//                                    Glide.with(MainActivity.this).load(R.mipmap.ad_001).into(mIvTextPic);
//                                    return false;
//                                }
//
//                                @Override
//                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                    //图片加载完成时调用
//                                    return false;
//                                }
//                            }).into(mIvTextPic);
                            }
                        } else if (advertisementData.getMediaType() == 2) {//图片
                            mTvText.setVisibility(View.GONE);
                            mIvTextPic.setVisibility(View.VISIBLE);
                            mSuperPlayerView.setVisibility(View.GONE);
                            mSuperPlayerView.onPause();
                            if(advertisementData.getMedia().contains("assets://")){
                                LogUtil.println("assets advertisementData.getMedia():" + advertisementData.getMedia());
                                LogUtil.println("assets advertisementData.getMedia() substring:" + advertisementData.
                                        getMedia().substring(advertisementData.getMedia().indexOf("//")));
//                                    Glide.with(mIvTextPic.getContext()).load(getResources().getAssets().
//                                            open(advertisementData.getMedia().substring(advertisementData.getMedia().indexOf("//"))))
//                                            .into(mIvTextPic); //加载assets图片
                                ImageLoader.getInstance().displayImage(advertisementData.getMedia(),mIvTextPic);
                            }else {
                                RequestOptions option = new RequestOptions();
                                //option.centerCrop();
                                //option.placeholder(R.mipmap.ad_001);
                                option.placeholder(new ColorDrawable(Color.BLACK));
                                option.fallback(R.mipmap.ad_001);
                                option.error(R.mipmap.ad_001);
                                Glide.with(getApplicationContext()) .load(advertisementData.getMedia()) .apply(option) .into(mIvTextPic);
                                //ImageLoader.getInstance().displayImage(advertisementData.getMedia(), mIvTextPic,options);
//                                Glide.with(MainActivity.this).load(advertisementData.getMedia()).listener(new RequestListener<Drawable>() {
//                                    @Override
//                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        //图片加载失败调用
//                                        Glide.with(MainActivity.this).load(R.mipmap.ad_001).into(mIvTextPic);
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        //图片加载完成时调用
//                                        return false;
//                                    }
//                                }).into(mIvTextPic);
                            }
                        } else if (advertisementData.getMediaType() == 3) {//广告
                            mTvText.setVisibility(View.GONE);
                            mIvTextPic.setVisibility(View.GONE);
                            mSuperPlayerView.setVisibility(View.VISIBLE);
                            playVideo(advertisementData.getMedia());
                        }

                        if(!TextUtils.isEmpty(advertisementData.getAdId())) {
                            if (KareApplication.mAdRemarkMap.containsKey(advertisementData.getAdId())) {
                                AdRemarkData adRemarkData = KareApplication.mAdRemarkMap.get(advertisementData.getAdId());
                                adRemarkData.setAllCount(adRemarkData.getAllCount() + 1);
                                adRemarkData.setAllTime(adRemarkData.getAllTime() + advertisementData.getDuration());
                                KareApplication.mAdRemarkMap.put(advertisementData.getAdId(), adRemarkData);
                            } else {
                                AdRemarkData adRemarkData = new AdRemarkData();
                                adRemarkData.setAdId(advertisementData.getAdId());
                                adRemarkData.setAllCount(1);
                                adRemarkData.setAllTime(advertisementData.getDuration());
                                KareApplication.mAdRemarkMap.put(advertisementData.getAdId(), adRemarkData);
                            }
                        }
                        nowPosition = nowPosition + 1;
                        mHandler.sendEmptyMessageDelayed(GO_AD, advertisementData.getDuration() * 1000);
//                        Intent kaerIntent = new Intent();
//                        kaerIntent.setAction(KareApplication.ACTION_TUISONG_JSON);
//                        kaerIntent.putExtra("function", "1");
//                        kaerIntent.putExtra("state", "3");
//                        kaerIntent.putExtra("value", "-30");
//                        sendBroadcast(kaerIntent);
                        //推送广播给到service做不同的事情
//                        Intent kaerIntent = new Intent();
//                        String function = String.valueOf(nowPosition);
//                        kaerIntent.putExtra("function",function);
//                        if (function.equals("1")) {
//                            kaerIntent.putExtra("state","1");
//                            kaerIntent.putExtra("value","0");
//                        } else if (function.equals("2")) {
//                            kaerIntent.putExtra("state","2");
//                            kaerIntent.putExtra("value","50");
//                        }
//                        else if (function.equals("3")) {
//                            kaerIntent.putExtra("state","1");
//                            kaerIntent.putExtra("value","");
//                        }else if (function.equals("4")) {
//                            kaerIntent.putExtra("state","1");
//                            kaerIntent.putExtra("value","");
//                        }
//                        kaerIntent.setAction(KareApplication.ACTION_TUISONG_JSON);
//                        sendBroadcast(kaerIntent);
                    }else{
                        initList();
                    }
                    break;
                case GET_PIC:
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "kaer.jpg");
                    Bitmap bitmap = screenShot(MainActivity.this);
                    try {
                        LogUtil.println("screenShot bitmap:" + (bitmap!=null));
                        if (!file.exists())
                            file.createNewFile();
                        boolean ret = save(bitmap, file, Bitmap.CompressFormat.JPEG, true);
                        LogUtil.println("screenShot ret:" + ret);
                        if (ret) {
                            //上传图片回传接口
                            Intent picIntent = new Intent();
                            picIntent.setAction(KareApplication.ACTION_IMAGE_UPLOAD_SUCESS);
                            sendBroadcast(picIntent);

                            //Toast.makeText(getApplicationContext(), "截图已保持至 " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case VERSION_SUCCESS:
                    if (mRenewData != null) {
                        LogUtil.println("deviceVersion mRenewData.getVersion():"+mRenewData.getVersion());
                        LogUtil.println("deviceVersion APPUtil.packageCode(MainActivity.this)):"+APPUtil.packageCode(MainActivity.this));
                        int nowVersion = Integer.parseInt(APPUtil.packageCode(MainActivity.this).replace(".",""));
                        int renewVersion = Integer.parseInt(mRenewData.getVersion().replace(".",""));
                        LogUtil.println("deviceVersion nowVersion:"+nowVersion);
                        LogUtil.println("deviceVersion renewVersion:"+renewVersion);
                        //对比版本号--和本地版本号对比
                        if (renewVersion<=nowVersion) {
                            //ToastUtils.shortToast(MainActivity.this,"版本相同");
                            LogUtil.println("deviceVersion 版本相同");
                        }else{
                            //ToastUtils.shortToast(MainActivity.this,"downLoadApk");
                            LogUtil.println("deviceVersion downLoadApk");
                            apkUrl = mRenewData.getLink();
                            downloadApk();

//                            long id = APPUtil.downLoadApk(MainActivity.this,"Kaer",mRenewData.getLink());
//                            LogUtil.println("deviceVersion id" + id);
//                            listener(id);
                        }
//                        mTvNowVer.setText("现有版本：Version " + mRenewData.getLowestVer());
//                        mTvUpdateVer.setText("可用版本：Version " + mRenewData.getNewVer());
//                        LogUtil.println("Kare::getLink:= " + mRenewData.getLink());

                    }
                    break;
                case VERSION_FALSE:
                    break;
                case VERSION_SHOW:
                    mTvDeviceID.setText("设备号:"+KareApplication.default_imei+
                            "\n版本号："+APPUtil.packageCode(MainActivity.this)
                            +"\ndeviceVersion progress:"+progress);
                    break;
                case NOTICE_DEVICE_SUCCESS:
                    break;
                case NOTICE_DEVICE_FALSE:
                    break;
                case NOTICE_DEVICE:
                    mNoticeDeviceTask = new NoticeDeviceTask();
                    mNoticeDeviceTask.execute(STATE_OPEN);
                    break;
                case UPDATE_SHOW:
                    LogUtil.println("onClick UPDATE_SHOW isShow:"+isShow);
                    if(isShow){
                        isShow = false;
                        mTvDeviceID.setVisibility(View.GONE);
                    }else{
                        isShow = true;
                        mTvDeviceID.setVisibility(View.VISIBLE);
                    }
                    break;
                case OPT_DEVICE_SUCCESS:
                    KareApplication.mOperate = (String) msg.obj;
                    //缓存营业时间
                    SettingSharedPerferencesUtil.SetOPTDeviceValue(MainActivity.this,KareApplication.mOperate);
                    optInit();
                    break;
                case OPT_DEVICE_FALSE:
                    //失败读取缓存
                    KareApplication.mOperate = SettingSharedPerferencesUtil.GetOPTDeviceConfig(MainActivity.this);
                    optInit();
                    break;
                case REPEAT_OPT:
                    mOperateDeviceTask = new OperateDeviceTask();
                    mOperateDeviceTask.execute();
                    break;
                case SHOW_TIME_AD:
                    LogUtil.println("adSearch MainReceiver mGetTimeAdTask SHOW_TIME_AD显示广告");
                    LogUtil.println("adSearch MainReceiver mGetTimeAdTask KareApplication." +
                            "mTimeAdvertisementList.size:"+KareApplication.mTimeAdvertisementList.size());
                    if(KareApplication.mTimeAdvertisementList.size()>0) {
                        AdvertisementData advertisementData = KareApplication.mTimeAdvertisementList.get(0);
                        RequestOptions option = new RequestOptions();
                        option.placeholder(new ColorDrawable(Color.BLACK));
                        option.fallback(R.mipmap.ad_001);
                        option.error(R.mipmap.ad_001);
                        Glide.with(getApplicationContext()).load(advertisementData.getMedia()).apply(option).into(mIvTimeAd);
                        mIvTimeAd.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(SHOW_TIME_AD_END, 30000);
                    }else{
                        isShowTimeAd = false;
                    }
                    break;
                case SHOW_TIME_AD_END:
                    isShowTimeAd = false;
                    mIvTimeAd.setVisibility(View.GONE);
                    LogUtil.println("adSearch MainReceiver mGetTimeAdTask SHOW_TIME_AD_END关闭广告");
                    break;
            }
        }
    };
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LogUtil.println("notice MainActivity onCreate");
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
        intentFilter.addAction(KareApplication.ACTION_UPDATE_AD);
        intentFilter.addAction(KareApplication.ACTION_UPDATE_TIME_AD);
        intentFilter.addAction(KareApplication.ACTION_IMAGE_UPLOAD);
        registerReceiver(mMainReceiver, intentFilter);
        mRenewTask = new RenewTask();
        mRenewTask.execute("");

        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.mipmap.ad_001)
                .showImageForEmptyUri(R.mipmap.ad_001)
                .showImageOnFail(R.mipmap.ad_001).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();

        //发送接口
        mNoticeDeviceTask = new NoticeDeviceTask();
        mNoticeDeviceTask.execute(STATE_OPEN);

        mOperateDeviceTask = new OperateDeviceTask();
        mOperateDeviceTask.execute();

    }
    private static final String STATE_OPEN = "O";
    private static final String STATE_CLOSE = "F";
    private boolean isShowTimeAd = false;

    private MainReceiver mMainReceiver;

    public class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KareApplication.ACTION_UPDATE_AD)) {
                LogUtil.println("adSearch MainReceiver ACTION_UPDATE_AD nowPosition:"+nowPosition);
                //开始执行第一条
                nowPosition = 0;
                LogUtil.println("adSearch MainReceiver ACTION_UPDATE_AD nowPosition 0000");
            }else if (action.equals(KareApplication.ACTION_IMAGE_UPLOAD)) {
                //截图上传
                mHandler.sendEmptyMessage(GET_PIC);
            }else if (action.equals(KareApplication.ACTION_UPDATE_TIME_AD)){
                LogUtil.println("adSearch MainReceiver mGetTimeAdTask ACTION_UPDATE_TIME_AD");
                if(!isShowTimeAd) {
                    isShowTimeAd = true;
                    mHandler.sendEmptyMessage(SHOW_TIME_AD);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mHandler.sendEmptyMessageDelayed(GET_PIC,5000);

        //LogUtil.println("initView Device.getDeviceId():" + Device.getDeviceId(this));
        //LogUtil.println("initView Device.getDeviceSN():" + Device.getDeviceSN());
    }

    private void initList() {
        //("drawable://" + imageId）
        //assets://image.png
        KareApplication.mAdvertisementList = new ArrayList<AdvertisementData>();
        AdvertisementData advertisementData = new AdvertisementData();//模拟图片
        advertisementData.setMediaType(2);
        advertisementData.setDuration(10);
        advertisementData.setMedia("assets://ad_001.jpg");
        KareApplication.mAdvertisementList.add(advertisementData);

        advertisementData = new AdvertisementData();//模拟图片
        advertisementData.setMediaType(2);
        advertisementData.setDuration(10);
        advertisementData.setMedia("assets://ad_002.png");
        KareApplication.mAdvertisementList.add(advertisementData);
        //开始执行第一条
        nowPosition = 0;
        mHandler.sendEmptyMessage(GO_AD);
        //获取网络数据
        //先模拟数据-默认广告
//        KareApplication.mAdvertisementList = new ArrayList<AdvertisementData>();
//        AdvertisementData advertisementData = new AdvertisementData();//模拟文本
//        advertisementData.setMediaType(1);
//        advertisementData.setContent("广告测试文本1");
//        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396474168&di=f209408b5f8bcbfdcd27dec8ff9a7c9a&imgtype=0&src=http%3A%2F%2Fpic25.nipic.com%2F20121112%2F9252150_150552938000_2.jpg");
//        advertisementData.setDuration(10);
//        KareApplication.mAdvertisementList.add(advertisementData);
//        advertisementData = new AdvertisementData();//模拟视频
//        advertisementData.setMediaType(3);
//        advertisementData.setDuration(30);
//        advertisementData.setMedia("http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4");
//        KareApplication.mAdvertisementList.add(advertisementData);
//        advertisementData = new AdvertisementData();//模拟图片
//        advertisementData.setMediaType(2);
//        advertisementData.setDuration(10);
//        advertisementData.setMedia("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561396439119&di=b52f8459a8c209324c638503b4c9005c&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20141219%2Fzhongguofengdaodeliyizhanbanzhijing_3744115.jpg");
//        KareApplication.mAdvertisementList.add(advertisementData);
//        advertisementData = new AdvertisementData();//模拟视频
//        advertisementData.setMediaType(3);
//        advertisementData.setDuration(30);
//        advertisementData.setMedia("http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4");
//        KareApplication.mAdvertisementList.add(advertisementData);
//        //开始执行第一条
//        nowPosition = 0;
//        mHandler.sendEmptyMessage(GO_AD);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        //String videoUrl = "http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4";
        mSuperPlayerView = (SuperPlayerView) findViewById(R.id.video_player_item);
        LogUtil.println("initView mSuperPlayerViewId:" + (mSuperPlayerView.getId()));
        mIvTextPic = (ImageView) findViewById(R.id.iv_text_and_pic);
        mIvTimeAd = (ImageView)findViewById(R.id.iv_time_pic);
        LogUtil.println("initView mIvTextPicId:" + (mIvTextPic.getId()));
        mTvText = (TextView) this.findViewById(R.id.tv_text);
        LogUtil.println("initView mTvTextgetId:" + (mTvText.getId()));
        mRLMainBG = (RelativeLayout) findViewById(R.id.rl_main);
        mTvDeviceID = (TextView) this.findViewById(R.id.tv_device_id);
        mTvDeviceID.setText("设备号:"+KareApplication.default_imei+"\n版本号："+APPUtil.packageCode(MainActivity.this));
        mTvDeviceID.setVisibility(View.GONE);
    }
    private boolean isShow = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.println("onClick onKeyDown keyCode:"+keyCode);
        if(keyCode==66){
            mHandler.sendEmptyMessage(UPDATE_SHOW);
        }
        return super.onKeyDown(keyCode, event);
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
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)) {
                permissions.add(Manifest.permission.WRITE_SETTINGS);
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
        LogUtil.println("notice MainActivity onDestroy");
        mNoticeDeviceTask = new NoticeDeviceTask();
        mNoticeDeviceTask.execute(STATE_CLOSE);
        unregisterReceiver(mMainReceiver);
        //unregisterReceiver(broadcastReceiver);
        mSuperPlayerView.resetPlayer();
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        LogUtil.println("initLocation init");
        try {
            KareApplication.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取所有可用的位置提供器
            List<String> providers = KareApplication.locationManager.getProviders(true);
            LogUtil.println("initLocation providers.size:"+providers.size());
            LogUtil.println("initLocation providers.toString:"+providers.toString());
            for(int i=0;i<providers.size();i++){
                LogUtil.println("initLocation providers i=:"+i+"|providers:"+providers.get(i));
            }
            //String locationProvider = null;
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                LogUtil.println("initLocation NETWORK_PROVIDER");
                //如果是Network
                KareApplication.locationProvider = LocationManager.NETWORK_PROVIDER;
            } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                LogUtil.println("initLocation GPS_PROVIDER");
                //如果是GPS
                KareApplication.locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                LogUtil.println("initLocation ACTION_LOCATION_SOURCE_SETTINGS");
                Intent i = new Intent();
                i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
            //获取Location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LogUtil.println("initLocation 缺少权限");
                return;
            }
            if(!providers.contains(LocationManager.GPS_PROVIDER)){
                LogUtil.println("initLocation 不含GPS_PROVIDER直接使用4G");
                KareApplication.locationProvider = LocationManager.NETWORK_PROVIDER;
            }else{
                KareApplication.locationManager.requestLocationUpdates(KareApplication.locationProvider, 2000, 20, gpslocationListener);
            }
            //监视地理位置变化
            KareApplication.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 20, locationListener);
            Location location = KareApplication.locationManager.getLastKnownLocation(KareApplication.locationProvider);
            if (location != null) {
                String string = "纬度为：" + location.getLatitude() + ",经度为："
                        + location.getLongitude();
                KareApplication.mLatitude = String.valueOf(location.getLatitude());
                KareApplication.mLongitude = String.valueOf(location.getLongitude());
                LogUtil.println("initLocation locationProvider location:" + string);
            }else{
                location = KareApplication.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    String string = "纬度为：" + location.getLatitude() + ",经度为："
                            + location.getLongitude();
                    KareApplication.mLatitude = String.valueOf(location.getLatitude());
                    KareApplication.mLongitude = String.valueOf(location.getLongitude());
                    LogUtil.println("initLocation NETWORK_PROVIDER location:" + string);
                }else {
                    LogUtil.println("initLocation location==null");
                }
            }
        }catch (Exception e){
            LogUtil.println("initLocation Exception");
            e.printStackTrace();
        }
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
            LogUtil.println("initLocation onLocationChanged");
            if(location!=null){
                String string = "纬度为：" + location.getLatitude() + ",经度为："
                        + location.getLongitude();
                KareApplication.mLatitude = String.valueOf(location.getLatitude());
                KareApplication.mLongitude = String.valueOf(location.getLongitude());
                LogUtil.println("initLocation locationListener:" + string);
            }
            //遍历广告中有经纬度定位的广告
            for (int i = 0; i < KareApplication.mAdvertisementList.size(); i++) {
                AdvertisementData advertisementData = KareApplication.mAdvertisementList.get(i);
                if (!TextUtils.isEmpty(advertisementData.getLocation())) {
                    LogUtil.println("initLocation advertisementData.getLocation():" + advertisementData.getLocation());
                    if(advertisementData.getLocation().contains(",")) {
                        String[] locations = advertisementData.getLocation().split(",");
                        //拆分advertisementData.getLocation();
                        double longitude = Double.valueOf(locations[0]);;
                        double latitude = Double.valueOf(locations[1]);
                        //判断上下距离 1km等于经纬度多少。1/111 1/111 0.009
                        double laDistance = location.getLatitude() - latitude;
                        double lgDistance = location.getLongitude() - longitude;
                        LogUtil.println("initLocation laDistance:" + laDistance);
                        LogUtil.println("initLocation lgDistance:" + lgDistance);
                        if (laDistance < -0.009 * Double.valueOf(advertisementData.getLimits()) && laDistance > 0.009 * Double.valueOf(advertisementData.getLimits())) {
                            //重新获取新的广告
                            LogUtil.println("initLocation 重新获取新的广告");
                            getAdSearch();
                        }else {
                            if (longitude < -0.009 * Double.valueOf(advertisementData.getLimits()) && longitude > 0.009 * Double.valueOf(advertisementData.getLimits())) {
                                //重新获取新的广告
                                LogUtil.println("initLocation 重新获取新的广告");
                                getAdSearch();
                            }
                        }
                    }
                }
            }
            //如果有判断是否符合在距离之内，否则发送广播请求新的广告
        }
    };

    public LocationListener gpslocationListener = new LocationListener() {
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
            LogUtil.println("initLocation onLocationChanged");
            if(location!=null){
                String string = "纬度为：" + location.getLatitude() + ",经度为："
                        + location.getLongitude();
                KareApplication.mLatitude = String.valueOf(location.getLatitude());
                KareApplication.mLongitude = String.valueOf(location.getLongitude());
                LogUtil.println("initLocation gpslocationListener:" + string);
            }
            //遍历广告中有经纬度定位的广告
            for (int i = 0; i < KareApplication.mAdvertisementList.size(); i++) {
                AdvertisementData advertisementData = KareApplication.mAdvertisementList.get(i);
                if (!TextUtils.isEmpty(advertisementData.getLocation())) {
                    LogUtil.println("initLocation advertisementData.getLocation():" + advertisementData.getLocation());
                    if(advertisementData.getLocation().contains(",")) {
                        String[] locations = advertisementData.getLocation().split(",");
                        //拆分advertisementData.getLocation();
                        double longitude = Double.valueOf(locations[0]);;
                        double latitude = Double.valueOf(locations[1]);
                        //判断上下距离 1km等于经纬度多少。1/111 1/111 0.009
                        double laDistance = location.getLatitude() - latitude;
                        double lgDistance = location.getLongitude() - longitude;
                        LogUtil.println("initLocation laDistance:" + laDistance);
                        LogUtil.println("initLocation lgDistance:" + lgDistance);
                        if (laDistance < -0.009 * Double.valueOf(advertisementData.getLimits()) && laDistance > 0.009 * Double.valueOf(advertisementData.getLimits())) {
                            //重新获取新的广告
                            LogUtil.println("initLocation 重新获取新的广告");
                            getAdSearch();
                        }else {
                            if (longitude < -0.009 * Double.valueOf(advertisementData.getLimits()) && longitude > 0.009 * Double.valueOf(advertisementData.getLimits())) {
                                //重新获取新的广告
                                LogUtil.println("initLocation 重新获取新的广告");
                                getAdSearch();
                            }
                        }
                    }
                }
            }
            //如果有判断是否符合在距离之内，否则发送广播请求新的广告
        }
    };


    /**
     * 保存图片到文件File。
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return true 成功 false 失败
     */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src))
            return false;

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled())
                src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 获取当前屏幕截图，不包含状态栏（Status Bar）。
     *
     * @param activity activity
     * @return Bitmap
     */
    public static Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int statusBarHeight = getStatusBarHeight(activity);
        int width = (int) getDeviceDisplaySize(activity)[0];
        int height = (int) getDeviceDisplaySize(activity)[1];
        Bitmap ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        // 获取状态栏高度
//        Rect frame = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        // 去掉标题栏 //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
//        Bitmap ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
//                - statusBarHeight);
//        view.destroyDrawingCache();

        LogUtil.println("screenShot width:" + width);
        LogUtil.println("screenShot height:" + height);
        LogUtil.println("screenShot statusBarHeight:" + statusBarHeight);
        LogUtil.println("screenShot bmp:" + bmp);
        return ret;
    }

    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap;
        if (view.getWidth() > 0 && view.getHeight() > 0)
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        else if (view.getMeasuredWidth() > 0 && view.getMeasuredHeight() > 0)
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(1000, 1000 * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static float[] getDeviceDisplaySize(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        float[] size = new float[2];
        size[0] = width;
        size[1] = height;

        return size;
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }

        return height;
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }











    BroadcastReceiver broadcastReceiver;
    private void listener(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.println("deviceVersion onReceive OK");
                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                // 这里是通过下面这个方法获取下载的id，
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                LogUtil.println("deviceVersion onReceive ID" + ID);
                // 这里把传递的id和广播中获取的id进行对比是不是我们下载apk的那个id，如果是的话，就开始获取这个下载的路径
                if (ID == Id) {

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(Id);

                    Cursor cursor = manager.query(query);
                    LogUtil.println("deviceVersion onReceive cursor.getCount:" + cursor.getCount());
                    if (cursor.moveToFirst()){
                        // 获取文件下载路径
                        String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        LogUtil.println("deviceVersion onReceive fileName:" + fileName);

                        // 如果文件名不为空，说明文件已存在,则进行自动安装apk
                        if (fileName != null){

                            openAPK(fileName);

                        }
                    }
                    cursor.close();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void openAPK(String fileSavePath){
        LogUtil.println("deviceVersion openAPK fileSavePath:" + fileSavePath);
        LogUtil.println("deviceVersion openAPK Uri.parse(fileSavePath).getPath():" + Uri.parse(fileSavePath).getPath());
        File file=new File(Uri.parse(fileSavePath).getPath());
        String filePath = file.getAbsolutePath();
        LogUtil.println("deviceVersion openAPK filePath:" + filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
//            // 生成文件的uri，，
//            // 注意 下面参数com.ausee.fileprovider 为apk的包名加上.fileprovider，
//            data = FileProvider.getUriForFile(MainActivity.this, "com.kaer.more.fileprovider", new File(filePath));
//            LogUtil.println("deviceVersion openAPK data:" + data);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
//        } else {
            data = Uri.fromFile(file);
            LogUtil.println("deviceVersion openAPK data:" + data);
//        }

        PackageManagerExtra pm = PackageManagerExtra.getInstance();
        pm.installPackage(this, data, new IPackageInstallListener(){

            @Override
            public void packageInstalled(String s, int i) {
                android.util.Log.e("PackageManager", "packageInstalled::s= " + s + " code= " + i);
            }
        });
        //intent.setDataAndType(data, "application/vnd.android.package-archive");
        //startActivity(intent);

    }

    private RenewTask mRenewTask;
    private RenewData mRenewData;

    private class RenewTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String deviceID = KareApplication.default_imei;
            mRenewData = HttpSendJsonManager.deviceVersion(MainActivity.this, deviceID);
            if (mRenewData.isOK()) {
                mHandler.sendEmptyMessage(VERSION_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(VERSION_FALSE);
            }
            return null;
        }
    }


    private static final String savePath = "/sdcard/updatedemo/";
    private static final String saveFileName = savePath
            + "kaer_1.0.6.apk";
    // 下载线程
    private Thread downLoadThread;
    private int progress;// 当前进度
    private String apkUrl;
    private boolean intercept = false;




    /**
     * 从服务器下载APK安装包
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File apkFile = new File(saveFileName);
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte[] buf = new byte[1024];
                while (!intercept) {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);

                    mHandler.sendEmptyMessage(VERSION_SHOW);
                    LogUtil.println("deviceVersion progress:"+progress);
                    // 下载进度
                    //mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        LogUtil.println("deviceVersion DOWN_OVER");
                        // 下载完成通知安装
                        //mHandler.sendEmptyMessage(DOWN_OVER);
                        openAPK(saveFileName);
                        //SilentInstall.install(saveFileName);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                fos.close();
                ins.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 安装APK内容
     */
    private void installAPK() {
        LogUtil.println("deviceVersion installAPK");
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        startActivity(intent);
        LogUtil.println("deviceVersion over");
    }

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
            mHandler.sendEmptyMessageDelayed(NOTICE_DEVICE,NOTICE_DEVICE_TIME);
            return null;
        }
    }

    //上传状态
    private OperateDeviceTask mOperateDeviceTask;

    private class OperateDeviceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //读取设备识别号跟常规android系统读取有区别吗？
            String deviceID = KareApplication.default_imei;
            LogUtil.println("operateDevice deviceID:" + deviceID);
            if (!TextUtils.isEmpty(deviceID)) {
                //上传检测是否存在这个设备号
                OperateData operateData = HttpSendJsonManager.operateDevice(KareApplication.mInstance, deviceID);
                LogUtil.println("operateDevice operateData:" + operateData.toString());
                if (operateData.isOK()) {
                    Message message = new Message();
                    message.what = OPT_DEVICE_SUCCESS;
                    message.obj = operateData.getOperate();
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(OPT_DEVICE_FALSE);
                }
            }
            return null;
        }
    }
    private void optInit(){
        if(!TextUtils.isEmpty(KareApplication.mOperate)) {
            //解析时间
            String startTime = KareApplication.mOperate.substring(0,KareApplication.mOperate.indexOf("-"));
            String[] startTimes = startTime.split(":");
            String endTime = KareApplication.mOperate.substring(KareApplication.mOperate.indexOf("-")+1);
            String[] endTimes = endTime.split(":");
//            LogUtil.println("operateDevice startTimes:" + startTimes);
//            LogUtil.println("operateDevice endTimes:" + endTimes);
//            boolean isOpt = TimeUtil.isCurrentInTimeScope(Integer.parseInt(startTimes[0]),Integer.parseInt(startTimes[1]),
//                    Integer.parseInt(endTimes[0]),Integer.parseInt(endTimes[1]));
//            if(isOpt){
//                //开机
//                LogUtil.println("operateDevice 开机");
//                Device.setProjectorLedPower(1);
//                //请求广告接口+逻辑判断
//                KareApplication.mGetAd = true;
//            }else{
//                //关机
//                LogUtil.println("operateDevice 关机");
//                Device.setProjectorLedPower(0);
//                //请求广告接口+逻辑判断
//                KareApplication.mGetAd = false;
//            }
            //算出到终结时间的时间差发个延迟
            int startT = Integer.parseInt(startTime.replace(":",""));
            int endT = Integer.parseInt(endTime.replace(":",""));
            int nowT = Integer.parseInt(KareApplication.dateFormat.format(new Date()));
            LogUtil.println("operateDevice startT:" + startT);
            LogUtil.println("operateDevice endT:" + endT);
            LogUtil.println("operateDevice nowT:" + nowT);
            //算出到终结时间的时间差发个延迟
            //以距离结束时间差为刷新点
            if(startT>endT){//代表跨天
                if(nowT<startT) {
                    // 22 3   n：2      距离end  ok 结束时间触发关机 ok
                    if(nowT<endT){
                        LogUtil.println("operateDevice 22 3   n：2      距离end");
                        LogUtil.println("operateDevice 开机");
                        Device.setProjectorLedPower(1);
                        //请求广告接口+逻辑判断
                        KareApplication.mGetAd = true;
                        new TimeUtil(endTime,mHandler);
                    }else {
                        // 22 3   n：21     距离start  ok开始时间触发开机ok
                        // 22 3   n：4      距离start  ok开始时间触发开机ok
                        LogUtil.println("operateDevice 22 3   n：21      距离start or 22 3   n：4       距离start");
                        LogUtil.println("operateDevice 关机");
                        Device.setProjectorLedPower(0);
                        //请求广告接口+逻辑判断
                        KareApplication.mGetAd = false;
                        new TimeUtil(startTime,mHandler);
                    }
                }else {
                    // 22 3   n：23     距离end  结束时间触发关机ok
                    LogUtil.println("operateDevice 22 3   n：23     距离end");
                    LogUtil.println("operateDevice 开机");
                    Device.setProjectorLedPower(1);
                    //请求广告接口+逻辑判断
                    KareApplication.mGetAd = true;
                    new TimeUtil(endTime,mHandler);
                }
            }else{
                if(nowT<startT){
                    // 2  4   n：1      距离start ok 开始时间触发开机 ok
                    LogUtil.println("operateDevice 2  4   n：1      距离start");
                    LogUtil.println("operateDevice 关机");
                    Device.setProjectorLedPower(0);
                    //请求广告接口+逻辑判断
                    KareApplication.mGetAd = false;
                    new TimeUtil(startTime,mHandler);
                }else {
                    if(nowT<endT) {
                        // 2  4   n：3      距离end  ok 结束时间触发关机ok
                        LogUtil.println("operateDevice 2  4   n：3      距离end");
                        LogUtil.println("operateDevice 开机");
                        Device.setProjectorLedPower(1);
                        //请求广告接口+逻辑判断
                        KareApplication.mGetAd = true;
                        new TimeUtil(endTime,mHandler);
                    }else {
                        // 2  4   n：5      距离start ok 开始时间触发开机 ok
                        LogUtil.println("operateDevice 2  4   n：5      距离start");
                        LogUtil.println("operateDevice 关机");
                        Device.setProjectorLedPower(0);
                        //请求广告接口+逻辑判断
                        KareApplication.mGetAd = false;
                        new TimeUtil(startTime,mHandler);
                    }
                }
            }

            //
//            if(nowT<endT){
//                if(nowT<startT){
//                    LogUtil.println("operateDevice nowT<startT<endT");
//                    //开启定时器-重新获取次接口外加刷新下界面
//                    new TimeUtil(startTime,mHandler);
//                }else{
//                    LogUtil.println("operateDevice startT<nowT<endT");
//                    //开启定时器-重新获取次接口外加刷新下界面
//                    new TimeUtil(endTime,mHandler);
//                }
//            }else{
//                if(nowT<startT){
//                    LogUtil.println("operateDevice startT<endT<nowT");
//                    //开启定时器-重新获取次接口外加刷新下界面
//                    new TimeUtil(startTime,mHandler);
//                }else{
//                    //有问题
//                    LogUtil.println("operateDevice startT<endT<nowT");
//                    //开启定时器-重新获取次接口外加刷新下界面
//                    new TimeUtil(startTime,mHandler);
//                }
//            }

            //开启定时器-重新获取次接口外加刷新下界面
        }else{
            //无限制
            //无逻辑判断
            LogUtil.println("operateDevice 无限制");
            KareApplication.mGetAd = true;
        }
    }

    private void getAdSearch(){
        Intent kaerIntent = new Intent();
        kaerIntent.putExtra("function", "6");
        kaerIntent.setAction(KareApplication.ACTION_TUISONG_JSON);
        sendBroadcast(kaerIntent);
    }
}
