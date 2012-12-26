package com.waterwagen.study.algorithms.other;

import java.util.Set;

public class TestUnionQuickFind extends TestUnionFind
{
	@Override
	UnionFind newUnionFindInstance(Set<Integer> site_ids)
	{
		return new UnionQuickFind(site_ids);
	}
}