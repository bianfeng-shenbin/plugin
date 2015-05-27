package org.cocos2dx.plugin;

import android.app.Application;
import android.util.Log;

/**
 * @author wangzhe01
 */
public class SimpleGameApplication extends Application{
	public void onCreate(){
		super.onCreate();
		try{
			System.loadLibrary("megjb");
		}catch(Exception e){
			Log.e("SimpleGameApplication", "Cmgame Library Load Error!!!", e);
		}	
	}
}