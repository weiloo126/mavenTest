package com.sample.mavenTest.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射(Reflection)是Java 程序开发语言的特征之一，它允许运行中的 Java 程序获取自身的信息，并且可以操作类或对象的内部属性。
 * 通过反射，我们可以在运行时获得程序或程序集中每一个类型的成员和成员的信息。
 * 程序中一般的对象的类型都是在编译期就确定下来的，而Java反射机制可以动态地创建对象并调用其属性，这样的对象的类型在编译期是未知的。所以我们可以通过反射机制直接创建对象，即使这个对象的类型在编译期是未知的。
 * 反射的核心是JVM在运行时才动态加载类或调用方法/访问属性，它不需要事先（写代码的时候或编译期）知道运行对象是谁。
 * 
 * Java反射框架主要提供以下功能：
 * 1.在运行时判断任意一个对象所属的类；
 * 2.在运行时构造任意一个类的对象；
 * 3.在运行时判断任意一个类所具有的成员变量和方法（通过反射甚至可以调用private方法）；
 * 4.在运行时调用任意一个对象的方法
 * 
 * 反射的主要用途:
 * 当我们在使用IDE(如Eclipse，IDEA)时，当我们输入一个对象或类并想调用它的属性或方法时，一按点号，编译器就会自动列出它的属性或方法，这里就会用到反射。
 * 反射最重要的用途就是开发各种通用框架。很多框架（比如Spring）都是配置化的（比如通过XML文件配置JavaBean,Action之类的），为了保证框架的通用性，它们可能需要根据配置文件加载不同的对象或类，调用不同的方法，这个时候就必须用到反射——运行时动态加载需要加载的对象。
 * 
 * 由于反射会额外消耗一定的系统资源，因此如果不需要动态地创建一个对象，那么就不需要用反射。
 * 另外，反射调用方法时可以忽略权限检查，因此可能会破坏封装性而导致安全问题。
 * 
 * @date 2017年12月15日
 */
public class ReflectionBaseTest {
	
	public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// three methods of getting Class obj
		Class<?> clazz = Class.forName("java.lang.Integer");
		Class<?> clazz1 = int.class; 
		System.out.println(clazz1);
		Class<?> clazz2 = Integer.TYPE;

		// two methods of judging whether is instance of some class
		Integer i = 1; 
		System.out.println(i instanceof Integer);
		System.out.println(Integer.class.isInstance(i));
		
		// two methods of creating instance
		Class<?> clazz3 = String.class;
		Object object = null;
		// this method requires this class to have default constructor
		object = clazz3.newInstance();
		
		Constructor<?> constructor = null;
		// get constructor with specified type parameters
		constructor = clazz3.getConstructor(String.class);
		Object object1 = constructor.newInstance("23333");
		
		// getting methods
		Method[] allMethodsExceptInheritMethods = clazz3.getDeclaredMethods();
		Method[] allPublicMethodsBesidesInheritPublicMethods = clazz3.getMethods();
		Method method = clazz3.getMethod("valueOf", int.class);
		
		// invoke of method
		Object result = method.invoke(object, 1);
		System.out.println("Execution result of method: " + result);
		
		// a array can be assigned to a object reference
		// Array class is java.lang.reflect.Array
		Class<?> clazz4 = Class.forName("java.lang.String");
		Object array = Array.newInstance(clazz4, 25);
		Array.set(array, 0, "hello");
        Array.set(array, 1, "Java");
        Array.set(array, 2, "fuck");
        Array.set(array, 3, "Scala");
        Array.set(array, 4, "Clojure");
        System.out.println(Array.get(array, 4));
        System.out.println(Array.get(array, 5));
	}
}
