package org.cocos2dx.plugin;

import java.util.Hashtable;

import com.cmgame.homesdk.HomeSDK;
import com.cmgame.homesdk.PayResultInfo;
import com.cmgame.homesdk.PaymentInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class IAPHeyidongTV implements InterfacePay {

	private static final String LOG_TAG = "IAPHeyidongTV";
	
	private static final int MSG_TRADE_RESULT = 0;
	private static final int MSG_CLOSE_TRADE = 1;

	private static Activity mContext = null;
	private static IAPHeyidongTV mPayInstance = null;
	private static boolean mIsDebug = false;

	private static Handler mHandler = null;
	private static AlertDialog mPayTipDialog = null;
	
	private static HomeSDK mHomeSDK = null;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPHeyidongTV(Context context) {
		mContext = (Activity)context;
		mPayInstance = this;
	}
	
	public static void initHomeSDK(HomeSDK homeSDK) {
		mHomeSDK = homeSDK;
//		mHomeSDK.initGameBilling(mContext);
	}
	
	private static void initHandler(Context context) {
		// dialog
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("支付提示");
		builder.setCancelable(false);
		builder.setMessage("处理中...请收到短信后按要求回复以完成支付。");
//		builder.setMessage("处理中...请收到短信后按要求回复以完成支付，\n"
//				+ "或直接点击“返回”取消本次充值。\n"
//				+ "取消后请 勿 回复短信。");
//		// 设置按钮
//		builder.setPositiveButton("返回", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				mHandler.sendEmptyMessage(MSG_CLOSE_TRADE);
//			}
//		});
		mPayTipDialog = builder.create();
		// handler
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				LogD("handle msg.what:"+msg.what+",msg.arg1:"+msg.arg1+",msg.object:"+(String)msg.obj);
				
				switch (msg.what) {
				case MSG_CLOSE_TRADE:
					if (mPayTipDialog.isShowing()) {
						mPayTipDialog.dismiss();
					}
					IAPHeyidongTV.payResult(PayWrapper.PAYRESULT_CANCEL, "付款取消");
					Toast.makeText(mContext,
							"已取消充值", Toast.LENGTH_LONG)
							.show();
					break;
				case MSG_TRADE_RESULT:
					{
						switch (msg.arg1) {
						case PayResultInfo.PAY_RESULT_SUCCESS:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							IAPHeyidongTV.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
							Toast.makeText(mContext,
									"支付成功 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						case PayResultInfo.PAY_RESULT_FAILED:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							IAPHeyidongTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
							Toast.makeText(mContext,
									"订单失败 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						case PayResultInfo.PAY_RESULT_CANCEL:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							IAPHeyidongTV.payResult(PayWrapper.PAYRESULT_CANCEL, "付款取消");
							Toast.makeText(mContext,
									"支付取消 " + (String)msg.obj, Toast.LENGTH_SHORT)
									.show();
							break;
						default:
							if (mPayTipDialog.isShowing()) {
								mPayTipDialog.dismiss();
							}
							Toast.makeText(mContext,
									(String)msg.obj, Toast.LENGTH_LONG)
									.show();
							IAPHeyidongTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
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
		
		PluginWrapper.runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				initHandler(mContext);
			}
		});
	}

	// 有登录的 放这里
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
			payResult(PayWrapper.PAYRESULT_FAIL, "网络不可用");
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
//                BuyData.isRepeated = true;
//                BuyData.useSms = "0";
                BuyData.propertyID = productInfo.get("propertyID");
                BuyData.billingIndex = productInfo.get("billingIndex");
                BuyData.cpParam = productInfo.get("ext");
                
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setBillingIndex(BuyData.billingIndex);
                paymentInfo.setPropertyId(BuyData.propertyID);
                paymentInfo.setIsRepeated(BuyData.isRepeated);
                paymentInfo.setUseSms(BuyData.useSms);
                paymentInfo.setCpparam(BuyData.cpParam);
                mHomeSDK.pay(paymentInfo, mContext);
//                PluginWrapper.runOnMainThread(new Runnable() {
//        			
//        			@Override
//        			public void run() {
//                        GameInterface.doBilling(mContext, true, BuyData.billingName, 
//                        		BuyData.billingIndex, BuyData.billingFee, BuyData.billingCount,
//                        		BuyData.extStr, BuyData.sign,
//                        		new IPayCallback() {
//        							
//        							@Override
//        							public void onResult(int resultCode, String billingIndex, Object arg) {
//        								LogD(resultCode + ":"
//        										+ billingIndex + " message：" + arg);
//        								
//        								Message msg = mHandler.obtainMessage();
//        								msg.what = MSG_TRADE_RESULT;
//        								msg.arg1 = resultCode;
//        								msg.obj = billingIndex + "|" + arg;
//        								mHandler.sendMessage(msg);
//        							}
//        						});
//        			}
//        		});
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
		PayWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("payResult:" + ret + " msg:" + msg);
	}
	
	public static void onPayResult(PayResultInfo payResult) {
		LogD("payResult = " + payResult.getPayResult());
		
		Message msg = new Message();
		msg.what = MSG_TRADE_RESULT;
		msg.arg1 = payResult.getPayResult();
		
		mHandler.sendMessage(msg);
//        final int payResut = payResult.getPayResult();
//        mHandler.post(new Runnable()
//        {
//            
//            @Override
//            public void run()
//            {
//                try
//                {
//                    switch (payResut)
//                    {
//                        case PayResultInfo.PAY_RESULT_CANCEL:
//                            Toast.makeText(MainActivity.this.getApplicationContext(),
//                                    R.string.pay_cancel,
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                            break;
//                        case PayResultInfo.PAY_RESULT_FAILED:
//                            Toast.makeText(MainActivity.this.getApplicationContext(),
//                                    R.string.pay_failed,
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                            break;
//                        case PayResultInfo.PAY_RESULT_SUCCESS:
//                            Toast.makeText(MainActivity.this.getApplicationContext(),
//                                    R.string.pay_success,
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                catch (NullPointerException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        });
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
