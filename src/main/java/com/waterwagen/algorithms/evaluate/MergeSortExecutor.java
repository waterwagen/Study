package com.waterwagen.algorithms.evaluate;

import java.util.List;

import com.waterwagen.study.algorithms.sorting.MergeSort;

public class MergeSortExecutor extends ListDataAlgorithmExecutor
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
		mList = buildListOfIntegers(n);
	}
	
	@Override
	public void execute()
	{
		mSort.sort(mList);
	}
}
