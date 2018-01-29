package com.sample.mavenTest.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 解决线程同步的两种典型方案：
 * 1、Java中内置了语言级的同步原语－synchronized关键字(从JAVA SE1.0开始，java中的每一个对象都有一个内部锁，如果一个方法使用synchronized关键字进行声明，那么这个对象将保护整个方法，也就是说调用该方法线程必须获得内部的对象锁。)
 * 2、Java SE5.0引入了Lock锁对象的相关类
 * 
 * @date 2018/01/29 20:59
 */
public class LockAndSynchronizedTest {
	
	public static void main(String[] args) {
//		// 创建一个卖票线程任务对象
//		Ticket1 ticket1 = new Ticket1();          
//        // 创建4个线程模拟卖票窗口同时卖票  
//        Thread t1 = new Thread(ticket1);  
//        Thread t2 = new Thread(ticket1);  
//        Thread t3 = new Thread(ticket1);  
//        Thread t4 = new Thread(ticket1);  
//        //启动线程  
//        t1.start();  
//        t2.start();  
//        t3.start();  
//        t4.start();  

		Ticket2 ticket2 = new Ticket2();          
        //创建4个线程同时卖票  
        Thread t5 = new Thread(ticket2);  
        Thread t6 = new Thread(ticket2);  
        Thread t7 = new Thread(ticket2);  
        Thread t8 = new Thread(ticket2);  
        //启动线程  
        t5.start();  
        t6.start();  
        t7.start();  
        t8.start();  
	}
	
	// 通过ReentrantLock来解决卖票线程的线程同步（安全）问题
	private static class Ticket1 implements Runnable {  
	    // 创建锁对象  
	    private Lock ticketLock = new ReentrantLock();  
	    // 当前拥有的票数  
	    private int num = 100;  
	    public void run() {  
	        while(true) {         
                ticketLock.lock();//获取锁  
                if(num > 0) {                    
                    try{  
                        Thread.sleep(10);  
                        //输出卖票信息  
                        System.out.println(Thread.currentThread().getName()+".....Ticket1 sale...."+num--);  
                    }catch (InterruptedException e){  
                        Thread.currentThread().interrupt();//出现异常就中断  
                    }finally{  
                        ticketLock.unlock();//释放锁  
                    }     
                }  
	        }  
	    }  
	}  
	
	private static class Ticket2 implements Runnable {  
	    private  int num = 100;  
	    // obj是对象锁，可以是任意对象
	    Object obj = new Object();  
	    public void run() {  
	        while(true) {  
	            synchronized(obj) {  
	                if(num > 0) {  
	                    try{
	                    	Thread.sleep(10);
                    	}catch (InterruptedException e){
                    		
                    	}  
	                      
	                    System.out.println(Thread.currentThread().getName()+".....Ticket2 sale...."+num--);  
	                }  
	            }  
	        }  
	    }  
	} 
}
