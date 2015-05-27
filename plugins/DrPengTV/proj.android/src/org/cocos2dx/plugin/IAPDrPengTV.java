package org.cocos2dx.plugin;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class IAPDrPengTV implements InterfacePay {

	private static final String LOG_TAG = "IAPDrPeng";
	
	private static final String PAY_OK_CODE = "N000000";
	private static final int REQUEST_PAY_CASH_CODE = 6;
	
	private static Activity mContext = null;
	private static IAPDrPengTV mPayInstance = null;
	private static boolean mIsDebug = false;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPDrPengTV(Context context) {
		mContext = (Activity)context;
		mPayInstance = this;
	}
	
	// 常量的部分 放这里 这部分在发送请求前
	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
//    	BuyData.chargingName = cpInfo.get("chargingName");
//    	BuyData.chargingDuration = Long.valueOf(cpInfo.get("chargingDuration"));
//    	BuyData.partnerId = cpInfo.get("partnerId");
//    	BuyData.token = cpInfo.get("token");
//    	BuyData.packageName = mContext.getPackageName();
//    	BuyData.appendAttr = cpInfo.get("appendAttr");
	}

	// 有登录的 放这里
	@Override
	public void pluginLogin(Hashtable<String, String> cpInfo) {
		LogD("pluginLogin invoked " + cpInfo.toString());
		try{
			String loginInfoFlag = cpInfo.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}

	// 在这里处理实际发送交易请求给第三方的部分
	@Override
	public void payForProduct(Hashtable<String, String> cpInfo) {
		LogD("payForProduct invoked " + cpInfo.toString());
		if (! networkReachable()) {
			payResult(PayWrapper.PAYRESULT_FAIL, "网络不可用");
			return;
		}
		
		final Hashtable<String, String> productInfo = cpInfo;
		new AsyncTask<Integer, Object, String>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();  
            }
            @Override
            protected String doInBackground(Integer... params) {
            	/////////////////////////////////////////
            	Intent intent = new Intent();
            	intent.setAction("com.hiveview.pay.cashpay");//设置action
            	intent.addCategory("android.intent.category.DEFAULT");//设置参数
            	
            	BuyData.cashAmt = Integer.valueOf(productInfo.get("price"));
            	BuyData.productName = productInfo.get("productName");
            	
            	BuyData.chargingName = productInfo.get("chargingName");
            	BuyData.chargingDuration = Long.valueOf(productInfo.get("chargingDuration"));
            	BuyData.partnerId = productInfo.get("partnerId");
            	BuyData.packageName = mContext.getPackageName();
            	BuyData.token = productInfo.get("token");
            	BuyData.appendAttr = productInfo.get("appendAttr");
            	
            	intent.putExtra("cashAmt", ""+BuyData.cashAmt);//支付金额
            	intent.putExtra("productName", BuyData.productName);//产品名称
            	intent.putExtra("chargingName", BuyData.chargingName);//计费名称
            	intent.putExtra("chargingDuration", BuyData.chargingDuration);//计费时长
            	intent.putExtra("partnerId", BuyData.partnerId);//商户id
            	intent.putExtra("token", BuyData.token);//秘钥
            	intent.putExtra("packageName", BuyData.packageName);//包名
            	intent.putExtra("appendAttr", BuyData.appendAttr);//业务参数json
            	//使用返回式启动activity
            	mContext.startActivityForResult(intent, REQUEST_PAY_CASH_CODE);//REQUEST_PAY_CASH_CODE=6 现金支付对应的访问吗

            	return "";
            	/////////////////////////////////////////
            }
            protected void onPostExecute(String result) {
//            	Toast.makeText(mContext, result, 20).show();
            	super.onPostExecute(result);
            };
         }.execute();
	}

	private boolean networkReachable() {
		boolean bRet = false;
		try {
			ConnectivityManager conn = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conn.getActiveNetworkInfo();
			bRet = (null == netInfo) ? false : netInfo.isAvailable();
		} catch (Exception e) {
			LogE("Fail to check network status", e);
		}
		LogD("NetWork reachable : " + bRet);
		return bRet;
	}
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_PAY_CASH_CODE) {
			LogD("req code:" + requestCode + ", not DrPengTV pay");
			return ;
		}
		
		//返回json类型数组  格式{"code":"N000000","message":"错误获成功信息"}
		//N000000 为成功码 ，其余的返回系统对应的错误信息
		String json = data.getStringExtra("payCashResult");
		
		if (json != null) {
			String msg = "";
			try {
			    JSONTokener jsonParser = new JSONTokener(json);
			    JSONObject result = (JSONObject)jsonParser.nextValue();
			    String payResultCode = result.getString("code");
			    String payResultMsg = result.getString("message");
			    
			    if (payResultCode.equals(PAY_OK_CODE)) {
			    	IAPDrPengTV.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
			    	msg = "付款成功";
				} else {
					IAPDrPengTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
					msg = "付款失败,"+ payResultCode;
					LogD(msg + ":" + payResultMsg);
				}
			} catch (JSONException e) {
			    LogE("DrPeng pay failed!", e);
			    IAPDrPengTV.payResult(PayWrapper.PAYRESULT_FAIL, "支付异常");
			    if (mIsDebug) {
			    	msg = "付款异常" + e.getMessage();
				} else {
					msg = "付款异常";
				}
			} finally {
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		} else {
//			Toast.makeText(mContext, "异常的回调数据", Toast.LENGTH_SHORT).show();
			IAPDrPengTV.payResult(PayWrapper.PAYRESULT_FAIL, "异常的回调数据");
			LogD("异常的回调数据" + "json==null");
		}
	}
	
	// 这里处理交易结果
	public static void payResult(int ret, String msg){
		PayWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("DrPeng payResult:" + ret + " msg:" + msg);
	}
	
	@Override
	public void setDebugMode(boolean debug) {
		mIsDebug = debug;
	}

	@Override
	public String getSDKVersion() {
		return "1.2";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.1";
	}

}
