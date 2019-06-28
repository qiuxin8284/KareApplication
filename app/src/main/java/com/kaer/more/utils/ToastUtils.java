package com.kaer.more.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xin on 2017/2/24.
 */

public class ToastUtils {
    public static void shortToast(Context context, String text){
        Toast.makeText(context,text, Toast.LENGTH_LONG).show();
    }
    public static void shortToast(Context context, int textID){
        Toast.makeText(context,textID, Toast.LENGTH_LONG).show();
    }
    public static void longToast(Context context, String text){
        Toast.makeText(context,text, Toast.LENGTH_LONG).show();
    }
}
