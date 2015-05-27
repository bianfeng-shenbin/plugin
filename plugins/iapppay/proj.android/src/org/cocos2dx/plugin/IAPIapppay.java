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

import java.net.URLEncoder;
import java.util.Hashtable;

import org.json.JSONObject;

import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.iapppay.utils.RSAHelper;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPIapppay implements InterfacePay{

	//爱贝插件化参数
	private static final String LOG_TAG = "IAPIappPay";
	private static Activity mContext = null;
	private static IAPIapppay mIappPay = null;
	private static boolean bDebug = false;
	
	private static String AB_appid = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPIapppay(Context context){
		mContext = (Activity)context;
		mIappPay = this;
	}	
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String loginFlag = devInfo.get("screenOrient");
					int flag = Integer.parseInt(loginFlag);
					// flag = 0 : IAppPay.LANDSCAPE
					// flag = 1 : IAppPay.PORTRAIT
					
					AB_appid = devInfo.get("appId");
					IAppPay.init(mContext, flag, AB_appid);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String genUrl = genUrl(productInfo);
					
					IAppPay.startPay(mContext, genUrl, new IPayResultCallback(){

						public void onPayResult(int resultCode, String signValue, String resultInfo) {
							switch (resultCode) {
							
							case IAppPay.PAY_SUCCESS:
								IAPIapppay.payResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
								break;
							case IAppPay.PAY_SMSING:
								IAPIapppay.payResult(PayWrapper.PAYRESULT_SUCCESS, "已经下单成功，等待核实");
								break ;
							case IAppPay.PAY_CANCEL:
								IAPIapppay.payResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
								break;
							case IAppPay.PAY_ERROR:
								IAPIapppay.payResult(PayWrapper.PAYRESULT_FAIL, "支付错误");
								break;
							default:
								IAPIapppay.payResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
								break;
							}
							LogD("requestCode:"+resultCode + ",signvalue:" + signValue + ",resultInfo:"+resultInfo);
						}
					});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private String genUrl( Hashtable<String, String> productInfo) {
		String json = "";

		JSONObject obj = new JSONObject();
		try {
			String waresid = productInfo.get("waresid");
			int index = waresid.indexOf(".");
			String sChargePoint = waresid.substring(index+1, waresid.length());
			int nWaresid = Integer.parseInt(sChargePoint);
			String orderId = productInfo.get("orderId");
			String price = productInfo.get("price");
			double dPrice = Double.valueOf(price);
			String appUserid = productInfo.get("appUserid");
			
			obj.put("appid", AB_appid);
			obj.put("waresid", nWaresid);
			obj.put("cporderid", orderId);
			obj.put("appuserid", appUserid);
			obj.put("price", dPrice);//单位 元
			obj.put("currency", "RMB");//如果做服务器下单 该字段必填
			//obj.put("waresname", "客户端传入名称");//开放价格名称(用户可自定义，如果不传以后台配置为准)
	
			/*CP私有信息，选填*/
			//obj.put("cpprivateinfo", "");
			//obj.put("notifyurl", "");
			
			json = obj.toString();
			
			LogD("genUrl: " + json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sign = "";
		try {
			String cppk = productInfo.get("AB_appkey");
			LogD("AB_appkey: " + cppk);
			sign = RSAHelper.signForPKCS1(json, cppk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String genUrlData = "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
		LogD("genUrlData: " + genUrlData);
		return  "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
	}
	
	private static void payResult(int ret, String msg) {
		PayWrapper.onPayResult(mIappPay, ret, msg);
		LogD("IappPay result : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "3.4.3";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}