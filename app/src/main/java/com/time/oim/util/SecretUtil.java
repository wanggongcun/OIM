package com.time.oim.util;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SecretUtil {
	
	public static String RSAencrypt(String data){
		try {
			PublicKey publicKey = RSAUtils.loadPublicKey(RSAUtils.PUBLIC_KEY);
			// 从文件中得到公钥
//				InputStream inPublic = getResources().getAssets().open(rsa_public_key.pem);
//				PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
			// 加密
			byte[] encryptByte = RSAUtils.encryptData(data.getBytes(), publicKey);
			// 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
			String afterencrypt = Base64Utils.encode(encryptByte);
			return afterencrypt;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String RSAdecrypt(String data){
		
		try {
			 PrivateKey privateKey = RSAUtils.loadPrivateKey(RSAUtils.PRIVATE_KEY);
			// 从文件中得到私钥
//				InputStream inPrivate = getResources().getAssets().open(pkcs8_rsa_private_key.pem);
//				PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
			// 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
			byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(data), privateKey);
			String decryptStr = new String(decryptByte);
			return decryptStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String AESencrypt(String data){
		try {
			String encryptingCode = AESUtils.encrypt("oim",  data);
			return encryptingCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String AESdecrypt(String data){
		try {
			String decryptingCode = AESUtils.decrypt("oim", data);
			return decryptingCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
	}
}
