/****************************************************************************
Copyright (c) 2012-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/

/**
 * @author wangzhe01
 */
package org.cocos2dx.plugin;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONObject;

import com.Sdk.CyberSDK;
import com.Sdk.Interface.ISdkCallBack;
import com.Sdk.Vo.CSDK_Result;
import com.Sdk.Vo.GetAppProductInfoResult;
import com.Sdk.Vo.GetRankListInfoResult;
import com.Sdk.Vo.GetUserInfoResult;
import com.Sdk.Vo.OrderProductResult;
import com.google.gson.Gson;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;

public class IAPCyberCloudTV implements InterfacePay{
	
	//视博云 TV插件化参数
	private static final String LOG_TAG = "IAPCyberCloudTV";
	private static Activity mContext = null;
	private static IAPCyberCloudTV mCyberCloudTV = null;
	private static boolean bDebug = false;
	
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPCyberCloudTV(Context context){
		mContext = (Activity)context;
		mCyberCloudTV = this;

	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					
					myCallback callback=new myCallback();
					CyberSDK.CSDK_Init(mContext, callback);
					
					LogD("11111SDK init success!");
					//Atet_appID = devInfo.get("Atet_appID");
					
					//SDKApi.init(mContext, SDKApi.LANDSCAPE, Atet_appID, false);
					
				}catch(Exception e){
					LogE("11111Developer info parse error!", e);
				}
			}
		});
	}
	
	public void pluginLogin(Hashtable<String, String> info) {
		LogD("pluginLogin invoked " + info.toString());
		try{
			String loginInfoFlag = info.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}
	
	public void payForProduct(Hashtable<String, String> info) {
		LogD("payForProduct invoked " + info.toString());
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					
					Map<String, String> param_map=new HashMap<String, String>();
					
					
					try{
						param_map.put("AppOrderNumber", productInfo.get("orderId"));
						//LogD("11111payForProduct invoked 11" + param_map.toString());
						param_map.put("BackUri", "http://mapi2.bianfeng.com/v1/pay/notify/cyber");
						//LogD("11111payForProduct invoked 22" + param_map.toString());
						param_map.put("ProductCode", productInfo.get("productId"));
						//LogD("11111payForProduct invoked 33" + param_map.toString());
						//int iamount= Integer.valueOf( amount.getText().toString());//数量是传整形
						param_map.put("Amount","1");
						//LogD("11111payForProduct invoked 44" + param_map.toString());
						//param_map.put("TotalPrice","");
						String info = "orderid:"+productInfo.get("orderId") + ";amount:"+productInfo.get("productPrice");
						param_map.put("PrivateInfo", info);
						//LogD("11111payForProduct invoked 55" + param_map.toString());
						CyberSDK.CSDK_OrderProduct(param_map);
					}catch (Exception e){
						
						LogE("1111Product info parse error!", e);
						//TextView tv=(TextView)findViewById(R.id.resultTextView);
						//tv.setText("orderproductClick 出错"+e.getMessage());  			
					}
				}catch(Exception e){
					LogE("1111Product info parse error!", e);
					IAPCyberCloudTV.PayResult(PayWrapper.PAYRESULT_FAIL, "未知错误");
				}
			}
		});
	}
	
	/*IPayResultCallback resultListener = new IPayResultCallback(){
		public void onPayResult(int resultCode, String resultInfo) {
            //resultInfo = 应用编号&商品编号&外部订单号
            if (resultCode == SDKApi.PAY_SUCCESS ) {
            	IAPCyberCloudTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
            } else if(SDKApi.PAY_CANCEL == resultCode){
            	IAPCyberCloudTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
            }else {
            	IAPCyberCloudTV.PayResult(PayWrapper.PAYRESULT_FAIL, "未知错误");
            }
		}
	};*/
	
public class myCallback implements ISdkCallBack{
		
		@Override
		public void InitCalBack(CSDK_Result result) {		
			LogD("11111CyberCloudTV init success!");
		}
		@Override
		public void DeinitCalBack(CSDK_Result result) {
			// TODO Auto-generated method stub
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("DeinitCalBack:"+js);*/
		}
		@Override
		public void GetUserInfoCallBack(GetUserInfoResult result) {
			// TODO Auto-generated method stub
			//gson
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("GetUserInfoCallBack:"+js);*/
		}
		@Override
		public void SubmitUserScore1CallBack(CSDK_Result result) {
			// TODO Auto-generated method stub
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("SubmitUserScore1CallBack:"+js);*/
		}
		@Override
		public void GetRankListInfoCallBack(GetRankListInfoResult result) {
			// TODO Auto-generated method stub
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("GetRankListInfoCallBack:"+js);*/
		}
		@Override
		public void GetAppProductInfoCallBack(GetAppProductInfoResult result) {
			
			// TODO Auto-generated method stub
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("GetAppProductInfoCallBack:"+js);*/
		}
		@Override
		public void OrderStateCallback(String order_number, int order_state, String order_state_desc) {
			// TODO Auto-generated method stub
			LogD("11111CyberCloudTV OrderStateCallback success!");
			//String js=new Gson().toJson(result);
			//LogD("11111CyberCloudTV order_state:"+order_state+"order_state_desc"+order_state_desc+"order_number"+order_number);
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			StringBuffer st=new StringBuffer();
			st.append("order_number =").append(order_number);
			st.append(";order_state =").append(order_state);
			st.append(";order_state_desc =").append(order_state_desc);
			//String js=new Gson().toJson(result);
			tv.setText("OrderStateCallback:"+st);*/
		}
		@Override
		public void ConfirmOrderCallBack(CSDK_Result result) {
			// TODO Auto-generated method stub
			LogD("11111CyberCloudTV ConfirmOrderCallBack success!");
			/*TextView tv=(TextView)findViewById(R.id.resultTextView);
			String js=new Gson().toJson(result);
			tv.setText("ConfirmOrderCallBack:"+js);*/
		}
		@Override
		public void OrderProductCallback(OrderProductResult result) {
			LogD("11111CyberCloudTV orderproduct success!");
			String js=new Gson().toJson(result);
			LogD("11111CyberCloudTV orderproduct result:"+js);
			
			JSONObject jsonObject=new JSONObject();    
			//jsonObject.optJSONObject(result);
			CSDK_Result sjf = result.ret_desc;
			//LogD("11111111111111111" + sjf);
			
			//String orderNumber = jsonObject.optString("order_number");
			//LogD("11111CyberCloudTV orderproduct orderNumber:"+orderNumber);
			
			//JSONObject retDesc = jsonObject.optJSONObject("ret_desc");
			//LogD("11111111222222222222");
			//String resultDesc = retDesc.optString("result_desc");
			//LogD("11111CyberCloudTV orderproduct resultDesc:"+resultDesc);
			int resultCode = sjf.result_code;//.optString("result_code");
			//LogD("11111111222222222222" + resultCode);
			//int code = retDesc.optInt("result_code");
			LogD("11111CyberCloudTV orderproduct resultCode:"+resultCode);
			
			if(resultCode == 0){
				PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功！");
			}
			else{
				PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败！");
			}
		}
		
	}
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mCyberCloudTV, ret, msg);
		LogD("CyberCloudTV payResult : " + ret + " msg : " + msg);
	}
	
	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "4.0.1";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}