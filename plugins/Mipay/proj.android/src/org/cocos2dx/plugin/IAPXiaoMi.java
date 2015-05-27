package org.cocos2dx.plugin;

import java.util.Hashtable;

import android.app.Activity;
import android.util.Log;
import android.content.Context;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;

public class IAPXiaoMi implements InterfaceIAP {

	//小米插件化参数
	private static final String LOG_TAG = "IAPXiaoMi";
	private static Activity mContext = null;
	private static IAPXiaoMi mXiaoMi = null;
	private static boolean bDebug = false;
	
	private static String MI_appid = null;
	private static String MI_appkey = null;
	private static String mUserId = null;
	private static String mSessionId = null; 
	
	public static MiAppInfo appInfo;

	
	
	protected static void LogE(String msg, Exception e){
		Log.e(LOG_TAG, msg ,e);
		e.printStackTrace();
	}
	
	protected static void LogD(String msg){
		if(bDebug){
			Log.d(LOG_TAG, msg);
		}
	}
	
	public IAPXiaoMi(Context context){
		mContext = (Activity)context;
		mXiaoMi = this;
	}
	
	public void configDeveloperInfo(Hashtable<String, String>info){
		LogD("lpl:configDeveloperInfo invoked " + info.toString());
		final Hashtable<String, String>devInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run() {
				try {
					MI_appid = devInfo.get("MI_appid");
					MI_appkey = devInfo.get("MI_appkey");
					
					/** SDK初始化 */	
					appInfo = new MiAppInfo();
					appInfo.setAppId(MI_appid);
					appInfo.setAppKey(MI_appkey);		
					MiCommplatform.Init( mContext, appInfo );
					LogD("lpl:小米Init,MI_appid="+MI_appid+",MI_appkey="+MI_appkey);					
				} catch (Exception e) {
					LogE("lpl:Developer info is wrong!", e);
				}
			}
			
		});
		
	
	}
	
	public void pluginLogin(Hashtable<String, String>info){
		LogD("lpl:pluginLogin ");
		final Hashtable<String, String>loginInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{					
					String loginInfoFlag = loginInfo.get("flag");
					MiCommplatform.getInstance().miLogin(mContext, 
							new OnLoginProcessListener()
					{
						@Override
						public void finishLoginProcess( int code ,MiAccountInfo arg1){
							LogD("lpl:finishLoginProcess code:"+code);
							switch( code )
							{
							  	case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
								       // 登陆成功
							  		//获取用户的登陆后的UID（即用户唯一标识）
							  		long uid = arg1.getUid();
					         
							  		/**以下为获取session并校验流程，如果是网络游戏必须校验，如果是单机游戏或应用可选**/
							  		//获取用户的登陆的Session（请参考5.3.3流程校验Session有效性）
							  		mSessionId = arg1.getSessionId();
							  		//请开发者完成将uid和session提交给开发者自己服务器进行session验证
							  		
							  		mUserId=Long.toString(uid);
							  		
							  		IAPXiaoMi.loginResult(IAPWrapper.LOGINRESULT_SUCCESS,mUserId,mSessionId,"登录成功");
										break;
							  	case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
										// 登陆失败
							  		IAPXiaoMi.loginResult(IAPWrapper.LOGINRESULT_FAIL,mUserId,mSessionId,"登录失败");
										break;
							  	case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL:
								     // 取消登录
							  		IAPXiaoMi.loginResult(IAPWrapper.LOGINRESULT_CANCEL,mUserId,mSessionId,"取消登录");
							  		break;
							  	case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:	
							  		//登录操作正在进行中
							  		IAPXiaoMi.loginResult(IAPWrapper.LOGINRESULT_FAIL,mUserId,mSessionId,"正在登录中");
							  		break;		
							  	default:
										// 登录失败
							  		IAPXiaoMi.loginResult(IAPWrapper.LOGINRESULT_FAIL,mUserId,mSessionId,"未知错误");
							  		break;
							}
						}
					});
					LogD("lpl:PluginLogin result: " + loginInfoFlag);
				}catch(Exception e){
					LogE("lpl:Login info parse error!", e);
				}
			}
		});	
	}
	
	public void payForProduct(Hashtable<String, String>info){
		LogD("payForProduct invoked " + info.toString());
		
		final Hashtable<String, String>productInfo = info;
		PluginWrapper.runOnMainThread(new Runnable(){
			public void run(){
				try{					
					MiBuyInfo miBuyInfo= new MiBuyInfo();
					miBuyInfo.setCpOrderId( productInfo.get("orderId").toString());	//订单号唯一（不为空）
					miBuyInfo.setCpUserInfo( "cpUserInfo" );	//此参数在用户支付成功后会透传给CP的服务器					
					miBuyInfo.setAmount( Integer.parseInt(productInfo.get("price")));
						
					LogD("lpl:订单号："+productInfo.get("orderId").toString()+",充值金额："+Integer.parseInt(productInfo.get("price")));
					MiCommplatform.getInstance().miUniPay(mContext, miBuyInfo, 
							new OnPayProcessListener()
							{
							@Override
								public void finishPayProcess( int code ) {
									switch( code ) {
									case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
										//购买成功
										IAPXiaoMi.PayResult(IAPWrapper.PAYRESULT_SUCCESS, "购买成功");
							   			break;
									case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
							   			//取消购买
										IAPXiaoMi.PayResult(IAPWrapper.PAYRESULT_CANCEL, "取消购买");
										break;
									case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
							   			//购买失败
										IAPXiaoMi.PayResult(IAPWrapper.PAYRESULT_FAIL, "购买失败");
										break;       
							case  MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:	
							   //操作正在进行中
							   break;		
									default:
										//购买失败
										IAPXiaoMi.PayResult(IAPWrapper.PAYRESULT_FAIL, "操作正在进行中");
							  			break;
									}
								}
							});

				}catch(Exception e){
					LogE("Product info parse error!", e);
				}
			}
		});

	}
	
	public static void loginResult(int ret, String strUserId,String strSessionId,  String strInfo){
		Hashtable<String, String> info = new Hashtable<String, String>();
		if(strUserId!=null)	info.put("userId", strUserId);
		if(strSessionId!=null)	info.put("sessionId", strSessionId);
		info.put("loginInfo", strInfo);
		
		IAPWrapper.onLoginResult(mXiaoMi, ret, info);
		LogD("lpl:XiaoMi loginResult : " + ret + " info : " + info);
	}
	
	public static void PayResult(int ret, String msg){
		IAPWrapper.onPayResult(mXiaoMi, ret, msg);
		LogD("lpl:XiaoMi payResult : " + ret + " msg : " + msg);
	}
	
	
	public void setDebugMode(boolean debug){
		bDebug = debug;
	}
	
	public String getSDKVersion(){
		return "4.0.11";
	}
	
	public String getPluginVersion(){
		return "1.0.2";
	}	
	
}
