package com.kaer.more.http;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kaer.more.KareApplication;

import java.util.Locale;


public class DeviceUtil {
	private DeviceData mDeviceData;
	private String DeviceType = "1";//"Android Phone";
	private String token = "";

	public DeviceUtil(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			mDeviceData = new DeviceData();
			mDeviceData.setDeviceName(Build.MANUFACTURER);
			mDeviceData.setDeviceOs(String
					.valueOf(Build.VERSION.SDK_INT));
			mDeviceData.setDeviceToken("");
			mDeviceData.setDeviceVersion(Build.VERSION.RELEASE);
			mDeviceData.setDeviceType(DeviceType);
			mDeviceData.setNetType(String.valueOf(NetworkUtil.getNowNetworkState(context)));
			mDeviceData.setVersion(VersionUtil.getVersionName(context));
			mDeviceData.setToken(token);
			if (Locale.getDefault().getLanguage().equals("zh")) {
				//中文：
				if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
					mDeviceData.setLang("zh_cn");
				}
				//繁体中文（台湾）：
				else if (context.getResources().getConfiguration().locale.getCountry().equals("TW")) {
					mDeviceData.setLang("zh_tw");
				}
				//繁体中文（香港）
				else if (context.getResources().getConfiguration().locale.getCountry().equals("HK")) {
					mDeviceData.setLang("zh_hk");
				}
			} else if (Locale.getDefault().getLanguage().equals("en")) {
				mDeviceData.setLang("en");
			} else {
				mDeviceData.setLang("en");
			}
		}else {
			KareApplication.default_imei = TextUtils.isEmpty(tm.getDeviceId()) ? "0" : tm
					.getDeviceId();
			mDeviceData = new DeviceData();
			mDeviceData.setDeviceId(TextUtils.isEmpty(tm.getDeviceId()) ? "0" : tm
					.getDeviceId());
			mDeviceData.setDeviceName(Build.MANUFACTURER);
			mDeviceData.setDeviceOs(String
					.valueOf(Build.VERSION.SDK_INT));
			mDeviceData.setDeviceToken("");
			mDeviceData.setDeviceVersion(Build.VERSION.RELEASE);
			mDeviceData.setDeviceType(DeviceType);
			mDeviceData.setNetType(String.valueOf(NetworkUtil.getNowNetworkState(context)));
			mDeviceData.setVersion(VersionUtil.getVersionName(context));
			mDeviceData.setToken(token);
			if (Locale.getDefault().getLanguage().equals("zh")) {
				//中文：
				if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
					mDeviceData.setLang("zh_cn");
				}
				//繁体中文（台湾）：
				else if (context.getResources().getConfiguration().locale.getCountry().equals("TW")) {
					mDeviceData.setLang("zh_tw");
				}
				//繁体中文（香港）
				else if (context.getResources().getConfiguration().locale.getCountry().equals("HK")) {
					mDeviceData.setLang("zh_hk");
				}
			} else if (Locale.getDefault().getLanguage().equals("en")) {
				mDeviceData.setLang("en");
			} else {
				mDeviceData.setLang("en");
			}
		}
	}


	public DeviceData getDeviceData() {
		return mDeviceData;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
		mDeviceData.setToken(token);
	}
}
