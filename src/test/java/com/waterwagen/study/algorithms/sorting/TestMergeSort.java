package com.waterwagen.study.algorithms.sorting;

public class TestMergeSort extends AbstractSortTest
{
	@Override
	public Sort<Integer> sortInstance()
	{
		return new MergeSort<Integer>();
	}
}