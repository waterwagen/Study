package com.waterwagen.algorithms.evaluate.implementations.sorts;

import java.util.List;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.sorting.MergeSort;

public class MergeSortExecutor extends DataAlgorithmExecutor
{
	private final MergeSort<Integer> mSort;
	private List<Integer> mList;
	
	public MergeSortExecutor()
	{
		mSort = new MergeSort<>();
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
