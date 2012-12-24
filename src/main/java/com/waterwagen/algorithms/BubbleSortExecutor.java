package com.waterwagen.algorithms;

import java.util.List;

import com.waterwagen.study.algorithms.sorting.BubbleSort;

public class BubbleSortExecutor extends ListDataAlgorithmExecutor
{
	private final BubbleSort<Integer> mSort;
	private List<Integer> mList;
	
	public BubbleSortExecutor()
	{
		mSort = new BubbleSort<>();
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
