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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.bianfeng.base.BaseSdk;
import com.bianfeng.base.GameEvent;
import com.bianfeng.base.ObtainDeviceidCallback;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SysfuncPlus implements InterfaceSysfuncPlus {

	private static final String LOG_TAG = "TalkingData";
	private static Activity mActivity = null;
	private static SysfuncPlus mSysfuncPlus = null;
	private static boolean bDebug = false;

	static TelephonyManager Tel;
	
	protected static void LogE(String msg, Exception e) {
		Log.e(LOG_TAG, msg, e);
		e.printStackTrace();
	}

	protected static void LogD(String msg) {
		if (bDebug) {
			Log.d(LOG_TAG, msg);
		}
	}

	public SysfuncPlus(Context context) {
		mActivity = (Activity) context;
		mSysfuncPlus = this;
	}

	@Override
	public void configDeveloperInfo(Hashtable<String, String> info) {
		Log.d("Sysfuncplus",
				"Sysfuncplus.configDeveloperInfo: " + info.toString());
		final Hashtable<String, String> curInfo = info;
		PluginWrapper.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				try {
	
				} catch (Exception e) {
					LogE("Developer info is wrong!", e);
				}
			}
		});
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
	public void setDebugMode(boolean debug) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void installApk(String lpszMsg) {

		try {
            String command = "chmod 777 " + lpszMsg;
            Log.v("debug", "command = " + command);
            Runtime runtime = Runtime.getRuntime(); 

            runtime.exec(command);
           } catch (IOException e) {
            Log.v("debug","chmod fail!!!!");
            e.printStackTrace();
           }
		
		File k = new File(lpszMsg);
		Log.v("debug", "AC do Local APK file exist?");
		if (!k.exists()) return;
		Log.v("debug", "AC yes");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.v("debug", "AC new intent");
		intent.setDataAndType(Uri.fromFile(k), "application/vnd.android.package-archive"); 
		Log.v("debug", "AC start action"); 
		mActivity.startActivity(intent);
				
	}

	// 功能描述：删除下载到本地的新APK
	@Override
	public void deleteApk(String path) {
		
		// 删除本地APK
		File file = new File(path);
		Log.v("debug", "AC do Local APK file exist?");
		if (!file.exists()) return;
		Log.v("debug", "AC delete file");
		if(file.delete())
		{
			Log.v("debug", "AC delete success");
		}
		else
		{
			Log.v("debug", "AC delete failed");
		}
	}

	// 获取手机号码
	public String getPhoneNumber()
	{
		return Tel.getLine1Number();
	}
	
	public int sendSms(String phonenumber,String msg)
	{
        if(PhoneNumberUtils.isGlobalPhoneNumber(phonenumber)&&phonenumber.length()>0&&msg.length()>0)
        {
            PendingIntent pi=PendingIntent.getBroadcast(mActivity, 0, new Intent(mActivity,mActivity.getClass()), 0);
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage(phonenumber, null, msg, pi, null);//发送信息到指定号码
            return 0;
        }
        else
        {
        	//SysMessageBox("短信提示","转正短信发送失败","确定",11);
        	return 1;
        }
	}
	final static int nString = 0;
	final static int nInt = 1;
	static String sConName = "config";
		// 功能描述: 保存字符数据
	public void SaveStringData(String lpszKey, byte lpszData[]) {
		String data = new String(lpszData);
		WriteConfig(lpszKey, data, nString);
	}

	// 功能描述: 读取字符数据
	public String LoadStringData(String lpszKey) {
		return Readconfig(lpszKey, nString);
	}

	// 功能描述: 删除数据
	public void RemoveData(String lpszKey) {
		RemoveConfig(lpszKey);
	}
	
		public static void WriteConfig(String label, String value, int type) {

		// 通过Activity自带的getSharedPreferences方法，得到SharedPreferences对象
		// 第一个参数表示保存后 xml 文件的名称(底层实现是将数据保存到xml文档中)。
		// 第二个参数表示xml文档的权限为私有，并且重新写的数据会覆盖掉原来的数据
		try {
			SharedPreferences preferences = mActivity.getSharedPreferences(
					sConName, PreferenceActivity.MODE_PRIVATE);
			Editor ed = preferences.edit();
			if (label != null && label.length() > 0 && value != null) {
				if (type == nString) {
					ed.putString(label, value);
				} else if (type == nInt) {
					try {
						int num = Integer.parseInt(value);
						ed.putInt(label, num);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				ed.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void RemoveConfig(String label) {

		try {
			SharedPreferences preferences = mActivity.getSharedPreferences(
					sConName, PreferenceActivity.MODE_PRIVATE);

			Editor ed = preferences.edit();
			if (label != null && label.length() > 0) {
				ed.remove(label);
				ed.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String Readconfig(String label, int type) {
		SharedPreferences preferences = null;
		try {
			preferences = mActivity.getSharedPreferences(sConName,
					PreferenceActivity.MODE_PRIVATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 得到文件中的name标签值，第二个参数表示如果没有这个标签的话，返回的默认值
		if (type == nString) {
			if (preferences == null) {
				return "";
			}
			if (preferences.contains(label)) {
				return preferences.getString(label, "");
			} else {
				return "";
			}
		} else if (type == nInt) {
			if (preferences == null)
				return "0";
			if (preferences.contains(label))
				return String.valueOf(preferences.getInt(label, 0));
			else
				return "0";
		}

		return "";
	}

	
}
