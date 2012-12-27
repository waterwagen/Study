package com.waterwagen.algorithms.evaluate.implementations.unionfind;

import java.util.Iterator;

public abstract class UnionFindExecutorForConnectedMsg extends AbstractUnionFindExecutor
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
		mUnionFind.connected(mSiteId1, mSiteId2);
	}
}
