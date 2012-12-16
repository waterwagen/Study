package com.waterwagen.study.algorithms.searching;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestBinarySearch
{
	private BinarySearch<Integer> mBinarySearch;
	private int mSize;
	private int mSearchCount;
	private int mMaximumValue;
	private List<Integer> mList;

	@Before
	public void setUp()
	{
		mSize = 1_000;
		mSearchCount = 100;
		mMaximumValue = mSize*10;
		mList = buildUniqueRandomIntegerList(mSize, mMaximumValue);
		Collections.sort(mList);
		mBinarySearch = new BinarySearch<Integer>();
	}
	
	@Test
	public void testBinarySearchForPresentValues()
	{
		for(int i = 1; i <= mSearchCount; i++)
		{
			int index_to_find = (mSize - 1)/i;
			Integer int_to_find = mList.get(index_to_find);
			int result = mBinarySearch.find(mList, int_to_find);

			assertThat(result, equalTo(index_to_find));
		}
	}
	
	@Test
	public void testBinarySearchForMissingValue()
	{
		for(int i = 1; i <= mSearchCount; i++)
		{
			Integer int_not_in_list = Integer.valueOf(mMaximumValue + 1);
			int result = mBinarySearch.find(mList, int_not_in_list);

			assertThat(result, equalTo(-1));
		}
	}

	//////////////////////
	/// Helper Methods ///
	//////////////////////
	
	private List<Integer> buildUniqueRandomIntegerList(int num_ints, int maximum_value)
	{
		Random random = new Random(System.currentTimeMillis());
		Set<Integer> uniques = new HashSet<>();
		while(uniques.size() < num_ints)
			uniques.add(new Integer(random.nextInt(maximum_value)));
		
		return new ArrayList<>(uniques);
	}
}