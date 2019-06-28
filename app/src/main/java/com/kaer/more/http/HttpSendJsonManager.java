package com.kaer.more.http;


import android.content.Context;

import com.jordan.httplibrary.utils.Base64;
import com.jordan.httplibrary.utils.CommonUtils;
import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.utils.LogUtil;
import com.safari.core.protocol.RequestMessage;

import org.json.JSONObject;

public class HttpSendJsonManager {

    /**
     * 检查绑定
     * @param context
     * @param imei 设备ID
     * @return
     */
    public static boolean checkDevice(Context context,
                                     String imei) {
        String url = "v0/device/check.do";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("imei", imei);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("checkDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("checkDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 绑定
     * @param context
     * @param imei 设备ID
     * @param jpushId 极光ID
     * @return
     */
    public static boolean bindDevice(Context context,
                                      String imei,String jpushId) {
        String url = "v0/device/bind.do";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("imei", imei);
            mainJSONObject.put("jpushId", jpushId);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("bindDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("bindDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.bindDevice(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 上传状态
     * @param context
     * @param type 类型 1.开机 2.关机
     * @return
     */
    public static boolean noticeDevice(Context context,
                                      String type) {
        String url = "v0/device/notice.do";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("type", type);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("noticeDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("noticeDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传定位
     * @param context
     * @param longitude 经度
     * @param latitude 纬度
     * @return
     */
    public static boolean locationDevice(Context context,
                                     String longitude,String latitude) {
        String url = "v0/device/location.do";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("longitude", longitude);
            mainJSONObject.put("latitude", latitude);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("locationDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("locationDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 预警
     * @param context
     * @param desp 简要描述
     * @param content 详细内容
     * @return
     */
    public static boolean excpDevice(Context context,
                                         String desp,String content) {
        String url = "v0/device/excp.do";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("desp", desp);
            mainJSONObject.put("content", content);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("excpDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("excpDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }
}