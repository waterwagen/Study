package com.waterwagen.study.concurrency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import org.junit.Test;

public class TestModernConcurrencyClasses
{
	/**
	 * Most of the interesting parts of this test are in the test fork/join task (inner class) it uses.
	 * Check the class implementation details.
	 */
	@Test
	public void testForkJoinPool() throws InterruptedException, ExecutionException
	{
		ForkJoinPool pool = new ForkJoinPool();
		Future<String> result = pool.submit(new TestRecursiveTask(buildTestIntArray(20), 5));
		
		long start_of_wait_time = System.nanoTime();
		while(!result.isDone()) Thread.sleep(10L);
		long end_of_wait_time = System.nanoTime();
		System.out.println("Total processing time was " + (end_of_wait_time - start_of_wait_time)/1_000_000 + "ms");
		
		assertTrue("Expected a String instance as a result, but it was null.", result.get() != null);
		assertTrue("Expected a String value as a result, but the length was zero.", result.get().length() > 0);
		assertEquals("Unexpected result value from the RecursiveTask.", "012345678910111213141516171819", result.get());
	}
	
	/**
	 * Most of the interesting parts of this test are in the test thread (inner class) it uses.
	 * Check the class implementation details.
	 */
	@Test
	public void testProperThreadShutdownTechnique() throws InterruptedException
	{
		MyThread thread = new MyThread();
		thread.start();
		Thread.sleep(500L);
		
		assertTrue("Expected thread to have started by now.", thread.isAlive());
		thread.endThread();
		Thread.sleep(600L); // we'll wait 100ms longer than the amount of time the thread sleeps
		assertTrue("Expected thread to have stopped by now.", !thread.isAlive());
	}
	
	@Test
	public void testTransferQueue() throws InterruptedException
	{
		final TransferQueue<String> queue = new LinkedTransferQueue<>();
		final String transfer_str = "abcdef";
		Thread transferer = new Thread() 
		{ 
			@Override 
			public void run() 
			{
				try{queue.transfer(transfer_str);}catch (InterruptedException exc){};
			}
		};
		transferer.start();
		Thread.sleep(500L);
		assertTrue(transferer.isAlive());
		String result = queue.peek();
		assertEquals("Unexpected value transfered.", transfer_str, result);
		assertTrue(transferer.isAlive());
		result = queue.poll();
		assertEquals("Unexpected value transfered.", transfer_str, result);
		}

	@Test
	public void testUsingScheduledThreadPool() throws InterruptedException, ExecutionException
	{
		int thread_count = 5;
		ScheduledExecutorService thread_pool = Executors.newScheduledThreadPool(thread_count);
		Future<String> future_task = thread_pool.schedule(new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				return "boo!";
			}	
		}, 1000, TimeUnit.MILLISECONDS);

		Thread.sleep(400L);
		assertTrue("Didn't expect the future task to be finished yet.", !future_task.isDone());

		Thread.sleep(400L);
		assertTrue("Didn't expect the future task to be finished yet.", !future_task.isDone());
		
		Thread.sleep(400L);
		assertTrue("Expected the future task to be finished.", future_task.isDone());
	}
	
	@Test
	public void testUsingCallableAndFuture() throws InterruptedException, ExecutionException
	{
		final long future_task_execution_time = 2000L;
		ExecutorService thread_pool = Executors.newCachedThreadPool();
		Future<Boolean> test_code = thread_pool.submit(new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception
			{
				Thread.sleep(future_task_execution_time);
				return true;
			}
		});
		Boolean result = false;
		while(!test_code.isDone())
		{
			Thread.sleep(1000L);
			System.out.println("Waited 1 second for result to finish, trying again.");
		}
		result = test_code.get();
		assertEquals("Unexpected result value.", true, result);
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private int[] buildTestIntArray(int size)
	{
		int[] result = new int[size];
		for(int i = 0; i < result.length; i++)
			result[i] = i;
		return result;
	}
	
	///////////////////////
	/// Utility Classes ///
	///////////////////////
	
	@SuppressWarnings("serial")
	private static class TestRecursiveTask extends RecursiveTask<String>
	{
		private int[] mIntArray;
		private int mSerialLimit;

		private TestRecursiveTask(int[] int_array, int serial_limit)
		{
			mIntArray = int_array;
			mSerialLimit = serial_limit;
		}

		@Override
		protected String compute()
		{
			int length = mIntArray.length;
			if(length <= mSerialLimit)
				return convertToString(mIntArray);
			
			ForkJoinTask<String> fork1 = new TestRecursiveTask(Arrays.copyOfRange(mIntArray, 0, length/2), mSerialLimit).fork(); 
			ForkJoinTask<String> fork2 = new TestRecursiveTask(Arrays.copyOfRange(mIntArray, length/2, length), mSerialLimit).fork(); 
			return fork1.join() + fork2.join();
		}

		private String convertToString(int[] intArray)
		{
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < intArray.length; i++)
			{
				buffer.append(intArray[i]);
			}
			return buffer.toString();
		}
	}

	private static class MyThread extends Thread
	{
		private volatile boolean isRunning = false;
		
		@Override
		public void run()
		{
			isRunning = true;
			while(isRunning)
			{
				try{Thread.sleep(500L);}catch(InterruptedException exc) {}
			}
		}
		
		private void endThread()
		{
			isRunning = false;
		}
	}
}
