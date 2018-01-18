package com.sample.mavenTest.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 现在有T1、T2、T3三个线程，保证T2在T1执行完后执行，T3在T2执行完后执行   
 * 
 * @date 2018年1月18日
 */
public class JoinTest2 {
	  
    public static void main(String[] args) {  
  
    	// 方案一：用thread.join()先执行完thread，再执行当前线程
        final Thread t1 = new Thread(new Runnable() {  
  
            @Override  
            public void run() {  
                System.out.println("t1");  
            }  
        });  
        final Thread t2 = new Thread(new Runnable() {  
  
            @Override  
            public void run() {  
                try {  
                    //引用t1线程，等待t1线程执行完  
                    t1.join();  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                System.out.println("t2");  
            }  
        });  
        Thread t3 = new Thread(new Runnable() {  
  
            @Override  
            public void run() {  
                try {  
                    //引用t2线程，等待t2线程执行完  
                    t2.join();  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                System.out.println("t3");  
            }  
        });  
        t3.start();  
        t2.start();  
        t1.start();  
        
        // 方案二：将三个任务一次提交到单线程的线程池，三个任务只能按提交顺序依次被处理
        Thread t11 = new Thread(new Runnable() {  
        	  
            @Override  
            public void run() {  
                System.out.println("t11");  
            }  
        });  
        Thread t21 = new Thread(new Runnable() {  
  
            @Override  
            public void run() {  
                System.out.println("t21");  
            }  
        });  
        Thread t31 = new Thread(new Runnable() {  
  
            @Override  
            public void run() { 
                System.out.println("t31");  
            }  
        });  
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(t11);
        executorService.execute(t21);
        executorService.execute(t31);
        executorService.shutdown();
    }  
}
