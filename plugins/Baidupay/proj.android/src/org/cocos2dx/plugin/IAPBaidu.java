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

import com.baidu.gamesdk.ActivityAdPage;
import com.baidu.gamesdk.ActivityAdPage.Listener;
import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

public class IAPBaidu implements InterfacePay, PluginListener{

	//百度插件化参数
	private static final String LOG_TAG = "IAPBaidu";
	private static Activity mContext = null;
	private static IAPBaidu mBaidu = null;
	private static boolean bDebug = false;
	private ActivityAdPage mActivityAdPage;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPBaidu(Context context){
		mContext = (Activity)context;
		mBaidu = this;
		changeAccount();
	}
	
	public void onResume() {
		mActivityAdPage.onResume();
	}

	public void onPause() {
		mActivityAdPage.onPause();
	}

	public void onDestroy() {
		BDGameSDK.destroy();
		mActivityAdPage.onDestroy();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	public void onNewIntent(Intent intent) {
		
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String> devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					int appID = Integer.getInteger(devInfo.get("appID"));
					String appKey = devInfo.get("appKey");
					
					BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
					mBDGameSDKSetting.setAppID(appID);// 5331394
					mBDGameSDKSetting.setAppKey(appKey);// 7DizYL8Xt1IpcFsinHvS6vwd
					mBDGameSDKSetting.setDomain(Domain.RELEASE);//TODO release
					
					BDGameSDK.init(mContext, mBDGameSDKSetting,
							new IResponse<Void>() {
						
								public void onResponse(int resultCode,
										String resultDesc, Void extraData) {
									switch (resultCode) {
									case ResultCode.INIT_SUCCESS:
										LogD("加载插件成功");
										break;

									case ResultCode.INIT_FAIL:
									default:
										LogD("加载插件失败");
									}
								}
							});
					
					mActivityAdPage = new ActivityAdPage(mContext, new Listener(){

						public void onClose() {
							LogD("暂停页onClose 继续游戏");
						}
					});
					
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String loginInfoFlag = loginInfo.get("flag");
					
					BDGameSDK.login(new IResponse<Void>() {
						@Override
						public void onResponse(int resultCode, String resultDesc, Void extraData) {
							Log.d("login", "this resultCode is " + resultCode);
							switch (resultCode) {
							case ResultCode.LOGIN_SUCCESS:
								String accessToken = BDGameSDK.getLoginAccessToken();
								IAPBaidu.loginResult(PayWrapper.LOGINRESULT_SUCCESS, accessToken);
								break;
								
							case ResultCode.LOGIN_CANCEL:
								Log.e(LOG_TAG, "百度支付  用户取消");
								IAPBaidu.loginResult(PayWrapper.LOGINRESULT_CANCEL, "");
								break;
								
							case ResultCode.LOGIN_FAIL:
							default:
								Log.e(LOG_TAG, "百度支付  登录失败" + resultDesc);
								IAPBaidu.loginResult(PayWrapper.LOGINRESULT_FAIL, "");
								break;
							}
						}
					});	
					
					LogD("PluginLogin result: " + loginInfoFlag);
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
					String orderId = productInfo.get("orderId");
					String productName = productInfo.get("productName");
					String strPrice = productInfo.get("price");
					long lPrice = (long)Float.parseFloat(strPrice);
					
					
					PayOrderInfo payOrderInfo = new PayOrderInfo(); 
					payOrderInfo.setCooperatorOrderSerial(orderId); //CP订单号 
					payOrderInfo.setProductName(productName); //商品名称 
					payOrderInfo.setTotalPriceCent(lPrice*100);//以分为单位，long类型 
					payOrderInfo.setRatio(1); //兑换比例，此时不生效 
					payOrderInfo.setExtInfo("");//该字段在支付通知中原样返回,不超过500个字符
					
					BDGameSDK.pay(payOrderInfo, null,
							new IResponse<PayOrderInfo>() {
								@Override
								public void onResponse(int resultCode,
										String resultDesc,
										PayOrderInfo extraData) {
									switch (resultCode) {
									case ResultCode.PAY_SUCCESS:
										IAPBaidu.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功:" + resultDesc);
										break;
									case ResultCode.PAY_CANCEL:
										IAPBaidu.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
										break;
									case ResultCode.PAY_FAIL:
										IAPBaidu.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败：" + resultDesc);
										break;
									case ResultCode.PAY_SUBMIT_ORDER:// 订单已经提交，支付结果未知（比如：已经请求了，但是查询超时）
										IAPBaidu.PayResult(PayWrapper.PAYRESULT_TIMEOUT, "订单已经提交，支付结果未知");
										break;
									}
								}
							});
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	public void changeAccount(){
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				BDGameSDK.setSuspendWindowChangeAccountListener(new IResponse<Void>(){
					  public void onResponse(int resultCode, String resultDesc, Void extraData) {
					    switch(resultCode){
					      case ResultCode.LOGIN_SUCCESS:
					    	  String accessToken = BDGameSDK.getLoginAccessToken();
					    	  IAPBaidu.loginResult(PayWrapper.LOGINRESULT_SUCCESS, accessToken);
					    	break;
					      case ResultCode.LOGIN_FAIL:
					    	  Log.e(LOG_TAG, "切换账号 百度支付  登录失败" + resultDesc);
					    	  IAPBaidu.loginResult(PayWrapper.LOGINRESULT_FAIL, "");
					    	break;
					      case ResultCode.LOGIN_CANCEL:
					    	  Log.e(LOG_TAG, "切换账号 百度支付  用户取消");
					    	  IAPBaidu.loginResult(PayWrapper.LOGINRESULT_CANCEL, "");
					    	break;
					   }
					}    
				});
			}
		});
	}
	
	public static void loginResult(int ret, String accessToken){
		Hashtable<String, String> info = new Hashtable<String, String>();
		info.put("accessToken", accessToken);
		
		PayWrapper.onLoginResult(mBaidu, ret, info);
		LogD("BaiduPay loginResult : " + ret + " info : " + info);
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mBaidu, ret, msg);
		LogD("BaiduPay payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "3.1.3";
	}

	public String getPluginVersion() {
		return "1.0.1";
	}
}