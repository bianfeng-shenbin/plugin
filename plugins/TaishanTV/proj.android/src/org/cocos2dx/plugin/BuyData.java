package org.cocos2dx.plugin;

public class BuyData {
	public static String userId;		// 用户ID
	public static String payTime;		// 收到支付结果的时间
	public static String gameId;		// 游戏ID
	public static String productId;		// 道具ID
	public static String productPrice;	// 道具价格  单位：分
	public static String notifyUrl;		// 回调地址[不传]
	public static String orderId;		// 订单号
	public static String payResult;		// 支付结果  0:成功  1:失败
	public static String gameKey;		// 签名key[不传]
	public static String sign;			// 签名串
}
