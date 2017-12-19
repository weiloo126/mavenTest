package com.sample.mavenTest.dynamicProxy.jdk;

/**
 * 
 * @date 2017年12月19日
 */
public class SayHelloImpl implements ISayHello {

	@Override
	public void say() {
		System.out.println("hello jdk dynamic proxy!");  
	}

}
