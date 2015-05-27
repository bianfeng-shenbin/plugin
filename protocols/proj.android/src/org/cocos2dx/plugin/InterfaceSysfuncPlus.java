package org.cocos2dx.plugin;

import java.util.Hashtable;

public interface InterfaceSysfuncPlus {
	public final int PluginType = 10;

	public void configDeveloperInfo(Hashtable<String, String> info);

	public void setDebugMode(boolean debug);

	public String getSDKVersion();

	public String getPluginVersion();

	public void installApk(String path);
	
	public void deleteApk(String path);
	
	public String getPhoneNumber();
	
	public int sendSms(String phonenumber,String msg);
}
