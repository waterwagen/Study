package com.waterwagen.algorithms;

import java.util.List;

import com.waterwagen.study.algorithms.sorting.QuickSort;

public class QuickSortExecutor extends ListDataAlgorithmExecutor
{
	private final QuickSort<Integer> mSort;
	private List<Integer> mList;
	
	public QuickSortExecutor()
	{
		mSort = new QuickSort<>();
	}

	@Override
	public void prepare(int n)
	{
		mList = buildListOfIntegers(n);
	}
	
	@Override
	public void execute()
	{
		mSort.sort(mList);
	}
}
