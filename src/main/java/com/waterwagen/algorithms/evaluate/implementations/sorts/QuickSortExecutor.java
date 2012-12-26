package com.waterwagen.algorithms.evaluate.implementations.sorts;

import java.util.List;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.sorting.QuickSort;

public class QuickSortExecutor extends DataAlgorithmExecutor
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
		mList = buildListOfRandomIntegers(n, n*2);
	}
	
	@Override
	public void execute()
	{
		mSort.sort(mList);
	}
}
