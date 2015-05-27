package org.cocos2dx.plugin;

import android.app.Application;
import android.util.Log;

import com.baidu.frontia.FrontiaApplication;


public class SimpleGameApplicationBaiduPush extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try{
			Log.d("BaiduPush", "SimpleGameApplicationBaiduPush_load_megjb");
			System.loadLibrary("megjb");
		}catch(Exception e){
			Log.d("SimpleGameApplication", "Cmgame Library Load Error!!!");
		}
		catch(java.lang.UnsatisfiedLinkError fatalError) {
			Log.d("SimpleGameApplication", "Cmgame Library Load fatalError!!!");
		}
		FrontiaApplication.initFrontiaApplication(this);
	}

}
