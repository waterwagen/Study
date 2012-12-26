package com.waterwagen.algorithms.evaluate;

public class UnionQuickFindExecutorForFindMsg extends AbstractUnionQuickFindExecutor
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
		mUnionQuickFind.find(mSiteId);
	}
}
