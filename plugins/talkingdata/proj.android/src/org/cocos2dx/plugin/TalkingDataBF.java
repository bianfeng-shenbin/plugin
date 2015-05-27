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
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class TalkingDataBF implements InterfaceTk {

	private static final String LOG_TAG = "TalkingData";
	private static Activity mActivity = null;
	private static TalkingDataBF mTalkingData = null;
	private static boolean bDebug = false;

	// private static String mDeviceId = null;
	private static GameEvent.LoginInfo mLoginInfo = new GameEvent.LoginInfo();
	private static GameEvent.ItemInfo mBftkItemInfoTpl = new GameEvent.ItemInfo(
			0, 0);
	private static ObtainDeviceidCallback mDeviceIdCb; // 获得deviceId的回调
	private static boolean mIsBaseSdkInit = false;

	protected static void LogE(String msg, Exception e) {
		Log.e(LOG_TAG, msg, e);
		e.printStackTrace();
	}

	protected static void LogD(String msg) {
		if (bDebug) {
			Log.d(LOG_TAG, msg);
		}
	}

	public TalkingDataBF(Context context) {
		mActivity = (Activity) context;
		mTalkingData = this;
	}

	@Override
	public void configDeveloperInfo(Hashtable<String, String> info) {
		Log.d("TalkingData",
				"TalkingDataJava.configDeveloperInfo: " + info.toString());
		final Hashtable<String, String> curInfo = info;
		PluginWrapper.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				try {
					int isUserNew = Integer.getInteger(
							curInfo.get("isUserNew"), 1);
					int tvMode = Integer.getInteger(curInfo.get("tvMode"), 1);
					String areaId = curInfo.get("areaId");
					String areaName = curInfo.get("areaName");
					String serverId = curInfo.get("serverId");
					String serverName = curInfo.get("serverName");
					String channelName = curInfo.get("channelName");
					String device_code = curInfo.get("device_code");
					String appId = curInfo.get("appId");
					Log.d("BaseSdk", "onInitBFData tvMode!!: " + tvMode
							+ " appId: " + appId + " channelName: "
							+ channelName + " areaId: " + areaId);
					BaseSdk.init(mActivity, appId, channelName, areaId);
					mIsBaseSdkInit = true;
					Log.v("BaseSdk", "BaseSdk init success!!!~");
					GameEvent.LaunchInfo baseLaunch = new GameEvent.LaunchInfo();
					baseLaunch.areaId = areaId;
					baseLaunch.areaName = areaName;
					baseLaunch.serverId = serverId;
					baseLaunch.serverName = serverName;
					baseLaunch.channelName = channelName;
					GameEvent.onLaunch(mActivity, baseLaunch);
					Log.v("BaseSdk", "call getDeviceId");
					BaseSdk.getDeviceId(mActivity, mDeviceIdCb);
					Log.v("BaseSdk", "call end getDeviceid: " + mDeviceIdCb);
					Log.v("BaseSdk", "BaseSdk onInitBFData isUserNew = " + isUserNew
							+ " areaId = " + areaId + " areaName = " + areaName
							+ " channelName = " + channelName);
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

//	@Override
//	public void onInitBFData(int isUserNew, int tvMode, String areaId,
//			String areaName, String serverId, String serverName,
//			String channelName, String device_code, String appId) {
//		Log.d("BaseSdk", "onInitBFData tvMode!!: " + tvMode + " appId: "
//				+ appId + " channelName: " + channelName + " areaId: " + areaId);
//		// //TCAgent.setClientType(String.valueOf(tvMode)); 改为从manifest中获取！
//		// //TCAgent.init(mActivity, appId, channelName); 日志是刚刚加的吗？
//		BaseSdk.init(mActivity, appId, channelName, areaId);
//		mIsBaseSdkInit = true;
//		Log.v("BaseSdk", "BaseSdk init success!!!~");
//		// //TCAgent.setDeviceId(device_code);
//		// BASESDK内部获取设备号，不再需要设置进去。如果需要获得设备号，可以getDeviceId
//		// mDeviceId = device_code;
//		// //Log.v("BaseSdk", "BaseSdk onInitBFData setDeviceId device_code = "
//		// + device_code);
//		// //TCAgent.gameLaunch(isUserNew, isUserNew, isUserNew, areaId,
//		// areaName, serverId, serverName, channelName);
//		GameEvent.LaunchInfo baseLaunch = new GameEvent.LaunchInfo();
//		baseLaunch.areaId = areaId;
//		baseLaunch.areaName = areaName;
//		baseLaunch.serverId = serverId;
//		baseLaunch.serverName = serverName;
//		baseLaunch.channelName = channelName;
//		GameEvent.onLaunch(mActivity, baseLaunch);
//		Log.v("BaseSdk", "call getDeviceId");
//		BaseSdk.getDeviceId(mActivity, mDeviceIdCb);
//		Log.v("BaseSdk", "call end getDeviceid: " + mDeviceIdCb);
//		Log.v("BaseSdk", "BaseSdk onInitBFData isUserNew = " + isUserNew
//				+ " areaId = " + areaId + " areaName = " + areaName
//				+ " channelName = " + channelName);
//	}

	@Override
	public void onEventMapBFData(final String eveID, final String eveLab,
			final String mapMsg) {
		Log.v("BaseSdk", "BaseSdk onEventMapBFData eveID = " + eveID
				+ " eveLab = " + eveLab + " mapMsg = " + mapMsg);
		final Map<String, Object> map = new HashMap<String, Object>();
		int head = 0;
		Log.v("BaseSdk", "onEventMapBFData:" + eveID);
		do {
			int end = mapMsg.indexOf(";", head);
			if (end == -1)
				end = mapMsg.length();
			String couple = mapMsg.substring(head, end);
			if (couple.equals(""))
				break;
			int cEnd = couple.indexOf(",");
			map.put(couple.substring(0, cEnd),
					couple.substring(cEnd + 1, couple.length()));

			head = end + 1;
			if (head >= mapMsg.length())
				break;
		} while (true);

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("BaseSdk", "onEventMapBFData: " + eveID + " : " + eveLab
						+ " : " + mapMsg);
				BaseSdk.onEvent(mActivity, eveID, eveLab, map);
			}
		});

	}

	private class MyObtainDeviceidCallback implements ObtainDeviceidCallback {
		@Override
		public void onReceived(String deviceId) {
			// TODO Auto-generated method stub
			Log.d("BaseSdk", "ObtainDeviceidCallback onReceived~~: " + deviceId);
			if (deviceId.equals("3f32096b3ad1c2ed4116be0af6a6a30a")) {
				Log.d("BaseSdk", "wrong device id");
				SharedPreferences pr = mActivity.getSharedPreferences(
						"pref.deviceid.key.bf", Context.MODE_PRIVATE);
				pr.edit().clear().commit();
				if (isSdcardReady()) {
					Log.d("BaseSdk", "delete sdcard tidbf");
					File file1 = new File(getSdcardPath() + ".tidbf");
					if (file1 != null && file1.exists())
						file1.delete();
					File file2 = new File(getSdcardPath() + "tmp/.tidbf");
					if (file2 != null && file2.exists())
						file2.delete();
				} else {
					Log.d("BaseSdk", "no sdcard");
				}

			} else {
				Log.d("BaseSdk", "right device id");
			}
		}
	}

	private InetAddress getLocalIpAddress() throws UnknownHostException {
		WifiManager wifiManager = (WifiManager) mActivity
				.getSystemService(android.content.Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return InetAddress.getByName(String.format("%d.%d.%d.%d",
				(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
				(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
	}

	// 开局事件，统计盘数
	@Override
	public void onGameBegin(String userLevel) {
		Log.d("BaseSdk", "GameEvent.onGameBegin: " + userLevel);
		HashMap<String, Object> localHashMap = new HashMap<String, Object>();
		localHashMap.put("userName", mLoginInfo.userName);
		localHashMap.put("userId", mLoginInfo.userId);
		localHashMap.put("userType", mLoginInfo.userType);
		localHashMap.put("areaId", mLoginInfo.areaId);
		localHashMap.put("areaName", mLoginInfo.areaName);
		localHashMap.put("serverId", mLoginInfo.serverId);
		localHashMap.put("serverName", mLoginInfo.serverName);
		localHashMap.put("channelName", mLoginInfo.channelName);
		localHashMap.put("userLev", userLevel);
		String ipaddr = "";
		try {
			ipaddr = getLocalIpAddress().toString();
		} catch (UnknownHostException e) {
		}
		localHashMap.put("clientIp", ipaddr);
		BaseSdk.onEvent(mActivity, "15", "15", localHashMap);
	}

	// 任务事件，统计任务和任务发放金币
	@Override
	public void onGameTask(String userLevel, String taskType, String taskName,
			String amount) {
		Log.d("BaseSdk", "GameEvent.onGameTask: " + userLevel + " " + taskType
				+ " " + taskName + " " + amount);
		HashMap<String, Object> localHashMap = new HashMap<String, Object>();
		localHashMap.put("taskType", taskType);
		localHashMap.put("taskName", taskName);
		localHashMap.put("amount", Double.valueOf(amount));
		localHashMap.put("coinType", 1);
		localHashMap.put("userName", mLoginInfo.userName);
		localHashMap.put("userId", mLoginInfo.userId);
		localHashMap.put("userType", mLoginInfo.userType);
		localHashMap.put("areaId", mLoginInfo.areaId);
		localHashMap.put("areaName", mLoginInfo.areaName);
		localHashMap.put("serverId", mLoginInfo.serverId);
		localHashMap.put("serverName", mLoginInfo.serverName);
		localHashMap.put("channelName", mLoginInfo.channelName);
		localHashMap.put("userLev", userLevel);
		String ipaddr = "";
		try {
			ipaddr = getLocalIpAddress().toString();
		} catch (UnknownHostException e) {
		}
		localHashMap.put("clientIp", ipaddr);
		BaseSdk.onEvent(mActivity, "17", "17", localHashMap);
	}

	// 领取低保事件
	@Override
	public void onCostSilver(final String dibaoType, final String dibaoCost) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("BaseSdk", "GameEvent.onCostSilver: " + dibaoType + " "
						+ dibaoCost);
				GameEvent.ItemInfo item = new GameEvent.ItemInfo(0, 0);
				item.buyType = dibaoType; // buytype对应协议中的type，“低保”
				item.consume = Double.valueOf(dibaoCost); // consume代表领取低保的金币数
				item.areaId = mLoginInfo.areaId;
				item.areaName = mLoginInfo.areaName;
				item.channelName = mLoginInfo.channelName;
				item.isUserNew = mLoginInfo.isUserNew;
				item.isAreaNew = mLoginInfo.isAreaNew;
				item.isGameNew = mLoginInfo.isGameNew;
				item.failReason = mLoginInfo.failReason;
				item.serverId = mLoginInfo.serverId;
				item.serverName = mLoginInfo.serverName;
				item.userId = mLoginInfo.userId;
				item.userName = mLoginInfo.userName;
				item.userType = mLoginInfo.userType;
				GameEvent.onCostSilver(mActivity, item);

				// TCAgent.gameLogin(type,isUserNew,isUserNew,isUserNew,userName,ID,userType,areaId,areaName,
				// serverId,serverName,reason,channelName,roomName);
			}
		});
	}

	@Override
	public void onPlayerAccountBFData(final int type, final int isUserNew,
			final String userName, final String userId, final String userType,
			final String areaId, final String areaName, final String serverId,
			final String serverName, final String reason,
			final String channelName, final String roomName,
			final String userLevel) {
		final long ID = Long.parseLong(userId);
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("BaseSdk", "GameEvent.onLogin: " + userLevel);

				mLoginInfo.areaId = areaId;
				mLoginInfo.areaName = areaName;
				mLoginInfo.channelName = channelName;
				mLoginInfo.isUserNew = isUserNew == 1 ? true : false;
				mLoginInfo.isAreaNew = mLoginInfo.isUserNew;
				mLoginInfo.isGameNew = mLoginInfo.isUserNew;
				mLoginInfo.failReason = reason;
				mLoginInfo.roomName = roomName;
				mLoginInfo.serverId = serverId;
				mLoginInfo.serverName = serverName;
				mLoginInfo.userId = Long.getLong(userId, 876678); // 不确定userId是否可以转成long
				mLoginInfo.userName = userName;
				mLoginInfo.userType = userType;
				mLoginInfo.type = type;
				mLoginInfo.userLevel = userLevel;
				GameEvent.onLogin(mActivity, mLoginInfo);

				// TCAgent.gameLogin(type,isUserNew,isUserNew,isUserNew,userName,ID,userType,areaId,areaName,
				// serverId,serverName,reason,channelName,roomName);
			}
		});
		Log.v("BaseSdk", "BaseSdk onPlayerAccountBFData type = " + type
				+ " isUserNew = " + isUserNew + " userName = " + userName
				+ " userId = " + userId + " areaId = " + areaId
				+ " areaName = " + areaName + " channelName = " + channelName);
	}

	@Override
	public void onLoginOutBFData() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.v("BaseSdk", "BaseSdk GameEvent.onLoginOuttttuuuuu "
						+ getDuration());
				GameEvent.onLoginOut(mActivity, mLoginInfo);
			}
		});
	}

	private int getDuration() {
		try {
			Class<?> cls = Class.forName("com.bianfeng.base.GameEvent");
			Field f = cls.getDeclaredField("a");
			f.setAccessible(true);
			long loginTime = f.getLong(null);

			Method m = cls.getDeclaredMethod("diffTimeFormCurrentTime",
					long.class);
			m.setAccessible(true);
			int r = (Integer) m.invoke(null, loginTime);
			System.out.println("result is " + r);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void onEventSingleBFData(final String eveID) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("BaseSdk", "onEventSingleBFData: " + eveID);
				BaseSdk.onEvent(mActivity, eveID);
			}
		});

	}

	@Override
	public void onEventDoubleBFData(final String eveID, final String eveLab) {

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("BaseSdk", "onEventDoubleBFData: " + eveID + " : "
						+ eveLab);
				BaseSdk.onEvent(mActivity, eveID, eveLab);
			}
		});
	}

	/**
	 * 检查SD卡是否存在
	 * 
	 * @return 存在返回true，否则返回false
	 */
	private boolean isSdcardReady() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获得SD路径
	 * 
	 * @return sdCard path end with separator
	 */
	private String getSdcardPath() {
		return Environment.getExternalStorageDirectory().toString()
				+ File.separator;
	}
}
