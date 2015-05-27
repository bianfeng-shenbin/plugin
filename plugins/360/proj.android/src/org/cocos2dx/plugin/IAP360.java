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

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

public class IAP360 implements InterfacePay , PluginListener{
	
	//360插件化参数
	private static final String LOG_TAG = "IAP360";
	private static Activity mContext = null;
	private static IAP360 m360 = null;
	private static boolean bDebug = false;
	private static QihooPayInfo PayInfo = new QihooPayInfo();
	private static String mAccessToken = null;

	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAP360(Context context){
		mContext = (Activity)context;
		m360 = this;
	}
	
	public void onResume() {

	}

	public void onPause() {
		LogD("----------IAP360.onPause----------");
	}

	public void onDestroy() {
		LogD("----------360 Matrix.destroy----------");
//		Matrix.destroy(mContext);
		PluginWrapper.removeListener(m360);
	}
	
	public void onNewIntent(Intent intent) {
		
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					Matrix.init(mContext);
					
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
					
					PluginWrapper.addListener(m360);
					
					LogD("---------PluginWrapper.Listener Settle----------");
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String loginFlag = loginInfo.get("loginFlag");
					int flag = Integer.parseInt(loginFlag);
					
					Intent intent = new Intent();
					
					switch(flag){
						case 0:
							intent = getLoginIntent(true, true,false);
							break;
							
						case 1:
							intent = getLoginIntent(true, true,true);
							break;
							
						default:
							return;
					}
					
					Matrix.invokeActivity(mContext, intent, mLoginCallback);
					
				}catch(Exception e){
					LogE("Login info parse error!", e);
				}
			}
		});
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
			        // 支付基础参数
					Intent intent = getPayIntent(true, true, productInfo);
					
			        // 必需参数，使用360SDK的支付模块。
					intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);
					
			        // 界面相关参数，360SDK登录界面背景是否透明。
					intent.putExtra(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, true);
					
					Matrix.invokeActivity(mContext, intent, mPayCallback);
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
    // -----------------------------------退出接口---------------------------------------
	
	public void SDKQuit(){
		Intent intent = getQuitIntent(true);
		
		Matrix.invokeActivity(mContext, intent, mQuitCallback);
	}
	
    // -----------------------------------参数Intent-------------------------------------

    /***
     * 生成调用360SDK登录接口的Intent
     *
     * @param isLandScape 是否横屏
     * @param isBgTransparent 是否背景透明
     * @param appKey 应用或游戏的AppKey
     * @param appChannel 应用或游戏的自定义子渠道
     * @return Intent
     */
	private Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent,boolean isSwitchAccount){
		
		Bundle bundle = new Bundle();
		
        // 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
		
        // 界面相关参数，360SDK登录界面背景是否透明。
		bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);
		
        // *** 以下非界面相关参数 ***

        // 必需参数，使用360SDK的登录模块。
		if(isSwitchAccount){
			bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);
		}else{
			bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
		}
		
		Intent intent = new Intent(mContext, ContainerActivity.class);
		intent.putExtras(bundle);
		
		return intent;
	}
	
    /***
     * 生成调用360SDK支付接口基础参数的Intent
     *
     * @param isLandScape
     * @param pay
     * @return Intent
     */
	private Intent getPayIntent(boolean isLandScape, boolean isFixed, Hashtable<String, String>info){
		
		Bundle bundle = new Bundle();
		
		QihooPayInfo pay = getQihooPayInfo(isFixed, info);
		
        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        
        // 设置QihooPay中的参数。
        // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
        bundle.putString(ProtocolKeys.ACCESS_TOKEN, pay.getAccessToken());

        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

        // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
        bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

        // 可选参数，应用扩展信息1，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

        // 可选参数，应用扩展信息2，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());
        
        Intent intent = new Intent(mContext, ContainerActivity.class);
        intent.putExtras(bundle);
		
		return intent;
	}
	
    /***
     * 生成调用360SDK退出接口的Intent
     *
     * @param isLandScape
     * @return Intent
     */
    private Intent getQuitIntent(boolean isLandScape) {

        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的退出模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUIT);

        Intent intent = new Intent(mContext, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
	
    // -----------------------------------PayInfo参数-------------------------------------
	
	public void UpdatePayInfo(JSONObject info){
		LogD("UpdatePayInfo invoked! Info : " + info.toString());
		if(info != null){
			try{
				String userID = info.getString("Param1");
				String userName = info.getString("Param2");
				
				PayInfo.setQihooUserId(userID);
				PayInfo.setAppUserName(userName);
				PayInfo.setAppUserId(userID);	
			}catch(Exception e){
				LogE("Extra info parse error!", e);
			}
		}
	}
	
	private QihooPayInfo getQihooPayInfo(boolean isFixed, Hashtable<String, String>info){
		PayInfo.setAccessToken(mAccessToken);
		
		PayInfo.setExchangeRate("1");
		String m360_callbackUrl = info.get("callbackUrl");
		PayInfo.setNotifyUri(m360_callbackUrl);
		
		String m360_productName = info.get("productName");
		PayInfo.setProductName(m360_productName);
		
		String m360_productId = info.get("productId");
		PayInfo.setProductId(m360_productId);
		
		String m360_orderId = info.get("orderId");
		PayInfo.setAppOrderId(m360_orderId);
		
		String m360_appName = info.get("360_appName");
		PayInfo.setAppName(m360_appName);
	
		String strPrice = info.get("price");
		int iPrice = (int)Float.parseFloat(strPrice);
		String m360_price = String.valueOf(iPrice * 100);
		PayInfo.setMoneyAmount(m360_price);
		
		return PayInfo;
	}
	
    // ---------------------------------360SDK接口的回调-----------------------------------
	
	private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {
		 
		public void onFinished(String data) {
			LogD("360SDK mAccountCallback result: " + data);
		    procGotTokenInfoResult(data);
		}
	};
	
	 // 支付的回调
	private IDispatcherCallback mPayCallback = new IDispatcherCallback(){
		
        public void onFinished(String data) {
            LogD("mPayCallback, data is " + data);
            boolean isCallbackParseOk = false;
            JSONObject jsonRes;
            try {
                jsonRes = new JSONObject(data);
                // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中。
                // error_msg 状态描述
                int errorCode = jsonRes.getInt("error_code");
                switch (errorCode) {
                    case 0:{
                    	IAP360.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
                    }
                    	break;
                    case 1:{
                    	IAP360.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
                    }
                    	break;
                    case -1:{
                    	IAP360.PayResult(PayWrapper.PAYRESULT_CANCEL, "支付取消");
                    }
                    	break;
                    case -2: {
                    	IAP360.PayResult(PayWrapper.PAYRESULT_TIMEOUT, "支付进行中");
                    }
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 用于测试数据格式是否异常。
            if (!isCallbackParseOk) {
                Log.e(LOG_TAG, "PayInfo Data Format Error!!!");
            }
        }
	};
	
    // 退出的回调
	private IDispatcherCallback mQuitCallback = new IDispatcherCallback(){
		
        public void onFinished(String data) {
        	final String dataString = data;
        	PluginWrapper.runOnMainThread(new Runnable(){
        		public void run(){
                    LogD("mQuitCallback, data is " + dataString);
                    JSONObject json;
                    try {
                        json = new JSONObject(dataString);
                        int which = json.optInt("which", -1);
                        String label = json.optString("label");

                        switch (which) {
                            case 0: // 用户关闭退出界面
                                return;
                            case 2:{
                            	LogD("ConfirmExit Exit Game!!");
                            	LogD("--------Matrix.destroy--------");
                            	Matrix.destroy(mContext);
                            	LogD("--------IAP360.finish--------");
                            	mContext.finish();
                            	LogD("--------IAP360.exit--------");
                            	System.exit(0);
                            	LogD("--------IAP360.killProcess--------");
                            	android.os.Process.killProcess(android.os.Process.myPid());
                            }
                            default:
                                return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        		}
        	});
        }
	};
	
    // ---------------------------------360SDK获取Token-----------------------------------	
	
	private void procGotTokenInfoResult(String data) {
		boolean isCallbackParseOk = false;
		 
		if(!TextUtils.isEmpty(data)) {
			JSONObject jsonRes;
		 
			try {
				jsonRes = new JSONObject(data);
				// error_code 状态码： 0 登录成功， -1 登录取消， 其他值：登录失败
				int errorCode = jsonRes.optInt("error_code");
				String dataString = jsonRes.optString("data");
		 
				switch (errorCode) {
					case 0:
						TokenInfo tokenInfo = TokenInfo.parseJson(dataString);
						if (tokenInfo != null && tokenInfo.isValid()) {
							isCallbackParseOk = true;
							mAccessToken = tokenInfo.getAccessToken();
							IAP360.loginResult(PayWrapper.LOGINRESULT_SUCCESS, mAccessToken);
						}
						break;
		 
					case -1:
						Log.e(LOG_TAG, "用户取消登录");
						IAP360.loginResult(PayWrapper.LOGINRESULT_CANCEL, null);
						return;
		 
					default:
						Log.e(LOG_TAG, "登录失败 Result: " + dataString);
						IAP360.loginResult(PayWrapper.LOGINRESULT_FAIL, null);
						break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		 
		if(!isCallbackParseOk) {
			Log.e(LOG_TAG, "Token解析失败");
			IAP360.PayResult(PayWrapper.LOGINRESULT_FAIL, null);
		}
	}
	
	private static void loginResult(int ret, String accessToken){
		Hashtable<String, String>info = new Hashtable<String, String>();
		info.put("accessToken", accessToken);
		
		PayWrapper.onLoginResult(m360, ret, info);
		LogD("360 loginResult : " + ret + " info : " + info);
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(m360, ret, msg);
		LogD("360 payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.1.0";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}