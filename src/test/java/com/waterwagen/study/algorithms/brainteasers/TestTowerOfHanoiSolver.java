package com.waterwagen.study.algorithms.brainteasers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTowerOfHanoiSolver
{
	private static final int DISC_COUNT = 15;

	private Stack<Integer> t1;
	private Stack<Integer> t2;
	private Stack<Integer> t3;
	private TowerOfHanoiSolver mSolver;

	@Before
	public void warmUpJVM()
	{
//		// warm up?
//		Stack<Integer> temp1 = new Stack<>();
//		Stack<Integer> temp2 = new Stack<>();
//		Stack<Integer> temp3 = new Stack<>();
//
//		for(int i = 10; i >= 1; i--)
//			temp1.push(Integer.valueOf(i));
//
//		TowerOfHanoiSolver solver = new TowerOfHanoiSolver();
//		solver.solveSimple(temp1, temp2, temp3);
//		solver.solveSpeedOptimized(temp1, temp2, temp3);
		
		// test object
		t1 = new Stack<>();
		t2 = new Stack<>();
		t3 = new Stack<>();
		for(int i = DISC_COUNT; i >= 1; i--)
			t1.push(Integer.valueOf(i));
		
		mSolver = new TowerOfHanoiSolver();

		printTowerState("Before:");
	}
	
	@After
	public void tearDown()
	{
		printTowerState("After:");
	}
	
	/////////////
	/// Tests ///
	/////////////
	
	@Test
	public void successfullyMovedSpeedOptimizedRecursion()
	{
		long start_time = System.nanoTime();
		mSolver.solveSpeedOptimized(t1, t2, t3);
		long end_time = System.nanoTime();
		
		System.out.println(String.format("Speed optimized recursion solved in %f seconds.", (double)(end_time - start_time)/1_000_000_000));
		System.out.println();
		
		verifyStacksStates();
	}

	@Test
	public void successfullyMovedSimpleRecursion()
	{		
		long start_time = System.nanoTime();
		mSolver.solveSimple(t1, t2, t3);
		long end_time = System.nanoTime();
		
		System.out.println(String.format("Simple recursion solved in %f seconds.", (double)(end_time - start_time)/1_000_000_000));
		System.out.println();
		
		verifyStacksStates();
	}

	////////////////////////////////
	/// Assertion Helper Methods ///
	////////////////////////////////
	
	private void verifyStacksStates()
	{
		assertThat(t1.isEmpty(), is(equalTo(true)));
		assertThat(t2.isEmpty(), is(equalTo(true)));
		assertThat(t3.isEmpty(), is(equalTo(false)));
		assertThat(t3.size(), is(equalTo(Integer.valueOf(DISC_COUNT))));
		for(int i = 1; i <= DISC_COUNT; i++)
			assertThat(t3.pop(), is(equalTo(Integer.valueOf(i))));
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private void printTowerState(String header)
	{
		System.out.println(header);
		System.out.println(String.format("Tower1=%s\nTower2=%s\nTower3=%s", t1, t2, t3));
		System.out.println();
	}
}