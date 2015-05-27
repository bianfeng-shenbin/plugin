package org.cocos2dx.plugin;

import com.baidu.frontia.FrontiaApplication;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

import android.app.Application;
import android.util.Log;

/**
 * @author wangzhe01
 */
public class SimpleGameApplicationBaiduPXUnipay extends Application{
	public void onCreate(){
		super.onCreate();
		
		try{
			FrontiaApplication.initFrontiaApplication(this.getApplicationContext());
			
			Utils.getInstances().initSDK(this, new UnipayPayResultListener(){

				public void PayResult(String arg0, int arg1, int arg2, String arg3) {
					Log.d("SimpleGameApplication", "BaiduPX+联通Unipay Application intialization Success!!!");
				}
			});
		}catch(Exception e){
			Log.e("SimpleGameApplication", "BaiduPX+联通Unipay Application intialization Error!!!", e);
		}	
	}
}