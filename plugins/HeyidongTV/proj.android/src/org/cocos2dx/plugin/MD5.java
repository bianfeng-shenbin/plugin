package org.cocos2dx.plugin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

//	public static String md5sum(String filename) {
//		InputStream fis;
//		byte[] buffer = new byte[1024];
//		int numRead = 0;
//		MessageDigest md5;
//		try {
//			fis = new FileInputStream(filename);
//			md5 = MessageDigest.getInstance("MD5");
//			while ((numRead = fis.read(buffer)) > 0) {
//				md5.update(buffer, 0, numRead);
//			}
//			fis.close();
//			return toHexString(md5.digest());
//		} catch (Exception e) {
//			System.out.println("error");
//			return null;
//		}
//	}

	public static String md5sum(String string) {
		try {
			// Create MD5 Hash
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(string.getBytes());
			byte messageDigestByteArray[] = messageDigest.digest();
			if (messageDigestByteArray == null
					|| messageDigestByteArray.length == 0) {
				return "";
			}

			// Create hexadecimal String
			StringBuffer hexadecimalStringBuffer = new StringBuffer();
			int length = messageDigestByteArray.length;
			for (int i = 0; i < length; i++) {
				
				if ((0xFF & messageDigestByteArray[i]) < 0x10) hexadecimalStringBuffer.append("0");
				hexadecimalStringBuffer.append(Integer
						.toHexString(0xFF & messageDigestByteArray[i]));
			}
			return hexadecimalStringBuffer.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String md5(String string) {
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
}
