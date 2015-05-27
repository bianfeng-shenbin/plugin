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

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;
import cn.play.dserv.CheckTool;
import cn.play.dserv.ExitCallBack;

public class IAPCtcpay implements InterfacePay{

	//电信爱游戏插件化参数
	private static final String LOG_TAG = "IAPCtcpay";
	private static Activity mContext = null;
	private static IAPCtcpay mCtcpay = null;
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
	
	public IAPCtcpay(Context context){
		mContext = (Activity)context;
		mCtcpay = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String>info){
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					EgamePay.init(mContext);
					
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}
	
	public void pluginLogin(Hashtable<String, String>info){
		LogD("pluginLogin invoked " + info.toString());
		try{
			String loginInfoFlag = info.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}
	
	public void payForProduct(Hashtable<String, String>info){
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					  String toolsPrice = (String)productInfo.get("toolsPrice");
			          String cpParams = (String)productInfo.get("cpParams");
			          String toolsDesc = (String)productInfo.get("toolsDesc");
			          
			          HashMap<String, String> payInfo = new HashMap<String, String>();
			          payInfo.put("toolsPrice", toolsPrice);
			          payInfo.put("cpParams", cpParams);
			          payInfo.put("toolsDesc", toolsDesc);
			          
			          EgamePay.pay(mContext, payInfo, payCallback);
			          
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	public void moreGame(){
		CheckTool.more(mContext);
	}
	
	public void exit(){
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				CheckTool.exit(mContext, new ExitCallBack(){

					public void cancel() {
						LogD("User Canceled!!");
					}

					public void exit() {
						LogD("ConfirmExit Exit Game!!");
						System.exit(0);
					}
				});
			}
		});
	}
	
	private EgamePayListener payCallback = new EgamePayListener(){

		@Override
		public void paySuccess(Map<String, String> params) {
			IAPCtcpay.PayResult(PayWrapper.PAYRESULT_SUCCESS,
												"道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付成功");
		}

		@Override
		public void payFailed(Map<String, String> params, int errorInt) {
			IAPCtcpay.PayResult(PayWrapper.PAYRESULT_FAIL,
												"道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付失败：错误代码："+errorInt);
		}

		@Override
		public void payCancel(Map<String, String> params) {
			IAPCtcpay.PayResult(PayWrapper.PAYRESULT_CANCEL,
												"道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付已取消");
		}
	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mCtcpay, ret, msg);
		LogD("Ctcpay payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug){
		bDebug = debug;
	}
	
	public String getSDKVersion(){
		return "4.0.6Uni";
	}
	
	public String getPluginVersion(){
		return "1.0.2";
	}
}
