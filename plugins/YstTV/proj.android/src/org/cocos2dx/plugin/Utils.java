package org.cocos2dx.plugin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	/**
     * 本地查找apk 是否存在
     * 
     * @param packageName 包名
     * */
    public static boolean checkPackageInfo(Context mContext, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo app = mContext.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            if (app != null) {
                return true;
            } else {
                return false;
            }
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    
    public static boolean isNetworkReachable(Context mContext) {
		boolean bRet = false;
		try {
			ConnectivityManager conn = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conn.getActiveNetworkInfo();
			bRet = (null == netInfo) ? false : netInfo.isAvailable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

}
