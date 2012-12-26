package com.waterwagen.algorithms.evaluate.implementations.searches;

import java.util.List;

import com.waterwagen.algorithms.evaluate.implementations.ListDataAlgorithmExecutor;
import com.waterwagen.study.algorithms.searching.BinarySearch;

public class BinarySearchExecutor extends ListDataAlgorithmExecutor
{
	private BinarySearch<Integer> mSearch;
	private List<Integer> mList;
	private Integer mTargetValue;

	public BinarySearchExecutor()
	{
		mSearch = new BinarySearch<Integer>();
	}

	@Override
	public void prepare(int n)
	{
		mList = buildListOfIntegers(n);
//		Random random_gen = new Random(System.currentTimeMillis());
		mTargetValue = mList.get(0);
	}
	
	@Override
	public void execute()
	{
		mSearch.find(mList, mTargetValue);
	}
}