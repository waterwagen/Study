package com.waterwagen.algorithms.evaluate.implementations.sorts;

import java.util.List;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.sorting.BubbleSort;

public class BubbleSortExecutor extends DataAlgorithmExecutor
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
		mList = buildListOfRandomIntegers(n, n*2);
	}
	
	@Override
	public void execute()
	{
		mSort.sort(mList);
	}
}
