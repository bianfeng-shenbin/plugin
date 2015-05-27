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
import com.mi.ui.ChannelActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.Context;

public class IAPVoginsTV implements InterfacePay{
	
	//联通VoginsTV插件化参数
	private static final String LOG_TAG = "IAPVoginsTV";
	private static Activity mContext = null;
	private static IAPVoginsTV mVoginsTV = null;
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
	
	public IAPVoginsTV(Context context){
		mContext = (Activity)context;
		mVoginsTV = this;
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

	@SuppressLint("HandlerLeak") 
	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String channelId = productInfo.get("channelId");
					String appId = productInfo.get("appId");
					String payId = productInfo.get("payId");
					String propsName = productInfo.get("propsName");
					String strPayPrice = productInfo.get("price");
					int iPrice = (int)Float.parseFloat(strPayPrice);
					String orderId = productInfo.get("orderId");
					
					ChannelActivity.pay(mContext, channelId, appId, payId, propsName, iPrice * 100, orderId, new Handler(){
						public void handleMessage(Message msg){
							super.handleMessage(msg);
							switch(msg.arg1){
								case 1:
									IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
									break;
								case 2:
									IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "支付取消");
									break;
								case 3:
									IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
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
	
//	@SuppressLint("HandlerLeak") 
//	private Handler payHandler = new Handler(){
//		public void handleMessage(Message msg){
//			super.handleMessage(msg);
//			switch(msg.arg1){
//				case 1:
//					IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
//					break;
//				case 2:
//					IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "支付取消");
//					break;
//				case 3:
//					IAPVoginsTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
//					break;
//				
//			}
//		}
//	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mVoginsTV, ret, msg);
		LogD("VoginsTV payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "2.1.8";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}