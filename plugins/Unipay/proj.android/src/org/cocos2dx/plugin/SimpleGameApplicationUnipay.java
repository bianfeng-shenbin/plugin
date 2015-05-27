package org.cocos2dx.plugin;

import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

import android.app.Application;
import android.util.Log;

/**
 * @author wangzhe01
 */
public class SimpleGameApplicationUnipay extends Application{
	public void onCreate(){
		super.onCreate();
		
		try{
			Utils.getInstances().initSDK(this, new UnipayPayResultListener(){

				public void PayResult(String arg0, int arg1, int arg2, String arg3) {
					Log.d("SimpleGameApplication", "联通Unipay intialization Success!!!");
				}
			});
		}catch(Exception e){
			Log.e("SimpleGameApplication", "联通Unipay intialization Error!!!", e);
		}	
	}
}