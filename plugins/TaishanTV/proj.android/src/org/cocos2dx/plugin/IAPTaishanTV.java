package org.cocos2dx.plugin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.toltech.appstore.service.ConsumeService;
import com.toltech.appstore.service.OrderResult;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class IAPTaishanTV implements InterfaceIAP {

	private static final String LOG_TAG = "IAPTaishanTV";
	
	private static final int MSG_TRADE_RESULT = 0;
//	private static final int MSG_CLOSE_TRADE = 1;

	private static Activity mContext = null;
	private static IAPTaishanTV mPayInstance = null;
	private static boolean mIsDebug = false;

//	private static AlertDialog mPayTipDialog = null;
	private static Handler mHandler = null;
	
	public static ConsumeService mComsumeService = null;
	public static ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mComsumeService = ConsumeService.Stub.asInterface(service);
		}
	};
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPTaishanTV(Context context) {
		if (mContext == null) {
			mContext = (Activity)context;
		}
		mPayInstance = this;
		
		PluginWrapper.runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				initService(mContext);
				initHandler(mContext);
			}
		});
	}
	
	private static void initService(Context context) {
		BuyData.userId = mContext.getIntent().getStringExtra("account");
		context.bindService(new Intent("itv.service.consume"), 
				mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private static void initHandler(Context context) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
//				1212204：用户账号不存在
//				1212208：业务道具码配置出错
//				1212303：用户没有订购相应产品和订购关系已失效或未生效
//				1212209：产品配置出错
//				1212200：业务订购成功
//				4702：用户未开通订购增值业务产品权限!请联系电信客服开通权限!
//				1212306：用户消费达到封顶或者分组不能消费
				switch (msg.what) {
				case MSG_TRADE_RESULT: {
						String sMsg = "";
						boolean isPaySuccess = false;
						OrderResult result = (OrderResult)msg.obj;
						String retCode = result.getResultCode();
						if (retCode.equalsIgnoreCase("1212200")) {
							sMsg = "支付成功，业务订购成功！";
							isPaySuccess = true;
						} else if (retCode.equalsIgnoreCase("1212204")) {
							sMsg = "支付失败，用户账号不存在！";
						} else if (retCode.equalsIgnoreCase("1212208")) {
							sMsg = "支付失败，业务道具码配置出错！";
						} else if (retCode.equalsIgnoreCase("1212303")) {
							sMsg = "支付失败，用户没有订购相应产品和订购关系已失效或未生效！";
						} else if (retCode.equalsIgnoreCase("1212209")) {
							sMsg = "支付失败，产品配置出错！";
						} else if (retCode.equalsIgnoreCase("4702")) {
							sMsg = "支付失败，用户未开通订购增值业务产品权限!请联系电信客服开通权限!";
						} else if (retCode.equalsIgnoreCase("1212306")) {
							sMsg = "支付失败，用户消费达到封顶或者分组不能消费！";
						} else {
							sMsg = result.getMessage();
						}
						
						if (isPaySuccess) {
							IAPTaishanTV.payResult(IAPWrapper.PAYRESULT_SUCCESS, "付款成功");
							BuyData.payResult = "0";
						} else {
							IAPTaishanTV.payResult(IAPWrapper.PAYRESULT_FAIL, "付款失败");
							BuyData.payResult = "1";
						}
						
						LogD("user id:"+BuyData.userId);
						if (BuyData.userId == null) {
							BuyData.userId = "";
						}
						// 给web发回调
						String string = "gameId=" + BuyData.gameId 
								+ "&orderId=" + BuyData.orderId 
								+ "&payResult=" + BuyData.payResult
								+ "&payTime=" + BuyData.payTime 
								+ "&productId=" + BuyData.productId 
								+ "&productPrice=" + BuyData.productPrice 
								+ "&userId=" + BuyData.userId;
		                BuyData.sign = MD5Signature.md5(string + BuyData.gameKey);
		                String param = string + "&sign=" + BuyData.sign;
		                try {
		                	String ret = doHttpGet(BuyData.notifyUrl, param);
		                	if (!ret.equalsIgnoreCase("success")) {
		                		LogD("notify no response!");
		                		ret = doHttpGet(BuyData.notifyUrl, param);
		                		if (!ret.equalsIgnoreCase("success")) {
			                		LogD("notify no response! end!");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						Toast.makeText(mContext,
								sMsg, Toast.LENGTH_LONG)
								.show();
					}
					break;

				default:
					break;
				}
				
				super.handleMessage(msg);
			}
		};
	}
	
	//将输入流转换成字符串  
    private static String inStream2String(InputStream is) throws Exception {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        byte[] buf = new byte[1024];  
        int len = -1;  
        while ((len = is.read(buf)) != -1) {  
            baos.write(buf, 0, len);  
        }  
        return new String(baos.toByteArray());  
    }
    
	public static String doHttpGet(String notifyUrl, String param) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(notifyUrl + "?" + param);  
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            InputStream is = response.getEntity().getContent();
            return inStream2String(is);
        }
        
        return "";
    }
	
	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
		LogD("configDeveloperInfo invoked " + cpInfo.toString());
	}

	// 
	@Override
	public void pluginLogin(Hashtable<String, String> cpInfo) {
		LogD("pluginLogin invoked " + cpInfo.toString());
		try{
			String loginInfoFlag = cpInfo.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}

	// 在这里处理实际发送交易请求给第三方的部分
	@Override
	public void payForProduct(Hashtable<String, String> cpInfo) {
		LogD("payForProduct invoked " + cpInfo.toString());
		if (! Utils.isNetworkReachable(mContext)) {
			payResult(IAPWrapper.PAYRESULT_FAIL, "网络不可用");
			return;
		}
		
		final Hashtable<String, String> productInfo = cpInfo;
		new AsyncTask<Integer, Object, String>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();  
            }
            @Override
            protected String doInBackground(Integer... params) {

            	
            	/////////////////////////////////////////
                BuyData.gameId = productInfo.get("gameId");
                BuyData.productId = productInfo.get("productId");
                BuyData.productPrice = ""+(Integer.parseInt(productInfo.get("productPrice"))*100);
                BuyData.notifyUrl = productInfo.get("notifyUrl");
                BuyData.orderId = productInfo.get("orderId");
                BuyData.gameKey = productInfo.get("gameKey");

                try {
    				OrderResult result = mComsumeService.propOrder(BuyData.gameId,
    						BuyData.productId);
    				if (result != null) {
    					LogD(result.getResultCode() + "--" + result.getMessage());
    					
    					BuyData.payTime = DateFormat.format("yyyyMMddhhmmss", System.currentTimeMillis()).toString();

    					Message msg = new Message();
    					msg.what = MSG_TRADE_RESULT;
    					msg.obj = result;
    					mHandler.sendMessage(msg);
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}

            	return "";
            	/////////////////////////////////////////
            }
            protected void onPostExecute(String result) {
            	super.onPostExecute(result);
            };
         }.execute();
	}
	
	// 这里处理交易结果
	public static void payResult(int ret, String msg){
		IAPWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("payResult:" + ret + " msg:" + msg);
	}
	
	@Override
	public void setDebugMode(boolean debug) {
		mIsDebug = debug;
	}

	@Override
	public String getSDKVersion() {
		return "1.0";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.1";
	}

}
