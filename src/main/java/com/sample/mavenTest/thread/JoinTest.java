package com.sample.mavenTest.thread;

/**
 * 
 * join方法是通过调用线程的wait方法来达到同步的目的的。例如，A线程中调用了B线程的join方法，则相当于A线程调用了B线程的wait方法，在调用了B线程的wait方法后，A线程就会进入阻塞状态，只有当B线程执行完毕时，A线程才能继续执行。
 * join方法必须在线程start方法调用之后调用才有意义。这个也很容易理解：如果一个线程都没有start，那它也就无法同步了。
 * 
 * @date 2017年12月28日
 */
public class JoinTest{
	
	public static void main(String [] args) throws InterruptedException {
        ThreadJoinTest t1 = new ThreadJoinTest("小明");
        ThreadJoinTest t2 = new ThreadJoinTest("小东");
        t1.start();
        /**
         * join的意思是使得放弃当前线程的执行，并返回对应的线程，例如下面代码的意思就是：
         * 程序在main线程中调用t1线程的join方法，则main线程放弃cpu控制权，并返回t1线程继续执行直到线程t1执行完毕
         * 所以结果是t1线程执行完后，才到主线程执行，相当于在main线程中同步t1线程，t1执行完了，main线程才有执行的机会
         */
        //t1.join();
        
        /**
         * join方法可以传递参数，join(3)表示main线程会等待t1线程3毫秒，3毫秒过去后，
         * main线程和t1线程之间执行顺序由串行执行变为普通的并行执行
         */
        t1.join(3);
        t2.start();
    }
}

class ThreadJoinTest extends Thread{
	
    public ThreadJoinTest(String name){
        super(name);
    }
    
    @Override
    public void run(){
        for(int i=0; i<100000; i++){
            System.out.println(this.getName() + ":" + i);
        }
    }
}
