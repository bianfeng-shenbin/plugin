package org.cocos2dx.plugin;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.cocos2dx.plugin.AdsWrapper;
import org.cocos2dx.plugin.InterfaceAds;
import org.cocos2dx.plugin.PluginWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.appx.BDBannerAd;
import com.baidu.appx.BDInterstitialAd;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;


public class AdsBaiduAppX implements InterfaceAds{
	private static Activity mContext = null;
	private static AdsBaiduAppX mAdapter = null;
	private static BDBannerAd bannerAdView = null;
	private static BDInterstitialAd appxInterstitialAdView = null;
	private static RelativeLayout mAdContainer = null;
	private static Handler BaiduAppXHandler = null;  
	
	public AdsBaiduAppX(Context context) {
		mContext = (Activity) context;
		mAdapter = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> devInfo)
	{		
		Log.d("AdsBaiduAppX",
				"AdsBaiduAppX.configDeveloperInfo : " + devInfo.toString());
		
		final Hashtable<String, String> curInfo = devInfo;
        PluginWrapper.runOnMainThread(new Runnable(){
        	public void run(){
        		try{
        			String strType = curInfo.get("type");
        			String[] StrTypeArray = strType.split("&");
        			

        			 for (int i = 0; i < StrTypeArray.length; i++) {

        				 //横屏广告
        				 if(StrTypeArray[i].equals("BannerAd")) {
        					 bannerAdView = new BDBannerAd(mContext, curInfo.get("apiKey"),
        							 								 curInfo.get("adId"));
        				
        					 FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        												FrameLayout.LayoutParams.FILL_PARENT,
        												FrameLayout.LayoutParams.WRAP_CONTENT);
        			
        					 mAdContainer = new RelativeLayout(mContext);
        					 mAdContainer.addView(bannerAdView);
        					 mContext.addContentView(mAdContainer,params);
        				 }
        			
        				 //插屏广告
        				 if(StrTypeArray[i].equals("InterstitialAd")) {
        					 appxInterstitialAdView = new BDInterstitialAd(mContext, curInfo.get("apiKey"),
        							 												 curInfo.get("adId"));
        				
        					 // 加载广告
        					 appxInterstitialAdView.loadAd();
        				 }
        			 }
        			 
        			//控制广告是否显示 obj为tyep值 arg1为控制是否显示
        			BaiduAppXHandler = new Handler(){  
        	            public void handleMessage(Message msg){  
        	        
        	            	Log.d("AdsBaiduAppX",
        	        				"handleMessage msg.obj: " + msg.obj.toString() + " msg.arg1: " + msg.arg1);
        	            
        	            	String strType = msg.obj.toString();
                			String[] StrTypeArray = strType.split("&");
                			
                			for (int i = 0; i < StrTypeArray.length; i++) {
                				//横屏广告
                				if(StrTypeArray[i].equals("BannerAd")) {
            	            		mAdContainer.setVisibility(msg.arg1);
            	            	}
                				
                				//插屏广告
                				if(StrTypeArray[i].equals("InterstitialAd")) {
            	    				// 展示插屏广告前先请先检查下广告是否加载完毕
            	    				if (appxInterstitialAdView.isLoaded()) {
            	    					appxInterstitialAdView.showAd();
     
            	    				} else {
            	    					appxInterstitialAdView.loadAd();
            	    				}
            	            	}
                			 }
        	            }  
        	        }; 
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
	}
	
	public void showAds(Hashtable<String, String> adsInfo, int pos)
	{
		Log.v("BaiduAppX", "showAds.info:" + adsInfo.toString());
		final Hashtable<String, String> curInfo = adsInfo;
    	Message msg = BaiduAppXHandler.obtainMessage();
    	msg.obj = curInfo.get("type");
		msg.arg1 = View.VISIBLE;
		BaiduAppXHandler.sendMessage(msg);
	}
	
	public void hideAds(Hashtable<String, String> adsInfo)
	{
		Log.v("BaiduAppX", "hideAds.info: " + adsInfo.toString() );	
		final Hashtable<String, String> curInfo = adsInfo;
    	Message msg = BaiduAppXHandler.obtainMessage();
    	msg.obj = curInfo.get("type");
		msg.arg1 = View.GONE;
		BaiduAppXHandler.sendMessage(msg);
	}
	
	public void queryPoints()
	{
		Log.v("BaiduAppX", "queryPoints");
	}
	
	public void spendPoints(int points)
	{
		Log.v("BaiduAppX", "spendPoints");
	}
	
	public void setDebugMode(boolean debug)
	{
		Log.v("BaiduAppX", "setDebugMode");
	}
	
	public String getSDKVersion()
	{
		Log.v("BaiduAppX", "getSDKVersion");
		return "";
	}
	
	public String getPluginVersion()
	{
		Log.v("BaiduAppX", "getPluginVersion");
		return "";
	}
}
