package com.waterwagen.algorithms.evaluate.implementations.unionfind;

import java.util.HashSet;
import java.util.Set;

import com.waterwagen.algorithms.evaluate.implementations.DataAlgorithmExecutor;
import com.waterwagen.study.algorithms.other.UnionFind;

public abstract class AbstractUnionFindExecutor extends DataAlgorithmExecutor
{
	UnionFind mUnionFind;
	Set<Integer> mSiteIds = new HashSet<>();

	@Override
	public void prepare(int n)
	{
		mSiteIds.clear();
		mSiteIds.addAll(buildSetOfOrderedIntegersUpTo(n));
		mUnionFind = newUnionFindInstance(mSiteIds);
	}

	protected abstract UnionFind newUnionFindInstance(Set<Integer> site_ids);
}