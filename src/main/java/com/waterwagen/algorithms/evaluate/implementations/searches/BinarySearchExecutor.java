package com.waterwagen.algorithms.evaluate.implementations.searches;

import java.util.List;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.searching.BinarySearch;

public class BinarySearchExecutor extends DataAlgorithmExecutor
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
		mList = buildListOfRandomIntegers(n, n*2);
//		Random random_gen = new Random(System.currentTimeMillis());
		mTargetValue = mList.get(0);
	}
	
	@Override
	public void execute()
	{
		mSearch.find(mList, mTargetValue);
	}
}