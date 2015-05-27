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

import com.coocaa.ccapi.ApiDefData;
import com.coocaa.ccapi.CcApi;
import com.coocaa.ccapi.CcApi.LoginCallBack;
import com.coocaa.ccapi.CcApi.PurchaseCallBack;
import com.coocaa.ccapi.OrderData;
import com.coocaa.ccapi.UserInfo;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPSkyworthTV implements InterfacePay{

	//创维酷开插件化参数
	private static final String LOG_TAG = "IAPSkyworthTV";
	private static Activity mContext = null;
	private static IAPSkyworthTV mSkyworthTV = null;
	private static boolean bDebug = false;
	private CcApi api = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPSkyworthTV(Context context){
		mContext = (Activity)context;
		mSkyworthTV = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					api = new CcApi(mContext);
					
					api.Login(loginCallBack);
					
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}
	
	LoginCallBack loginCallBack = new LoginCallBack(){

		public void lgBack(UserInfo userInfo) {
			if(userInfo.loginstatus == ApiDefData.LOGINFORBID){
				Log.e(LOG_TAG, "----------非法终端，登录失败！！----------");
			}else if(userInfo.loginstatus == ApiDefData.LOGINERROR){
				Log.e(LOG_TAG, "----------登录失败！！----------");
			}else if(userInfo.loginstatus == ApiDefData.LOGINPASS){
				Log.i(LOG_TAG, "注册成功: " + " 等级： " + userInfo.userlever + " , 手机号： " + userInfo.tel);
			}
		}
	};

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		try{
			String loginInfoFlag = info.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String appCode = productInfo.get("appCode");
					String productName = productInfo.get("productName");
					String productType = productInfo.get("productType");
					String specialType = productInfo.get("specialType");
					String strAmount = productInfo.get("amount");
					Float fAmount = Float.parseFloat(strAmount);
					String tradeId = productInfo.get("tradeId");
					
					OrderData order = new OrderData();
					order.setappcode(appCode);
					order.setProductName(productName);
					order.setProductType(productType);
					order.setSpecialType(specialType);
					order.setamount(fAmount);
					order.setTradeId(tradeId);
					
					api.purchase(order, purchaseCallBack);
					
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	PurchaseCallBack purchaseCallBack = new PurchaseCallBack(){

		public void pBack(int resultstatus, String tradeid, String uslever, String resultmsg,
				double balance, String purchWay) {
			if(resultstatus == 0){
				IAPSkyworthTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
			}else if(resultstatus == 1){
				IAPSkyworthTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
			}else {
				IAPSkyworthTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "未知错误");
			}
		}
	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mSkyworthTV, ret, msg);
		LogD("SkyworthTV payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "0.2";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}