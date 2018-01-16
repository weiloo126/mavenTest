package com.sample.mavenTest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SignUtil {

	private static Logger log = LoggerFactory.getLogger(SignUtil.class);

	public static String getMD5Sign(Map<String, String> paramMap,String secret){
		
		StringBuffer sb = new StringBuffer(getParamString(paramMap));
	    sb.append(secret);
	    String  sign = null;
	    try {
			sign = SignUtil.md5StrEncode(sb.toString());
		}catch(Exception e) {
			log.error("calc sign error,string to sign:" + sb.toString(),e);
		}
	    return sign;
	}
	
	public static String getParamString(Map<String, String> paramMap){
		Collection<String> keyset= paramMap.keySet();  
	    List<String> list = new ArrayList<String>(keyset);  
	       
	    //对key键值按字典升序排序  
	    Collections.sort(list);  
	    StringBuffer sb = new StringBuffer();
	    boolean isFirst = true;
	    for(String key:list){
	    	if(isFirst){isFirst = false;}
	    	else {sb.append("&");}
			sb.append(key + "=" +paramMap.get(key));

	    }
	    return sb.toString();
	}

	/**
	 * 用MD5算法进行加密
	 * @param {String} str 需要加密的字符串
	 * @return {String} MD5加密后的结果
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5StrEncode(String str) {
		return md5StrEncode(str, "utf-8");
	}
	/**
     * 用MD5算法进行加密 
     * @param {String} str 需要加密的字符串 
     * @return {String} MD5加密后的结果 
	 * @throws NoSuchAlgorithmException 
	 */
    public static String md5StrEncode(String str, String encoding) {
		// 操作字符串
		StringBuffer sb = new StringBuffer();
		try {
			// MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 添加要进行计算摘要的信息,使用 text 的 byte 数组更新摘要。
			md.update(str.getBytes(encoding));
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					sb.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				sb.append(Integer.toHexString(i));
			}
		} catch (Exception e) {
			log.error("calc md5 error,strs:" + str, e);
		}
		return sb.toString();
	}

	/**
	 * 用MD5算法进行加密
	 * @param {String} str 需要加密的字符串
	 * @return {String} MD5加密后的结果
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5FromFile(File file) {
		// 操作字符串
		StringBuffer sb = new StringBuffer();
		try {
			InputStream inputStream = new FileInputStream(file);
			// MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while((numRead = inputStream.read(buffer)) > 0){
				// 添加要进行计算摘要的信息,使用 text 的 byte 数组更新摘要。
				md.update(buffer, 0, numRead);
			}
			inputStream.close();
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					sb.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				sb.append(Integer.toHexString(i));
			}
		} catch (Exception e) {
			log.error("calc md5 error,file:" + file.getAbsolutePath(), e);
		}

		return sb.toString();
	}

	public static void main(String[] args){
		System.out.println("md5:" + md5FromFile(new File("C:\\Users\\Administrator\\Desktop\\cao.jpg")));
	}
}
