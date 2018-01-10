package com.sample.mavenTest.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *@author yankang
 *@date 2015年8月12日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
	String keyPrefix()  default ""; // key 为这个值 加上 函数参数， 显示的写出key 方便管理
	int expires() default 0; // 缓存超时时间
	Class<?> typeRef() default Void.class;
	String totalTime() default ""; // key day为一天
}
