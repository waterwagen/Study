package com.waterwagen.algorithms.evaluate.implementations.unionfind;

public abstract class UnionFindExecutorForFindMsg extends AbstractUnionFindExecutor
{
	private int mSiteId;

	@Override
	public void prepare(int n)
	{
		super.prepare(n);
		mSiteId = mSiteIds.iterator().next().intValue(); 
	}
	
	@Override
	public void execute()
	{
		mUnionFind.find(mSiteId);
	}
}
