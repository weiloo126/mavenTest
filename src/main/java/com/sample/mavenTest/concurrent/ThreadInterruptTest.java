package com.sample.mavenTest.concurrent;

/*
 * 线程中断问题
 * 
 * 在java中启动线程非常容易，大多数情况下是让一个线程执行完自己的任务然后自己停掉，但是有时候需要取消某个操作，比如你在网络下载时，有时候需要取消下载。
 * 实现线程的安全中断并不是一件容易的事情，因为Java并不支持安全快速中断线程的机制。
 * 
 * 在Thread类中除了interrupt()方法还有另外两个非常相似的方法：interrupted()和 isInterrupted()方法，下面来对这几个方法进行说明：
 * interrupt() 此方法是实例方法，用于告诉此线程外部有中断请求，并且将线程中的中断标记设置为true。
 * interrupted() 此方法是类方法，测试当前线程是否已经中断。线程的中断状态 由该方法清除。换句话说，如果连续两次调用该方法，则第二次调用将返回 false（在第一次调用已清除了其中断状态之后，且第二次调用检验完中断状态前，当前线程再次中断的情况除外）。
 * isInterrupted() 此方法是实例方法，测试线程是否已经中断。线程的中断状态 不受该方法的影响。线程中断被忽略，因为在中断时不处于活动状态的线程将由此返回 false 的方法反映出来。
 * 
 * 处理线程中断的常用方法：
 * 1、设置取消标记变量
 * 2、通过interrupt() 和 isInterrupted() 方法来中断线程，并在InterruptedException异常的catch语句中调用 Thread.currentThread().interrupt()方法来让高层知道中断请求并处理该中断，因为在抛出InterruptedException 的同时，线程的中断标志被清除了。
 * 
 * @date 2018/01/25 22:37
 */
public class ThreadInterruptTest {

	public static void main(String[] args) {
		// thread.interrupt() 方法并不能中断线程，该方法仅仅告诉线程外部已经有中断请求，至于是否中断还取决于线程自己
		/*NRunnable runnable = new NRunnable();
		Thread t = new Thread(runnable);
		t.start();
		System.out.println("t is start......");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
		// 如果interrupt方法能够中断线程，那么在打印了is interrupt…….之后应该是没有log了，但通过结果可以发现子线程并没有中断 
		t.interrupt();
		System.out.println("t is interrupt......");*/
		

		// 设置取消标记变量
		// 我们发现线程确实已经中断了，但是发现一个问题，调用cancel方法和最后线程执行完毕之间隔了好几秒的时间，也就是说线程不是立马中断的 
		// 原因是：子线程退出的条件是while循环结束，也就是cancel标示设置为true，但是当我们调用cancel方法将isCancel标记设置为true时，while循环里面有一个耗时操作(sleep方法模拟)，只有等待耗时操作执行完毕后才会去检查这个标记，所以cancel方法和线程退出中间有时间间隔。
		NRunnable1 runnable1 = new NRunnable1();
		Thread t1 = new Thread(runnable1);
		t1.start();
		System.out.println("t1 is start......");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
		// 如果interrupt方法能够中断线程，那么在打印了is interrupt…….之后应该是没有log了，但通过结果可以发现子线程并没有中断 
		runnable1.cancel();
		System.out.println("cancel ..."+System.currentTimeMillis());
		
		
		// 通过interrupt() 和 isInterrupted() 方法来中断线程
		// 这次是立马中断的，但是这种方法是有局限性的，这种方法仅仅对于会抛出InterruptedException 异常的任务是有效的，比如java中的sleep、wait等方法，对于不会抛出这种异常的任务其效果其实和第一种方法一样，都会有延迟性
		// 这个例子中还有一个非常重要的地方就是catch语句中，我们调用了Thread.currentThread().interrupt()， 若把这句代码去掉，运行你会发现这个线程无法终止，因为在抛出InterruptedException 的同时，线程的中断标志被清除了，所以在while语句中判断当前线程是否中断时，返回的是false
		// 针对InterruptedException异常，一定不能在catch语句块中什么也不干，如果你实在不想处理，你可以将异常抛出来，让调用抛异常的方法也成为一个可以抛出InterruptedException的方法，如果自己要捕获此异常，那么最好在catch语句中调用 Thread.currentThread().interrupt()方法来让高层知道中断请求并处理该中断。
		Thread t2 = new NThread();
	    t2.start();
	    System.out.println("t2 is start.......");
	    try {
	      Thread.sleep(3000);
	    } catch (InterruptedException e) {

	    }
	    System.out.println("t2 start interrupt..."+System.currentTimeMillis());
	    t2.interrupt();
	    System.out.println("t2 end interrupt ..."+System.currentTimeMillis());
	}

	private static class NRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				System.out.println("我没有被中断");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("t catchs InterruptedException");
				}
			}
		}
	}

	private static class NRunnable1 implements Runnable {
		public boolean isCancel = false;

		@Override
		public void run() {
			while (!isCancel) {
				System.out.println("我没有被中断");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {

				}
			}
			System.out.println("我已经结束了..." + System.currentTimeMillis());
		}

		public void cancel() {
			this.isCancel = true;
		}
	}

	private static class NThread extends Thread {

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				System.out.println("我没有被中断");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					System.out.println("t2 catchs InterruptedException");
					Thread.currentThread().interrupt();
				}
			}
			System.out.println("我已经结束了..." + System.currentTimeMillis());
		}
	}
}
