package org.basecode.common.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密是有长度限制的.单纯用RSA加密较长数据时得使用分段加密,效率低下
 * 用RSA+AES是比较主流的做法
 * 后续考虑改进
 */
public class RSAUtil {

	/**
	 * 密钥长度 于原文长度对应 以及越长速度越慢
	 */
	private final static int KEY_SIZE = 1024;
	private static final int MAX_ENCRYPT_BLOCK = 117;

	//RSA最大解密密文大小
	private static final int MAX_DECRYPT_BLOCK = KEY_SIZE / 8;

	/**
	 * 用于封装随机产生的公钥与私钥
	 */
	private static Map<String, String> keyMap = new HashMap<String, String>();


	public static final String PUBLIC_KEY = "RSAPublicKey";
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	
	/**
	 * 生成RSA的公钥和私钥
	 */
	public static void genKeyPair() throws Exception{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(KEY_SIZE,new SecureRandom());  //512-65536 & 64的倍数
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 得到公钥字符串
		String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		// 得到私钥字符串
		String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		keyMap.put(PUBLIC_KEY, publicKeyString);
		keyMap.put(PRIVATE_KEY, privateKeyString);
	}


	/**
	 * RSA公钥加密
	 *
	 * @param str       加密字符串
	 * @param publicKey 公钥
	 * @return 密文
	 * @throws Exception 加密过程中的异常信息
	 */
	public static String encrypt(String str, String publicKey) throws Exception {
		//base64编码的公钥
		byte[] decoded = Base64.getDecoder().decode(publicKey.replace("\r\n", ""));
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] data = str.getBytes("UTF-8");
		byte[] enBytes = null;
		for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
			// 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
			byte[] doFinal = cipher.doFinal(org.apache.commons.lang3.ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
			enBytes = org.apache.commons.lang3.ArrayUtils.addAll(enBytes, doFinal);
		}
		String outStr = Base64.getEncoder().encodeToString(enBytes);
		return outStr;
	}
	/**
	 * RSA私钥解密
	 *
	 * @param str        加密字符串
	 * @param privateKey 私钥
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public static String decrypt(String str, String privateKey) throws Exception {
		//64位解码加密后的字符串
		byte[] data = Base64.getDecoder().decode(str);
		//base64编码的私钥
		byte[] decoded = Base64.getDecoder().decode(privateKey.replace("\r\n", ""));
		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		// 解密时超过字节报错。为此采用分段解密的办法来解密
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
			byte[] doFinal = cipher.doFinal(org.apache.commons.lang3.ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
			sb.append(new String(doFinal));
		}
		String outStr = sb.toString();
		return outStr;
	}

	public static void main(String[] args) throws Exception {
		long temp = System.currentTimeMillis();
		//生成公钥和私钥
		genKeyPair();
		//加密字符串
		System.out.println("公钥:" + keyMap.get(PUBLIC_KEY));
		System.out.println("私钥:" + keyMap.get(PRIVATE_KEY));
		System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
		String message = "RSA测试ABCD~!@#$";
		System.out.println("原文:" + message);
		temp = System.currentTimeMillis();
		String messageEn = encrypt(message, keyMap.get(PUBLIC_KEY));
		System.out.println("密文:" + messageEn);
		System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
		temp = System.currentTimeMillis();
		String messageDe = decrypt(messageEn, keyMap.get(PRIVATE_KEY));
		System.out.println("解密:" + messageDe);
		System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
	}
}
