package com.kaer.more.http;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jordan.httplibrary.utils.Base64;
import com.jordan.httplibrary.utils.CommonUtils;
import com.kaer.more.KareApplication;
import com.kaer.more.R;
import com.kaer.more.entitiy.AdRemarkData;
import com.kaer.more.entitiy.AdvertisementData;
import com.kaer.more.entitiy.AdvertisementListData;
import com.kaer.more.entitiy.RenewData;
import com.kaer.more.entitiy.UploadData;
import com.kaer.more.utils.LogUtil;
import com.safari.core.protocol.RequestMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class HttpSendJsonManager {

    /**
     * 检查绑定
     * @param context
     * @param imei 设备ID
     * @return
     */
    public static boolean checkDevice(Context context,
                                     String imei) {
        String url = "v0/device/check.htm";
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
            return HttpAnalyJsonManager.checkDevice(synchronousResult, context);
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
        String url = "v0/device/bind.htm";
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
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
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
                                      String type,String imei) {
        String url = "v0/device/notice.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("type", type);
            mainJSONObject.put("imei", imei);
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
                                     String longitude,String latitude,String imei,String address) {
        String url = "v0/device/location.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("longitude", longitude);
            mainJSONObject.put("latitude", latitude);
            mainJSONObject.put("imei", imei);
            mainJSONObject.put("address", address);
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
                                         String desp,String content,String imei) {
        String url = "v0/device/excp.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("desp", desp);
            mainJSONObject.put("content", content);
            mainJSONObject.put("imei", imei);
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


    private static final String CHARSET = "utf-8"; //编码格式

    public static UploadData uploadMedia(Context context, int type, String name, String file, String time) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + name;
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        String urlPath = "media";


        //边界标识 随机生成，这个作为boundary的主体内容
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--";
        //回车换行，用于调整协议头的格式
        String LINE_END = "\r\n";
        //格式的内容信息
        String CONTENT_TYPE = "multipart/form-data";
        try {
            URL url = new URL(CommunicateConfig.GetHttpClientAdress() + urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            //这里设置请求方式以及boundary的内容，即上面生成的随机字符串
            httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            //这里的StringBuffer 用来拼接我们的协议头
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\"type\""
                    + LINE_END + LINE_END);
            sb.append("3" + LINE_END);
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data;name=\"postKey\";filename=\"" + name + "\"" + LINE_END);
            //这里Content-Type 传给后台一个mime类型的编码字段，用于识别扩展名
            sb.append("Content-Type: mp3; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());

            //将SD 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);

            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(LINE_END);
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            dos.flush();

            //读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            dos.close();
            is.close();
            LogUtil.println("upload result:" + result);
            return HttpAnalyJsonManager.uploadMedia(result, context);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadData;
    }

    public static boolean imgDevice(Context context,
                                     String img,String imei) {
        String url = "v0/device/img.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("img", img);
            mainJSONObject.put("imei", imei);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("imgDevice" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("imgDevice synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }


    public static AdvertisementListData adSearch(Context context,
                                     String imei,String longitude,String latitude, ArrayList<AdRemarkData> list) {
        AdvertisementListData advertisementListData = new AdvertisementListData();
        advertisementListData.setOK(false);
        String url = "v0/ad/search.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            JSONArray deviceAdsJsonArray = new JSONArray();
            for(int i=0;i<list.size();i++){
                AdRemarkData adRemarkData = list.get(i);
                JSONObject deviceAdsJSONObject = new JSONObject();
                deviceAdsJSONObject.put("adId", adRemarkData.getAdId());
                deviceAdsJSONObject.put("count", adRemarkData.getAllCount());
                deviceAdsJsonArray.put(deviceAdsJSONObject);
            }
            mainJSONObject.put("deviceAds", deviceAdsJsonArray);
            mainJSONObject.put("imei", imei);
            mainJSONObject.put("longitude", longitude);
            mainJSONObject.put("latitude", latitude);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("adSearch" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("adSearch synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.adSearch(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return advertisementListData;
        }
    }

    public static boolean deviceUpload(Context context, String imei,
                                       ArrayList<AdRemarkData> list) {
        String url = "v0/device/upload.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

			JSONArray deviceAdsJsonArray = new JSONArray();
			for(int i=0;i<list.size();i++){
				AdRemarkData adRemarkData = list.get(i);
				JSONObject deviceAdsJSONObject = new JSONObject();
                deviceAdsJSONObject.put("adId", adRemarkData.getAdId());
                deviceAdsJSONObject.put("count", adRemarkData.getAllCount());
                deviceAdsJsonArray.put(deviceAdsJSONObject);
			}
			mainJSONObject.put("deviceAds", deviceAdsJsonArray);
            mainJSONObject.put("imei", imei);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("deviceUpload" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("deviceUpload synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.onResult(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    public final static String RENEW_TYPE_OTA = "1";//"Android Phone";
    public final static String RENEW_TYPE_IOS = "2";//"Android Phone";
    public final static String RENEW_TYPE_ANDROID = "3";//"Android Phone";

    /**
     * 获取版本信息
     *
     * @param context
     * @return
     */
    public static RenewData deviceVersion(Context context, String imei) {
        RenewData renewData = new RenewData();
        renewData.setOK(false);
        String url = "v0/device/ver.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("imei", imei);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), KareApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("deviceVersion" + json);
            String synchronousResult = KareApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("deviceVersion synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.deviceVersion(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return renewData;
        }
    }
}