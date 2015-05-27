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

import java.util.Hashtable;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfoOnline;
import com.xiaomi.gamecenter.sdk.entry.MiGameType;
import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class IAPMipay implements InterfacePay {

	private static final String LOG_TAG = "IAPMipay";
	private static Activity mContext = null;
	private static boolean bDebug = false;
	private static Handler mHandler = null;
	private static IAPMipay mAdapter = null;

	protected static void LogE(String msg, Exception e) {
		Log.e(LOG_TAG, msg, e);
		e.printStackTrace();
	}

	protected static void LogD(String msg) {
		if (bDebug) {
			Log.d(LOG_TAG, msg);
		}
	}

	public IAPMipay(Context context) {
		mContext = (Activity) context;
		mAdapter = this;
	}

	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
		LogD("initDeveloperInfo invoked " + cpInfo.toString());
		try {
			/**SDK初始化*/
			MiAppInfo appInfo = new MiAppInfo();
			LogD(cpInfo.get("AppId") );
			appInfo.setAppId( cpInfo.get("AppId") );
			LogD(cpInfo.get("AppKey") );
			appInfo.setAppKey( cpInfo.get("AppKey") );
			//appInfo.setAppType( MiGameType.online );
			MiCommplatform.Init( mContext, appInfo );
			

		} catch (Exception e) {
			LogE("Developer info is wrong!", e);
		}
	}

	@Override
	public void payForProduct(Hashtable<String, String> info) {

		LogD("payForProduct invoked " + info.toString());
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
            	MiBuyInfoOnline online = new MiBuyInfoOnline();
            	online.setCpOrderId(productInfo.get("OrderId"));//订单号唯一（不为空）
            	online.setCpUserInfo( productInfo.toString() ); //此参数在用户支付成功后会透传给CP的服务器
            	online.setMiBi( Integer.parseInt(productInfo.get("price")) ); //必须是大于1的整数，10代表10米币，即10元人民币（不为空）

				String errorMsg = "";
            	try
            	{
	            	//用户信息
	            	Bundle mBundle = new Bundle();
	            	mBundle.putString( GameInfoField.GAME_USER_BALANCE, productInfo.get("balance") );   //用户余额
	            	mBundle.putString( GameInfoField.GAME_USER_ROLE_NAME, productInfo.get("roleName") ); //角色名称
	            	mBundle.putString( GameInfoField.GAME_USER_ROLEID, productInfo.get("roleId") );    //角色id
	
	            	MiCommplatform.getInstance().miUniPayOnline(mContext, online, mBundle, 
	            		new OnPayProcessListener()
		            	{
			            	@Override
			            	public void finishPayProcess( int code ) {
			            		LogD("payForProduct result code:" + code);
				            	switch( code ) {
				            	case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
				            	//购买成功
				            		IAPMipay.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
				            	break;
				            	case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_CANCEL:
				            	//取消购买
				            		IAPMipay.payResult(PayWrapper.PAYRESULT_CANCEL, "取消购买");
				            	break;
				            	case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_FAILURE:
				            	//购买失败
				            		IAPMipay.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
				            	break;       
				            	case  MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:	
				            	//操作正在进行中
				            		IAPMipay.payResult(PayWrapper.PAYRESULT_FAIL, "操作正在进行中");
				            	break;	
				            	case  MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:	
					            //未登录
					            	IAPMipay.payResult(PayWrapper.PAYRESULT_FAIL, "您还没有登陆");
					            break;	
				            	default:
				            	//购买失败
				            		IAPMipay.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
				            	break;
				            	}
			            	}
		            	}
	            	);
            	} catch ( Exception e )
				{
					e.printStackTrace();
					errorMsg = e.getMessage() + "----" + e.getLocalizedMessage();
				}
            	
            	return errorMsg;
            }
            protected void onPostExecute(String result) {
            	if (result != "") {
					Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				}
            };
         }.execute(); 
		
		
	}

	@Override
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	@Override
	public String getSDKVersion() {
		return "Unknown version";
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
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		LogD("Mipay result : " + ret + " msg : " + msg);
	}

	@Override
	public String getPluginVersion() {
		return "0.2.0";
	}

	public static void loginResult(int ret, String uid, String session){
		Hashtable<String, String> info = new Hashtable<String, String>();
		info.put("uid", uid);
		info.put("session", session);
		
		PayWrapper.onLoginResult(mAdapter, ret, info);
		LogD("Mipay loginResult : " + ret + " info : " + info);
	}

	@Override
	public void pluginLogin(Hashtable<String, String> info) {
		// TODO Auto-generated method stub
		LogD("pluginLogin invoked " + info.toString());
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String loginInfoFlag = loginInfo.get("flag");
					// 小米账号登录
					MiCommplatform.getInstance().miLogin( mContext, 
							new OnLoginProcessListener()
							{
								@Override
								public void finishLoginProcess( int code ,MiAccountInfo arg1)
								{
									String uid = "";
									String session  = "";
									switch( code )
									{
										case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
										// 登陆成功
										//获取用户的登陆后的UID（即用户唯一标识）
										uid = String.valueOf(arg1.getUid());
										//获取用户的登陆的Session（请参考4.2.3.3流程校验Session有效性）
										session = arg1.getSessionId();//若没有登录返回null
										//请开发者完成将uid和session提交给开发者自己服务器进行session验证
										IAPMipay.loginResult(PayWrapper.LOGINRESULT_SUCCESS, uid, session);
										break;
										case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:
										// 登陆失败
											IAPMipay.loginResult(PayWrapper.LOGINRESULT_FAIL, uid, session);
										break;
										case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL:
										// 取消登录
											IAPMipay.loginResult(PayWrapper.LOGINRESULT_CANCEL, uid, session);
										break;
										case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:	
										//登录操作正在进行中
											IAPMipay.loginResult(PayWrapper.LOGINRESULT_FAIL, uid, session);
										break;		
										default:
										// 登录失败
											IAPMipay.loginResult(PayWrapper.LOGINRESULT_FAIL, uid, session);
										break;
									}
								}
							} 
					);
					LogD("ConfigDeveloperInfo result: " + loginInfoFlag);
				}catch(Exception e){
					LogE("Login info parse error!", e);
				}
			}
		});	

	}
}
