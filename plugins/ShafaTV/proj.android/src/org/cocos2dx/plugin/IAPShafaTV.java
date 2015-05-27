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

import org.json.JSONException;
import org.json.JSONObject;

import com.xmxgame.pay.PayInfo;
import com.xmxgame.pay.XMXPayManager;
import com.xmxgame.pay.XMXPayManager.PayCallback;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPShafaTV implements InterfacePay{

	//沙发支付TV插件化参数
	private static final String LOG_TAG = "IAPShafaTV";
	private static Activity mContext = null;
	private static IAPShafaTV mShafaTV = null;
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
	
	public IAPShafaTV(Context context){
		mContext = (Activity)context;
		mShafaTV = this;
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
					String orderId = productInfo.get("orderId");
					String callBackUrl = productInfo.get("callBackUrl");
					String productName = productInfo.get("productName");
					String strQuantity = productInfo.get("quantity");
					int iQuantity = Integer.parseInt(strQuantity);
					String strPrice = productInfo.get("price");
					Float fPrice = Float.parseFloat(strPrice);
					
					PayInfo payInfo = new PayInfo();
					JSONObject extraParam = new JSONObject();
					try{
						extraParam.put("orderId", orderId);
					}catch(JSONException e){
						LogE("Extra Param set Error!", e);
					}
					payInfo.setCustomData(extraParam);
					payInfo.setNotifyUrl(callBackUrl);
					payInfo.setName(productName);
					payInfo.setQuantity(iQuantity);
					payInfo.setPay_Way(1);
					payInfo.setPrice(fPrice);
					XMXPayManager.getInstance(mContext).pay(payInfo, payCallback);
					
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	PayCallback payCallback = new PayCallback(){

		public void OnStatusCallback(int status, PayInfo info) {
			switch(status){
			case XMXPayManager.STATUS_ORDER_BACK:
                if (null != info) {
//                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_FAIL, "订单信息返回");
                	Log.v(LOG_TAG, "订单信息返回");
                }
                break;
            case XMXPayManager.STATUS_ORDER_REQUEST_ERROR:
                if (null != info) {
//                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_FAIL, "订单信息请求出错");
                	Log.v(LOG_TAG, "订单信息请求出错");
                }
                break;
            case XMXPayManager.STATUS_ORDER_STATUS_SUCCESS:
                if (null != info) {
                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
                }
                break;
            case XMXPayManager.STATUS_ORDER_STATUS_OPEN:
                if (null != info) {
                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
                	Log.e(LOG_TAG, "订单状态 打开");
                }
                break;
            case XMXPayManager.STATUS_ORDER_STATUS_CLOSE:
                if (null != info) {
                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
                	Log.e(LOG_TAG, "订单状态 关闭");
                }
                break;
            case XMXPayManager.STATUS_ORDER_STATUS_ERROR:
                if (null != info) {
                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_FAIL, "请求订单状态出错");
                }
                break;
            case XMXPayManager.STATUS_ERROR_OVER_TIME:
                if (null != info) {
                	IAPShafaTV.PayResult(PayWrapper.PAYRESULT_TIMEOUT, "请求订单超时");
                }
                break;
            default:
                break;
			}
		}
	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mShafaTV, ret, msg);
		LogD("ShafaTV payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.0.0";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}