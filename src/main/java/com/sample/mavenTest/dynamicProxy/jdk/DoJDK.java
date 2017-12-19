package com.sample.mavenTest.dynamicProxy.jdk;

/**
 * 
 * @date 2017年12月19日
 */
public class DoJDK {
	public static void main(String[] args) {  
        JdkProxy proxy = new JdkProxy();  
        ISayHello sayHelloProxy = (ISayHello) proxy.bind(new SayHelloImpl());  
        sayHelloProxy.say();  
    }  
}
