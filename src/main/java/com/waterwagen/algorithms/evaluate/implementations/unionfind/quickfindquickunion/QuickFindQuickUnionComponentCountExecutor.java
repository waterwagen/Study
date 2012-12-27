package com.waterwagen.algorithms.evaluate.implementations.unionfind.quickfindquickunion;

import java.util.Set;

import com.waterwagen.algorithms.evaluate.implementations.unionfind.UnionFindExecutorForComponentCountMsg;
import com.waterwagen.study.algorithms.other.QuickFindQuickUnion;
import com.waterwagen.study.algorithms.other.UnionFind;

public class QuickFindQuickUnionComponentCountExecutor extends UnionFindExecutorForComponentCountMsg
{
	@Override
	protected UnionFind newUnionFindInstance(Set<Integer> site_ids)
	{
		return new QuickFindQuickUnion(site_ids);
	}
}
