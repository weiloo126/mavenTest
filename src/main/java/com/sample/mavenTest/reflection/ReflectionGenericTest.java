package com.sample.mavenTest.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 为了通过反射操作泛型以迎合实际开发的需要, Java新增了：
 * java.lang.reflect.ParameterizedType
 * java.lang.reflect.GenericArrayType
 * java.lang.reflect.TypeVariable
 * java.lang.reflect.WildcardType
 * 几种类型来代表不能归一到Class类型但是又和原始类型同样重要的类型.
 * 使用ParameterizedType来获取泛型信息.
 * 
 * @date 2017年12月15日
 */
public class ReflectionGenericTest {
	
	private Map<String, Object> objectMap;
	
	public void test(Map<String, Integer> map, String string) {
    }
	
	public Map<Integer, String> test() {
        return null;
    }
	
	public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        System.out.println("-----------------testFieldType----------------");
        testFieldType();
        System.out.println("-----------------testParamType----------------");
        testParamType();
        System.out.println("-----------------testReturnType----------------");
        testReturnType();
	}
	
	/**
     * 使用反射获取泛型信息: 测试属性类型
     *
     * @throws NoSuchFieldException
     */
    public static void testFieldType() throws NoSuchFieldException {
		Field field = ReflectionBaseTest.class.getDeclaredField("objectMap");
        Type gType = field.getGenericType();
        // 打印type与generic type的区别
        System.out.println(field.getType());
        System.out.println(gType);
        System.out.println("**************");
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            Type[] types = pType.getActualTypeArguments();
            for (Type type : types) {
                System.out.println(type.toString());
            }
        }
    }
    
    /**
     * 使用反射获取泛型信息: 测试参数类型
     *
     * @throws NoSuchMethodException
     */
    public static void testParamType() throws NoSuchMethodException {
        Method testMethod = ReflectionBaseTest.class.getMethod("test", Map.class, String.class);
        Type[] parameterTypes = testMethod.getGenericParameterTypes();
        for (Type type : parameterTypes) {
            System.out.println("type -> " + type);
            if (type instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
                for (Type actualType : actualTypes) {
                    System.out.println("\tactual type -> " + actualType);
                }
            }
        }
    }
    
    /**
     * 使用反射获取泛型信息: 测试返回值类型
     *
     * @throws NoSuchMethodException
     */
    public static void testReturnType() throws NoSuchMethodException {
        Method testMethod = ReflectionBaseTest.class.getMethod("test");
        Type returnType = testMethod.getGenericReturnType();
        System.out.println("return type -> " + returnType);
 
        if (returnType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
            for (Type actualType : actualTypes) {
                System.out.println("\tactual type -> " + actualType);
            }
        }
    }
}
