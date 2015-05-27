package org.cocos2dx.plugin;

import java.util.Hashtable;

public interface InterfaceTk {
	public final int PluginType = 9;

	public void configDeveloperInfo(Hashtable<String, String> info);

	public void setDebugMode(boolean debug);

	public String getSDKVersion();

	public String getPluginVersion();

	public void onEventMapBFData(String eveID, String eveLab, String mapMsg);

//	public void onInitBFData(int isUserNew, int tvMode, String areaId,
//			String areaName, String serverId, String serverName,
//			String channelName, String device_code, String appId);

	public void onGameBegin(String userLevel);

	public void onGameTask(String userLevel, String taskType, String taskName,
			String amount);

	public void onCostSilver(String dibaoType, String dibaoCost);

	public void onPlayerAccountBFData(final int type, final int isUserNew,
			final String userName, final String userId, final String userType,
			final String areaId, final String areaName, final String serverId,
			final String serverName, final String reason,
			final String channelName, final String roomName,
			final String userLevel);

	public void onLoginOutBFData();

	public void onEventSingleBFData(final String eveID);

	public void onEventDoubleBFData(final String eveID, final String eveLab);
}
