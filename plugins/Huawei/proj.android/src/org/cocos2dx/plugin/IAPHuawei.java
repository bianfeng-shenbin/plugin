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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.cocos2dx.plugin.ReqTask.Delegate;

import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.PayParameters;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gamebox.buoy.sdk.IBuoyOpenSDK;
import com.huawei.gamebox.buoy.sdk.IGameCallBack;
import com.huawei.gamebox.buoy.sdk.InitParams;
import com.huawei.gamebox.buoy.sdk.UpdateInfo;
import com.huawei.gamebox.buoy.sdk.impl.BuoyOpenSDK;
import com.huawei.gamebox.buoy.sdk.inter.UserInfo;
import com.huawei.hwid.openapi.OpenHwID;
import com.huawei.hwid.openapi.out.IHwIDCallBack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

public class IAPHuawei implements InterfacePay, PluginListener{
	
	//华为插件化参数
	private static final String LOG_TAG = "IAPHuawei";
	private static Activity mContext = null;
	private static IAPHuawei mHuawei = null;
	private static boolean bDebug = false;
	public static String HW_appId = null;
	public static String HW_payId = null;
	public static String PAY_RSA_PRIVATE = "";
	public static int PAY_ORI = 2;
	private static String userID = null;
	
    /**
     * 保存浮标信息
     */
    public static IBuoyOpenSDK hwBuoy = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPHuawei(Context context){
		mContext = (Activity)context;
		mHuawei = this;
	}

	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					HW_appId = devInfo.get("appId");
					HW_payId = devInfo.get("payId");
					PAY_RSA_PRIVATE = devInfo.get("payPrivateKey");
					String buoSecretKey = devInfo.get("buoSecretKey");
					String payOrient = devInfo.get("payOrient");
					PAY_ORI = Integer.valueOf(payOrient);// 屏幕方向 1: 竖屏；2: 横屏
					
					InitParams param = new InitParams(HW_appId, HW_payId, buoSecretKey, new GameCallBack());
					
					hwBuoy = BuoyOpenSDK.getIntance();

					// 如果游戏的引擎为cocos2d或者unity3d，将下面一句代码打开
					 hwBuoy.setShowType(2);

					// 浮标初始化
					hwBuoy.init(mContext, param);
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 华为角标初始化回调类
	 */
	private class GameCallBack implements IGameCallBack{

		public void onInitStarted() {
			
		}

	    /**
	     * 初始化成功类
	     */
		public void onInitSuccessed() {
			
		}
		
		public void onInitFailed(int errorCode) {
			
		}
		
		public void onShowSuccssed() {
			
		}
		
		public void onShowFailed(int errorCode) {
			
		}
		
		public void onHidenSuccessed() {
			
		}
		
		public void onHidenFailed(int errorCode) {
			
		}
		
		public void onDestoryed() {
			
		}

		/**
	     * 应用更新检查回调函数
	     */
		public void onUpdateCheckFinished(UpdateInfo info) {
			
		}

		public void onUpdateError(int errorCode) {
			
		}

		public void onValidFail() {
			
		}
	}

	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
	                Bundle loginBundle = new Bundle();
	                // 为登录设置代理
	                OpenHwID.setLoginProxy(mContext, HW_appId, new LoginCallBack(), loginBundle);
	                OpenHwID.login(new Bundle());
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private class LoginCallBack implements IHwIDCallBack{
		/**
         * 参数userInfo是加密过后的用户json数据，需要调用浮标的onUserInfo才能解密
         */
		public void onUserInfo(String userInfo) {
			/**
             * 浮标的接口onUserInfo接受两个参数： userinfo: 加密过后的用户信息 UserInfo实例：
             * userInfo被解密并处理成HashMap<String, String>类型存放，CP解析此变量并实现登录后的逻辑
             * 
             * CP在dealUserInfo中实现用户的登录信息解析并跳转相应的页面
             */
            if (null == hwBuoy)
            {
                hwBuoy = BuoyOpenSDK.getIntance();
            }
            boolean userResult = hwBuoy.onUserInfo(userInfo, new UserInfo()
            {
                public void dealUserInfo(HashMap<String, String> userInfo)
                {
                     LogD(userInfo.toString());

                     // 用户信息为空，登录失败
                     if (null == userInfo) {
                         LogD("用户信息为null");
                         IAPHuawei.loginResult(PayWrapper.LOGINRESULT_FAIL, "");
                     }
                     // 使用华为账号登录且成功，进行accessToken验证
                     else if ("1".equals((String) userInfo.get("loginStatus"))) {
                    	 LogD("使用华为账号登录，进行accessToken校验");
                         
                         // 保存userID，供用户登录信息显示
                         userID = (String)userInfo.get("userID");
                         
                         // 进行accessToken校验                       
                         IAPHuawei.loginResult(PayWrapper.LOGINRESULT_SUCCESS, (String)userInfo.get("accesstoken"));
                     }
                }
            }, mContext); // 此处必须为Activity
            
            if (!userResult)
            {
                LogD("Fail to login on");
                IAPHuawei.loginResult(PayWrapper.LOGINRESULT_FAIL, "");
            }
		}
	}

	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				String price = productInfo.get("price");
				String productName = productInfo.get("productName");
				String productDesc = productInfo.get("productDesc");
				String orderId = productInfo.get("orderId");
				final String PAY_RSA_PUBLIC = productInfo.get("PAY_RSA_PUBLIC");
				
