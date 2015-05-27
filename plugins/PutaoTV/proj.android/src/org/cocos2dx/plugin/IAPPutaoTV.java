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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.putaolab.pdk.api.PtFacade;
import com.putaolab.pdk.api.PtProduct;
import com.putaolab.pdk.api.PtPurchaseListener;
import com.putaolab.pdk.api.PtReceipt;
import com.putaolab.pdk.api.PtResponseListener;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

public class IAPPutaoTV implements InterfacePay{
	
	//葡萄游戏厅(TV)支付插件化参数
	private static final String LOG_TAG = "IAPPutaoTV";
	private static Activity mContext = null;
	private static IAPPutaoTV mPutaoTV = null;
	private static boolean bDebug = false;
	private HashMap<String, PtProduct> priceMap = null;
	private PtFacade facadeInstance = null;
	private String payProductID = null;
	private String payOrderID = null;

	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPPutaoTV(Context context){
		mContext = (Activity)context;
		mPutaoTV = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String> info) {
		LogD("configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{
					String developerID = devInfo.get("developerID");
					String signKey = devInfo.get("signKey");
					String verifyKey = devInfo.get("verifyKey");
					
					facadeInstance = PtFacade.getInstance();
					
					facadeInstance.init(mContext, developerID, signKey, verifyKey);
					
				}catch(Exception e){
					LogE("Developer info parse error!", e);
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
					String productIdList = productInfo.get("productIdList");
					String productId = productInfo.get("productId");
					String orderId = productInfo.get("orderId");
					
					payProductID = productId;
					payOrderID = orderId;
					
					facadeInstance.requestProductList(productIdList, productListListener);
					
				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});
	}
	
	PtResponseListener<ArrayList<PtProduct>> productListListener = new PtResponseListener<ArrayList<PtProduct>>(){
		
		public void onSuccess(ArrayList<PtProduct> Products) {
			priceMap = new HashMap<String, PtProduct>();
			
			for(PtProduct product : Products){
				String productId = product.getProductId();
				Log.i(LOG_TAG, "PtProductListListener onSuccess productId: " + productId);
				
				priceMap.put(productId, product);
			}
			
			PtProduct product = priceMap.get(payProductID);
			
			facadeInstance.requestPurchase(product, payOrderID, 1, purchaseListener, true);
		}
		
		public void onFailure(int errorCode, String errorMessage) {
			Log.e(LOG_TAG, "PtProductListListener  onFailure :" + errorMessage);
		}
		
		public void onCancel() {
			Log.e(LOG_TAG, "PtProductListListener  onCancel :");
		}
	};
	
	PtPurchaseListener<PtReceipt> purchaseListener = new PtPurchaseListener<PtReceipt>(){
		public void onSuccess(PtReceipt receipt) {
			String id = receipt.getGamerId();
			int count = receipt.getCount();
			String productid = receipt.getProductId();
			int status = receipt.getStatus();
			String uuid = receipt.getUuid();
			Log.i(LOG_TAG, " CreateProductListener onSuccess id: " + id + " count: " + count + " status: " + status + " productid: " + productid + " uuid: " + uuid);
			
			IAPPutaoTV.PayResult(PayWrapper.PAYRESULT_SUCCESS, "支付成功");
		}
		
		public void onFailure(int errorcode, String errormsg) {
			IAPPutaoTV.PayResult(PayWrapper.PAYRESULT_FAIL, "支付失败");
		}
		
		public void onCancel() {
			IAPPutaoTV.PayResult(PayWrapper.PAYRESULT_CANCEL, "取消支付");
		}

		public void onReady() {
			Log.i(LOG_TAG, "CreateProductListener  onReady ready!!");
		}
	};
	
	public static void PayResult(int ret, String msg){
		PayWrapper.onPayResult(mPutaoTV, ret, msg);
		LogD("PutaoTV payResult : " + ret + " msg : " + msg);
	}

	public void setDebugMode(boolean debug) {
		bDebug = debug;
	}

	public String getSDKVersion() {
		return "1.5";
	}

	public String getPluginVersion() {
		return "1.0.0";
	}
}
