package com.sample.mavenTest.dynamicProxy.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * JDK实现动态代理需要实现类通过接口定义业务方法，对于没有接口的类，如何实现动态代理呢，这就需要CGLib了。
 * CGLib采用了非常底层的字节码技术，其原理是通过字节码技术为一个类创建子类，并在子类中采用方法拦截的技术拦截所有父类方法的调用，顺势织入横切逻辑。
 * JDK动态代理与CGLib动态代理均是实现Spring AOP的基础。
 * 
 * 该类实现了创建子类的方法与代理的方法。
 * getProxy(SuperClass.class)方法通过入参即父类的字节码，通过扩展父类的class来创建代理对象。
 * intercept()方法拦截所有目标类方法的调用，obj表示目标类的实例，method为目标类方法的反射对象，args为方法的动态入参，proxy为代理类实例。
 * proxy.invokeSuper(obj, args)通过代理类调用父类中的方法。
 * 
 * CGLib创建的动态代理对象性能比JDK创建的动态代理对象的性能高不少，但是CGLib在创建代理对象时所花费的时间却比JDK多得多，
 * 所以对于单例的对象，因为无需频繁创建对象，用CGLib合适，反之，使用JDK方式要更为合适一些。
 * 同时，由于CGLib由于是采用动态创建子类的方法，对于final方法，无法进行代理。
 * 
 * @date 2017年12月19日
 */
public class CglibProxy implements MethodInterceptor {
	
	private Enhancer enhancer = new Enhancer();  
	
	public Object getProxy(Class<?> clazz){  
		// 设置需要创建子类的类  
		enhancer.setSuperclass(clazz);  
		enhancer.setCallback(this);  
		// 通过字节码技术动态创建子类实例  
		return enhancer.create();  
	}  
	 
	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		System.out.println("前置代理");  
		// 通过代理类调用父类中的方法  
		Object result = proxy.invokeSuper(obj, args);  
		System.out.println("后置代理");  
		return result;  
	}

}
