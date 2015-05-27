package org.cocos2dx.plugin;

import com.baidu.frontia.FrontiaApplication;

import android.app.Application;
import android.util.Log;
/**
 * @author wangzhe01
 */
public class SimpleGameApplicationBaiduPX extends Application{
	public void onCreate(){
		super.onCreate();
		try{
			FrontiaApplication.initFrontiaApplication(this.getApplicationContext());
		}catch(Exception e){
			Log.e("SimpleGameApplication", "BaiduPX Application Init Error!!!", e);
		}	
	}
}