				GameBoxUtil.pay(mContext, price, productName, productDesc, orderId, new IPayHandler(){
					
					public void onFinish(Map<String, String> payResp) {
			            LogD("支付结束：payResp= " + payResp);
			            // 处理支付结果
			            String pay = "支付未成功！";
			            LogD("支付结束，返回码： returnCode=" + payResp.get(PayParameters.returnCode));
			            // 支付成功，进行验签
			            if ("0".equals(payResp.get(PayParameters.returnCode)))
			            {
			                if ("success".equals(payResp.get(PayParameters.errMsg)))
			                {
			                    // 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
			                    if (payResp.containsKey("isCheckReturnCode") && "yes".equals(payResp.get("isCheckReturnCode")))
			                    {
			                        payResp.remove("isCheckReturnCode");
			                    }
			                    else
			                    {// 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
			                        payResp.remove("isCheckReturnCode");
			                        payResp.remove(PayParameters.returnCode);
			                    }
			                    // 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
			                    String sign = payResp.remove(PayParameters.sign);
			                    String noSigna = HuaweiPayUtil.getSignData(payResp);
			                    
			                    // 使用公钥进行验签
			                    boolean s = Rsa.doCheck(noSigna, sign, PAY_RSA_PUBLIC);
			                    
			                    if (s)
			                    {
			                        IAPHuawei.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功！");
			                    }
			                    else
			                    {
			                        IAPHuawei.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功，但验签失败！");
			                    }
			                    LogD("支付结束：sign= " + sign + "，待验证字段：" + noSigna + "，Rsa.doChec = " + s);
			                }
			                else
			                {
			                    LogD("支付失败 errMsg= " + payResp.get(PayParameters.errMsg));
			                    IAPHuawei.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败 errMsg= " + payResp.get(PayParameters.errMsg));
			                }
			            }
			            else if ("30002".equals(payResp.get(PayParameters.returnCode)))
			            {
			                IAPHuawei.PayResult(PayWrapper.PAYRESULT_TIMEOUT, "支付结果查询超时！");
			            }
			            else if ("200001".equals(payResp.get(PayParameters.returnCode)))
			            {
			                IAPHuawei.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付！");
			            }
			            else
			            {
			            	IAPHuawei.PayResult(PayWrapper.PAYRESULT_FAIL, "未知异常");
			            }
					}
				});
			}
		});
	}

	public static void loginResult(int ret, String accessToken){
		Hashtable<String, String> info = new Hashtable<String, String>();
		info.put("accessToken", accessToken);
		
		PayWrapper.onLoginResult(mHuawei, ret, info);
		LogD("Huawei loginResult : " + ret + " info : " + info);
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mHuawei, ret, msg);
		LogD("Huawei payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.3.1";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}

	public void onResume() {
		// 获取用户之前已存储的角色信息
		hwBuoy.getRoleInfo(mContext, userID);
		// 存储用户当前的角色信息
		hwBuoy.saveRoleInfo(null, userID, mContext);
		
		 if (null != hwBuoy) 
		    {
		        synchronized (hwBuoy)
		        {
		            hwBuoy.showSmallWindow(mContext.getApplicationContext());
		        }
		    }

	}

	public void onPause() {
		if (null != hwBuoy)
	    {
	        hwBuoy.hideSmallWindow(mContext.getApplicationContext());
	        hwBuoy.hideBigWindow(mContext.getApplicationContext());
	    }
	}

	public void onDestroy() {
		if (null != hwBuoy) {
	        hwBuoy.destroy(mContext.getApplicationContext());
	        hwBuoy = null;
	    }
	    // 清空帐号资源
	    OpenHwID.releaseResouce();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	public void onNewIntent(Intent intent) {
		
	}
}