package org.basecode.common.util;

import com.alibaba.fastjson.JSON;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES算法编程实现
 *
 */
public class AESUtil {

	/**
	 * 生成密钥
	 * @throws Exception 
	 */
	public static String initKey() throws Exception{
		//密钥生成器
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		//初始化密钥生成器
		keyGen.init(128);  //默认128，获得无政策权限后可用192或256
		//生成密钥
		SecretKey secretKey = keyGen.generateKey();
		// 得到公钥字符串
		String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		return keyString;
	}
	
	/**
	 * 加密
	 * @throws Exception 
	 */
	public static String encrypt(String data, String key) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.getDecoder().decode(key.replace("\r\n", ""));
		//恢复密钥
		SecretKey secretKey = new SecretKeySpec(decoded, "AES");
		//Cipher完成加密
		Cipher cipher = Cipher.getInstance("AES");
		//根据密钥对cipher进行初始化
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		//加密
		byte[] encrypt = cipher.doFinal(data.getBytes("UTF-8"));
		
		return Base64.getEncoder().encodeToString(encrypt);
	}
	/**
	 * 解密
	 */
	public static String decrypt(String data, String key) throws Exception{
		//64位解码加密后的字符串
		byte[] deData = Base64.getDecoder().decode(data);
		byte[] decoded = Base64.getDecoder().decode(key.replace("\r\n", ""));
		//恢复密钥生成器
		SecretKey secretKey = new SecretKeySpec(decoded, "AES");
		//Cipher完成解密
		Cipher cipher = Cipher.getInstance("AES");
		//根据密钥对cipher进行初始化
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] plain = cipher.doFinal(deData);
		return new String(plain);
	}

	public static void main(String[] args) throws Exception {
		long temp = System.currentTimeMillis();
		//生成公钥
		String key = initKey();
		//加密字符串
		System.out.println("公钥:" + key);
		System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");

		String message = JSON.toJSONString(New.hashMap("clientId", "sys_zyun_message", "clientSecret", "admin123", "channelType",1));
		System.out.println("原文:" + message);
		temp = System.currentTimeMillis();
		String messageEn = encrypt(message, key);
		System.out.println("密文:" + messageEn);
		System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");

		temp = System.currentTimeMillis();
		String messageDe = decrypt(messageEn, key);
		System.out.println("解密:" + messageDe);
		System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
	}
}
