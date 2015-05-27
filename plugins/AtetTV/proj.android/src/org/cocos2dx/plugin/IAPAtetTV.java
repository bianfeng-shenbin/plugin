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

import com.atet.api.SDKApi;
import com.atet.api.pay.ui.service.IPayResultCallback;
import com.atet.api.pay.ui.service.PayRequest;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPAtetTV implements InterfacePay{
	
	//联通ATET TV插件化参数
	private static final String LOG_TAG = "IAPAtetTV";
	private static Activity mContext = null;
	private static IAPAtetTV mAtetTV = null;
	private static boolean bDebug = false;
	
	//联通ATET TV初始化参数
	private static final String Atet_cpId = "20140725103337526118";
	private static String Atet_appID = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPAtetTV(Context context){
		mContext = (Activity)context;
		mAtetTV = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					Atet_appID = devInfo.get("Atet_appID");
					
					SDKApi.init(mContext, SDKApi.LANDSCAPE, Atet_appID, false);
					
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
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
					String Atet_curl = productInfo.get("Atet_curl");
					String Atet_appKey = productInfo.get("Atet_appKey");
					String Atet_waresid = productInfo.get("productId");
					String Atet_waresname = productInfo.get("productName");
					String strPrice = productInfo.get("price");
					int iPrice = (int)Float.parseFloat(strPrice);
					String strQuantity = productInfo.get("quantity");
					int iQuantity = Integer.parseInt(strQuantity);
					String Atet_orderId = productInfo.get("orderId");
					
					PayRequest payRequest = new PayRequest();
					payRequest.addParam(PayRequest.NOTIFYURL, Atet_curl);
					Log.d(LOG_TAG, "ATET CallBack URL: " + Atet_curl);
					payRequest.addParam(PayRequest.APPKEY, Atet_appKey);
					payRequest.addParam(PayRequest.APPID, Atet_appID);
					payRequest.addParam(PayRequest.CPID, Atet_cpId);
					payRequest.addParam(PayRequest.WARESID, Atet_waresid);
					payRequest.addParam(PayRequest.WARESNAME, Atet_waresname);
					payRequest.addParam(PayRequest.PRICE, iPrice * 100);
					payRequest.addParam(PayRequest.QUANTITY, iQuantity);
					payRequest.addParam(PayRequest.EXORDERNO, Atet_orderId);
					
					SDKApi.startPay(mContext, payRequest, resultListener);
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	IPayResultCallback resultListener = new IPayResultCallback(){
		public void onPayResult(int resultCode, String resultInfo) {
            //resultInfo = 应用编号&商品编号&外部订单号
            if (resultCode == SDKApi.PAY_SUCCESS ) {
            	IAPAtetTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
            } else if(SDKApi.PAY_CANCEL == resultCode){
            	IAPAtetTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
            }else {
            	IAPAtetTV.PayResult(PayWrapper.PAYRESULT_FAIL, "未知错误");
            }
		}
	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mAtetTV, ret, msg);
		LogD("AtetTV payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "4.0.1";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}