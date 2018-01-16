package com.sample.mavenTest.concurrent;

/**
 * wait()、notify()、notifyAll()是三个定义在Object类里的方法，可以用来控制线程的状态。
 * 这三个方法最终调用的都是jvm级的native方法。随着jvm运行平台的不同可能有些许差异。
 * 
 * 这儿要非常注意的几个事实：
 * 1、任何一个时刻，对象的控制权（monitor）只能被一个线程拥有。
 * 2、无论是执行对象的wait、notify还是notifyAll方法，必须保证当前运行的线程取得了该对象的控制权（monitor）
 * 3、如果在没有控制权的线程里执行对象的以上三种方法，就会报java.lang.IllegalMonitorStateException异常。
 * 4、JVM基于多线程，默认情况下不能保证运行时线程的时序性
 * 
 * 基于以上几点事实，我们需要确保让线程拥有对象的控制权。
 * 也就是说在waitThread中执行wait方法时，要保证waitThread对flag有控制权；
 * 在notifyThread中执行notify方法时，要保证notifyThread对flag有控制权。
 * 
 * 调用wait方法后，线程是会释放对monitor对象的所有权的。
 * 一个通过wait方法阻塞的线程，必须同时满足以下两个条件才能被真正执行：
 * 1、线程需要被唤醒（超时唤醒或调用notify/notifyAll）。
 * 2、线程唤醒后需要竞争到锁（monitor）。
 * 
 * 线程取得控制权的方法有三：
 * 执行对象的某个同步实例方法。
 * 执行对象对应类的同步静态方法。
 * 执行对该对象加同步锁的同步块。
 * 
 * 我们用第三种方法来做说明：将以上notify和wait方法包在同步块中
 * 
 * @date 2017年12月8日
 */
public class WaitNotifyTest {

	private String[] flag = {"true"};  
	  
    class NotifyThread extends Thread{  
        public NotifyThread(String name) {  
            super(name);  
        }  
        public void run() {       
            try {  
                sleep(3000);//推迟3秒钟通知  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
              
            synchronized (flag) {
            	flag[0] = "false";                 
                // 有三个wait线程，只有一个notify线程，notify线程运行notify方法的时候，是随机通知一个正在等待的线程，所以，现在应该还有两个线程在waiting。
                // 我们只需要将NotifyThread线程类中的flag.notify()方法改成notifyAll()就可以了。notifyAll方法会通知所有正在等待对象控制权的线程。 
                //flag.notify(); 
                flag.notifyAll();
			}
        }  
    };  
  
    class WaitThread extends Thread {  
        public WaitThread(String name) {  
            super(name);  
        }  
  
        public void run() {  
        	synchronized (flag) {
                while (!flag[0].equals("false")) {  
                    System.out.println(getName() + " begin waiting!");  
                    long waitTime = System.currentTimeMillis();  
                    try {  
                    	// 始终应该使用wait循环模式来调用wait方法，永远不要在循环之外调用wait方法。循环会在等待之前和之后测试条件。
                        flag.wait();  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                    waitTime = System.currentTimeMillis() - waitTime;  
                    System.out.println("wait time :"+waitTime);  
                }  
                System.out.println(getName() + " end waiting!");                
			}
        }  
    }  
  
    public static void main(String[] args) throws InterruptedException {  
        System.out.println("Main Thread Run!");  
        WaitNotifyTest test = new WaitNotifyTest();  
        NotifyThread notifyThread = test.new NotifyThread("notify01");  
        WaitThread waitThread01 = test.new WaitThread("waiter01");  
        WaitThread waitThread02 = test.new WaitThread("waiter02");  
        WaitThread waitThread03 = test.new WaitThread("waiter03");  
        notifyThread.start();  
        waitThread01.start();  
        waitThread02.start();  
        waitThread03.start();  
    }  
}
