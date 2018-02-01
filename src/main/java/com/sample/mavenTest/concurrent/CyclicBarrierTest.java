package com.sample.mavenTest.concurrent;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。
 * 在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier很有用。
 * 因为该 barrier在释放等待线程后可以重用，所以称它为循环的 barrier。
 * 
 * 使用场景：
 * 需要所有的子任务都完成时，才执行主任务，这个时候就可以选择使用CyclicBarrier。
 * 
 * 在所有参与者都已经在 barrier上调用 await()方法之前，将一直等待。
 * 如果当前线程不是将到达的最后一个线程，出于调度目的，将禁用它，且在发生以下情况之一前，该线程将一直处于休眠状态：
 * (1)最后一个线程到达；
 * (2)或者其他某个线程中断当前线程；
 * (3)或者其他某个线程中断另一个等待线程；
 * (4)或者其他某个线程在等待 barrier时超时；
 * (5)或者其他某个线程在此 barrier上调用 reset()。
 * 
 * 如果当前线程：
 * 在进入await()方法时已经设置了该线程的中断状态；
 * 或者在等待时被中断
 * 则抛出 InterruptedException，并且清除当前线程的已中断状态。
 * 
 * 如果在线程处于等待状态时 barrier被 reset()，
 * 或者在调用 await()时，抑或任意一个线程正处于等待状态， barrier被损坏，
 * 则抛出 BrokenBarrierException异常。
 * 
 * 如果任何线程在等待时被中断，则其他所有等待线程都将抛出 BrokenBarrierException异常，并将 barrier置于损坏状态。
 * 如果当前线程是最后一个将要到达的线程，并且构造方法中提供了一个非空的屏障操作，则在允许其他线程继续运行之前，当前线程将运行该操作。如果在执行屏障操作过程中发生异常，则该异常将传播到当前线程中，并将 barrier 置于损坏状态。
 * 
 * 代码举例：赛跑时，等待所有人都准备好时，才起跑
 * @date 2018/02/01 21:47
 */
public class CyclicBarrierTest {
	
	public static void main(String[] args) {		
		//如果将参数改为4，但是下面只加入了3个选手，这永远等待下去  
	    //Waits until all parties have invoked await on this barrier. 
		CyclicBarrier barrier = new CyclicBarrier(3);
		
		ExecutorService es = Executors.newFixedThreadPool(3);
		es.submit(new Runner(barrier, "player1"));
		es.submit(new Runner(barrier, "player2"));
		es.submit(new Runner(barrier, "player3"));
		
		es.shutdown();
	}
	
	private static class Runner implements Runnable{
		
		private CyclicBarrier barrier;		
		private String name;
		
		public Runner(CyclicBarrier barrier, String name) {
			super();
			this.barrier = barrier;
			this.name = name;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(1000 * (new Random().nextInt(8)));
				System.out.println(name + " is ready...");
				// 返回值 表示到达的当前线程的索引，其中，索引 getParties() - 1 指示将到达的第一个线程，零指示最后一个到达的线程
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			System.out.println(name + " ready go!");
		}		
	}
	
}
