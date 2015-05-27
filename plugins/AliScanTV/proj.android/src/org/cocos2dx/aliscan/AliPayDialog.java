package org.cocos2dx.aliscan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AliPayDialog {
	private static final String DLG_TITLE = "支付提示";
	
	private static final int ID_QR_BASE	= 0;
	private static final int ID_QR_IMG	= ID_QR_BASE+1;
	private static final int ID_QR_TIP	= ID_QR_BASE+2;
	private static final int ID_QR_TIP2	= ID_QR_BASE+3;
	
	private static final int ID_PI_BASE		= 10;
	private static final int ID_PI_TIP		= ID_PI_BASE+1;
	private static final int ID_PI_NAME		= ID_PI_BASE+2;
	private static final int ID_PI_PRICE	= ID_PI_BASE+3;
	private static final int ID_PI_TRADE_NO	= ID_PI_BASE+4;
	private static final int ID_PI_USER		= ID_PI_BASE+5;
	
	private static final int ID_PH_BASE		= 20;
	private static final int ID_PH_TIP		= ID_PH_BASE+1;
	private static final int ID_PH_TIP2		= ID_PH_BASE+2;
	private static final int ID_PH_TIP3		= ID_PH_BASE+3;
	private static final int ID_PH_TRADE_NO	= ID_PH_BASE+4;
	private static final int ID_PH_USER		= ID_PH_BASE+5;

	private AliPayHandler mHandler;
	private Context mContext;
	
	// ui
	private ProgressDialog mProgressDialog = null;
	private AlertDialog mFailedDialog = null;
	private AlertDialog mSuccedDialog = null;
	
	// sp1
	private Dialog mQRCodeDlg = null;
	private RelativeLayout mQRCodeView = null;
	private ImageView mQRCodeImg = null;
	private TextView mQRCodeTipTv = null;
	private TextView mQRCodeTip2Tv = null;
	
	// sp2
	private Dialog mPayInfoDlg = null;
	private RelativeLayout mPayInfoView = null;
	private TextView mPayInfoTipTv = null;
	private TextView mPayInfoNameTv = null;
	private TextView mPayInfoPriceTv = null;
	private TextView mPayInfoTradeNoTv = null;
	private TextView mPayInfoUserTv = null;
	
	// sp3
	private Dialog mPayOnPhoneDlg = null;
	private RelativeLayout mPayOnPhoneView = null;
	private TextView mPayOnPhoneTipTv = null;
	private TextView mPayOnPhoneTip2Tv = null;
	private TextView mPayOnPhoneTip3Tv = null;
	private TextView mPayOnPhoneTradeNoTv = null;
	private TextView mPayOnPhoneUserTv = null;
	
	public AliPayDialog(Context context, AliPayHandler handler) {
		mContext = context;
		mHandler = handler;
		
		initUI();
	}
	
	public void initUI() {
		// 初始化
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("支付中...");
		
		mFailedDialog = new AlertDialog.Builder(mContext)
		.setTitle(DLG_TITLE)
		.setCancelable(false)
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				hideFailedDlg();
			}
		}).create();
		
		mSuccedDialog = new AlertDialog.Builder(mContext)
		.setTitle(DLG_TITLE)
		.setCancelable(false)
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				hideSuccedDlg();
			}
		}).create();
		
		// sp1
		initQRCodeDlg();
		initPayInfoDlg();
		initPayOnPhoneDlg();
	}
	
	private void initQRCodeDlg() {
		mQRCodeView = new RelativeLayout(mContext);
		
		mQRCodeImg = new ImageView(mContext);
		mQRCodeImg.setBackgroundColor(0xFFFFFFFF);
		mQRCodeImg.setId(ID_QR_IMG);
		
		mQRCodeTipTv = new TextView(mContext);
		mQRCodeTipTv.setTextColor(0xFFFFFFFF);
		mQRCodeTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0f);
		mQRCodeTipTv.setId(ID_QR_TIP);
		mQRCodeTip2Tv = new TextView(mContext);
		mQRCodeTip2Tv.setTextColor(0xFFFF0000);
		mQRCodeTip2Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mQRCodeTip2Tv.setId(ID_QR_TIP2);
		
		mQRCodeTipTv.setText("请打开支付宝钱包扫描二维码以完成绑定");
		mQRCodeTip2Tv.setText("授权仅需一次，以后支付更便捷");
		
		// 添加
		LayoutParams lpTv = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		lpTv.topMargin = 10;
		mQRCodeView.addView(mQRCodeTipTv, lpTv);
		
		LayoutParams lpTv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv2.addRule(RelativeLayout.BELOW, ID_QR_TIP);
		lpTv2.topMargin = 10;
		mQRCodeView.addView(mQRCodeTip2Tv, lpTv2);
		
		LayoutParams lpImg = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpImg.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpImg.addRule(RelativeLayout.BELOW, ID_QR_TIP2);
		lpImg.topMargin = 10;
		mQRCodeView.addView(mQRCodeImg, lpImg);
		
		// 
		mQRCodeDlg = new AlertDialog.Builder(mContext)
		.setTitle(DLG_TITLE)
		.setView(mQRCodeView)
		.setCancelable(false)
		.setPositiveButton("取消授权", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 解除绑定
				mHandler.enableGetAuthState(false);
			}
		}).create();
	}
	
	private void initPayInfoDlg() {
		mPayInfoView = new RelativeLayout(mContext);

		mPayInfoTipTv = new TextView(mContext);
		mPayInfoTipTv.setTextColor(0xFFFFFFFF);
		mPayInfoTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayInfoTipTv.setId(ID_PI_TIP);
		mPayInfoNameTv = new TextView(mContext);
		mPayInfoNameTv.setTextColor(0xFFFFFFFF);
		mPayInfoNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayInfoNameTv.setId(ID_PI_NAME);
		mPayInfoPriceTv = new TextView(mContext);
		mPayInfoPriceTv.setTextColor(0xFFFFFFFF);
		mPayInfoPriceTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayInfoPriceTv.setId(ID_PI_PRICE);
		mPayInfoTradeNoTv = new TextView(mContext);
		mPayInfoTradeNoTv.setTextColor(0xFFFFFFFF);
		mPayInfoTradeNoTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);
		mPayInfoTradeNoTv.setId(ID_PI_TRADE_NO);
		mPayInfoUserTv = new TextView(mContext);
		mPayInfoUserTv.setTextColor(0xFFFFFFFF);
		mPayInfoUserTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);
		mPayInfoUserTv.setId(ID_PI_USER);
		
		mPayInfoTipTv.setText("您是否确定要购买此商品？");
		
		// 添加
		LayoutParams lpTv = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		lpTv.topMargin = 10;
		mPayInfoView.addView(mPayInfoTipTv, lpTv);
		
		LayoutParams lpTv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv2.addRule(RelativeLayout.BELOW, ID_PI_TIP);
		lpTv2.topMargin = 20;
		mPayInfoView.addView(mPayInfoNameTv, lpTv2);
		
		LayoutParams lpTv3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv3.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv3.addRule(RelativeLayout.BELOW, ID_PI_NAME);
		lpTv3.topMargin = 5;
		mPayInfoView.addView(mPayInfoPriceTv, lpTv3);
		
		LayoutParams lpTv4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv4.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv4.addRule(RelativeLayout.BELOW, ID_PI_PRICE);
		lpTv4.topMargin = 20;
		mPayInfoView.addView(mPayInfoTradeNoTv, lpTv4);
		
		LayoutParams lpTv5 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv5.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv5.addRule(RelativeLayout.BELOW, ID_PI_TRADE_NO);
		lpTv5.topMargin = 10;
		mPayInfoView.addView(mPayInfoUserTv, lpTv5);
		
		// 
		mPayInfoDlg = new AlertDialog.Builder(mContext)
		.setTitle(DLG_TITLE)
		.setView(mPayInfoView)
		.setCancelable(false)
		.setPositiveButton("买买买", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 请求支付
				mHandler.sendEmptyMessage(AliPayHandler.MSG_REQUEST_PAY);
			}
		})
		.setNegativeButton("不买了", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 取消交易
				mHandler.sendEmptyMessage(AliPayHandler.MSG_NOT_PAY);
			}
		}).create();
	}
	
	private void initPayOnPhoneDlg() {
		mPayOnPhoneView = new RelativeLayout(mContext);

		mPayOnPhoneTipTv = new TextView(mContext);
		mPayOnPhoneTipTv.setTextColor(0xFFFFFFFF);
		mPayOnPhoneTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayOnPhoneTipTv.setId(ID_PH_TIP);
		mPayOnPhoneTip2Tv = new TextView(mContext);
		mPayOnPhoneTip2Tv.setTextColor(0xFFFFFFFF);
		mPayOnPhoneTip2Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayOnPhoneTip2Tv.setId(ID_PH_TIP2);
		mPayOnPhoneTip3Tv = new TextView(mContext);
		mPayOnPhoneTip3Tv.setTextColor(0xFFFF0000);
		mPayOnPhoneTip3Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
		mPayOnPhoneTip3Tv.setId(ID_PH_TIP3);
		mPayOnPhoneTradeNoTv = new TextView(mContext);
		mPayOnPhoneTradeNoTv.setTextColor(0xFFFFFFFF);
		mPayOnPhoneTradeNoTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);
		mPayOnPhoneTradeNoTv.setId(ID_PH_TRADE_NO);
		mPayOnPhoneUserTv = new TextView(mContext);
		mPayOnPhoneUserTv.setTextColor(0xFFFFFFFF);
		mPayOnPhoneUserTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);
		mPayOnPhoneUserTv.setId(ID_PH_USER);
		
		mPayOnPhoneTipTv.setText("您未开通免密或超出您设定的免密限额");
		mPayOnPhoneTip2Tv.setText("订单已发送至您的支付宝钱包");
		mPayOnPhoneTip3Tv.setText("请于30分钟内完成支付 或 点击“取消交易”以取消本次交易");
		
		// 添加
		LayoutParams lpTv = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		lpTv.topMargin = 10;
		mPayOnPhoneView.addView(mPayOnPhoneTipTv, lpTv);
		
		LayoutParams lpTv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv2.addRule(RelativeLayout.BELOW, ID_PH_TIP);
		lpTv2.topMargin = 5;
		mPayOnPhoneView.addView(mPayOnPhoneTip2Tv, lpTv2);
		
		LayoutParams lpTv3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv3.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv3.addRule(RelativeLayout.BELOW, ID_PH_TIP2);
		lpTv3.topMargin = 5;
		mPayOnPhoneView.addView(mPayOnPhoneTip3Tv, lpTv3);
		
		LayoutParams lpTv4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv4.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv4.addRule(RelativeLayout.BELOW, ID_PH_TIP3);
		lpTv4.topMargin = 20;
		mPayOnPhoneView.addView(mPayOnPhoneTradeNoTv, lpTv4);
		
		LayoutParams lpTv5 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTv5.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lpTv5.addRule(RelativeLayout.BELOW, ID_PH_TRADE_NO);
		lpTv5.topMargin = 10;
		mPayOnPhoneView.addView(mPayOnPhoneUserTv, lpTv5);
		
		// 
		mPayOnPhoneDlg = new AlertDialog.Builder(mContext)
		.setTitle(DLG_TITLE)
		.setView(mPayOnPhoneView)
		.setCancelable(false)
		.setPositiveButton("取消交易", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 请求支付
				mHandler.enableGetPayResult(false);
			}
		}).create();
	}
	
	public void showProgressDlg() {
		mProgressDialog.show();
	}
	
	public void hideProgressDlg() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			System.gc();
		}
	}
	
	public void showQRCodeDlg(Drawable drawable) {
		mQRCodeImg.setImageDrawable(drawable);
		mQRCodeDlg.show();
	}
	
	public void hideQRCodeDlg() {
		if (mQRCodeDlg.isShowing()) {
			mQRCodeDlg.dismiss();
			mQRCodeImg.setImageDrawable(null);
			System.gc();
		}
	}
	
	public void showPayInfoDlg(BuyData buyData) {
		mPayInfoNameTv.setText("商品名称:"+buyData.mProductName);
		mPayInfoPriceTv.setText("商品价格："+buyData.mProductPrice);
		mPayInfoTradeNoTv.setText("订单号："+buyData.mTradeNo);	// 使用我们的订单号 支付宝这边的用户不可见
		mPayInfoUserTv.setText("支付宝钱包账户："+buyData.mAuthUser);
		mPayInfoDlg.show();
	}
	
	public void hidePayInfoDlg() {
		if (mPayInfoDlg.isShowing()) {
			mPayInfoDlg.dismiss();
			System.gc();
		}
	}
	
	public void showPayOnPhoneDlg(BuyData buyData) {
		mPayOnPhoneTradeNoTv.setText("订单号："+buyData.mTradeNo);	// 使用我们的订单号 支付宝这边的用户不可见
		mPayOnPhoneUserTv.setText("支付宝钱包账户："+buyData.mAuthUser);
		mPayOnPhoneDlg.show();
	}
	
	public void hidePayOnPhoneDlg() {
		if (mPayOnPhoneDlg.isShowing()) {
			mPayOnPhoneDlg.dismiss();
			System.gc();
		}
	}
	
	public void showFailedDlg(String failedString) {
		mFailedDialog.setMessage(failedString);
		mFailedDialog.show();
	}
	
	public void hideFailedDlg() {
		if (mFailedDialog.isShowing()) {
			mFailedDialog.dismiss();
			mHandler.sendEmptyMessage(AliPayHandler.MSG_FAIL);
			System.gc();
		}
	}
	
	public void showSuccedDlg(String succedString) {
		mSuccedDialog.setMessage(succedString);
		mSuccedDialog.show();
	}
	
	public void hideSuccedDlg() {
		if (mSuccedDialog.isShowing()) {
			mSuccedDialog.dismiss();
			mHandler.sendEmptyMessage(AliPayHandler.MSG_SUCCEED);
			System.gc();
		}
	}
	
	public void hideAllDlg() {
		hideProgressDlg();
		hideQRCodeDlg();
		hidePayInfoDlg();
		hidePayOnPhoneDlg();
		hideFailedDlg();
		hideSuccedDlg();
	}
}
