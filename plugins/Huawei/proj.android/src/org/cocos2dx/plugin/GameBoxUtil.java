package org.cocos2dx.plugin;

import java.util.HashMap;
import java.util.Map;

import org.cocos2dx.plugin.ReqTask.Delegate;

import android.app.Activity;

import com.android.huawei.pay.plugin.IHuaweiPay;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.MobileSecurePayHelper;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gamebox.buoy.sdk.util.DebugConfig;

/**
 * 公共类，提供加载插件和支付接口
 * 
 * @author h00193325
 * 
 */
public class GameBoxUtil
{
	
	public static final String GET_PAY_PRIVATE_KEY = "https://ip:port/HuaweiServerDemo/getPayPrivate";
    
    // 日志标签
    public static final String TAG = GameBoxUtil.class.getSimpleName();
    
    public static void pay(
        final Activity activity,
        final String price,
        final String productName,
        final String productDesc,
        final String requestId,
        final IPayHandler handler)
    {
        
        Map<String, String> params = new HashMap<String, String>();
        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
        params.put("userID", IAPHuawei.HW_payId);
        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
        params.put("applicationID", IAPHuawei.HW_appId);
        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
        params.put("amount", price);
        // 必填字段，不能为null或者""，道具名称
        params.put("productName", productName);
        // 必填字段，不能为null或者""，道具描述
        params.put("productDesc", productDesc);
        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
        params.put("requestId", requestId);
        
        String noSign = HuaweiPayUtil.getSignData(params);
        DebugConfig.d("startPay", "签名参数noSign：" + noSign);
        
        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
        String sign = Rsa.sign(noSign, IAPHuawei.PAY_RSA_PRIVATE);
        IAPHuawei.LogD("签名： " + sign);
        
        Map<String, Object> payInfo = new HashMap<String, Object>();
        // 必填字段，不能为null或者""
        payInfo.put("amount", price);
        // 必填字段，不能为null或者""
        payInfo.put("productName", productName);
        // 必填字段，不能为null或者""
        payInfo.put("requestId", requestId);
        // 必填字段，不能为null或者""
        payInfo.put("productDesc", productDesc);
        // 必填字段，不能为null或者""，请填写自己的公司名称
        payInfo.put("userName", "杭州边锋网络技术有限公司");
        // 必填字段，不能为null或者""
        payInfo.put("applicationID", IAPHuawei.HW_appId);
        // 必填字段，不能为null或者""
        payInfo.put("userID", IAPHuawei.HW_payId);
        payInfo.put("sign", sign);
        payInfo.put("notifyUrl", null);
        
        // 必填字段，不能为null或者""，此处写死X6
        payInfo.put("serviceCatalog", "X6");
        
        // 调试期可打开日志，发布时注释掉
        payInfo.put("showLog", true);
        
        // 设置支付界面横竖屏，默认竖屏
        payInfo.put("screentOrient", IAPHuawei.PAY_ORI);
        
        DebugConfig.d("startPay", "支付请求参数 : " + payInfo.toString());
        
        IHuaweiPay payHelper = new MobileSecurePayHelper();
        /**
         * 开始支付
         */
        payHelper.startPay(activity, payInfo, handler);
        
    }
    
    /**
     * 支付方法，实现参数签名与调起支付服务
     * 
     * @param activity
     * @param price
     * @param productName
     * @param productDesc
     * @param requestId
     * @param handler
     */
    public static void startPay(
        final Activity activity,
        final String price,
        final String productName,
        final String productDesc,
        final String requestId,
        final IPayHandler handler)
    {
        // 尚未获取支付的私钥，需要从服务端获取并保存到内存中
        if ("".equals(IAPHuawei.PAY_RSA_PRIVATE))
        {
            ReqTask getPayPrivate = new ReqTask(new Delegate()
            {
                
                public void execute(String privateKey)
                {
                    /** 
                     * 从服务端获取的支付私钥，由于没有部署最终的服务端，所以返回值写死一个值，CP需要从服务端获取，服务端代码参考华为Demo
                     * 请查阅 华为游戏中心SDK开发指导书.docx 的2.5节
                     */
                    privateKey = "";
                    IAPHuawei.PAY_RSA_PRIVATE = privateKey;
                    pay(activity, price, productName, productDesc, requestId, handler);
                }
            }, null, GET_PAY_PRIVATE_KEY);
            getPayPrivate.execute();
        }
        else
        // 如果已经获取过支付私钥，则直接调起支付
        {
            pay(activity, price, productName, productDesc, requestId, handler);
        }
        
    }
}
