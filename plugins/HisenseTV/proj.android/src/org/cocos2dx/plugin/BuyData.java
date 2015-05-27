package org.cocos2dx.plugin;

public class BuyData {		
//	action--"com.hisense.hitv.payment.MAIN"
//	appName--应用名称如“聚好看”
//	packageName--包名如"com.fs.am"
//	paymentMD5Key—签名应用开发时海信会分配给第三方一个固定的md5的key值用此key对应用包名进行签名，如果海信支付验签没通过则此应用不能支付
//	   tradeNum—商品单号（ 由商户应用自己定）
//	   goodsPrice—商品价格单位元
//	   goodsName—商品名称如“小马过河”//商品名限制为汉字数字英文字母
//	   alipayUserAmount—商户收款的支付宝账户
//	   notifyUrl—商户后台的回调地址
	public static String appName;
	public static String packageName;
	public static String paymentMD5Key;
	public static String tradeNum;
	public static String goodsPrice;
	public static String goodsName;
	public static String alipayUserAmount;
	public static String notifyUrl;
}
