package com.sample.mavenTest.dynamicProxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK的动态代理机制只能代理实现了接口的类，而不能实现接口的类就不能实现JDK的动态代理，cglib是针对类来实现代理的，他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，所以不能对final修饰的类进行代理。 
 * java.lang.reflect 包中的Proxy类和InvocationHandler 接口提供了生成动态代理类的能力。 
 * 
 * @date 2017年12月19日
 */
public class JdkProxy implements InvocationHandler {
	
	private Object target;  
	
    /** 
     * Proxy类是专门完成代理的操作类，可以通过此类为一个或多个接口动态地生成实现类
     * 绑定委托对象并返回一个代理类 
     * @param target 
     * @return 
     */  
    public Object bind(Object target) {  
        this.target = target;  
        /*
         * 取得代理对象  
         * ClassLoader loader：类加载器 
         * Class<?>[] interfaces：得到全部的接口 
         * InvocationHandler h：得到InvocationHandler接口的子类实例 
         */
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),  
                target.getClass().getInterfaces(), this);   //要绑定接口(这是一个缺陷，cglib弥补了这一缺陷)  
    }  
  
    /**
     * 代理方法
     * proxy：指被代理的对象。 
     * method：要调用的方法 
     * args：方法调用时所需要的参数
     */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result=null;  
        System.out.println("start");  
        //执行方法  
        result = method.invoke(target, args);  
        System.out.println("end");  
        return result;  
	}

}
