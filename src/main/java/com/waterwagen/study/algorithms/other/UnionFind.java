package com.waterwagen.study.algorithms.other;

public interface UnionFind
{
	int find(int site_id);
	
	boolean connected(int site_id1, int site_id2);
	
	void union(int site_id1, int site_id2);
	
	int componentCount();
}
