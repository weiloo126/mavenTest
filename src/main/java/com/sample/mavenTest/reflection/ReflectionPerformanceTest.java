package com.sample.mavenTest.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 反射性能测试
 * 
 * Method/Constructor/Field/Element都继承了AccessibleObject, AccessibleObject类中有一个setAccessible方法，该方法有两个作用:
 * 1. 启用/禁用访问安全检查开关：值为true，则指示反射的对象在使用时取消Java语言访问检查；值为false，则指示应该实施Java语言的访问检查;
 * 2. 可以禁止安全检查, 提高反射的运行效率.
 * 
 * 在实际开发中,其实是不用担心反射机制带来的性能消耗,而且禁用访问权限检查,也会有性能的提升.
 * 
 * @date 2017年12月15日
 */
public class ReflectionPerformanceTest {	

	public Map<Integer, String> test() {
        return null;
    }
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		testNoneReflect();
		testNotAccessible();
		testUseAccessible();
	}

    public static void testNoneReflect() {
    	ReflectionPerformanceTest obj = new ReflectionPerformanceTest();
 
        long start = System.currentTimeMillis();
        for (long i = 0; i < Integer.MAX_VALUE; ++i) {
        	obj.test();
        }
        long count = System.currentTimeMillis() - start;
        System.out.println("没有反射, 共消耗 <" + count + "> 毫秒");
    }
 
    public static void testNotAccessible() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    	ReflectionPerformanceTest obj = new ReflectionPerformanceTest();
        Method method = ReflectionPerformanceTest.class.getMethod("test");
 
        long start = System.currentTimeMillis();
        for (long i = 0; i < Integer.MAX_VALUE; ++i) {
            method.invoke(obj, null);
        }
        long count = System.currentTimeMillis() - start;
        System.out.println("有访问安全检查（没有访问权限）, 共消耗 <" + count + "> 毫秒");
    }
 
    public static void testUseAccessible() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    	ReflectionPerformanceTest obj = new ReflectionPerformanceTest();
        Method method = ReflectionPerformanceTest.class.getMethod("test");
        method.setAccessible(true);
 
        long start = System.currentTimeMillis();
        for (long i = 0; i < Integer.MAX_VALUE; ++i) {
            method.invoke(obj, null);
        }
        long count = System.currentTimeMillis() - start;
        System.out.println("没有访问安全检查（有访问权限）, 共消耗 <" + count + "> 毫秒");
    }
}
