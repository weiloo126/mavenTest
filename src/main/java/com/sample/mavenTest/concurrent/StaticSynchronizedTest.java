package com.sample.mavenTest.concurrent;

/**
 * synchronized 和 static synchronized 的区别
 * 一个是实例锁（锁在某一个实例对象上，如果该类是单例，那么该锁也具有全局锁的概念），一个是全局锁（该锁针对的是类，无论实例多少个对象，那么线程都共享该锁）。
 * 实例锁对应的就是synchronized关键字，而类锁（全局锁）对应的就是static synchronized（或者是锁在该类的class或者classloader对象上）。
 * 
 * synchronized是对类的当前实例（当前对象）进行加锁，防止其他线程同时访问该类的该实例的所有synchronized块，注意这里是“类的当前实例”， 类的两个不同实例就没有这种约束了。
 * static synchronized恰好就是要控制类的所有实例的并发访问，static synchronized是限制多线程中该类的所有实例同时访问jvm中该类所对应的代码块。
 * 实际上，在类中如果某方法或某代码块中有 synchronized，那么在生成一个该类实例后，该实例也就有一个监视块，防止线程并发访问该实例的synchronized保护块，而static synchronized则是所有该类的所有实例公用得一个监视块，这就是他们两个的区别。也就是说synchronized相当于 this.synchronized，而static synchronized相当于SomeClass.synchronized.
 * 
 * 下面代码synchronized同时修饰静态方法和实例方法，但是运行结果是交替进行的，这证明了类锁和对象锁是两个不一样的锁，控制着不同的区域，它们是互不干扰的。同样，线程获得对象锁的同时，也可以获得该类锁，即同时获得两个锁，这是允许的。
 * 
 * @date 2018年1月16日
 */
public class StaticSynchronizedTest {

    public synchronized void test1(){    
    	int i = 5;    
    	while( i-- > 0){    
    		System.out.println(Thread.currentThread().getName() + " : " + i);    
    		try{    
    			Thread.sleep(500);    
			}catch (InterruptedException ie){    
			}    
    	}    
    }    
      
    public static synchronized void test2() {    
         int i = 5;    
         while(i-- > 0)   {    
              System.out.println(Thread.currentThread().getName() + " : " + i);    
              try{    
                   Thread.sleep(500);    
              }catch (InterruptedException ie){    
              }    
         }    
    }    
      
    public static void main(String[] args) {    
         final StaticSynchronizedTest myt2 = new StaticSynchronizedTest();    
         Thread test1 = new Thread(new Runnable() {  
        	 public void run() { 
        		 myt2.test1();  
    		 }  
    	 }, "test1");    
         Thread test2 = new Thread(new Runnable() {  
        	 public void run() { 
        		 StaticSynchronizedTest.test2();   
    		 }  
    	 }, "test2");    
         test1.start();    
         test2.start();
    }   
}
