package org.cocos2dx.plugin;

import java.util.Hashtable;

import com.ystgame.sdk.billing.api.BillingResult;
import com.ystgame.sdk.billing.api.GameInterface;
import com.ystgame.sdk.billing.api.GameInterface.IPayCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class IAPYstTV implements InterfaceIAP {

	private static final String LOG_TAG = "IAPYstTV";
	
	private static final int MSG_TRADE_RESULT = 0;
	private static final int MSG_CALL_CLOSE_TRADE = 1;
	private static final int MSG_CLOSE_TRADE = 2;
	private static final int MSG_CANCEL_CLOSE_TRADE = 3;

	private static Activity mContext = null;
	private static IAPYstTV mPayInstance = null;
	private static boolean mIsDebug = false;

	public static Handler mHandler = null;
	private static AlertDialog mPayTipDialog = null;
	private static AlertDialog mCancelDialog = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPYstTV(Context context) {
		mContext = (Activity)context;
		mPayInstance = this;
	}
	
	public static void initHandler(Context context) {
		// dialog
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("支付提示");
		builder.setCancelable(false);
//		builder.setMessage("处理中...请收到短信后按要求回复以完成支付。");
		builder.setMessage("处理中...请收到短信后按要求回复以完成支付，\n"
				+ "或直接点击“返回”取消本次充值。\n"
				+ "取消后请 勿 回复短信。");
		// 设置按钮
		builder.setPositiveButton("返回", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHandler.sendEmptyMessage(MSG_CALL_CLOSE_TRADE);
			}
		});
		mPayTipDialog = builder.create();
		// dialog2
		AlertDialog.Builder cancelBuilder = new Builder(context);
		cancelBuilder.setTitle("支付提示");
		cancelBuilder.setCancelable(false);
		cancelBuilder.setMessage("您确认要取消本次支付吗？");
		// 设置按钮
		cancelBuilder.setPositiveButton("确认", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHandler.sendEmptyMessage(MSG_CLOSE_TRADE);
			}
		});
		cancelBuilder.setNegativeButton("不", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHandler.sendEmptyMessage(MSG_CANCEL_CLOSE_TRADE);
			}
		});
		mCancelDialog = cancelBuilder.create();
		// handler
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				LogD("handle msg.what:"+msg.what+",msg.arg1:"+msg.arg1+",msg.object:"+(String)msg.obj);
				// 与当前订单号不同的 不处理
				if (msg.obj != null) {
					String tempString = (String)msg.obj;
					if (tempString.contains("|")) {
						String billingIndex = tempString.substring(0, tempString.indexOf("|"));
						// 非空 且 与当前index不同
						// 直接返回会出现空 
						if (!billingIndex.equalsIgnoreCase("null") && !billingIndex.equalsIgnoreCase(BuyData.billingIndex)) {
							return ;
						}
					}
				}
				
				switch (msg.what) {
				case MSG_CALL_CLOSE_TRADE:	// 召唤 取消支付
					if (!mCancelDialog.isShowing()) {
						mCancelDialog.show();
					}
					break;
				case MSG_CLOSE_TRADE:	// 取消支付
					if (mPayTipDialog.isShowing()) {
						mPayTipDialog.dismiss();
					}
					if (mCancelDialog.isShowing()) {
						mCancelDialog.dismiss();
					}
					BuyData.billingIndex = "";
					IAPYstTV.payResult(IAPWrapper.PAYRESULT_CANCEL, "付款取消");
					Toast.makeText(mContext,
							"已取消充值，请 勿 回复短信！", Toast.LENGTH_LONG)
							.show();
					break;
				case MSG_CANCEL_CLOSE_TRADE:	// 取消 取消支付
					if (mCancelDialog.isShowing()) {
						mCancelDialog.dismiss();
					}
					if (!mPayTipDialog.isShowing()) {
						mPayTipDialog.show();
					}
					break;
				case MSG_TRADE_RESULT:
					{
						switch (msg.arg1) {
						case BillingResult.SUCCESS:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							if (mCancelDialog.isShowing()) {
								mCancelDialog.dismiss();
							}
							BuyData.billingIndex = "";
							IAPYstTV.payResult(IAPWrapper.PAYRESULT_SUCCESS, "付款成功");
							Toast.makeText(mContext,
									"支付成功 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						case BillingResult.PROCESSING:
							LogD("处理中...");
							if (!mPayTipDialog.isShowing()) {
								mPayTipDialog.show();
							}
//							Toast.makeText(mContext,
//									"正在处理 " + (String)msg.obj, Toast.LENGTH_SHORT)
//									.show();
							break;
						case BillingResult.FAILED:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							if (mCancelDialog.isShowing()) {
								mCancelDialog.dismiss();
							}
							BuyData.billingIndex = "";
							IAPYstTV.payResult(IAPWrapper.PAYRESULT_FAIL, "付款失败");
							Toast.makeText(mContext,
									"订单失败 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						case BillingResult.CANCELLED:
						case BillingResult.CANCELLED_RETURN:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							if (mCancelDialog.isShowing()) {
								mCancelDialog.dismiss();
							}
							BuyData.billingIndex = "";
							IAPYstTV.payResult(IAPWrapper.PAYRESULT_CANCEL, "付款失败");
							Toast.makeText(mContext,
									"支付取消 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						case BillingResult.NET_UNCONNECT:
						case BillingResult.ORDER_ERROR:
						case BillingResult.QURRY_ERROR:
						case BillingResult.PARAM_ERROR:
						default:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							if (mCancelDialog.isShowing()) {
								mCancelDialog.dismiss();
							}
							BuyData.billingIndex = "";
							Toast.makeText(mContext,
									(String)msg.obj, Toast.LENGTH_LONG)
									.show();
							IAPYstTV.payResult(IAPWrapper.PAYRESULT_FAIL, "付款失败");
							break;
						}
					}
					break;
				default:
					break;
				}

				super.handleMessage(msg);
			};
		};
	}
	
	// 常量的部分 放这里 这部分在发送请求前
	@Override
	public void configDeveloperInfo(Hashtable<String, String> cpInfo) {
		LogD("configDeveloperInfo invoked " + cpInfo.toString());
		BuyData.gameId = Integer.parseInt(cpInfo.get("gameId"));
		BuyData.gameName = cpInfo.get("gameName");
		BuyData.provider = cpInfo.get("provider");
		BuyData.serviceTel = cpInfo.get("serviceTel");
		
		PluginWrapper.runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				GameInterface.initializeApp(mContext, BuyData.gameId, BuyData.gameName,
						BuyData.provider, BuyData.serviceTel);
				
				initHandler(mContext);
			}
		});
	}

	// 有登录的 放这里
	@Override
	public void pluginLogin(Hashtable<String, String> cpInfo) {
		LogD("pluginLogin invoked " + cpInfo.toString());
		try{
			String loginInfoFlag = cpInfo.get("flag");
			
			LogD("PluginLogin result: " + loginInfoFlag);
		}catch(Exception e){
			LogE("Login info parse error!", e);
		}
	}

	// 在这里处理实际发送交易请求给第三方的部分
	@Override
	public void payForProduct(Hashtable<String, String> cpInfo) {
		LogD("payForProduct invoked " + cpInfo.toString());
		if (! Utils.isNetworkReachable(mContext)) {
			payResult(IAPWrapper.PAYRESULT_FAIL, "网络不可用");
			return;
		}
		
		final Hashtable<String, String> productInfo = cpInfo;
		new AsyncTask<Integer, Object, String>() {
            @Override
            protected void onPreExecute() {
            	super.onPreExecute();  
            }
            @Override
            protected String doInBackground(Integer... params) {

            	
            	/////////////////////////////////////////
                BuyData.isRepeated = true;
                BuyData.billingName = productInfo.get("billingName");
                BuyData.billingIndex = productInfo.get("billingIndex");
                BuyData.billingFee = ""+(Integer.parseInt(productInfo.get("billingFee"))*100);
                BuyData.billingCount = Integer.parseInt(productInfo.get("billingCount"));
                BuyData.extStr = productInfo.get("extStr");
                BuyData.gameKey = productInfo.get("gameKey");
//                BuyData.sign = productInfo.get("sign");
                String string = "gameId=" + BuyData.gameId + "&billingIndex="
        				+ BuyData.billingIndex + "&billingFee=" + BuyData.billingFee
        				+ "&billingCount=" + BuyData.billingCount
        				+ "&extStr=" + BuyData.extStr;
        		BuyData.sign = MD5.md5(string + "#gameKey=" + BuyData.gameKey);
        		
                LogD("string:"+string+" sign:"+BuyData.sign);
                
                PluginWrapper.runOnMainThread(new Runnable() {
        			
        			@Override
        			public void run() {
                        GameInterface.doBilling(mContext, true, BuyData.billingName, 
                        		BuyData.billingIndex, BuyData.billingFee, BuyData.billingCount,
                        		BuyData.extStr, BuyData.sign,
                        		new IPayCallback() {
        							
        							@Override
        							public void onResult(int resultCode, String billingIndex, Object arg) {
        								LogD(resultCode + ":"
        										+ billingIndex + " message：" + arg);
        								
        								Message msg = mHandler.obtainMessage();
        								msg.what = MSG_TRADE_RESULT;
        								msg.arg1 = resultCode;
        								msg.obj = billingIndex + "|" + arg;
        								mHandler.sendMessage(msg);
        							}
        						});
        			}
        		});
//                GameInterface.doBilling(mContext, true, BuyData.billingName, 
//                		BuyData.billingIndex, BuyData.billingFee, BuyData.billingCount,
//                		BuyData.extStr, BuyData.sign,
//                		new IPayCallback() {
//							
//							@Override
//							public void onResult(int resultCode, String billingIndex, Object arg) {
//								LogD(resultCode + ":"
//										+ billingIndex + " message：" + arg);
//								
//								Message msg = mHandler.obtainMessage();
//								msg.what = resultCode;
//								msg.obj = billingIndex + "|" + arg;
//								mHandler.sendMessage(msg);
//							}
//						});

            	return "";
            	/////////////////////////////////////////
            }
            protected void onPostExecute(String result) {
//            	Toast.makeText(mContext, result, 20).show();
            	super.onPostExecute(result);
            };
         }.execute();
	}

	// 这里处理交易结果
	public static void payResult(int ret, String msg){
		IAPWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("payResult:" + ret + " msg:" + msg);
	}
	
	@Override
	public void setDebugMode(boolean debug) {
		mIsDebug = debug;
	}

	@Override
	public String getSDKVersion() {
		return "1.0";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.1";
	}

}
