package org.cocos2dx.aliscan;

import android.content.Context;
import android.os.Message;

public class AliPay {
	private AliPayHandler mAliPayHandler;
	private boolean mIsInited = false;
	
	public void init(Context context, AliPayListener l) {
		mAliPayHandler = new AliPayHandler(context, l);
		mIsInited = true;
	}
	
	public void payProduct(BuyData buyData) {
		if (!mIsInited) {
			Utils.LogD("Please call init before payProduct!");
			return ;
		}
		
		Message msg = new Message();
		msg.what = AliPayHandler.MSG_START;
		msg.obj = buyData;
		
		mAliPayHandler.sendMessage(msg);
	}
}
