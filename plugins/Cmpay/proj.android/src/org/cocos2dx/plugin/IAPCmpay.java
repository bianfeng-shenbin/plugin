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

import android.app.Activity;
import android.util.Log;
import android.content.Context;

import mm.purchasesdk.Purchase;

public class IAPCmpay implements InterfacePay{
	
	//移动MM插件化参数
	private static final String LOG_TAG = "IAPCmpay";
	private static Activity mContext = null;
	private static IAPCmpay mCmpay = null;
	private static boolean bDebug = false;
	
	//移动MM支付参数
		//移动MM支付实例
		public static Purchase purchase;
	private IAPListener mListener;	
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPCmpay(Context context){
		mContext = (Activity)context;
		mCmpay = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String>info){
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String> devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{	
					String Cmpay_appId = devInfo.get("Cmpay_appId");
					String Cmpay_appKey = devInfo.get("Cmpay_appKey");
					
					//step1 实例化PurchaseListener
					IAPHandler iapHandler = new IAPHandler(mContext);
					mListener = new IAPListener(mContext, iapHandler);
					
					//step2 获取Purchase实例
					purchase = Purchase.getInstance();
					
					//step3 向Purhase传入应用信息APPID,APPKEY
					try{
						purchase.setAppInfo(Cmpay_appId, Cmpay_appKey);
					}catch(Exception e){
						LogE("SetAppInfo error!", e);
					}
					
					//step4 IAP组件初始化开始
					try{
						purchase.init(mContext, mListener);
					}catch(Exception e){
						LogE("Initialization error!", e);
					}
					
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}			
			}
		});
	}
	
	public void pluginLogin(Hashtable<String, String>info){
		LogD("pluginLogin invoked " + info.toString());
		try{
			String loginInfoFlag = info.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}
	
	public void payForProduct(Hashtable<String, String>info){
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String Cmpay_payCode = productInfo.get("productId");
					String Cmpay_orderId = productInfo.get("orderId");
					String strQuantity = productInfo.get("quantity");
					int Cmpay_quantity = Integer.parseInt(strQuantity);
					purchase.order(mContext, Cmpay_payCode, Cmpay_quantity, Cmpay_orderId, false, mListener);
				}catch(Exception e){
					LogE("Product info parse error!", e);	
				}			
			}
		});	
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mCmpay, ret, msg);
		LogD("Cmpay payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug){
		bDebug = debug;
	}
	
	public String getSDKVersion(){
		return "3.1.3";
	}
	
	public String getPluginVersion(){
		return "1.0.4";
	}	
	
}
