package com.waterwagen.study.algorithms.other;

import java.util.Set;

public class TestQuickFindQuickUnion extends TestUnionFind
{
	@Override
	UnionFind newUnionFindInstance(Set<Integer> site_ids)
	{
		return new QuickFindQuickUnion(site_ids);
	}
}