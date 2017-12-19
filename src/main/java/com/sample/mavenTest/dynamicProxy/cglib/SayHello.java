package com.sample.mavenTest.dynamicProxy.cglib;

/**
 *  需要被代理的类，也就是父类，通过字节码技术创建这个类的子类，实现动态代理。
 * @date 2017年12月19日
 */
public class SayHello {
	public void say(){  
		System.out.println("hello cglib dynamic proxy!");  
	}  
}
