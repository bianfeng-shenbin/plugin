package org.cocos2dx.plugin;

import java.util.Hashtable;

import org.cocos2dx.aliscan.AliPay;
import org.cocos2dx.aliscan.AliPayListener;
import org.cocos2dx.aliscan.BuyData;
import org.cocos2dx.aliscan.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class IAPAliScanTV implements InterfacePay {

	private static final String LOG_TAG = "IAPAliScanTV";
	
	private static Activity mContext = null;
	private static IAPAliScanTV mPayInstance = null;
	private static boolean mIsDebug = false;
	
	private static AliPay mAliPay = null;
	private static BuyData mBuyData = new BuyData();
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPAliScanTV(Context context) {
		mContext = (Activity)context;
		mPayInstance = this;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				initHandler(mContext);
			}
		});
	}
	
	// 常量的部分 放这里 这部分在发送请求前
	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {

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
//        		mBuyData.mPartnerID = "2088311883949789";//"2088201564884561";//
//        		mBuyData.mTargetID = "天猫盒子";// 实际使用用户ID
//        		mBuyData.mTargetLogoUrl = "http://10.0.64.132:8050/epgdata/ImageAD/9000000564501.jpg";//"";
//        		mBuyData.mMD5Key = "wz74kqmwo1toiu9ouz2wscdewbc2c3fx";//"1wvbik6oiohycg3v7gfwxgsjw7dt81r6";//
//        		mBuyData.mTradeNo = "TV2014";
//        		mBuyData.mProductName = "欢乐豆10颗";
//        		mBuyData.mProductPrice = "0.01";
//        		mBuyData.mNotifyUrl = "http://mapi2.bianfeng.com/v1/pay/notify/hisense";
//        		mBuyData.mSeller = "tvgame@bianfeng.com";
            	mBuyData.mPartnerID = productInfo.get("partnerID");
            	mBuyData.mTargetID = productInfo.get("trargetID");
            	mBuyData.mTargetLogoUrl = productInfo.get("targetLogoUrl");
            	mBuyData.mNotifyUrl = productInfo.get("notifyUrl");
            	mBuyData.mMD5Key = productInfo.get("md5Key");
            	mBuyData.mTradeNo = productInfo.get("tradeNo");
            	mBuyData.mProductPrice = productInfo.get("productPrice");
            	mBuyData.mProductName = productInfo.get("productName");
            	mBuyData.mSeller = productInfo.get("seller");
            	
            	mAliPay.payProduct(mBuyData);

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
	
	public static void initHandler(Context context) {
		mAliPay = new AliPay();
		mAliPay.init(context, new AliPayListener() {
			
			@Override
			public void onAliPayFinished(boolean isSucceed, String msg) {
				LogD("===========支付结果:"+isSucceed+", 消息："+msg+"===========");
				
				String toastMsg = "";
				if (isSucceed) {
					IAPAliScanTV.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
					toastMsg = "付款成功";
				} else {
					IAPAliScanTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
					LogD("msg:" + msg);
					toastMsg = "付款失败";
				}
				
				Toast.makeText(mContext, toastMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	// 这里处理交易结果
	public static void payResult(int ret, String msg){
		PayWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("DrPeng payResult:" + ret + " msg:" + msg);
	}
	
	@Override
	public void setDebugMode(boolean debug) {
		mIsDebug = debug;
		// 设置内部debug状态
		Utils.SetDebugMode(debug);
	}

	@Override
	public String getSDKVersion() {
		return "2.1";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.1";
	}

}
