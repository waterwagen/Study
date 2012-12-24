package com.waterwagen.study.algorithms.sorting;

public class TestBubbleSort extends AbstractSortTest
{
	@Override
	public Sort<Integer> sortInstance()
	{
		return new BubbleSort<Integer>();
	}
	
	@Override
	int numberCount() 
	{
		return 10_000;
	}
}
