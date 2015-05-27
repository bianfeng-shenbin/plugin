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
package org.cocos2dx.plugin;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import com.yunos.mc.MagicCenter;
import com.yunos.mc.MagicCenter.IInitListener;
import com.yunos.mc.McSDK.SDK_TYPE;
import com.yunos.mc.pay.GetTokenParams;
import com.yunos.mc.pay.McBaodianPay;
import com.yunos.mc.pay.McBaodianPay.IConsumeListener;
import com.yunos.mc.pay.McBaodianPay.IGetConsumeTokenListener;
import com.yunos.mc.pay.McBaodianPay.OnStartPayListener;
import com.yunos.mc.utils.AliBaseError;
import com.yunos.mc.utils.McLog;
import com.yunos.mc.user.McUser;
import com.yunos.mc.user.AuthManager.IGetUserinfoListener;
import com.yunos.mc.user.McUser.IAuthListener;
import com.yunos.tv.baodian.utils.TBSUtils;
import com.de.aligame.topsdk.service.TopEvnConfig;
import com.de.aligame.tv.models.BaodianUserInfo;
import com.de.aligame.tv.models.TokenBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class IAPAlipay implements InterfacePay {

	private static final String LOG_TAG = "IAPAlipay";
	private static Activity mContext = null;
	private static boolean bDebug = false;
	private static Handler mHandler = null;
	private static IAPAlipay mAdapter = null;
	
	private static String mAliPayTV_key = null;
	private static String mAliPayTV_secret = null;
	
	private static TokenBean mTokenBean = null;
	private static boolean isGettingConsumeToken = false;
	private static Object lockObj = new Object();
	private static String mAlipayTV_price = null;
	private static String mAlipayTV_name = null;
	private static String mAlipayTV_orderid = null;
	private static int mAlipayTV_amount = 0;
	
	public enum PayType {
		TYPE_QR_CODE, TYPE_BAODIAN
	}
    

	protected static void LogE(String msg, Exception e) {
		Log.e(LOG_TAG, msg, e);
		e.printStackTrace();
	}

	protected static void LogD(String msg) {
		if (bDebug) {
			Log.d(LOG_TAG, msg);
		}
	}

	public IAPAlipay(Context context) {
		mContext = (Activity) context;
		mAdapter = this;
		
		PluginWrapper.runOnMainThread(new Runnable() {

			@Override
			public void run() {
				initUIHandle();
			}
		});
		
	}

	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
		LogD("initDeveloperInfo invoked " + cpInfo.toString());
		try {
			
			String strFlag = cpInfo.get("flag");
			LogD("1111111configDeveloperInfo  flag:"+ strFlag );
			
			if (strFlag.equalsIgnoreCase("1")){
			
			mAliPayTV_key = cpInfo.get("AliPayTV_key");
			mAliPayTV_secret = cpInfo.get("AliPayTV_secret");
			
			//LogD("1111111configDeveloperInfo  mAliPayTV_secret:"+ mAliPayTV_secret +",mAliPayTV_key:"+mAliPayTV_key);
			
			//检测盒子当前系统是否支持帐号授权
	        if(!MagicCenter.isSupportAuthorize(mContext)){
	        	McLog.d("alipay2","Sorry, this SDK doesn't support this system. You must install the latest account version.");
	        	LogD("1111111Sorry, this SDK doesn't support this system. You must install the latest account version.");
	        	return;
	        }
	        
	        MagicCenter.logSwitch(true);
	        
	        boolean isDebugMode = false;
	        //初始化sdk
			MagicCenter.init(mContext, mAliPayTV_key, mAliPayTV_secret, isDebugMode, new IInitListener() {		
				@Override
				public void onInitFinish() {
					// TODO Auto-generated method stub
					McLog.d("alipay2","init MagicCenter ok!!!");
					LogD("1111111init MagicCenter ok!!!");
				}
				
				@Override
				public void onInitError(int errCode) {
					// TODO Auto-generated method stub
					McLog.d("alipay2", "Init MagicCenter error. code:" + errCode + " msg:" + AliBaseError.getErrMsg(errCode));
					LogD("111111Init MagicCenter error. code:" + errCode + " msg:" + AliBaseError.getErrMsg(errCode));
				}
			});
			
			//设置监听
			MagicCenter.setAuthListener(new McUser.IAuthListener() {
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					LogD("1111111setAuthListener MagicCenter error!!!");
					payResult(PayWrapper.PAYRESULT_FAIL, "登录监听失败，请重新购买!");
				}
				
				@Override
				public void onAuthSucess(int arg0) {
					// TODO Auto-generated method stub
					LogD("1111111setAuthListener MagicCenter ok,start pay!!!");
					
					//startpayproduct();
					payByBaodian();
				}
			});
		
			
			}
			
		} catch (Exception e) {
			LogE("Developer info is wrong!", e);
		}
	}
	

	@Override
	public void payForProduct(Hashtable<String, String> info) {

		//LogD("1111111111payForProduct invoked " + info.toString());
		if (! networkReachable()) {
			payResult(PayWrapper.PAYRESULT_FAIL, "网络不可用");
			return;
		}
		final Hashtable<String, String> productInfo = info;
		 new AsyncTask<Integer, Object, String>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();  
            }
          @Override
            protected String doInBackground(Integer... params) {
        	  
        		mAlipayTV_price = productInfo.get("price");
        		mAlipayTV_name = productInfo.get("productName");
        		mAlipayTV_orderid = productInfo.get("orderId");
        		mAlipayTV_amount = 100*(Integer.parseInt(mAlipayTV_price));
        		   
        		/*McUser mcUser = (McUser)MagicCenter.getSDKService(SDK_TYPE.USER);
        		boolean isAuth = mcUser.isAuth();
				LogD("11111isAuth:" + isAuth);
        		if(!isAuth){
        			mcUser.loginAuth(0);
        			return "";
        		}*/
         		
         		startpayproduct();
         		
            	return "";
            }
            protected void onPostExecute(String result) {
            	Toast.makeText(mContext, result, 20).show();
            };
         }.execute();
		
	}

		//1.0.3.3
		/*McBaodianPay mcBaodianPay = (McBaodianPay)MagicCenter.getSDKService(SDK_TYPE.BAODIAN_PAY);
		String strPrice = mAlipayTV_price;
		
		LogD("1111111mAlipayTV_price:"+mAlipayTV_price+"   mAlipayTV_name:"+mAlipayTV_name+"  mAlipayTV_orderid:"+mAlipayTV_orderid);
		if(mAlipayTV_price.equalsIgnoreCase(null)){
			payResult(PayWrapper.PAYRESULT_FAIL, "价格为空");
			return;
		}
		if(mAlipayTV_name.equalsIgnoreCase(null)){
			payResult(PayWrapper.PAYRESULT_FAIL, "道具名为空");
			return;
		}
		if(mAlipayTV_orderid.equalsIgnoreCase(null)){
			payResult(PayWrapper.PAYRESULT_FAIL, "订单号为空");
			return;
		}
		
		int amount = 100*(Integer.parseInt(strPrice));
		LogD("111111111111111启动token");
		
		synchronized (lockObj) {
			if(isGettingConsumeToken){
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 1;
				msg.what = 1;
				msg.sendToTarget();
			}
			isGettingConsumeToken = true;
			getConsumeTokenAndConsume(mAlipayTV_name, amount,mAlipayTV_orderid);
		}*/
	
	public static void startpayproduct(){
		LogD("111111111startpayproduct start");
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				McBaodianPay pay = McBaodianPay.init();
				pay.startPay(new OnStartPayListener() {

					@Override
					public void onQrCodePay() {
						payByQrCode();
					}

					@Override
					public void onBaodianPay() {
						payByBaodian();
					}
				});
			}
		});
	}
		
		
	public static void payByQrCode() {
		LogD("111111111111111select paybyqrcode");
		buy(mAlipayTV_name, mAlipayTV_amount, PayType.TYPE_QR_CODE);
	}

	public static void payByBaodian() {
		LogD("111111111111111select paybybaodian1");
		if (!McUser.checkAuthWithLogin(1)) {
			LogD("111111111111111宝点支付前检测到没有登录成功");
			payResult(PayWrapper.PAYRESULT_FAIL, "请先登录魔盒帐号，或者直接扫描二维码支付！");
			return;
		}
		LogD("111111111111111select paybybaodian2");
		buy(mAlipayTV_name, mAlipayTV_amount, PayType.TYPE_BAODIAN);
	}
	
	public static void buy(String title, int amount, PayType payType) {
		synchronized (lockObj) {
			if (isGettingConsumeToken) {
				//TestToast.show("亲，正在获取消费信息, 您按的太快啦");
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 1;
				msg.what = 1;
				msg.sendToTarget();
				return;
			}
			isGettingConsumeToken = true;
			//buyOpStatus.put(title, Boolean.TRUE);
			//buyConStatus.put(title, Boolean.TRUE);
			getConsumeTokenAndConsume(title, amount, payType);
		}
	}
	
	public static void getConsumeTokenAndConsume(final String title, int amount, final PayType paytype){
		
		LogD("111111111111111getConsumeTokenAndConsume 获取token");
		McBaodianPay pay = McBaodianPay.init();
		GetTokenParams params2 = new GetTokenParams();

		//宝点数量
		params2.setAmount(amount);
		//宝点
		params2.setOption(0);
		//cp产生的订单id， 这个id需要cp服务端保存，消费成功后阿里服务器会通过notifyUrl通知到cp服务端，附带参数就是这个orderid。
		params2.setOrderId(mAlipayTV_orderid);
		//商品名称
		params2.setTitle(title);
		//消费成功后阿里服务端通知给cp服务端的notifyUrl
		params2.setNotifyUrl("http://mapi2.bianfeng.com/v1/pay/notify/alitv2");
		//LogD("111111111获取token title:"+title+"amount:"+amount+"orderid:"+mAlipayTV_orderid);
		pay.getCousumeToken(params2, new IGetConsumeTokenListener() {
			
			@Override
			public void onSuccess(TokenBean tokenBean) {
				// TODO Auto-generated method stub
				LogD("111111111111111getConsumeTokenAndConsume token获取成功");
				mTokenBean = tokenBean;
				//LogD("111111get tokenBean success:"+tokenBean);
				consumeBaodian(tokenBean, title, paytype);
				isGettingConsumeToken = false;
			}
			
			@Override
			public void onError(int errCode, String errMsg) {
				// TODO Auto-generated method stub
				LogD("111111111111111getConsumeTokenAndConsume token获取失败");
				//LogD("111111get tokenBean error:"+errMsg);
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 2;
				msg.what = 2;
				msg.sendToTarget();
				isGettingConsumeToken = false;
				return;
			}
		});
	}
	
	public static void consumeBaodian(TokenBean tokenBean, final String title,
			final PayType payType) {
		
		LogD("111111111111111consumeBaodian  开始支付");
		McBaodianPay pay = McBaodianPay.init();
		if (PayType.TYPE_QR_CODE.equals(payType)) {
			
			LogD("111111111111111consumeBaodian 二维码支付");
			pay.startQRPay(tokenBean.getToken(), new IConsumeListener() {

				@Override
				public void onSuccess() {
					//TestToast.show("二维码扫码支付成功");
					LogD("111111111111111consumeBaodian 二维码扫码支付成功");
					Message msg = mHandler.obtainMessage();
					msg.arg1 = 3;
					msg.what = 3;
					msg.sendToTarget();
					return;
				}

				@Override
				public void onError(int errCode, String errMsg) {
					//TestToast.show("二维码扫码支付失败");
					LogD("111111111111111consumeBaodian 二维码扫码支付失败");
					Message msg = mHandler.obtainMessage();
					msg.arg1 = 4;
					msg.what = 4;
					msg.sendToTarget();
					return;
				}

				@Override
				public void onBaodianPay() {
					//TestToast.show("用户使用宝点支付，请唤起登陆授权页面后支付");
					LogD("111111111111111consumeBaodian 二维码支付 中的宝点支付");
					payByBaodian();
					return;
				}
			});

		} else {
			LogD("111111111111111consumeBaodian  宝点支付");
			pay.consumeBaodian(tokenBean, title, new IConsumeListener() {

				@Override
				public void onSuccess() {
					
					//McLog.d(TAG, "consume " + title + " success!");
					payResult(PayWrapper.PAYRESULT_SUCCESS, "宝点支付成功!");
					return;
				}

				@Override
				public void onError(int errCode, String errMsg) {
					
					//McLog.d(TAG, "consume " + title + " error : " + errMsg
						//	+ " errCode:" + errCode);
					//String msg = "consume " + title + " error : " + errMsg	+ " errCode:" + errCode;
					payResult(PayWrapper.PAYRESULT_FAIL, "取消支付");
					return;
				}

				@Override
				public void onBaodianPay() {
					
					return;
					/**
					 * 宝点支付，不必care这个回调
					 */
				}
			});
		}
	}

	/*public static void consumeBaodian(TokenBean tokenBean, final String title){
		McBaodianPay pay = McBaodianPay.init();
		
		pay.consumeBaodian(mTokenBean, title,  PayType.TYPE_BAODIAN {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				//McLog.d(TAG, "consume " + title + " success!");
				LogD("111111consume " + title + " success!");
    	  		String msg = "consume " + title + " success!";
    	  		payResult(PayWrapper.PAYRESULT_SUCCESS, msg);
    	  		return;
			}
			
			@Override
			public void onError(int errCode, String errMsg) {
				// TODO Auto-generated method stub
				//McLog.d(TAG, "consume " + title + " error : " + errMsg + " errCode:" + errCode);
				LogD("11111111consume " + title + " error : " + errMsg + " errCode:" + errCode);
    	  		String msg = "consume " + title + " error : " + errMsg + " errCode:" + errCode;
    	  		payResult(PayWrapper.PAYRESULT_FAIL, msg);
    	  		return;
			}
		});
	}
	*/
	
	public static void uninit() {
		Log.d("111","111111111111111SDK uninit");
		MagicCenter.unInit();
	}
	
	public static void enter() {
		Log.d("111","111111111111111enter game");
		TBSUtils.enter();
	}
	
	public static void leave() {
		Log.d("111","111111111111111enter leave");
		TBSUtils.leave();
	}
	
	@Override
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	@Override
	public String getSDKVersion() {
		//return "Unknown version";
		return MagicCenter.getSdkVersion();
	}

	static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;
		AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	private static void initUIHandle() {
		//
		// the handler use to receive the pay result.
		// 杩���ユ����缁��锛��浠�����绔��姝ラ���
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					String strRet = (String) msg.obj;
					LogD("handle msg : " + msg.toString());
					switch (msg.what) {
					case 1:{
						String msff = ("亲，正在获取消费信息, 您按的太快啦!");
						Toast.makeText(mContext, msff, 2).show();
						payResult(PayWrapper.PAYRESULT_FAIL, msff);
					}
					break;
					case 2:{
						String msgg = "获取token错误!";
						Toast.makeText(mContext, msgg, 2).show();
						payResult(PayWrapper.PAYRESULT_FAIL, msgg);
					}
					break;
					case 3:{
						String msgg = "二维码扫码支付成功!";
						Toast.makeText(mContext, msgg, 2).show();
						payResult(PayWrapper.PAYRESULT_SUCCESS, msgg);
					}
					break;
					case 4:{
						String msgg = "二维码扫码支付失败!";
						Toast.makeText(mContext, msgg, 2).show();
						payResult(PayWrapper.PAYRESULT_FAIL, msgg);
					}
					break;
					default:
						mAdapter.closeProgress();
						payResult(PayWrapper.PAYRESULT_FAIL, "购买异常!");
						break;
					}

					super.handleMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	
	public static void loginResult(int ret, String userId){
		Hashtable<String, String> info = new Hashtable<String, String>();
		info.put("userId", userId);	
		LogD("11111111AliPayTV loginResult : " + ret + " info : " + info);
		PayWrapper.onLoginResult(mAdapter, ret, info);
		LogD("111111AliPayTV loginResult : " + ret + " info : " + info);
	}
	/**
	 * get the selected order info for pay. �峰����璁㈠�淇℃�
	 * 
	 * @param position
	 *            ����ㄥ�琛ㄤ腑���缃�
	 * @return
	 */
	private static String strPayAccount = "";
	private static String strReceiveAccount = "";
	private static float fPayPercent = 0.0f;
	private static String strRoyTip = "";
	private static String strNotifyUrl = "";
	private String getOrderInfo(Hashtable<String, String> info) {
		String strRet = null;
		try {
			float price = Float.parseFloat(info.get("productPrice"));//IAPProducts.getProductPrice(productID);
			String productName = info.get("productName");
			String productDesc = info.get("productDesc");
			String royParam = "";
			if (fPayPercent > 0 ) {
				float royValue = fPayPercent * price;
				royParam = strPayAccount + "^" + strReceiveAccount + "^" + royValue + "^"+ strRoyTip;
				royParam = "&royalty_parameters=\""+ royParam + "\"" + "&royalty_type=\"10" + "\"";
			}

			strRet = "partner=\"" + PartnerConfig.PARTNER + "\""
						+ "&seller=\"" + PartnerConfig.SELLER + "\""
						+ "&out_trade_no=\"" + getOutTradeNo() + "\""
						+ "&subject=\"" + productName + "\""
						+ "&body=\"" + productDesc + "\""
						+ "&total_fee=\"" + price + "\""
						+ "&notify_url=\"" + strNotifyUrl + "\""
						+ royParam;
		} catch (Exception e) {
			LogE("Product info parse error", e);
		}

		LogD("order info : " + strRet);
		return strRet;
	}

	/**
	 * get the out_trade_no for an order.
	 * �峰�澶��璁㈠���
	 * 
	 * @return
	 */
	String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);

		java.util.Random r = new java.util.Random();
		strKey = strKey + r.nextInt(10000);
		return strKey;
	}

	//
	//
	/**
	 *  sign the order info.
	 *  瀵硅���俊���琛����
	 *  
	 * @param signType	绛惧��瑰� 
	 * @param content		寰�������俊��
	 * @return
	 */
	/*private String sign(String signType, String content) {
		LogD("sign params :");
		LogD("type : " + signType + ", content : " + content + ", private : " + PartnerConfig.RSA_PRIVATE);
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}*/

	/**
	 * get the sign type we use.
	 * �峰�绛惧��瑰�
	 * 
	 * @return
	 */
	/*private String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}*/
	
	private ProgressDialog mProgress = null;
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean networkReachable() {
		boolean bRet = false;
		try {
			ConnectivityManager conn = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conn.getActiveNetworkInfo();
			bRet = (null == netInfo) ? false : netInfo.isAvailable();
		} catch (Exception e) {
			LogE("Fail to check network status", e);
		}
		LogD("NetWork reachable : " + bRet);
		return bRet;
	}

	private static void payResult(int ret, String msg) {
		PayWrapper.onPayResult(mAdapter, ret, msg);
		LogD("Alipay result : " + ret + " msg : " + msg);
	}
	
	public static void onDestroy() {
		//PayWrapper.onPayResult(mAdapter, ret, msg);
		MagicCenter.unInit();
		LogD("Alipay result : ");
	}

	@Override
	public String getPluginVersion() {
		return "0.2.0";
	}

	@Override
	public void pluginLogin(Hashtable<String, String> cpInfo) {
		// TODO Auto-generated method stub
		
	}
}
