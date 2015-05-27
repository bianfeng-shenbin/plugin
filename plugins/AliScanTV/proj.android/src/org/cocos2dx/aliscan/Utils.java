package org.cocos2dx.aliscan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class Utils {
	
	private static final int BYTE_ARRAY_LENTH = 2048;
	
	private static String mLogTag = "DebugUtils";
	private static boolean mIsDebug = false;
	
	public static void LogE(String msg, Exception e){
		Log.e(mLogTag, msg, e);
		e.printStackTrace();
	}
	
	public static void LogD(String msg){
		if(mIsDebug){
			Log.d(mLogTag, msg);
		}
	}
	
	public static void SetDebugMode(boolean isDebug){
		mIsDebug = isDebug;
	}
	
	public static String toUTF_8(String paramString) {
		if (paramString == null || paramString.equals("")) {
			LogD("toUTF_8 error:"+paramString);
			return "";
		}
		
		try
		{
			String str = new String(paramString.getBytes(), "UTF-8");
			return str;
		}
		catch (Exception localException)
		{
			LogE("toUTF_8 error:"+paramString, localException);
		}
		
		return "";
	}
	
	public static String toURLEncoded(String paramString) {
		if (paramString == null || paramString.equals("")) {
			LogD("toURLEncoded error:"+paramString);
			return "";
		}
		
		try
		{
			String str = new String(paramString.getBytes(), "UTF-8");
			str = URLEncoder.encode(str, "UTF-8");
			return str;
		}
		catch (Exception localException)
		{
			LogE("toURLEncoded error:"+paramString, localException);
		}
		
		return "";
	}
	
	public static String toURLDecoded(String paramString) {
		if (paramString == null || paramString.equals("")) {
			LogD("toURLDecoded error:"+paramString);
			return "";
		}
		
		try
		{
			String str = new String(paramString.getBytes(), "UTF-8");
			str = URLDecoder.decode(str, "UTF-8");
			return str;
		}
		catch (Exception localException)
		{
			LogE("toURLDecoded error:"+paramString, localException);
		}
		
		return "";
	}
	
	// make sign string
	public static String makeSignString(String unsignedString) {
		// sign
		String signedString = MD5Signature.md5(unsignedString);
		//debug
		Utils.LogD("before MD5:"+unsignedString);
		Utils.LogD("after MD5:"+signedString);
		
		return signedString;
	}
	
	// make unsigned string(not include md5 key)
	public static String makeUnsignedString(Map<String, String> map) {
		ArrayList<String> stringArray = new ArrayList<String>();
		// get all key and val
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			// 注意转换到utf8
			stringArray.add(key + "=" + Utils.toUTF_8(map.get(key)));
		}
		// sort
		String[] strings = new String[stringArray.size()];
		stringArray.toArray(strings);
		Arrays.sort(strings);
		// make string to sign
		String unsignedString = "";
		for (int i = 0; i < strings.length; i++) {
			unsignedString += strings[i];
			unsignedString += "&";
		}
		
		// 去掉最后一个&
		return unsignedString.substring(0, unsignedString.length()-1);
	}
	
	public static String inputStream2String(InputStream in) throws Exception {
		return new String(inputStream2ByteArray(in));
	}
	
	public static byte[] inputStream2ByteArray(InputStream in) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[BYTE_ARRAY_LENTH];
		int len = -1;
		while ((len = in.read(buf)) != -1) {
			bos.write(buf, 0, len);
		}
		
		return bos.toByteArray();
	}
	
	public static InputStream httpGetImage(String urlString) {
		InputStream inputStream = null;
		try {
			HttpClient client = new DefaultHttpClient();
	        HttpGet get = new HttpGet(urlString);
	        HttpResponse response = client.execute(get);
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            // 1次获取is
	            inputStream = response.getEntity().getContent();
	            return inputStream;
	            // 
	        }
		} catch (Exception e) {
			Utils.LogE("httpGet Error", e);
		}
		
		return null;
    }
	
	public static InputStream httpGet(String urlString) {
		InputStream inputStream = null;
		try {
			HttpClient client = new DefaultHttpClient();
	        HttpGet get = new HttpGet(urlString);
	        HttpResponse response = client.execute(get);
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	        	// 2次转换 主要是debug方便  如果不需要debug 那么可以直接一次获取is
	        	InputStream is = response.getEntity().getContent();
	            String result = null;
	            result = inputStream2String(is);
	            
	            Utils.LogD(result);
	            
	            if (result != null) {
	            	inputStream = new ByteArrayInputStream(result.getBytes(FinalValue.CHARSET_UTF_8));
	            	return inputStream;
				}
	            // 
	            
	            // 1次获取is
//	            inputStream = response.getEntity().getContent();
//	            return inputStream;
	            // 
	        }
		} catch (Exception e) {
			Utils.LogE("httpGet Error", e);
		}
		
		return null;
	}
}
