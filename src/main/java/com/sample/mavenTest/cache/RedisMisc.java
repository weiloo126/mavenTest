package com.sample.mavenTest.cache;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 *
 *@author yankang
 *@date 2015年10月21日
 */
public class RedisMisc {
	private final static Logger log = LoggerFactory.getLogger(RedisMisc.class);
	
	private final static String cachePrefix = "mavenTest.sample.com";
	
	public final static int DAY_SECONDS = 24 * 60 * 60;
	public final static int THREE_DAY_SECONDS = 3 * DAY_SECONDS;
	public final static int WEEK_SECONDS = 7 * DAY_SECONDS;
	public final static int MONTH_SECONDS = 30 * DAY_SECONDS;
	
	public static String getKey(Object...args) {
		String result = "";
		for(Object obj : args) {
			result += ("#"+ obj);
		}
		return cachePrefix + result;
	}
	
	/**
	 * 手动存json串到 redis
	 * @param expires
	 * @param redisKey
	 * @param proceed
	 * @return
	 * @throws IOException
	 */
	public static Object storeWithExpires(int expires, String redisKey, Object proceed, EnhancerRedis redis)
			throws IOException {
		String str = JSON.toJSONString(proceed);
		redis.setex(redisKey, expires, str);
		log.info("store data into redis with key = " + redisKey);
		return proceed;
	}

	public static String getKeyByPrefix(String keyPrefix, Object[] arguments) {
		String result = "";
		for(Object obj : arguments) {
			result += ("_"+ obj);
		}
		return  keyPrefix + result;
	}
}
