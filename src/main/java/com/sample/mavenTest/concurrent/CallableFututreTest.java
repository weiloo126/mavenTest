package com.sample.mavenTest.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/*
 * Callable<V>/Future<V>/FutureTask的使用
 * 
 * @date 2018/01/29 14:28
 */
public class CallableFututreTest {
	
	public static void main(String[] args) {
		// 使用Callable+Future获取执行结果：
		// 创建线程池 
		ExecutorService es = Executors.newSingleThreadExecutor();
		// 创建Callable对象任务 
		CallableDemo callableDemo = new CallableDemo();
		// 提交任务并获取执行结果 
		Future<Integer> future = es.submit(callableDemo);
		// 关闭线程池
		es.shutdown();
		try {
			Thread.sleep(2000);
			System.out.println("主线程在执行其他任务"); 
			if(future.get() != null){
				// 输出获取到的结果
				System.out.println("future.get() -> " + future.get());
			}else {
				System.out.println("future.get()未获取到结果");  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("主线程执行完成"); 
		System.out.println("---------------------------------"); 
		
		
		// 使用Callable+FutureTask获取执行结果：
		// 创建线程池 
		ExecutorService es1 = Executors.newSingleThreadExecutor();
		// 创建Callable对象任务 
		CallableDemo callableDemo1 = new CallableDemo();
		// 创建FutureTask 
		FutureTask<Integer> futureTask = new FutureTask<>(callableDemo1);
		// 执行任务  
		es1.submit(futureTask);
		// 关闭线程池
		es1.shutdown();
		try {
			Thread.sleep(2000);
			System.out.println("主线程在执行其他任务"); 
			if(futureTask.get() != null){
				// 输出获取到的结果
				System.out.println("futureTask.get() -> " + futureTask.get());
			}else {
				System.out.println("futureTask.get()未获取到结果");  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("主线程执行完成"); 
	}
	
	
	private static class CallableDemo implements Callable<Integer> {  
	      
	    private int sum;  
	    
	    @Override  
	    public Integer call() throws Exception {  
	        System.out.println("Callable子线程开始计算啦！");  
	        Thread.sleep(2000);  
	          
	        for(int i=0 ;i<5000;i++){  
	            sum=sum+i;  
	        }  
	        System.out.println("Callable子线程计算结束！");  
	        return sum;  
	    }  
	}  
}
