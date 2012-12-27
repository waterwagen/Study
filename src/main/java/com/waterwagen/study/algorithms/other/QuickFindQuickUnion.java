package com.waterwagen.study.algorithms.other;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class QuickFindQuickUnion implements UnionFind
{
	private final Integer[] mSites;
	private Map<Integer, Integer> mSiteToIndexHash;
	private Map<Integer, Set<Integer>> mComponentToSitesHash;
	private int mComponentCount;

	public QuickFindQuickUnion(Set<Integer> site_ids)
	{
		mSites = new Integer[site_ids.size()];
		mSiteToIndexHash = new HashMap<>();
		mComponentToSitesHash = new HashMap<>();
		int index = 0;
		for(Integer site_id : site_ids)
		{
			mSites[index] = site_id;
			mSiteToIndexHash.put(site_id, Integer.valueOf(index));
			Set<Integer> component_sites = new TreeSet<Integer>();
			component_sites.add(site_id);
			mComponentToSitesHash.put(mSites[mSiteToIndexHash.get(site_id)], component_sites);
			index++;
		}		

		mComponentCount = site_ids.size();	
	}

	@Override
	public int find(int site_id)
	{
		verifySiteIdsAreValid(site_id);
		
		return retrieveComponentForSite(site_id);
	}

	@Override
	public boolean connected(int site_id1, int site_id2)
	{
		verifySiteIdsAreValid(site_id1, site_id2);

		return retrieveComponentForSite(site_id1) == retrieveComponentForSite(site_id2);
	}

	@Override
	public void union(int site_id1, int site_id2)
	{
		verifySiteIdsAreValid(site_id1, site_id2);

		int component1 = retrieveComponentForSite(site_id1);
		int component2 = retrieveComponentForSite(site_id2);
		for(Integer comp2_siteid : mComponentToSitesHash.get(component2))
			mSites[mSiteToIndexHash.get(comp2_siteid)] = component1;

		mComponentCount--;
	}

	@Override
	public int componentCount()
	{
		return mComponentCount;
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private int retrieveComponentForSite(int site_id)
	{
		return mSites[mSiteToIndexHash.get(Integer.valueOf(site_id))].intValue();
	}

	private void verifySiteIdsAreValid(Integer... site_ids)
	{
		for(Integer site_id : site_ids)
		{
			if(!mSiteToIndexHash.containsKey(Integer.valueOf(site_id)))
				throw new IllegalArgumentException(String.format("Site id %d does not exist in this UnionFind.", site_id));			
		}
	}
}
