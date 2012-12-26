package com.waterwagen.algorithms.evaluate.implementations.unionfind;

import java.util.HashSet;
import java.util.Set;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.other.UnionQuickFind;

public abstract class AbstractUnionQuickFindExecutor extends DataAlgorithmExecutor
{
	UnionQuickFind mUnionQuickFind;
	Set<Integer> mSiteIds = new HashSet<>();

	@Override
	public void prepare(int n)
	{
		mSiteIds.clear();
		mSiteIds.addAll(buildSetOfOrderedIntegersUpTo(n));
		mUnionQuickFind = new UnionQuickFind(mSiteIds);
	}
}