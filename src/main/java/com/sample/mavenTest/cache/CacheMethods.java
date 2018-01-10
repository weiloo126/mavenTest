package com.sample.mavenTest.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;



/**
 *缓存的增强类
 *@author yankang
 *@date 2015年8月12日
 */
@Component("cacheMethods")
public class CacheMethods implements MethodInterceptor{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static String TOTAL_TIME_DAY = "day";
	@Autowired
	private EnhancerRedis redis;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Cache annotation = method.getAnnotation(Cache.class);
		String keyPrefix = annotation.keyPrefix();
		String totalTime = annotation.totalTime();
		
		int expires = annotation.expires();
		if(!StringUtils.isEmpty(totalTime)){
			if(totalTime.equals(TOTAL_TIME_DAY)){
				expires = getNextDayTime();
				
			}
		}
		keyPrefix = keyPrefix.equals("") ? method.getDeclaringClass().getSimpleName() + "_" + method.getName() : keyPrefix;
		String redisKey = RedisMisc.getKeyByPrefix(keyPrefix, invocation.getArguments());
		//Sfinal Type returnType = method.getReturnType();
		final Type returnType = method.getGenericReturnType();
		String value = redis.get(redisKey);
		// 存在多个线程同时进入的风险， 但是不影响正确性
		if(value == null) {
			Object proceed = invocation.proceed();
			if(proceed == null) return null;// 如果计算结果null, 没有缓存的必要
			return RedisMisc.storeWithExpires(expires, redisKey, proceed, redis);
		} else {
			TypeReference<Object> reference = new TypeReference<Object>(){
				@Override
				public Type getType(){
					return returnType;
				}
			};
			Object result = JSON.parseObject(value, reference);
			return result;
		}
	}
	

	public EnhancerRedis getRedis() {
		return redis;
	}
	public void setRedis(EnhancerRedis redis) {
		this.redis = redis;
	}
	
	public static int getNextDayTime() {
		int expires;
		Calendar cal = Calendar.getInstance();
		Calendar calNew = Calendar.getInstance();
		
		cal.setTime(new Date());
		calNew.setTime(new Date());
		calNew.set(Calendar.HOUR_OF_DAY, 7);
		calNew.set(Calendar.MINUTE, 30);
		if(calNew.before(cal))
			calNew.add(Calendar.DATE, 1);
		expires = (int) ((calNew.getTimeInMillis() - cal.getTimeInMillis())/1000);
		return expires;
	}
	
}
