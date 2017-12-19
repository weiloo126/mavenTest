package com.sample.mavenTest.thread;

/**
 * Thread.yield( )方法使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。把当前线程重新置入抢CPU时间的"队列"(队列只是说所有线程都在一个起跑线上，并非真正意义上的队列)。
 * cpu会从众多的可执行态里选择，也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，并不是说一定会执行其他线程而该线程在下一次中不会执行到了。
 * 
 * Java线程中有一个Thread.yield( )方法，很多人翻译成线程让步。顾名思义，就是说当一个线程使用了这个方法之后，它就会把自己CPU执行的时间让掉，让自己或者其它的线程运行。
 * 打个比方：现在有很多人在排队上厕所，好不容易轮到这个人上厕所了，突然这个人说：“我要和大家来个竞赛，看谁先抢到厕所！”，然后所有的人在同一起跑线冲向厕所，有可能是别人抢到了，也有可能他自己有抢到了。
 * 我们还知道线程有个优先级的问题，那么手里有优先权的这些人就一定能抢到厕所的位置吗? 不一定的，他们只是概率上大些，也有可能没特权的抢到了。
 * 
 * @date 2017年12月11日
 */
public class YieldTest extends Thread {

	public YieldTest(String name) {  
        super(name);  
    }  
  
    @Override  
    public void run() {  
        for (int i = 1; i <= 50; i++) {  
            System.out.println("" + this.getName() + "-----" + i);  
            // 当i为30时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）  
            if (i == 30) {  
                this.yield();  
            }  
        }  
    }  
  
    public static void main(String[] args) {  
        YieldTest yt1 = new YieldTest("张三");  
        YieldTest yt2 = new YieldTest("李四");  
        yt1.start();  
        yt2.start();  
    }  
}
