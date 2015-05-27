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

import org.json.JSONObject;

import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.duoku.platform.single.item.DKCMGBData;
import com.duoku.platform.single.item.DKCMMMData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

public class AdsBaiduPX implements InterfaceAds, PluginListener{
	
	private static final String LOG_TAG = "AdsBaiduPX";
	private static Activity mContext = null;
	private static boolean bDebug = false;
	private static AdsBaiduPX mAdapter = null;
	
	public void onResume() {
		DKPlatform.getInstance().resumeBaiduMobileStatistic(mContext); 
	}

	public void onPause() {
		DKPlatform.getInstance().pauseBaiduMobileStatistic(mContext); 
	}

	public void onDestroy() {
		DKPlatform.getInstance().stopSuspenstionService(mContext);
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	public void onNewIntent(Intent intent) {
		
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

	public AdsBaiduPX(Context context) {
		mContext = (Activity) context;
		mAdapter = this;
	}

	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String> devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String devInfoFlag = devInfo.get("flag");
					
					//回调函数
					IDKSDKCallBack initcompletelistener = new IDKSDKCallBack(){
						public void onResponse(String paramString) {
							LogD(paramString);
							try {
								JSONObject jsonObject = new JSONObject(paramString);
								// 返回的操作状态码
								int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
								
								//初始化完成
								if(mFunctionCode == DkErrorCode.BDG_CROSSRECOMMEND_INIT_FINSIH){
									initAds();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					
					//参数为测试数据，接入时请填入你真实数据
					//DKCMMMData mmData = new DKCMMMData("","ca0c4ff0d57b3344285e9187");
					//DKCMGBData gbData = new DKCMGBData();

					//初始化函数
					DKPlatform.getInstance().init(mContext, true, DKPlatformSettings.SdkMode.SDK_BASIC,null,null,initcompletelistener);
					
					LogD("ConfigDeveloperInfo result: " + devInfoFlag);
				}catch(Exception e){
					LogE("Developer info parse error!", e);
				}
			}
		});
	}
	
	private void initAds(){
		LogD("initAds_1");
		DKPlatform.getInstance().bdgameInit(mContext, new IDKSDKCallBack() {
			
			public void onResponse(String paramString) {
				LogD("AdsBaiduPX init success!");
			}
		});
	}

	public void exitGame(){
		LogD("-------------exitGame -------------");
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				LogD("-------------exitGame In main Thread-------------");
				DKPlatform.getInstance().bdgameExit(mContext, new IDKSDKCallBack() {
					public void onResponse(String paramString) {
					LogD("-------------退出游戏!!!-------------");
					mContext.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					}
				});
				LogD("-------------exitGame2222-------------");
			}
		});
	}	

	public void showAds(Hashtable<String, String> adsInfo, int pos) {
		
	}

	public void hideAds(Hashtable<String, String> adsInfo) {
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				DKPlatform.getInstance().bdgamePause(mContext, new IDKSDKCallBack() {
					public void onResponse(String paramString) {
						LogD("AdsBaiduPX bdgamePause success!!!");	}
				});
			}
		});
	}

	public void queryPoints() {
		LogD("Admob not support query points!");
	}

	public void spendPoints(int points) {
		LogD("Admob not support spend points!");
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.4.1v7";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}