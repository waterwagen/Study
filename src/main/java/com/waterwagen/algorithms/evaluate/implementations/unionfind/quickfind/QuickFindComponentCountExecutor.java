package com.waterwagen.algorithms.evaluate.implementations.unionfind.quickfind;

import java.util.Set;

import com.waterwagen.algorithms.evaluate.implementations.unionfind.UnionFindExecutorForComponentCountMsg;
import com.waterwagen.study.algorithms.other.UnionFind;
import com.waterwagen.study.algorithms.other.UnionQuickFind;

public class QuickFindComponentCountExecutor extends UnionFindExecutorForComponentCountMsg
{
	@Override
	protected UnionFind newUnionFindInstance(Set<Integer> site_ids)
	{
		return new UnionQuickFind(site_ids);
	}
}
