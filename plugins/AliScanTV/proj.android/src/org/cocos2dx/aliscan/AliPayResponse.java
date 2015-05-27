package org.cocos2dx.aliscan;

import java.util.HashMap;
import java.util.Map;

public class AliPayResponse {
	
	private boolean mIsSuc;
	private String mRespSign;
	private String mRespSignType;
	
	private Map<String, String> mResultMap = new HashMap<String, String>();
	
	public void clear() {
		mResultMap.clear();
	}
	
	public void setResult(String key, String value) {
		mResultMap.put(key, value);
	}
	
	public String getResult(String key) {
		return mResultMap.get(key);
	}
	
	public void setIsSuc(boolean isSuc) {
		mIsSuc = isSuc;
	}
	
	public boolean isSucced() {
		return mIsSuc;
	}
	
	public void setRespSign(String sRespSign) {
		mRespSign = sRespSign;
	}
	
	public void setRespSignType(String sRespSignType) {
		mRespSignType = sRespSignType;
	}
	
	private boolean isSignOK(String md5Key) {
		if (mRespSignType == null 
				|| !mRespSignType.equalsIgnoreCase(FinalValue.SIGN_TYPE_MD5)) {
			Utils.LogD("非MD5签名！");
			return false;
		}
		
		// 根据所有参数制造自己的签名
		String sSignString = Utils.makeSignString(Utils.makeUnsignedString(mResultMap)+md5Key);
		
		// 验证
		return mRespSign.equals(sSignString);
	}
	
	public boolean isRespOK(String md5Key) {
		// 如果返回签名错误  那么返回数据里不会有签名项 ====所以不可以先验证签名
		// 检查是否成功 以及签名是否正确  都通过了才算正确返回
//		if (!isSuc()) {
//			Utils.LogD("isSuc:false");
//			return false;
//		}
		if (!isSignOK(md5Key)) {
			Utils.LogD("isSignOK:false");
			return false;
		}
		
		return true;
	}
}
