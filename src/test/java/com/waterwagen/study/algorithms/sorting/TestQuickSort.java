package com.waterwagen.study.algorithms.sorting;

public class TestQuickSort extends AbstractSortTest
{
	@Override
	public Sort<Integer> sortInstance()
	{
		return new QuickSort<Integer>();
	}
}
