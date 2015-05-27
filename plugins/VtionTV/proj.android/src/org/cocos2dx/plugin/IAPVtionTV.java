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
import com.androids.app.payment.api.Payment;
import com.androids.app.payment.builder.PaymentBuilder;
import com.androids.app.payment.builder.PaymentBuilderRequest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.Context;

public class IAPVtionTV implements InterfacePay{
	
	//网讯TV插件化参数
	private static final String LOG_TAG = "IAPVtionTV";
	private static Activity mContext = null;
	private static IAPVtionTV mVtionTV = null;
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
	
	public IAPVtionTV(Context context){
		mContext = (Activity)context;
		mVtionTV = this;
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
					String channelID = productInfo.get("channelID");
					String strPrice = productInfo.get("price");
					Float fPrice = Float.parseFloat(strPrice);
					String orderId = productInfo.get("orderId");
					String callBackUrl = productInfo.get("callBackUrl");
					String iconUrl = productInfo.get("iconUrl");
					String productName = productInfo.get("productName");
					String productDesc = productInfo.get("productDesc");
					String productId = productInfo.get("productId");
					String strQuantity = productInfo.get("quantity");
					int iQuantity = Integer.parseInt(strQuantity);
					
					
					PaymentBuilder paymentBuilder=new PaymentBuilder();
					paymentBuilder.setPluginType(PaymentBuilder.Type.smartTV);
					paymentBuilder.setLoadPluginIndex(PaymentBuilder.Loader.onLine);
					paymentBuilder.setBuyID(PaymentBuilder.Prop.other);
					paymentBuilder.setChannelID(channelID);
					paymentBuilder.setPayMoney(fPrice);
					paymentBuilder.setDevOrderID(orderId);
					paymentBuilder.setDevBackURL(callBackUrl);
					paymentBuilder.setOrderIconUrl(iconUrl);
					paymentBuilder.setOrderSource("杭州边锋网络技术有限公司");
					paymentBuilder.setOrderTitle(productName);
					paymentBuilder.setOrderDesc(productDesc);
					paymentBuilder.setPropType(productId);
					paymentBuilder.setPropCounts(iQuantity);
					paymentBuilder.setHandler(new Handler(){
						public void handleMessage(Message message){
							super.handleMessage(message);
							PaymentBuilderRequest builderRequest=(PaymentBuilderRequest)message.obj;
							String handlerMessage="builderRequest.getPaymentResult():"+builderRequest.getPaymentResult()+"\n"+"builderRequest.getPaymentID():"+builderRequest.getPaymentID()+"\n"+"builderRequest.getPaymentError():"+builderRequest.getPaymentError()+"\n"+"builderRequest.getVersionCode():"+builderRequest.getVersionCode();
							Log.i(LOG_TAG, handlerMessage);
							if(true == builderRequest.getPaymentResult()){
								IAPVtionTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
							}else{
								IAPVtionTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
							}
						}
					});
					
					Payment payment=new Payment(mContext);
					payment.callStart(mContext, paymentBuilder);
					
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
//	@SuppressLint("HandlerLeak") 
//	Handler payCallBackHandler = new Handler(){
//		public void handleMessage(Message message){
//			super.handleMessage(message);
//			PaymentBuilderRequest builderRequest=(PaymentBuilderRequest)message.obj;
//			String handlerMessage="builderRequest.getPaymentResult():"+builderRequest.getPaymentResult()+"\n"+"builderRequest.getPaymentID():"+builderRequest.getPaymentID()+"\n"+"builderRequest.getPaymentError():"+builderRequest.getPaymentError()+"\n"+"builderRequest.getVersionCode():"+builderRequest.getVersionCode();
//			Log.i(LOG_TAG, handlerMessage);
//			if(true == builderRequest.getPaymentResult()){
//				IAPVtionTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
//			}else{
//				IAPVtionTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
//			}
//		}
//	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mVtionTV, ret, msg);
		LogD("VtionTV payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.1.4.32";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}