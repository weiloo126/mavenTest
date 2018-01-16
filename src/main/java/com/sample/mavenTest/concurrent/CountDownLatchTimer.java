package com.sample.mavenTest.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * 
 * 
 * @date 2017年12月8日
 */
public class CountDownLatchTimer {
	private CountDownLatchTimer() {
	} // Noninstantiable

	public static long time(Executor executor, int concurrency,
			final Runnable action) throws InterruptedException {
		final CountDownLatch ready = new CountDownLatch(concurrency);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(concurrency);

		for (int i = 0; i < concurrency; i++) {
			executor.execute(new Runnable() {
				public void run() {
					ready.countDown(); // Tell timer we're ready
					try {
						start.await(); // Wait till peers are ready
						action.run();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						done.countDown(); // Tell timer we're done
					}
				}
			});
		}

		ready.await(); // Wait for all workers to be ready
		long startNanos = System.nanoTime();
		start.countDown(); // And they're off!
		done.await(); // Wait for all workers to finish
		return System.nanoTime() - startNanos;
	}
	
	public static void main(String[] args) {
		try {
			//传递给time方法的executor必须允许创建至少与指定并发级别concurrency一样多的线程，否则这个测试就永远不会结束。这就是“线程饥饿死锁（thread starvation deadlock）”
			long time = time(Executors.newFixedThreadPool(4), 4, new Runnable() {				
				@Override
				public void run() {
					System.out.println("----------");
				}
			});
			System.out.println(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
