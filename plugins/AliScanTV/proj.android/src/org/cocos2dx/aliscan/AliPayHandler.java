package org.cocos2dx.aliscan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class AliPayHandler extends Handler {
	public static final int MSG_BASE 				= 10;
	public static final int MSG_START				= MSG_BASE+0;	// 获取auth state 显示处理中D
	public static final int MSG_START_OK			= MSG_BASE+1;	// 第一次获取auth state成功 根据结果处理下一步 已授权==创建交易   其他==获取二维码
	public static final int MSG_START_ERR			= MSG_BASE+2;	// 第一次获取auth state失败 结束
	public static final int MSG_GET_AUTH_QRCODE		= MSG_BASE+3;	// 获取二维码
	public static final int MSG_GET_AUTH_QRCODE_OK	= MSG_BASE+4;	// 获取二维码成功 提示扫码D 并开启线程循环调用查询auth state
	public static final int MSG_GET_AUTH_QRCODE_ERR	= MSG_BASE+5;	// 获取二维码失败 结束
	public static final int MSG_REMOVE_AUTH			= MSG_BASE+6;	// 取消授权
	public static final int MSG_REMOVE_AUTH_OK		= MSG_BASE+7;	// 取消授权成功 结束
	public static final int MSG_REMOVE_AUTH_ERR		= MSG_BASE+8;	// 取消授权失败 结束
	public static final int MSG_GET_AUTH_STATE		= MSG_BASE+9;	// 循环获取授权状态
	public static final int MSG_GET_AUTH_STATE_OK	= MSG_BASE+10;	// 循环获取授权状态  授权成功 开始创建交易
	public static final int MSG_GET_AUTH_STATE_ERR	= MSG_BASE+11;	// 授权没成功的各种状态 继续请求授权 无限循环 直到用户取消
	public static final int MSG_CREATE_PAY			= MSG_BASE+12;	// 开始创建交易
	public static final int MSG_CREATE_PAY_OK		= MSG_BASE+13;	// 创建交易成功 获取授权账户
	public static final int MSG_CREATE_PAY_ERR		= MSG_BASE+14;	// 创建交易失败 结束
	public static final int MSG_GET_AUTH_USER		= MSG_BASE+15;	// 获取授权账户
	public static final int MSG_GET_AUTH_USER_OK	= MSG_BASE+16;	// 获取授权账户成功 保存授权账户 显示是否支付框 D
	public static final int MSG_GET_AUTH_USER_ERR	= MSG_BASE+17;	// 获取授权账户失败 结束
	public static final int MSG_REQUEST_PAY			= MSG_BASE+18;	// 用户选择购买 开始请求支付
	public static final int MSG_NOT_PAY				= MSG_BASE+19;	// 用户选择不买 取消交易 
	public static final int MSG_CANCEL_PAY			= MSG_BASE+20;	// 开始取消交易
	public static final int MSG_CANCEL_PAY_OK		= MSG_BASE+21;	// 取消交易成功 结束
	public static final int MSG_CANCEL_PAY_ERR		= MSG_BASE+22;	// 取消交易失败 提示请勿支付 结束
	public static final int MSG_REQUEST_PAY_OK		= MSG_BASE+23;	// 请求支付成功 显示在手机上完成支付提示D 开始查询支付状态 无限循环 直到用户取消==如果用户取消交易 进入之前的MSG_NOT_PAY流程
	public static final int MSG_REQUEST_PAY_ERR		= MSG_BASE+24;	// 请求支付失败 结束
	public static final int MSG_QUERY_PAY			= MSG_BASE+25;	// 循环查询支付结果 
	public static final int MSG_QUERY_PAY_OK		= MSG_BASE+26;	// 支付完成 回调 结束
	public static final int MSG_QUERY_PAY_ERR		= MSG_BASE+27;	// 查询支付失败 或者 支付没有完成 循环查询支付结果 直到用户手动取消
	public static final int MSG_PAY_OK				= MSG_BASE+28;	// 支付结束 支付成功 显示提示框
	
	public static final int MSG_SHOW_QRCODE			= MSG_BASE+30;	// 显示二位码
	
	public static final int MSG_FAIL				= MSG_BASE+50;	// 失败的结果 发送结果给listener
	public static final int MSG_SUCCEED				= MSG_BASE+51;	// 成功的结果 发送结果给listener

	public static final int MSG_PARAM_ERR			= MSG_BASE+60;	// 参数错误  提示
	
	private Context mContext = null;
	private AliPayDialog mDialogManager = null;
	private AliPayListener mListener = null;
	private BuyData mBuyData = null;
	
	private boolean mGetAuthStateOn = false;
	private boolean mGetPayResultOn = false;

	public AliPayHandler(Context context, AliPayListener l) {
		mContext = context;
		mListener = l;
		init();
	}
	
	private void init() {
		mDialogManager = new AliPayDialog(mContext, this);
	}
	
	public void enableGetAuthState(boolean isOn) {
		mGetAuthStateOn = isOn;
	}
	
	public void enableGetPayResult(boolean isOn) {
		mGetPayResultOn = isOn;
	}
	
	private boolean isGetAuthStateOn() {
		return mGetAuthStateOn;
	}
	
	private boolean isGetPayResultOn() {
		return mGetPayResultOn;
	}

	@Override
	public void handleMessage(Message msg) {
		Utils.LogD("handle msg:"+msg.what);
		switch (msg.what) {
		case MSG_START:
		{
			mBuyData = (BuyData)msg.obj;
			if (mBuyData == null) {
				// 直接提示失败
				sendEmptyMessage(MSG_PARAM_ERR);
				return ;
			}
			// 显示处理中
			mDialogManager.showProgressDlg();
			// 开始第一次获取auth state 所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.getAuthState(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_START_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_START_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_START_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("获取设备授权状态失败，请稍后重试");
		}
		break;
		case MSG_START_OK:
		{
			AliPayInterfaceResult lastRet = (AliPayInterfaceResult)msg.obj;
			if (lastRet.mIsResultOK) {
				String authState = (String)lastRet.mUserData;
				if (authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_COMPLATE)) {
					// 已经授权结束 直接创建交易
					sendEmptyMessage(MSG_CREATE_PAY);
				} else {
					// 设备没有授权 请求授权
					sendEmptyMessage(MSG_GET_AUTH_QRCODE);
				}			
			} else {
				String authState = (String)lastRet.mUserData;
				if (authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_ERR_NOT_AUTHED)
						|| authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_ERR_INVALID_AUTH)
						|| authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_ERR_EXPIRE_AUTH)) {
					// 设备没有授权 请求授权
					sendEmptyMessage(MSG_GET_AUTH_QRCODE);
				} else {
					// 其他认为失败
					sendEmptyMessage(MSG_START_ERR);
				}
			}
		}
		break;
		case MSG_GET_AUTH_QRCODE:
		{
			// 开始第一次获取auth state 所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.getAuthQRCode(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_GET_AUTH_QRCODE_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_GET_AUTH_QRCODE_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_GET_AUTH_QRCODE_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("授权设备请求失败，请稍后重试");
		}
		break;
		case MSG_GET_AUTH_QRCODE_OK:
		{
			// 获取网络图片  弹出对话框
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				final String imgUrl = (String)result.mUserData;
				// 需要另外开线程 否则httpget报错
				new Thread(new Runnable() {
					@Override
					public void run() {
						Drawable drawable = null;
						try {
							Utils.LogD("img url:"+imgUrl);
							drawable = Drawable.createFromStream(Utils.httpGetImage(imgUrl), null);
							
							Message msgRet = new Message();
							msgRet.what = MSG_SHOW_QRCODE;
							msgRet.obj = drawable;
							
							sendMessage(msgRet);
						} catch (Exception e) {
							Utils.LogE("解析网络图片错误", e);
							// 提示
							sendEmptyMessage(MSG_GET_AUTH_QRCODE_ERR);
							return ;
						}
					}
				}).start();				
			} else {
				// 其他暂时直接认为出错
				sendEmptyMessage(MSG_GET_AUTH_QRCODE_ERR);
			}
		}
		break;
		case MSG_SHOW_QRCODE:
		{
			// 显示二维码 启动获取授权状态
			mDialogManager.hideAllDlg();
			mDialogManager.showQRCodeDlg((Drawable)msg.obj);
			
			enableGetAuthState(true);
			sendEmptyMessage(MSG_GET_AUTH_STATE);
		}
		break;
		case MSG_REMOVE_AUTH:
		{
			// 取消授权 所有与阿里的交互都在线程中处理
			enableGetAuthState(false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.removeAuth(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_REMOVE_AUTH_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_REMOVE_AUTH_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_REMOVE_AUTH_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("取消授权失败，请稍后重试");
		}
		break;
		case MSG_REMOVE_AUTH_OK:
		{
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				mDialogManager.hideAllDlg();
				mDialogManager.showFailedDlg("取消授权成功");				
			} else {
				sendEmptyMessage(MSG_REMOVE_AUTH_ERR);
			}
		}
		break;
		case MSG_GET_AUTH_STATE:
		{
			// 取消授权 所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.getAuthState(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_GET_AUTH_STATE_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_GET_AUTH_STATE_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_GET_AUTH_STATE_ERR:
		{
//			mDialogManager.hideAllDlg();
//			mDialogManager.showFailedDlg("获取设备授权状态失败，请稍后重试");
			// 获取授权状态失败 无限请求授权状态 直到用户取消授权
			Utils.LogD("获取设备授权状态失败");
			if (isGetAuthStateOn()) {
				sendEmptyMessage(MSG_GET_AUTH_STATE);
			} else {	// 否则 直接取消授权
				sendEmptyMessage(MSG_REMOVE_AUTH);
			}
		}
		break;
		case MSG_GET_AUTH_STATE_OK:
		{
			// 循环获取授权状态  授权成功 开始创建交易
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				String authState = (String)result.mUserData;
				
				if (authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_COMPLATE)) {
					// 授权成功 对话框在创建里面关闭
					sendEmptyMessage(MSG_CREATE_PAY);
					// 显示提示框
					Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
				} else if (authState.equalsIgnoreCase(FinalValue.XML_AUTH_STATE_CANCEL)) {
					// 授权取消
//					sendEmptyMessage(MSG_REMOVE_AUTH_OK);	// 直接转到用户取消授权
//					enableGetAuthState(false);
					sendEmptyMessage(MSG_REMOVE_AUTH);
				} else {
					// 其他情况都不断获取状态
//					sendEmptyMessage(MSG_GET_AUTH_STATE);
					if (isGetAuthStateOn()) {
						sendEmptyMessage(MSG_GET_AUTH_STATE);
					} else {	// 否则 直接取消授权
						sendEmptyMessage(MSG_REMOVE_AUTH);
					}
				}				
			} else {
				sendEmptyMessage(MSG_GET_AUTH_STATE_ERR);
			}
		}
		break;
		case MSG_CREATE_PAY:
		{
			// 开始创建交易  所有与阿里的交互都在线程中处理
			enableGetAuthState(false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.createPay(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_CREATE_PAY_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_CREATE_PAY_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_CREATE_PAY_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("创建交易失败，请稍后重试");
		}
		break;
		case MSG_CREATE_PAY_OK:
		{
			// 创建交易成功  开始获取授权用户
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				mBuyData.mAliTradeNo = (String)result.mUserData;
				Utils.LogD("ali trade no:"+mBuyData.mAliTradeNo);
				// 授权成功 对话框在创建里面关闭
				sendEmptyMessage(MSG_GET_AUTH_USER);			
			} else {
				sendEmptyMessage(MSG_CREATE_PAY_ERR);
			}
		}
		break;
		case MSG_GET_AUTH_USER:
		{
			// 开始获取授权账户  所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.getAuthUser(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_GET_AUTH_USER_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_GET_AUTH_USER_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_GET_AUTH_USER_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("获取设备授权用户失败，请稍后重试");
		}
		break;
		case MSG_GET_AUTH_USER_OK:
		{
			// 获取授权账户成功 保存授权账户 显示是否支付框 D
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				mBuyData.mAuthUser = (String)result.mUserData;
				Utils.LogD("auth user:"+mBuyData.mAuthUser);
				// 获取授权用户成功 显示支付框
				mDialogManager.hideAllDlg();
				mDialogManager.showPayInfoDlg(mBuyData);
			} else {
				sendEmptyMessage(MSG_GET_AUTH_USER_ERR);
			}
		}
		break;
		case MSG_REQUEST_PAY:
		{
			// 开始请求支付  所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.requestPay(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_REQUEST_PAY_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_REQUEST_PAY_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_NOT_PAY:
		{
			// 取消支付  用户点击了取消支付
			sendEmptyMessage(MSG_CANCEL_PAY);
		}
		break;
		case MSG_CANCEL_PAY:
		{
			// 开始请求支付  所有与阿里的交互都在线程中处理
			enableGetPayResult(false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.cancelPay(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_CANCEL_PAY_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_CANCEL_PAY_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_CANCEL_PAY_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("取消交易失败，请稍后重试");
		}
		break;
		case MSG_CANCEL_PAY_OK:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("交易已取消");
		}
		break;
		case MSG_REQUEST_PAY_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("请求支付失败，请稍后重试");
		}
		break;
		case MSG_REQUEST_PAY_OK:
		{
			// 请求支付成功 显示在手机上完成支付提示D 
			// 开始查询支付状态 无限循环 直到用户取消==如果用户取消交易 进入之前的MSG_NOT_PAY流程
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				String payResult = (String)result.mUserData;
				Utils.LogD("pay result:"+payResult);
				// 分析返回的支付结果
				if (payResult.equalsIgnoreCase(FinalValue.XML_PAY_RET_PAYMENT_SUCCESS)) {
					// 支付成功
					sendEmptyMessage(MSG_PAY_OK);
				} else if (payResult.equalsIgnoreCase(FinalValue.XML_PAY_RET_PAYMENT_FAIL)
						|| payResult.equalsIgnoreCase(FinalValue.XML_PAY_RET_BUYER_ACCOUNT_BLOCK)) {
					// 失败 取消交易 但是不显示交易取消 而是显示交易失败
					sendEmptyMessage(MSG_REQUEST_PAY_ERR);
					// 同时取消交易
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							AliPayInterfaceResult result = AliPayInterface.cancelPay(mBuyData);
							if (!result.mIsProcessOK) {
								// 失败 提示
								Utils.LogD("支付失败，取消交易失败！！！！");
							} else {
								Utils.LogD("支付失败，取消交易成功！");
							}
						}
					}).start();
				} else {
					// 其他情况都不断获取状态
					enableGetPayResult(true);
					sendEmptyMessage(MSG_QUERY_PAY);
					
					mDialogManager.hideAllDlg();
					mDialogManager.showPayOnPhoneDlg(mBuyData);
				}
			} else {
				sendEmptyMessage(MSG_REQUEST_PAY_ERR);
			}
		}
		break;
		case MSG_QUERY_PAY:
		{
			// 开始查询支付结果  所有与阿里的交互都在线程中处理
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					AliPayInterfaceResult result = AliPayInterface.queryPay(mBuyData);
					if (!result.mIsProcessOK) {
						// 失败 提示
						sendEmptyMessage(MSG_QUERY_PAY_ERR);
					} else {
						Message msgResutl = new Message();
						msgResutl.what = MSG_QUERY_PAY_OK;
						msgResutl.obj = result;
						
						sendMessage(msgResutl);
					}
				}
			}).start();
		}
		break;
		case MSG_QUERY_PAY_ERR:
		{
//			mDialogManager.hideAllDlg();
//			mDialogManager.showFailedDlg("查询交易状态失败，请稍后重试");
			// 查询交易状态失败 无限查询交易 直到用户取消交易
			Utils.LogD("查询交易状态失败");
			if (isGetPayResultOn()) {
				sendEmptyMessage(MSG_QUERY_PAY);
			} else {	// 否则 直接取消交易
				sendEmptyMessage(MSG_CANCEL_PAY);
			}
		}
		break;
		case MSG_QUERY_PAY_OK:
		{
			// 循环获取支付结果 支付成功 结束交易
			AliPayInterfaceResult result = (AliPayInterfaceResult)msg.obj;
			if (result.mIsResultOK) {
				String authState = (String)result.mUserData;
				
				if (authState.equalsIgnoreCase(FinalValue.XML_PAY_STATE_PAYMENT_SUCCESS)) {
					// 先关闭状态获取
					enableGetPayResult(false);
					// 支付成功 显示成功
					sendEmptyMessage(MSG_PAY_OK);
				} else if (authState.equalsIgnoreCase(FinalValue.XML_PAY_STATE_BIZ_CLOSED)) {
					// 先关闭状态获取
					enableGetPayResult(false);
					// 交易超时取消  直接提示
					mDialogManager.hideAllDlg();
					mDialogManager.showFailedDlg("交易超时自动关闭");
				} else {
					// 其他情况都不断获取状态
//					sendEmptyMessage(MSG_QUERY_PAY);
					if (isGetPayResultOn()) {
						sendEmptyMessage(MSG_QUERY_PAY);
					} else {	// 否则 直接取消交易
						sendEmptyMessage(MSG_CANCEL_PAY);
					}
				}				
			} else {
				sendEmptyMessage(MSG_QUERY_PAY_ERR);
			}
		}
		break;
		case MSG_PAY_OK:
		{
//			enableGetPayResult(false);
			mDialogManager.hideAllDlg();
			mDialogManager.showSuccedDlg("购买成功");
		}
		break;
		
		case MSG_PARAM_ERR:
		{
			mDialogManager.hideAllDlg();
			mDialogManager.showFailedDlg("购买失败，参数错误");
		}
		break;
		
		case MSG_FAIL:
		{
			mListener.onAliPayFinished(false, null);
		}
		break;
		case MSG_SUCCEED:
		{
			mListener.onAliPayFinished(true, null);
		}
		break;

		default:
			break;
		}
		
		super.handleMessage(msg);
	}
}
