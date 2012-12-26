package com.waterwagen.algorithms.evaluate;

import java.util.Iterator;

public class UnionQuickFindExecutorForUnionMsg extends AbstractUnionQuickFindExecutor
{
	private int mSiteId1;
	private int mSiteId2;

	@Override
	public void prepare(int n)
	{
		super.prepare(n);
		Iterator<Integer> it = mSiteIds.iterator();
		mSiteId1 = it.next().intValue();
		mSiteId2 = it.next().intValue();		
	}
		
	@Override
	public void execute()
	{
		mUnionQuickFind.union(mSiteId1, mSiteId2);
	}
}
