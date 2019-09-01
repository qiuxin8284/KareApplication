package com.kaer.more.utils;

import android.content.Context;


import java.util.ArrayList;
import java.util.Date;

import scifly.util.LogUtils;

public class SettingSharedPerferencesUtil {

    public static final String OPT_DEVCIE_USERNAME_PATH = "filepath_opt_device_";
    private static final String OPT_DEVCIE_USERNAME_CONFIG = "config_opt_device_";
    public static boolean SetOPTDeviceValue(Context context, String operate) {
        LogUtil.println("operateDevice SetOPTDeviceValue operate:" + operate);
        return PrefsHelper.save(context, OPT_DEVCIE_USERNAME_CONFIG, operate, OPT_DEVCIE_USERNAME_PATH);
    }

    public static String GetOPTDeviceConfig(Context context) {
        String operate = "";
        try {
            operate = PrefsHelper.read(context, OPT_DEVCIE_USERNAME_CONFIG, OPT_DEVCIE_USERNAME_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.println("operateDevice GetOPTDeviceConfig operate:" + operate);
        return operate;
    }

}
