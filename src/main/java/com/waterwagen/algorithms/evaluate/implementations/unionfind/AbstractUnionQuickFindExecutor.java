package com.waterwagen.algorithms.evaluate.implementations.unionfind;

import java.util.HashSet;
import java.util.Set;

import com.waterwagen.algorithms.evaluate.AlgorithmExecutor;
import com.waterwagen.study.algorithms.other.UnionQuickFind;

public abstract class AbstractUnionQuickFindExecutor implements AlgorithmExecutor
{
	UnionQuickFind mUnionQuickFind;
	Set<Integer> mSiteIds = new HashSet<>();

	@Override
	public void prepare(int n)
	{
		mSiteIds.clear();
		mSiteIds.addAll(buildSetOfIntegers(n));
		mUnionQuickFind = new UnionQuickFind(mSiteIds);
	}

	private Set<Integer> buildSetOfIntegers(int n)
	{
		Set<Integer> result = new HashSet<>(n);
		
		for(int i = 0; i < n; i++)
			result.add(i);
		
		return result;
	}
}