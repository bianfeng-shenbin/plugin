/****************************************************************************
Copyright (c) 2012-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/

/**
 * @author wangzhe01
 */
package org.cocos2dx.plugin;

import java.util.Hashtable;

import com.dapai178.qfsdk.QFSDK;
import com.dapai178.qfsdk.common.QFStatusCode;
import com.dapai178.qfsdk.transaction.QFExchange;
import com.dapai178.qfsdk.transaction.QFExchangeCallback;
import com.dapai178.qfsdk.transaction.QFExchangeResult;
import com.dapai178.qfsdk.transaction.QFGetAccountInfoCallback;
import com.dapai178.qfsdk.transaction.QFGetAccountInfoRequest;
import com.dapai178.qfsdk.transaction.QFGetAccountInfoResult;
import com.dapai178.qfsdk.transaction.QFPay;
import com.dapai178.qfsdk.transaction.QFPayCallback;
import com.dapai178.qfsdk.transaction.QFPayResult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class IAPQifan implements InterfacePay, PluginListener{

	//起凡插件化参数
	private static final String LOG_TAG = "IAPQifan";
	private static Activity mContext = null;
	private static IAPQifan mQifan = null;
	private static boolean bDebug = false;
	private static String text_userid = "";
	private static String text_qicount = "";
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPQifan(Context context){
		mContext = (Activity)context;
		mQifan = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String> devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					final String appid = getApplicationMetaDataValue(mContext, "QF_APP_ID");
					final String appkey = getApplicationMetaDataValue(mContext, "QF_APP_KEY");
					
					Intent intent = mContext.getIntent();
					if (appid != null && appkey != null) {
						//isPortrait 是否是竖屏显示 
						QFSDK.init(mContext, intent, appid, appkey, false);
					} else {
						LogD("no configure appid or appkey!");
					}	
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}
	
	public String getApplicationMetaDataValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				Object value = metaData.get(metaKey);
				if (value != null) {
					apiKey = String.valueOf(value);
				}
			}
		} catch (NameNotFoundException e) {
			LogD("无法获取到meta-data，key：" + metaKey);
		}
		return apiKey;
	}

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String loginInfoFlag = loginInfo.get("flag");
					
					QFSDK.getAccountInfo(new QFGetAccountInfoCallback() {
						public void onCallback(QFGetAccountInfoResult result, QFGetAccountInfoRequest arg1) {
							if(result.statusCode == QFStatusCode.SUCCESS)			
							{					
								text_userid = result.userId;
								text_qicount = result.qfCoinCount + "";
								LogD("登录成功:userId = " + text_userid + " qfCoinCount = " + text_qicount);
								IAPQifan.LoginResult(PayWrapper.LOGINRESULT_SUCCESS,"登录成功");
							}
							else if(result.statusCode == QFStatusCode.ERROR_USER_NOT_LOGIN)
							{
								LogD("登录失败:用户未登录");
								IAPQifan.LoginResult(PayWrapper.LOGINRESULT_FAIL,"用户未登录");
							}
							else
							{
								LogD("登录失败");
								IAPQifan.LoginResult(PayWrapper.LOGINRESULT_FAIL,"登录失败");
							}
						}
					});	
					LogD("ConfigDeveloperInfo result: " + loginInfoFlag);
				}catch(Exception e){
					LogE("Login info parse error!", e);
				}
			}
		});
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
				public void run(){
					try{
						String loginFlag = productInfo.get("payFlag");
						int flag = Integer.parseInt(loginFlag);
						
						String productId = productInfo.get("productId");
						String productName = productInfo.get("productName");
						String strPrice = productInfo.get("price");
						int price = (int)Float.parseFloat(strPrice);
						String gameOrder = productInfo.get("gameOrder");
						
						switch(flag){
						
							//综合收银台支付
							case 0:
								final QFPay pay = new QFPay();
								pay.productId = productId;
								pay.productName = productName;
								pay.price = price;
								pay.gameOrder = gameOrder;
								pay.needValidateOrder = false;
								boolean bQuickpay = price <= 10;
								pay.isQuickPay = bQuickpay;
								
								QFSDK.pay(pay, new QFPayCallback(){

									public void onCallback(QFPayResult result, QFPay request) {
										if (result.isSuccess()) {
											// 支付成功
											IAPQifan.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
										} else if (QFStatusCode.CANCEL == result.statusCode) {
											// 取消支付
											IAPQifan.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
										} else if (QFStatusCode.ERROR_ACCESS_NETWORK == result.statusCode) {
											// 无法连接网络
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败，无法连接网络");
										} else if(QFStatusCode.HIDEMESSAGE == result.statusCode){
											// 支付失败，但不提示
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败，但不提示");
										} else {
											// 支付失败
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
										}
									}
								});	
								break;
								
								
							//起凡币兑换
							case 1:
								final QFExchange exchange = new QFExchange();
								exchange.productId = productId;
								// 起凡币 代币支付 比例1：1
								exchange.requiredCoins = 2;
								exchange.gameOrder = gameOrder;

								QFSDK.exchange(exchange, new QFExchangeCallback() {
									public void onCallback(QFExchangeResult result, QFExchange request) {
										if (result.isSuccess()) {
											// 兑换成功
											IAPQifan.PayResult(PayWrapper.PAYRESULT_SUCCESS, "兑换成功");
										} else if (QFStatusCode.CANCEL == result.statusCode) {
											// 取消兑换
											IAPQifan.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消兑换");
										} else if (QFStatusCode.ERROR_ACCESS_NETWORK == result.statusCode) {
											// 无法连接网络
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "兑换失败，无法连接网络");
										}  else if (QFStatusCode.ERROR_NO_ENOUGH_COINS == result.statusCode) {
											// 起凡币不足
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "兑换失败，起凡币不足");
										} 
										else if (QFStatusCode.ERROR_INVALID_APP == result.statusCode) {
											// 无效应用
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "兑换失败，无效应用");
										} 
										else if (QFStatusCode.ERROR_USER_DENIED == result.statusCode) {
											// 用户错误
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "兑换失败，用户错误");
										} 				
										else {
											// 兑换失败
											IAPQifan.PayResult(PayWrapper.PAYRESULT_FAIL, "兑换失败");
										}
									}
								});
								break;
								
							default:
								break;
						}		
					}catch(Exception e){
						LogE("Product info parse error!", e);
					}
				}
			});
		}
	
	public static void LoginResult(int ret, String msg) {
		LogD("Qifan LoginResult start Jion");
		Hashtable<String, String> HashMsg = new Hashtable<String, String>();
		HashMsg.put("useid", text_userid);
		HashMsg.put("QFcode", text_qicount);
		HashMsg.put("msg", msg);
		
		PayWrapper.onLoginResult(mQifan, ret, HashMsg);
		LogD("Qifan LoginResult : " + ret + " msg : " + msg);
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mQifan, ret, msg);
		LogD("Qifan payResult : " + ret + " msg : " + msg);
	}	

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "2.0";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
	
	public void onDestroy() {
		QFSDK.dispose();
	}

	public void onResume() {
		
	}

	public void onPause() {
		
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	public void onNewIntent(Intent intent) {
		
	}
}