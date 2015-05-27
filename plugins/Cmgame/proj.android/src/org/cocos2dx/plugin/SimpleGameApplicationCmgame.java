package org.cocos2dx.plugin;

import android.app.Application;
import android.util.Log;

/**
 * @author wangzhe01
 */
public class SimpleGameApplicationCmgame extends Application{
	public void onCreate(){
		super.onCreate();
		//FrontiaApplication.initFrontiaApplication(Context context) 
		try{
			System.loadLibrary("megjb");
		}catch(Exception e){
			Log.e("SimpleGameApplication", "Cmgame Library Load Error!!!", e);
		}	
	}
}