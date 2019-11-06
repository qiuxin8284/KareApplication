package com.kaer.more.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.kaer.more.R;

import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    List<String> mPermissionList = new ArrayList<String>();
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private static final int GO_TO_LOGIN = 0;
    private static final int DELAY_TIME = 2000;
    private Handler mLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_TO_LOGIN:
                    Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();// 隐藏ActionBar
        setContentView(R.layout.activity_loading);
        //得到当前界面的装饰视图
        View decorView = getWindow().getDecorView();
//      SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        checkPermission();
    }

    @TargetApi(23)
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (this.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                mLoginHandler.sendEmptyMessageDelayed(GO_TO_LOGIN, DELAY_TIME);
            } else {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                this.requestPermissions(permissions, 1);
            }
        }
    }
}
