package com.waterwagen.study.algorithms.sorting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.waterwagen.Utilities;

public abstract class AbstractSortTest
{
	private static final int NUM_COUNT = 1_000_000;
	
	private Sort<Integer> sort;
	private List<Integer> list;
	
	@Before
	public void setUp()
	{
		list = new ArrayList<>(Utilities.randomCollectionOfIntsUpTo(NUM_COUNT));
		sort = sortInstance();
	}
	
	@Test
	public void testSortsInAscendingOrder()
	{
		long start_time = System.nanoTime();
		sort.sort(list);
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
				System.out.println(String.format("Failed on current value=%s and next value=%s", current, next));
//				System.out.println(list);
				in_asc_order = false;
				break;
			}
			current = next; 
		}

		assertThat(in_asc_order, equalTo(true));
	}

	public abstract Sort<Integer> sortInstance();
}