package com.waterwagen.study.algorithms.sorting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.waterwagen.Utilities;

public class TestQuickSort
{
	private static final int NUM_COUNT = 1_000_000;
	
	private List<Integer> list;
	private QuickSort<Integer> quicksort;
	
	@Before
	public void setUp()
	{
		list = new ArrayList<>(Utilities.randomCollectionOfIntsUpTo(NUM_COUNT));
//		list = new ArrayList<>(Arrays.asList(6,4,6));
//		list = new ArrayList<>();
//		list.add(Integer.valueOf(6));
//		list.add(Integer.valueOf(4));
		quicksort = new QuickSort<Integer>();
	}
	
	@Test
	public void test()
	{
		long start_time = System.nanoTime();
		quicksort.sort(list);
		long end_time = System.nanoTime();
		System.out.println(String.format("Sort time was %f seconds", (double)(end_time - start_time)/1_000_000_000));

		boolean in_asc_order = true;
		Iterator<Integer> it = list.iterator();
		Integer current = Integer.MIN_VALUE;
		Integer next = null;
		while(it.hasNext())
		{
			next = it.next();
			if(next.intValue() < current.intValue())
			{
				System.out.println("Failed on value " + next.intValue());
				in_asc_order = false;
				break;
			}
			current = next; 
		}

		assertThat(in_asc_order, equalTo(true));
	}
}