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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;

import com.alipay.sdk.app.PayTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

public class IAPAlipay implements InterfacePay{

	//支付宝插件化参数
	private static final String LOG_TAG = "IAPAlipay";
	private static Activity mContext = null;
	private static IAPAlipay mAlipay = null;
	private static boolean bDebug = false;
	
	public static String PARTNER = null;
	public static String SELLER = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPAlipay(Context context){
		mContext = (Activity)context;
		mAlipay = this;
	}

	public void configDeveloperInfo(Hashtable<String, String> info) {
	}

	public void pluginLogin(Hashtable<String, String> info) {
	}
	
	public String getOrderInfo(String orderId, String name, String desc, String price) {
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";
		orderInfo += "&subject=" + "\"" + name + "\"";
		orderInfo += "&body=" + "\"" + desc + "\"";
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://mapi2.bianfeng.com/v1/pay/notify/alitv" + "\"";
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";
		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		orderInfo += "&it_b_pay=\"30m\"";
		return orderInfo;
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String> productInfo = info;
		
		PluginWrapper.runOnMainThread(new Runnable() {
			public void run() {
				String name = "";
				String desc = "";
				String price = "";
				String orderId = "";
				String RSA_PRIVATE = "";

				try {
					orderId = productInfo.get("orderId");
					name = productInfo.get("name");
					desc = productInfo.get("desc");
					price = productInfo.get("price");
					PARTNER = productInfo.get("PARTNER");
					SELLER = productInfo.get("SELLER");
					RSA_PRIVATE = productInfo.get("RSA_PRIVATE");
				} catch (Exception e) {
					LogE("pay parameter wrong!", e);
				}
				final String orderInfo = getOrderInfo(orderId, name, desc,
						price);
				String sign = SignUtils.sign(orderInfo, RSA_PRIVATE);
				try {
					sign = URLEncoder.encode(sign, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
						+ "sign_type=\"RSA\"";

				BuyTask buyTask = new BuyTask();
				buyTask.execute(payInfo);
			};
		});
	}
	
	class BuyTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... arg0) {
			PayTask alipay = new PayTask(mContext);
			String result = alipay.pay(arg0[0]);
			return result;
		}
		
		protected void onPostExecute(String result) {
			PayResult payResult = new PayResult(result);
			String resultStatus = payResult.getResultStatus();

			LogD("{}{}{}" + resultStatus);
			if (TextUtils.equals(resultStatus, "9000")) {
				Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
				IAPAlipay.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
			} else {
				if (TextUtils.equals(resultStatus, "8000")) {
					Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
					IAPAlipay.PayResult(PayWrapper.PAYRESULT_FAIL, "支付结果确认中");
				} else {
					Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
					IAPAlipay.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
				}
			}
		}
	}
	
	class CheckTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... arg0) {
			PayTask payTask = new PayTask(mContext);
			boolean isExist = payTask.checkAccountIfExist();
			return isExist;
		}
		
		protected void onPostExecute(Boolean isExist) {
			LogD("********************** isExist = " + isExist);
		}
	}
	
	public void check(View v) {
		LogD("------------------------check called-----------------\n");
		PluginWrapper.runOnMainThread(new Runnable() {

			public void run() {
				CheckTask checkTask = new CheckTask();
				checkTask.execute();
			}});
	}
	
	public static void PayResult(int ret, String msg) {
		PayWrapper.onPayResult(mAlipay, ret, msg);
		LogD("Alipay payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		PayTask payTask = new PayTask(mContext);
		String version = payTask.getVersion();
		return version;
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}