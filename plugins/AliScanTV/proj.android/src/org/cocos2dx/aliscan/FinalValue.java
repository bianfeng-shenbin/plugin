package org.cocos2dx.aliscan;

public class FinalValue {
	// gateway
	public static final String ALI_GATEWAY = "https://mapi.alipay.com/gateway.do?";
	// service
	public static final String S_GET_QRCODE = "alipay.mobilesecurity.mobileauth.getqrcode";
	public static final String S_GET_AUTH_STATE = "alipay.mobilesecurity.mobileauth.getauthstate";
	public static final String S_GET_AUTH_ALIPAYUSER = "alipay.mobilesecurity.mobileauth.getauthalipayuser";
	public static final String S_REVOKE_AUTH = "alipay.mobilesecurity.mobileauth.revokeauth";
	public static final String S_CREATE_TRADE = "alipay.tvprod.mobileauth.createtrade";
	public static final String S_PAY = "alipay.tvprod.mobileauth.pay";
	public static final String S_QUERY_PAYRESULT = "alipay.tvprod.mobileauth.querypayresult";
	public static final String S_CLOSE_TRADE = "close_trade";
	// charset
	public static final String CHARSET_UTF_8 = "UTF-8";
	public static final String CHARSET_GBK = "GBK";
	public static final String CHARSET_GB2312 = "GB2312";
	// signType
	public static final String SIGN_TYPE_MD5 = "MD5";
	public static final String SIGN_TYPE_DSA = "DSA";
	public static final String SIGN_TYPE_RSA = "RSA";
	// target name
	public static final String TARGET_NAME = "边锋电视游戏";//"<边锋电 视游戏>";
	
	// target type
	public static final String TARGET_TYPE_TV = "TV";//"TV1";//
	public static final String TARGET_TYPE_PAD = "PAD";
	public static final String TARGET_TYPE_APP = "APP";
	// operate type
	public static final String OP_TYPE_AUTH = "AUTH";
	public static final String OP_TYPE_QRCODE = "QRCODE";
	// biz scene
	public static final String BIZ_SCENE_AUTH = "AUTH";
	public static final String BIZ_SCENE_AUTH_AND_PAY = "AUTH_AND_PAY";
	
	// royalty type
	public static final String ROYALTY_TYPE = "10";	// 目前只支持10
	// trade exp time
	public static final String DEF_IT_B_TYPE = "1m";	// 交易超时时间 30分钟
	
	// biz_type
	public static final String BIZ_TYPE = "trade";	// 目前只有trade
	
	// =======================param key=================================
	// base
	public static final String PKB_SERVEICE = "service";
	public static final String PKB_PARTNERID = "partner";
	public static final String PKB_INPUT_CHARSET = "_input_charset";
	public static final String PKB_SIGN_TYPE = "sign_type";	// 有部分接口需要sign_type参与签名
	public static final String PKB_SIGN = "sign";
	
	// requestQRCode
	public static final String PKQR_TARGET_ID = "target_id";
	public static final String PKQR_TARGET_NAME = "target_name";
	public static final String PKQR_TARGET_LOGO_URL = "target_logo_url";
	public static final String PKQR_TARGET_TYPE = "target_type";
	// ----can be empty---
	public static final String PKQR_TARGET_INFO = "target_info";
	public static final String PKQR_OP_TYPE = "operate_type";
	public static final String PKQR_USER_ID = "user_id";
	public static final String PKQR_EXP_DATE = "expire_date";
	public static final String PKQR_BIZ_SCENE = "biz_scene";
	public static final String PKQR_OUT_BIZ_NO = "out_biz_no";
	public static final String PKQR_EX_PARAM = "extend _param";
	
	// checkIsDeviceBinded
	public static final String PKCB_TARGET_ID = "target_id";
	
	// getBindedUser
	public static final String PKBU_TARGET_ID = "target_id";
	
	// unbindDevice
	public static final String PKUB_TARGET_ID = "target_id";
	
	// createPayProduct
	public static final String PKCP_NOTIFY_URL = "notify_url";
	public static final String PKCP_TARGET_ID = "target_id";
	public static final String PKCP_SUBJECT = "subject";
	public static final String PKCP_AMOUNT = "amount";
	public static final String PKCP_OUT_TRADE_NO = "out_trade_no";
	public static final String PKCP_SELLER = "seller";
	// ----can be empty---
	public static final String PKCP_BODY = "body";
	public static final String PKCP_ROYALTY_TYPE = "royalty_type";
	public static final String PKCP_ROYALTY_PARAMS = "royalty_parameters";
	public static final String PKCP_EX_PARAM = "extend_param";
	public static final String PKCP_IT_B_TYPE = "it_b_pay";
	
	// requestPayProduct
	public static final String PKPP_NOTIFY_URL = "notify_url";
	public static final String PKPP_TARGET_ID = "target_id";
	public static final String PKPP_BIZ_NO = "biz_no";
	public static final String PKPP_BIZ_TYPE = "biz_type";
	// ----can be empty---
	public static final String PKPP_BIZ_SCENE = "biz_scene";
	public static final String PKPP_EX_PARAM = "extend_param";
	
	// queryPayResult
	public static final String PKQP_TARGET_ID = "target_id";
	public static final String PKQP_BIZ_NO = "biz_no";
	public static final String PKQP_BIZ_TYPE = "biz_type";
	
