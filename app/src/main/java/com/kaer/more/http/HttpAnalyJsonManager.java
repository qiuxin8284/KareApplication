package com.kaer.more.http;


import android.content.Context;

import com.kaer.more.R;
import com.kaer.more.entitiy.PropellingMovementData;
import com.kaer.more.entitiy.UploadData;

import org.json.JSONException;
import org.json.JSONObject;


public class HttpAnalyJsonManager {
    public static String lastError;

    public static void lastErrorDefaultValue(Context context, String error) {
        HttpAnalyJsonManager.lastError = error;
//		if(error.equals("服务器忙，请稍后再操作")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1000);
//		}else if(error.equals("请求参数有误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1001);
//		}else if(error.equals("验证异常")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1002);
//		}else if(error.equals("请求参数格式有误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1003);
//		}else if(error.equals("短信发送频繁")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1004);
//		}else if(error.equals("验证失败或验证码已失效")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1005);
//		}else if(error.equals("非法请求")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1006);
//		}else if(error.equals("已存在")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1007);
//		}else if(error.equals("数据为空")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1008);
//		}else if(error.equals("注册失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1009);
//		}else if(error.equals("会话过期")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1010);
//		}else if(error.equals("用户未注册")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1011);
//		}else if(error.equals("短信发送失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1012);
//		}else if(error.equals("已绑定手机号")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1013);
//		}else if(error.equals("已绑定手机号")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1014);
//		}else if(error.equals("对象为空")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1015);
//		}else if(error.equals("支付中或支付失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}else if(error.equals("该账号已注册")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1017);
//		}else if(error.equals("原密码不对")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1018);
//		}else if(error.equals("手机号输入不正确")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1019);
//		}else if(error.equals("邮箱输入不正确")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1020);
//		}else if(error.equals("该邮箱已被占用")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1021);
//		}else if(error.equals("账号或密码输入错误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1022);
//		}else if(error.equals("未知异常")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_9999);
//		}

//		else if(error.equals("支付中")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}else if(error.equals("支付失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}
    }

    	public static boolean checkDevice(String json,Context context) throws JSONException{
		if(!lastError.equals("")) return false;
		JSONObject resultJson= new JSONObject(json);
		if(resultJson.getString("msg").equals("0"))
		{

			return true;
		}
		else
		{
			return false;
		}
	}
    public static boolean onResult(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }
    public static UploadData uploadMedia(String json, Context context) throws JSONException {
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        JSONObject resultJson = new JSONObject(json);
        String result = resultJson.getString("result");
        if(result.equals("0")){
            lastError = context.getResources().getString(R.string.upload_failed);
            return uploadData;
        }
//        String id = resultJson.getString("id");
//        String name = resultJson.getString("name");
        String url = resultJson.getString("path");
//        uploadData.setId(id);
//        uploadData.setName(name);
        uploadData.setUrl(url);
        uploadData.setOK(true);
        android.util.Log.e("uploadData", "uploadData:"+uploadData.toString());
        return uploadData;
    }

    public static PropellingMovementData propellingMovementFunction(String json, Context context) throws JSONException {
        PropellingMovementData mPropellingMovementData = new PropellingMovementData();
        mPropellingMovementData.setOK(false);
        if (!lastError.equals("")) {
            lastError = context.getResources().getString(R.string.upload_failed);
            return mPropellingMovementData;
        }
        JSONObject resultJson = new JSONObject(json);
        String funtion = resultJson.getString("funtion");
        mPropellingMovementData.setFunction(funtion);
        if (funtion.equals("1") || funtion.equals("2") || funtion.equals("3") || funtion.equals("4")) {
            //funtion 1、2、3、4包含state和value
            String state = resultJson.getString("state");
            String value = resultJson.getString("value");
            mPropellingMovementData.setState(state);
            mPropellingMovementData.setValue(value);
        }
        //funtion 5、6、7直接全跳过
        mPropellingMovementData.setOK(true);
        android.util.Log.e("propellingMovementFunction", "mPropellingMovementData:" + mPropellingMovementData.toString());
        return mPropellingMovementData;
    }


}
