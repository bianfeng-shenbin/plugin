package org.cocos2dx.aliscan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliPayRequest {

	private Map<String, String> mParamMap = new HashMap<String, String>();
	
	public void clear() {
		mParamMap.clear();
	}
	
	public void setParam(String key, String value) {
		mParamMap.put(key, value);
	}
	
	// make url
	public String makeUrlString(String md5Key) {
		if (mParamMap.size() <= 0) {
			Utils.LogD("mParamMap 没有数据！");
			return "";
		}
		
		String urlString = FinalValue.ALI_GATEWAY;
		
		String unsignedString = Utils.makeUnsignedString(mParamMap);
		
		//////////////////////////////////////////////
		// 处理url encode
		String urlEncodedString = "";
		// get all key and val
		Iterator<String> it2 = mParamMap.keySet().iterator();
		while (it2.hasNext()) {
			String key = (String)it2.next();
			// 注意转换到url encoded
			urlEncodedString += key;
			urlEncodedString += "=";
			urlEncodedString += Utils.toURLEncoded(mParamMap.get(key));
			urlEncodedString += "&";
		}
		urlString += urlEncodedString;
		//////////////////////////////////////////////
		
		//////////////////////////////////////////////
		// 不处理url encode 需要保证参数里没有特殊字符
//		urlString += unsignedString;
//		urlString += "&";
		//////////////////////////////////////////////
		
		// 签名统一使用MD5
		urlString += FinalValue.PKB_SIGN_TYPE;
		urlString += "=";
		urlString += FinalValue.SIGN_TYPE_MD5;
		urlString += "&";
		urlString += FinalValue.PKB_SIGN;
		urlString += "=";
		urlString += Utils.makeSignString(unsignedString+md5Key);
//		urlString += "&";
		
		Utils.LogD("req url:"+urlString);
		return urlString;
	}
}
