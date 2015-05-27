package org.cocos2dx.plugin;

import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class IAPHisenseTV implements InterfacePay {

	private static final String LOG_TAG = "IAPHisenseTV";
	
	private static final String PAY_ERR_NO_PAY_APP = "PAY_ERR_NO_PAY_APP";
	private static final String PAY_USER_CANCEL = "PAY_USER_CANCEL";
	
	private static final String PLATFORM_ZHIFUBAO = "1";
	private static final String PLATFORM_WEIXIN = "2";
	
	private static final int REQUEST_PAY_CASH_CODE = 12;
	
	private static final int MSG_NO_PAY_APP = -1;
	private static final int MSG_CALL_CLOSE_TRADE = 1;
	private static final int MSG_CLOSE_TRADE = 2;
	private static final int MSG_CANCEL_CLOSE_TRADE = 3;
	private static final int MSG_QUERY_TRADE = 4;
	
	private static Activity mContext = null;
	private static IAPHisenseTV mPayInstance = null;
	private static boolean mIsDebug = false;

	public static Handler mHandler = null;
	
	private static AlertDialog mPayTipDialog = null;
	private static AlertDialog mCancelDialog = null;
//	private static String mLastTradeNo = null;
//	private static String mLastPlatformID = null;
	private static boolean mNeedQuery = false;
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(mIsDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPHisenseTV(Context context) {
		mContext = (Activity)context;
		mPayInstance = this;
		
		PluginWrapper.runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				initHandler(mContext);
			}
		});
	}
	
	private static void initHandler(Context context) {
		// dialog
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("支付提示");
		builder.setCancelable(false);
		builder.setMessage("处理中...请在手机上完成支付，\n"
				+ "或直接点击“返回”取消本次充值。\n"
				+ "取消后请 勿 继续支付。");
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
				LogD("handle msg.what:"+msg.what+",msg.object:"+(String)msg.obj);
				
				switch (msg.what) {
				case MSG_NO_PAY_APP:
					{
						processResult((String)msg.obj, "", "");
						
						LogD("支付应用版本过低或没有发现支付应用！开始进入应用市场下载");
	                    Intent it = new Intent(Intent.ACTION_VIEW);
	                    it.setData(Uri.parse("himarket://details?id="
	                            + "com.hisense.hitv.payment"));
	                    mContext.startActivity(it);
					}
					break;
				case MSG_CALL_CLOSE_TRADE:	// 召唤 取消支付
					if (!mCancelDialog.isShowing()) {
						mCancelDialog.show();
					}
					break;
				case MSG_CLOSE_TRADE:	// 取消支付
					mNeedQuery = false;	// 不需要继续查询
					if (mPayTipDialog.isShowing()) {
						mPayTipDialog.dismiss();
					}
					if (mCancelDialog.isShowing()) {
						mCancelDialog.dismiss();
					}
//					String stringObj = (String)msg.obj;
//					String[] arrParam = stringObj.split("#");
//					LogD("arrParam:"+arrParam[0]+":"+arrParam[1]+":"+arrParam[2]);
					processResult(PAY_USER_CANCEL, "", "");
//					processResult(arrParam[0], arrParam[1], arrParam[2]);
					break;
				case MSG_CANCEL_CLOSE_TRADE:	// 取消 取消支付
					if (mCancelDialog.isShowing()) {
						mCancelDialog.dismiss();
					}
					if (!mPayTipDialog.isShowing()) {
						mPayTipDialog.show();
					}
					break;
				case MSG_QUERY_TRADE:
					{
						String stringObj = (String)msg.obj;
						String[] arrParam = stringObj.split("#");
						LogD("arrParam:"+arrParam[0]+":"+arrParam[1]+":"+arrParam[2]);
						
						// 如果对话框没有显示 那么显示对话框
						if (!mPayTipDialog.isShowing() && !mCancelDialog.isShowing()
								&& mNeedQuery) {
//							mLastTradeNo = arrParam[1];
//							mLastPlatformID = arrParam[2];
							
							mPayTipDialog.show();
						}
						
						if (mNeedQuery) {
							processResult(arrParam[0], arrParam[1], arrParam[2]);
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
//		BuyData.appName = cpInfo.get("appName");
//		BuyData.packageName = mContext.getPackageName();
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
            	// 检查是否有安装支付  没有跳至市场
            	if (!Utils.checkPackageInfo(mContext, "com.hisense.hitv.payment")) {
            		// 先处理失败 然后让用户下载
//            		processResult(PAY_ERR_NO_PAY_APP, "", "");
            		Message msg = new Message();
            		msg.what = MSG_NO_PAY_APP;
            		msg.obj = PAY_ERR_NO_PAY_APP;
            		mHandler.sendMessage(msg);
            		
            		LogD("没有检测到海信支付应用");
            		return "";
				}
            	
            	/////////////////////////////////////////
            	Intent intent = new Intent();
//                intent.setAction("com.hisense.hitv.payment.MAIN");
            	intent.setAction("com.hisense.hitv.payment.QC");

                BuyData.appName = productInfo.get("appName");
                BuyData.packageName = mContext.getPackageName();
                String sign = MD5Signature.md5(BuyData.packageName + productInfo.get("paymentMD5Key"));
                BuyData.paymentMD5Key = sign;
                BuyData.tradeNum = productInfo.get("tradeNum");
                BuyData.goodsPrice = productInfo.get("goodsPrice");
                BuyData.goodsName = productInfo.get("goodsName");
                BuyData.alipayUserAmount = productInfo.get("alipayUserAmount");
                BuyData.notifyUrl = productInfo.get("notifyUrl");
                
                LogD("packageName:"+BuyData.packageName);
                LogD("paymentMD5Key:"+sign);
                
                intent.putExtra("appName", BuyData.appName); //应用名称
                intent.putExtra("packageName", BuyData.packageName);//包名
                intent.putExtra("paymentMD5Key", BuyData.paymentMD5Key);
                intent.putExtra("platformId", "");	// 不传值  让用户选择支付方式  需要后台配置2个账户
                intent.putExtra("tradeNum", BuyData.tradeNum);
                intent.putExtra("goodsPrice", BuyData.goodsPrice);
                intent.putExtra("goodsName", BuyData.goodsName);
                intent.putExtra("alipayUserAmount", "");	// 后台配置支付宝和微信账户
                intent.putExtra("notifyUrl", "");	// 后台配置回调地址
//              intent.putExtra("alipayUserAmount", BuyData.alipayUserAmount);
//              intent.putExtra("notifyUrl", BuyData.notifyUrl);
                
                try {
                	//使用返回式启动activity
                	mContext.startActivityForResult(intent, REQUEST_PAY_CASH_CODE);
                	mNeedQuery = true;	// 第一次支付后 开启查询
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					
            		Message msg = new Message();
            		msg.what = MSG_NO_PAY_APP;
            		msg.obj = PAY_ERR_NO_PAY_APP;
            		mHandler.sendMessage(msg);
            		
            		LogD("没有检测到海信支付应用或版本过低");
            		return "";
				}
                
            	return "";
            	/////////////////////////////////////////
            }
            protected void onPostExecute(String result) {
//            	Toast.makeText(mContext, result, 20).show();
            	super.onPostExecute(result);
            };
         }.execute();
	}


	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * 支付结果分为三类
	 * 1.支付成功--PAYMENT_SUCCESS：付款成功。
	 * 2.推送到手机钱包或者短信确认上--PAYMENT_APPLIED：支付申请成功
	 *                               （此类情况比较特殊属于支付应用申请支付成功后没来的及查询一次结果窗口就退出了）
	 *                             --WAIT_USER_CONFIRM：等待用户钱包确认付款。
	 *                             --WAIT_BUYER_PAY ： 等待用户付款
	 *                             --WAIT_USER_MO：等待用户上行短信确认付款。
	 * 3.支付失败及支付错误--PAYMENT_FAIL：付款失败
	 *                    --BUYER_ACCOUNT_BLOCK：买家账号被冻结
	 *                    --BIZ_CLOSED：业务超时关闭
	 *                    --其他返回错误
	 * 第三方可以根据返回的结果来判断是否需要调用支付结果查询接口查询（第一第三一般需在需要查询了，第二种由于需要用户
	 * 确认，所以并不能算支付成功，需要调用查询接口查询支付结果）
	 */
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode != REQUEST_PAY_CASH_CODE) {
			LogD("req code:" + requestCode + ", not HisenseTV pay");
			return ;
		}
		
		if(resultCode == Activity.RESULT_OK){
			String payResult = data.getStringExtra("payResult");//支付结果
			String platformId =data.getStringExtra("platformId");//还需返回支付平台，
			String trade_no =data.getStringExtra("trade_no");//支付流水号
						
			processResult(payResult, trade_no, platformId);
		}
	}
	
	// 这里处理交易结果
	public static void payResult(int ret, String msg){
		mNeedQuery = false;	// 不需要继续查询
		PayWrapper.onPayResult(mPayInstance, ret, msg);
		LogD("Hisense payResult:" + ret + " msg:" + msg);
	}
	
	public static void processResult(String payResult, String trade_no, String platformId) {
		LogD("processResult");
		LogD("payResult:"+payResult);
		LogD("platformId:"+platformId);
		LogD("trade_no:"+trade_no);
		
		String msg = "";
//		1.支付成功
//		-- TRADE_SUCCESS：交易成功，且可对该交易做操作，如：多级分润、退款等。
//		-- TRADE_FINISHED：交易成功且结束，即不可再做任何操作。
//		2. 交易创建，等待买家付款。
//		--WAIT_BUYER_PAY：等待用户付款
//		3.支付失败及支付错误
//		--INVALID_PARAMETER：参数无效
//		--TRADE_NOT_EXIST：交易不存在
//		--ERROR：支付过程中出现错误
//		--TRADE_CLOSED：在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。
//		--TRADE_PENDING：等待卖家收款（买家付款后，如果卖家账号被冻结）。
//		其他结果详见支付宝接口文档查询支付结果接口错误码
		if (platformId.equalsIgnoreCase(PLATFORM_ZHIFUBAO)) {
			if (payResult.equalsIgnoreCase("TRADE_SUCCESS") 
					|| payResult.equalsIgnoreCase("TRADE_FINISHED")) {
				IAPHisenseTV.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
		    	msg = "付款成功";
		    	
				if (mPayTipDialog.isShowing()) {
					mPayTipDialog.dismiss();
				}
				if (mCancelDialog.isShowing()) {
					mCancelDialog.dismiss();
				}
		    	Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			} else if (payResult.equalsIgnoreCase("WAIT_BUYER_PAY")) {
				LogD("do close/query");
				queryTrade(trade_no, platformId);
			} else {
				IAPHisenseTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
				msg = "付款失败";
				LogD(msg + ":" + payResult
						+ " 支付平台:" + platformId
						+ " 流水号:" + trade_no);
		    	
				if (mPayTipDialog.isShowing()) {
					mPayTipDialog.dismiss();
				}
				if (mCancelDialog.isShowing()) {
					mCancelDialog.dismiss();
				}
		    	
		    	if (payResult.equals(PAY_ERR_NO_PAY_APP)) {
					msg += ",请安装海信支付客户端后重试!";
				}
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		} else if (platformId.equalsIgnoreCase(PLATFORM_WEIXIN)) {
//			1.成功：
//			--SUCCESS：支付成功
//			--REFUND：转入退款
//			--NOTPAY：未支付
//			--CLOSED：已关闭
//			--REVOKED：已撤销
//			--USERPAYING：用户支付中
//			--PAYERROR：支付失败(其他原因，如银行返回失败)
//			2.失败：
//			--ORDERNOTEXIST：此交易订单号不存在
//			--SYSTEMERROR：系统错误
//			--ERROR：支付过程中出现错误
//			其他结果详见微信接口文档查询支付结果接口错误码
			if (payResult.equalsIgnoreCase("SUCCESS")) {
				IAPHisenseTV.payResult(PayWrapper.PAYRESULT_SUCCESS, "付款成功");
		    	msg = "付款成功";
		    	
				if (mPayTipDialog.isShowing()) {
					mPayTipDialog.dismiss();
				}
				if (mCancelDialog.isShowing()) {
					mCancelDialog.dismiss();
				}
		    	Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			} else if (payResult.equalsIgnoreCase("USERPAYING")
					|| payResult.equalsIgnoreCase("NOTPAY")) {
				LogD("do close/query");
				queryTrade(trade_no, platformId);
			} else {
				IAPHisenseTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
				msg = "付款失败";
				LogD(msg + ":" + payResult
						+ " 支付平台:" + platformId
						+ " 流水号:" + trade_no);
		    	
				if (mPayTipDialog.isShowing()) {
					mPayTipDialog.dismiss();
				}
				if (mCancelDialog.isShowing()) {
					mCancelDialog.dismiss();
				}
		    	
		    	if (payResult.equals(PAY_ERR_NO_PAY_APP)) {
					msg += ",请安装海信支付客户端后重试!";
				}
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		} else {
			IAPHisenseTV.payResult(PayWrapper.PAYRESULT_FAIL, "付款失败");
			msg = "付款失败";
			LogD(msg + ":" + payResult
					+ " 支付平台:" + platformId
					+ " 流水号:" + trade_no);
	    	
			if (mPayTipDialog.isShowing()) {
				mPayTipDialog.dismiss();
			}
			if (mCancelDialog.isShowing()) {
				mCancelDialog.dismiss();
			}
	    	
	    	if (payResult.equals(PAY_ERR_NO_PAY_APP)) {
				msg += ",请安装海信支付客户端后重试!";
			}
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
//	public static void closeTrade(final String trade_no, final String platformId) {
//		LogD("closeTrade");
//		LogD("platformId:"+platformId);
//		LogD("trade_no:"+trade_no);
//		// 直接关闭交易
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Uri uri = Uri.parse("content://com.hisense.hitv.payment" + "/close");
//				ContentResolver contentResolver = mContext.getContentResolver();
//				int result = contentResolver.delete(uri, "trade_no=? and package_name=? and paymentMD5Key=? and platformId=?",
//						new String[]{
//						trade_no,
//						BuyData.packageName,
//						BuyData.paymentMD5Key,
//						platformId});
//				LogD("do closeTrade:"+result);
//				if(result > 0){
//					LogD("关闭交易成功");
//					
//					String payResult = "BIZ_CLOSED";
//					Message msg = new Message();
//					msg.what = MSG_CLOSE_TRADE;
////					msg.obj = "BIZ_CLOSED";
//					msg.obj = payResult+"#"+trade_no+"#"+platformId;
//					mHisenseHandler.sendMessage(msg);
//				}
//			}
//		}).start();
//	}
	
	public static void queryTrade(final String trade_no, final String platformId) {
		LogD("queryTrade");
		LogD("platformId:"+platformId);
		LogD("trade_no:"+trade_no);
		// 查询交易结果
		new Thread(new Runnable() {
			public void run() {
				Uri uri = Uri.parse("content://com.hisense.hitv.payment" +"/query");
				ContentResolver contentResolver = mContext.getContentResolver();
				Cursor cursor = contentResolver.query(uri, null, 
						"trade_no=? and package_name=? and paymentMD5Key=? and platformId=?",
						new String[]{
						trade_no,
						BuyData.packageName,
						BuyData.paymentMD5Key,
						platformId},
						null);
				if(cursor != null && !cursor.isClosed() && cursor.moveToNext()) {
					String payResult = cursor.getString(cursor.getColumnIndex("payresult"));
					cursor.close();
					
					LogD("交易查询成功");
					
					Message msg = new Message();
					msg.what = MSG_QUERY_TRADE;
					msg.obj = payResult+"#"+trade_no+"#"+platformId;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
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