	// cancelPay
	// 以下2选1可空
	public static final String PKCLP_TRADE_NO = "trade_no";
	public static final String PKCLP_OUT_ORDER_NO = "out_order_no";
	// ----can be empty---
	public static final String PKCLP_IP = "ip";
	public static final String PKCLP_TRADE_ROLE = "trade_role";

	// =======================param key=================================
	
	// =======================xml node key=================================
	public static final String XML_IS_SUC_T = "T";
	public static final String XML_IS_SUC_F = "F";
	
	public static final String XML_AUTH_STATE_INIT = "INIT";	// 初始 新设备
	public static final String XML_AUTH_STATE_WAIT = "WAIT_USER_CONFIRM";	// 等待用户授权
	public static final String XML_AUTH_STATE_COMPLATE = "COMPLATE_AUTH";	// 授权结束
	public static final String XML_AUTH_STATE_CANCEL = "CANCLE_AUTH";	// 取消授权
	public static final String XML_AUTH_STATE_EXP = "EXPIRE_AUTH";	// 授权过期
	
	// error
	public static final String XML_AUTH_STATE_ERR_NOT_AUTHED = "NOT_AUTHED";// 无授权记录。 
	public static final String XML_AUTH_STATE_ERR_INVALID_AUTH = "INVALID_AUTH";// 授权没有完成或者被取消 
	public static final String XML_AUTH_STATE_ERR_EXPIRE_AUTH = "EXPIRE_AUTH";// 授权已经过期 
	public static final String XML_AUTH_STATE_ERR_USER_NOT_EXISTS = "USER_NOT_EXISTS";// 用户不存在

	//
//	1.支付成功--PAYMENT_SUCCESS：付款成功。
//	2.推送到手机钱包或者短信确认上
//	--PAYMENT_APPLIED：支付申请成功
//	             （此类情况比较特殊属于支付应用申请支付成功后没来的及查询一次结果窗口就退出了）
//	          --WAIT_USER_CONFIRM：等待用户钱包确认付款。
//	           --WAIT_BUYER_PAY ： 等待用户付款
//		             --WAIT_USER_MO：等待用户上行短信确认付款。
//	3.支付失败及支付错误--PAYMENT_FAIL：付款失败
//		                   --BUYER_ACCOUNT_BLOCK：买家账号被冻结
//		                   --BIZ_CLOSED：业务超时关闭
//	--ERROR：支付过程中出现错误或者中途放弃支付 
//	其他结果详见支付宝接口文档查询支付结果接口错误码 
//	对于有一些支付失败的结果如BUYER_NOT_BIND_MOBILE（买家没有绑定手机）这时候支付宝可能已将账单推送到支付宝账户上去了
	public static final String XML_PAY_RET_PAYMENT_APPLIED = "PAYMENT_APPLIED";// 支付申请成功 。
	public static final String XML_PAY_RET_WAIT_USER_CONFIRM = "WAIT_USER_CONFIRM";//等待用户钱包确认付款。
	public static final String XML_PAY_RET_WAIT_USER_MO = "WAIT_USER_MO";//等待用户上行短信确认付款。 
	public static final String XML_PAY_RET_PAYMENT_SUCCESS = "PAYMENT_SUCCESS";// 付款成功。 
	public static final String XML_PAY_RET_PAYMENT_FAIL = "PAYMENT_FAIL";//付款失败。
	public static final String XML_PAY_RET_BUYER_ACCOUNT_BLOCK = "BUYER_ACCOUNT_BLOCK";//买家账号被冻结
	
	//WAIT_BUYER_PAY：等待用户付款 
	//PAYMENT_SUCCESS：支付已经成功
	//BIZ_CLOSED：业务超时关闭
	public static final String XML_PAY_STATE_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";//等待用户付款 
	public static final String XML_PAY_STATE_PAYMENT_SUCCESS = "PAYMENT_SUCCESS";//支付已经成功
	public static final String XML_PAY_STATE_BIZ_CLOSED = "BIZ_CLOSED";//业务超时关闭 
	
	public static final String XML_NB_REQUEST = "request";
	public static final String XML_NB_RESPONSE = "response";
	
	public static final String XML_NB_IS_SUC = "is_success";
	public static final String XML_NB_ERROR = "error";
	public static final String XML_NB_SIGN = "sign";
	public static final String XML_NB_SIGN_TYPE = "sign_type";
	
	public static final String XML_NQR_QRCODE = "qrcode_url";
	
	public static final String XML_NAUTH_STATE = "auth_state";
	public static final String XML_NAUTH_ID = "a_user_id";
	
	public static final String XML_NUSER_ID = "auth_alipay_user_id";
	public static final String XML_NUSER_LOGIN_ID = "auth_alipay_user_logon_id";
	
	public static final String XML_NCREATE_TRADE_NO = "trade_no";
	
	public static final String XML_NPAY_RESULT = "result_code";
	public static final String XML_NPAY_TYPE = "pay_type";
	public static final String XML_NPAY_MOBILE = "mobile";
	public static final String XML_NPAY_FUND_MONEY = "fund_money";
	
	public static final String XML_NQPAY_RESULT = "pay_result_status";
	public static final String XML_NQPAY_FUND_MONEY = "fund_money";
	// =======================xml node key=================================
}
