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

import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPUnipay implements InterfacePay{

	// 联通Unipay插件化参数
	private static final String LOG_TAG = "IAPUnipay";
	private static Activity mContext = null;
	private static IAPUnipay mUnipay = null;
	private static boolean bDebug = false;
	
	protected static void LogE(String msg, Exception e){ 
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPUnipay(Context context){
		mContext = (Activity)context;
		mUnipay = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		try{
			String devInfoFlag = info.get("flag");
			
			LogD("ConfigDeveloperInfo result: " + devInfoFlag);
		}catch(Exception e){
			LogE("Developer info parse error!", e);
		}
	}

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
					String number = productInfo.get("number");
					String tmpOrderid = productInfo.get("orderid");
					String orderid = "00000000" + tmpOrderid;
					
					LogD("Unipay OrderId is: " + orderid);
					
					Utils.getInstances().payOnline(mContext, number, "0", orderid, new UnipayPayResultListener(){
						public void PayResult(String paycode, int flag, int flag2,
								String error) {
							
							String strFlag2 = String.valueOf(flag2);
							LogD("Unipay PayResult Flag: " + strFlag2);
							
							switch (flag){
								case 1://success
									IAPUnipay.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
									break;

								case 2://fail
									IAPUnipay.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
									break;
								
								case 3://cancel
									IAPUnipay.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
									break;
								
								default:
									IAPUnipay.PayResult(PayWrapper.PAYRESULT_FAIL, "未知错误");
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
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mUnipay, ret, msg);
		LogD("Unipay payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "2.0.1";
	}
	
	public String getPluginVersion() {
		return "1.0.0";
	}
}