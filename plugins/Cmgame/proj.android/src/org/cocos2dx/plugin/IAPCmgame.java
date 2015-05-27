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
import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.GameInterface.GameExitCallback;
import cn.cmgame.billing.api.GameInterface.ILoginCallback;
import cn.cmgame.billing.api.LoginResult;

public class IAPCmgame implements InterfacePay {

	//移动游戏基地插件化参数
	private static final String LOG_TAG = "IAPCmgame";
	private static Activity mContext = null;
	private static IAPCmgame mCmgame = null;
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
	
	public IAPCmgame(Context context){
		mContext = (Activity)context;
		mCmgame = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String>info){
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					GameInterface.initializeApp(mContext, "gameName", "provider", "serviceTel", "loginNo", new ILoginCallback(){
						public void onResult(int resultCode, String userId, Object obj) {
							final boolean bIsMusicEnabled = GameInterface.isMusicEnabled();
							LogD("Login.Result= " + userId);
							if(resultCode == LoginResult.SUCCESS_EXPLICIT || resultCode == LoginResult.SUCCESS_IMPLICIT){
								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_SUCCESS, userId, bIsMusicEnabled);
							}
							if(resultCode == LoginResult.FAILED_EXPLICIT || resultCode == LoginResult.FAILED_IMPLICIT){
								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_FAIL, userId, bIsMusicEnabled);
							}
							if(resultCode == LoginResult.UNKOWN){
								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_CANCEL, userId, bIsMusicEnabled);
							}
						}
					});
					
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public void pluginLogin(Hashtable<String, String>info){
//		LogD("pluginLogin invoked " + info.toString());
//		final Hashtable<String, String>loginInfo = info;
//		PluginWrapper.runOnMainThread(new Runnable(){
//			public void run(){
//				try{
//					String loginInfoFlag = loginInfo.get("flag");
//					final boolean bIsMusicEnabled = GameInterface.isMusicEnabled();
//					
//					GameInterface.setLoginListener(mContext, new GameInterface.ILoginCallback() {
//						
//						@Override
//						public void onResult(int resultCode, String userId, Object obj) {
//							LogD("Login.Result= " + userId);
//							if(resultCode == LoginResult.SUCCESS_EXPLICIT || resultCode == LoginResult.SUCCESS_IMPLICIT){
//								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_SUCCESS, userId, bIsMusicEnabled);
//							}
//							if(resultCode == LoginResult.FAILED_EXPLICIT || resultCode == LoginResult.FAILED_IMPLICIT){
//								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_FAIL, userId, bIsMusicEnabled);
//							}
//							if(resultCode == LoginResult.UNKOWN){
//								IAPCmgame.loginResult(PayWrapper.LOGINRESULT_CANCEL, userId, bIsMusicEnabled);
//							}
//						}
//					});				
//					
//					LogD("PluginLogin result: " + loginInfoFlag);
//				}catch(Exception e){
//					LogE("Login info parse error!", e);
//				}
//			}
//		});	
	}
	
	public void payForProduct(Hashtable<String, String>info){
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String Cmgame_payCode = productInfo.get("productId");
					String Cmgame_orderId = productInfo.get("orderId");
					
					GameInterface.doBilling(mContext, true, true, Cmgame_payCode, Cmgame_orderId, payCallback);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private GameInterface.IPayCallback payCallback = new GameInterface.IPayCallback() {
		
		@Override
		public void onResult(int resultCode, String billingIndex, Object obj) {
			switch(resultCode){
				case BillingResult.SUCCESS :
					IAPCmgame.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
					break;
				case BillingResult.FAILED :
					IAPCmgame.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
					break;
				default :
					IAPCmgame.PayResult(PayWrapper.PAYRESULT_CANCEL, "支付失败");
					break;
			}	
		}
	};
	
	public static void loginResult(int ret, String userId, boolean bIsMusicEnabled){
		Hashtable<String, String> info = new Hashtable<String, String>();
		info.put("userId", userId);
		String strIsMusicEnabled = String.valueOf(bIsMusicEnabled);
		info.put("musicStatus", strIsMusicEnabled);
		
		PayWrapper.onLoginResult(mCmgame, ret, info);
		LogD("Cmgame loginResult : " + ret + " info : " + info);
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mCmgame, ret, msg);
		LogD("Cmgame payResult : " + ret + " msg : " + msg);
	}
	
	public void exitGame(){
//		GameInterface.exitApp();
		GameInterface.exit(mContext, new GameExitCallback(){
			public void onConfirmExit(){
				LogD("ConfirmExit Exit Game!!");
				System.exit(0);
			}
			
			public void onCancelExit(){
				LogD("User Canceled!!");
			}
		});
	}
	
	public void setDebugMode(boolean debug){
		bDebug = debug;
	}
	
	public String getSDKVersion(){
		return "21121";
	}
	
	public String getPluginVersion(){
		return "1.0.3";
	}	
	
}
