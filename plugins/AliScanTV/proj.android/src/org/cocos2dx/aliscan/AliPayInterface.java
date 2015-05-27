package org.cocos2dx.aliscan;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class AliPayInterface {

	
	
	public static AliPayInterfaceResult getAuthQRCode(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_GET_QRCODE);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKQR_TARGET_ID, buyData.mTargetID);
		param.setParam(FinalValue.PKQR_TARGET_NAME, buyData.mTargetName);
		param.setParam(FinalValue.PKQR_TARGET_LOGO_URL, buyData.mTargetLogoUrl);
		param.setParam(FinalValue.PKQR_TARGET_TYPE, buyData.mTargetType);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// qrurl
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NQR_QRCODE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NQR_QRCODE+" val:"+val);
									response.setResult(FinalValue.XML_NQR_QRCODE, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NQR_QRCODE);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult getAuthState(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_GET_AUTH_STATE);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKQR_TARGET_ID, buyData.mTargetID);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// auth state
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NAUTH_STATE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NAUTH_STATE+" val:"+val);
									response.setResult(FinalValue.XML_NAUTH_STATE, Utils.toURLDecoded(val));
								}
								// auth id
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NAUTH_ID)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NAUTH_ID+" val:"+val);
									response.setResult(FinalValue.XML_NAUTH_ID, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			// 这里特殊处理 
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NAUTH_STATE);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult getAuthUser(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_GET_AUTH_ALIPAYUSER);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKQR_TARGET_ID, buyData.mTargetID);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// user id
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NUSER_ID)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NUSER_ID+" val:"+val);
									response.setResult(FinalValue.XML_NUSER_ID, Utils.toURLDecoded(val));
								}
								// user login id(phone/mail)
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NUSER_LOGIN_ID)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NUSER_LOGIN_ID+" val:"+val);
									response.setResult(FinalValue.XML_NUSER_LOGIN_ID, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NUSER_LOGIN_ID);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult removeAuth(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_REVOKE_AUTH);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKQR_TARGET_ID, buyData.mTargetID);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			// 这里特殊 成功的不处理签名  因为数据里根本没签名...
			// 失败的才检查签名
			if (!response.isSucced()) {
				if (!response.isRespOK(buyData.mMD5Key)) {
					Utils.LogD("有问题的数据！！");
					apiResult.mIsProcessOK = false;
					return apiResult;	// 处理到此结束   或者给个标记					
				}
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult createPay(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_CREATE_TRADE);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKCP_NOTIFY_URL, buyData.mNotifyUrl);
		
		param.setParam(FinalValue.PKCP_TARGET_ID, buyData.mTargetID);
		param.setParam(FinalValue.PKCP_SUBJECT, buyData.mProductName);
		param.setParam(FinalValue.PKCP_AMOUNT, buyData.mProductPrice);
		param.setParam(FinalValue.PKCP_OUT_TRADE_NO, buyData.mTradeNo);
		param.setParam(FinalValue.PKCP_SELLER, buyData.mSeller);
		param.setParam(FinalValue.PKCP_IT_B_TYPE, FinalValue.DEF_IT_B_TYPE);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// trade no
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NCREATE_TRADE_NO)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NCREATE_TRADE_NO+" val:"+val);
									response.setResult(FinalValue.XML_NCREATE_TRADE_NO, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NCREATE_TRADE_NO);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult requestPay(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_PAY);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		param.setParam(FinalValue.PKPP_NOTIFY_URL, buyData.mNotifyUrl);
		
		param.setParam(FinalValue.PKPP_TARGET_ID, buyData.mTargetID);
		param.setParam(FinalValue.PKPP_BIZ_NO, buyData.mAliTradeNo);
		param.setParam(FinalValue.PKPP_BIZ_TYPE, FinalValue.BIZ_TYPE);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// result code
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NPAY_RESULT)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NPAY_RESULT+" val:"+val);
									response.setResult(FinalValue.XML_NPAY_RESULT, Utils.toURLDecoded(val));
								}
								// pay type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NPAY_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NPAY_TYPE+" val:"+val);
									response.setResult(FinalValue.XML_NPAY_TYPE, Utils.toURLDecoded(val));
								}
								// mobile
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NPAY_MOBILE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NPAY_MOBILE+" val:"+val);
									response.setResult(FinalValue.XML_NPAY_MOBILE, Utils.toURLDecoded(val));
								}
								// fund money
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NPAY_FUND_MONEY)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NPAY_FUND_MONEY+" val:"+val);
									response.setResult(FinalValue.XML_NPAY_FUND_MONEY, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NPAY_RESULT);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult queryPay(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_QUERY_PAYRESULT);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);

		param.setParam(FinalValue.PKQP_TARGET_ID, buyData.mTargetID);
		param.setParam(FinalValue.PKQP_BIZ_NO, buyData.mAliTradeNo);
		param.setParam(FinalValue.PKQP_BIZ_TYPE, FinalValue.BIZ_TYPE);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
								// pay result state
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NQPAY_RESULT)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NQPAY_RESULT+" val:"+val);
									response.setResult(FinalValue.XML_NQPAY_RESULT, Utils.toURLDecoded(val));
								}
								// fund money
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NQPAY_FUND_MONEY)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NQPAY_FUND_MONEY+" val:"+val);
									response.setResult(FinalValue.XML_NQPAY_FUND_MONEY, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			if (!response.isRespOK(buyData.mMD5Key)) {
				Utils.LogD("有问题的数据！！");
				apiResult.mIsProcessOK = false;
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			} else {
				apiResult.mUserData = response.getResult(FinalValue.XML_NQPAY_RESULT);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	public static AliPayInterfaceResult cancelPay(BuyData buyData) {
		AliPayInterfaceResult apiResult = new AliPayInterfaceResult();
		
		// 组织发送数据
		AliPayRequest param = new AliPayRequest();
		param.clear();
		param.setParam(FinalValue.PKB_SERVEICE, FinalValue.S_CLOSE_TRADE);
		param.setParam(FinalValue.PKB_INPUT_CHARSET, FinalValue.CHARSET_UTF_8);
		param.setParam(FinalValue.PKB_PARTNERID, buyData.mPartnerID);
		
		// 一下2种二选一即可   这里使用支付宝的
		param.setParam(FinalValue.PKCLP_TRADE_NO, buyData.mAliTradeNo);
//		param.setParam(FinalValue.PKCLP_OUT_ORDER_NO, buyData.mTradeNo);
		
		// 发送请求
		InputStream inputStream = Utils.httpGet(param.makeUrlString(buyData.mMD5Key));
		
		// 返回有数据 解析
		if (inputStream != null) {
			// 用于记录所有有用的数据 方便后期分析
			AliPayResponse response = new AliPayResponse();
			// 
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(inputStream, FinalValue.CHARSET_UTF_8);
				int eventType = xmlPullParser.getEventType();
				
				// 用于标记是否请求参数 如果是请求参数的 直到结束前 不做任何处理
				boolean isRequest = false;

				// 解析直到文档结束
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:	// 元素开始
						{
							do {
								if (isRequest) {
									break;
								}
								
								String nodeName = xmlPullParser.getName();
								Utils.LogD("start node:"+nodeName);
								
								// 处理需要的节点
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
									isRequest = true;
									break;
								}
								
								// 是否成功
								if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_IS_SUC)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_IS_SUC+" val:"+val);
									if (val.equalsIgnoreCase(FinalValue.XML_IS_SUC_T)) {
										response.setIsSuc(true);
									} else {
										response.setIsSuc(false);
									}
								}
								// sign
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN+" val:"+val);
									response.setRespSign(Utils.toURLDecoded(val));
								}
								// sign_type
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_SIGN_TYPE)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_SIGN_TYPE+" val:"+val);
									response.setRespSignType(Utils.toURLDecoded(val));
								}
								// err
								else if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_ERROR)) {
									String val = xmlPullParser.nextText();
									Utils.LogD(FinalValue.XML_NB_ERROR+" val:"+val);
									response.setResult(FinalValue.XML_NB_ERROR, Utils.toURLDecoded(val));
								}
							} while (false);
						}
						break;
					case XmlPullParser.END_TAG:		// 元素结束
						{
							String nodeName = xmlPullParser.getName();
							Utils.LogD("end node:"+nodeName);
							
							// 处理需要的节点
							if (nodeName.equalsIgnoreCase(FinalValue.XML_NB_REQUEST)) {
								isRequest = false;	// request结束
//								continue;
							}
						}
						break;

					default:	// 其他的一律不处理
						break;
					}
					
					// 继续下一个元素
					eventType = xmlPullParser.next();
				}
				
			} catch (Exception e) {
				Utils.LogE("xmlPullParser error", e);
				return apiResult;	// 处理到此结束   或者给个标记
			}
			
			// 解析结束
			// 开始处理 判断是否需要处理的数据
			// 特殊处理 不确定
			if (!response.isSucced()) {
				if (!response.isRespOK(buyData.mMD5Key)) {
					Utils.LogD("有问题的数据！！");
					apiResult.mIsProcessOK = false;
					return apiResult;	// 处理到此结束   或者给个标记
				}
			}
			
			Utils.LogD("成功的返回数据，开始处理...");
			
			// 下载二维码 处理结束   显示的部分放在外面做
			apiResult.mIsProcessOK = true;
			apiResult.mIsResultOK = response.isSucced();
			if (!apiResult.mIsResultOK) {
				apiResult.mUserData = response.getResult(FinalValue.XML_NB_ERROR);
			}
			
			return apiResult;
		}
		
		return apiResult;
	}
	
	
}
