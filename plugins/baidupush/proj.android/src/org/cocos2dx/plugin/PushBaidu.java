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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class PushBaidu implements InterfacePush, PluginListener{

	private static final String TAG = "PushBaidu";
	private static Activity mContext = null;
	private static PushBaidu mPushBaidu = null;
	private static boolean bDebug = false;
	private static String API_KEY = null;

	protected static void LogE(String msg, Exception e) {
		Log.e(TAG, msg, e);
		e.printStackTrace();
	}

	protected static void LogD(String msg) {
		if (bDebug) {
			Log.d(TAG, msg);
		}
	}

	public PushBaidu(Context context) {
		if(mPushBaidu == null)
		{
			mContext = (Activity) context;
			mPushBaidu = this;
		}
	}
	
	static public void addListenerToWrapper(Context context)
	{
		mPushBaidu = new PushBaidu(context);
		PluginWrapper.addListener(mPushBaidu);
	}

	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
		LogD("initDeveloperInfo invoked " + cpInfo.toString());
		final Hashtable<String, String> curCPInfo = cpInfo;
		PluginWrapper.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				try {
					API_KEY = Utils.getMetaValue(mContext, "api_key");
					Log.d(TAG, "PushBaidu_api_key 1: " + API_KEY);

					if (!Utils.hasBind(mContext.getApplicationContext())) {
						Log.d(TAG, "PushBaidu_api_key 2: " + API_KEY);
						PushManager.startWork(mContext,
								PushConstants.LOGIN_TYPE_API_KEY, API_KEY);
					}
					// 设置android标签
					List<String> tags = new ArrayList<String>();
					String tag = "android";
					tags.add(tag);
					PushManager.setTags(mContext, tags);
					// 通知设置
					String pkgName = mContext.getPackageName();
					Resources resource = mContext.getResources();
					Log.d("PushBaidu", "activity_name: "
							+ mContext.getClass().getCanonicalName());
					CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
							mContext.getApplicationContext(), resource
									.getIdentifier(
											"notification_custom_builder",
											"layout", pkgName), resource
									.getIdentifier("notification_icon", "id",
											pkgName), resource.getIdentifier(
									"notification_title", "id", pkgName),
							resource.getIdentifier("notification_text", "id",
									pkgName));
					cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
					cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
							| Notification.DEFAULT_VIBRATE);
					cBuilder.setStatusbarIcon(mContext.getApplicationInfo().icon);
					cBuilder.setLayoutDrawable(resource.getIdentifier(
							"simple_notification_icon", "drawable", pkgName));
					PushManager.setNotificationBuilder(mContext, 1, cBuilder);
				} catch (Exception e) {
					LogE("Developer info is wrong!", e);
				}
			}
		});
	}

	@Override
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public static void bindResult(int ret, Hashtable<String, String> resInfo) {
		Log.d("bdpush", "PushBaidu_bindResult");
		PushWrapper.onBindResult(mPushBaidu, ret, resInfo);
	}

	public static void notificationResult(String title, String description,
			String customContentString) {
		Log.d("bdpush", "PushBaidu_notificationResult:: " + title + " "
				+ description + " " + customContentString);
		PushWrapper.onNotificationResult(mPushBaidu, title, description,
				customContentString);
	}

	@Override
	public String getSDKVersion() {
		return "3.0.0";
	}

	@Override
	public String getPluginVersion() {
		return "0.2.0";
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreate() {
		Log.d("testBaidu", "testBaiduPluginListener 1");
		Hashtable<String, String> t = new Hashtable<String, String>();
		PushBaidu p = new PushBaidu(mContext);
		p.configDeveloperInfo(t);
		
	}
}
