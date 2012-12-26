package com.waterwagen.study.algorithms.other;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.waterwagen.Command;

public abstract class TestUnionFind
{
	//////////////////////
	/// Setup/Teardown ///
	//////////////////////

	private UnionFind mUnionFind;
	private List<Integer> mSiteIdList;

	@Before
	public void setUp() throws Exception
	{
		Set<Integer> mSiteIds = buildSiteIds();
		mSiteIdList = new ArrayList<>(mSiteIds);
		mUnionFind = newUnionFindInstance(mSiteIds);
	}

	/////////////
	/// Tests ///
	/////////////
	
	@Test
	public void testFindBeforeConnectionReturnsComponentNameSameAsSiteId()
	{
		for(int i = 0; i < mSiteIdList.size(); i++)
		{
			int site_id = mSiteIdList.get(i);
			assertThat(mUnionFind.find(site_id), equalTo(site_id));
		}
	}

	@Test
	public void testFindThrowsExceptionOnSearchForNonExistentSite()
	{
		Collections.sort(mSiteIdList);

		// check on site_id below lower bound
		int site_id = mSiteIdList.get(0) - 1;
		verifyExceptionForNonExistentSiteId(new FindCommand(mUnionFind, site_id), site_id);
		
		// check on site_id above upper bound
		site_id = mSiteIdList.get(mSiteIdList.size() - 1) + 1;
		verifyExceptionForNonExistentSiteId(new FindCommand(mUnionFind, site_id), site_id);
	}

	@Test
	public void testConnectedReturnsFalseBeforeAnyConnectionsAreMade()
	{
		for(int i = 0; i < mSiteIdList.size(); i++)
		{
			Integer site1 = mSiteIdList.get(i);
			for(int j = i + 1; j < mSiteIdList.size(); j++)
			{
				Integer site2 = mSiteIdList.get(j);
				assertThat(mUnionFind.connected(site1.intValue(), site2.intValue()), equalTo(false));
			}
		}
	}

	@Test 
	public void testConnectedReturnsTrueAfterUnionOfTwoSites()
	{
		int site1 = mSiteIdList.get(0);
		int site2 = mSiteIdList.get(1);
		mUnionFind.union(site1, site2);
		
		assertThat(mUnionFind.connected(site1, site2), equalTo(true));
	}

	@Test 
	public void testConnectedIsTransitive()
	{
		int site1 = mSiteIdList.get(0);
		int site2 = mSiteIdList.get(1);
		mUnionFind.union(site1, site2);
		assertThat(mUnionFind.connected(site1, site2), equalTo(true));

		int site3 = mSiteIdList.get(mSiteIdList.size() - 1);
		assertThat(mUnionFind.connected(site1, site3), equalTo(false));

		mUnionFind.union(site2, site3);
		assertThat(mUnionFind.connected(site1, site3), equalTo(true));
	}
	
	@Test
	public void testConnectedIsReflexive()
	{
		int site1 = mSiteIdList.get(0);
		assertThat(mUnionFind.connected(site1, site1), equalTo(true));
	}
	
	@Test
	public void testConnectedThrowsExceptionForNonExistentSiteIds()
	{
		Collections.sort(mSiteIdList);

		// check on invalid first site id argument
		int site_id1 = mSiteIdList.get(0) - 1;
		int site_id2 = mSiteIdList.get(mSiteIdList.size() - 1);
		verifyExceptionForNonExistentSiteId(new ConnectedCommand(mUnionFind, site_id1, site_id2), site_id1);

		// check on invalid second site id argument
		site_id1 = mSiteIdList.get(0);
		site_id2 = mSiteIdList.get(mSiteIdList.size() - 1) + 1;
		verifyExceptionForNonExistentSiteId(new ConnectedCommand(mUnionFind, site_id1, site_id2), site_id2);

		// check on both site id arguments being invalid
		site_id1 = mSiteIdList.get(0) - 1;
		site_id2 = mSiteIdList.get(mSiteIdList.size() - 1) + 1;
		verifyExceptionForNonExistentSiteId(new ConnectedCommand(mUnionFind, site_id1, site_id2), site_id1);
	}
	
	@Test
	public void testUnionThrowsExceptionForNonExistentSiteIds()
	{
		Collections.sort(mSiteIdList);

		// check on invalid first site id argument
		int site_id1 = mSiteIdList.get(0) - 1;
		int site_id2 = mSiteIdList.get(mSiteIdList.size() - 1);
		verifyExceptionForNonExistentSiteId(new UnionCommand(mUnionFind, site_id1, site_id2), site_id1);

		// check on invalid second site id argument
		site_id1 = mSiteIdList.get(0);
		site_id2 = mSiteIdList.get(mSiteIdList.size() - 1) + 1;
		verifyExceptionForNonExistentSiteId(new UnionCommand(mUnionFind, site_id1, site_id2), site_id2);

		// check on both site id arguments being invalid
		site_id1 = mSiteIdList.get(0) - 1;
		site_id2 = mSiteIdList.get(mSiteIdList.size() - 1) + 1;
		verifyExceptionForNonExistentSiteId(new UnionCommand(mUnionFind, site_id1, site_id2), site_id1);
	}
	
	@Test
	public void testComponentCountIsEqualToNumberOfSitesBeforeAnyUnionsAreDone()
	{
		assertThat(mUnionFind.componentCount(), equalTo(mSiteIdList.size()));
	}
	
	@Test
	public void testComponentCountIsReducedByOneByEachUnion()
	{
		int site_id1 = mSiteIdList.get(0);
		int component_count = mUnionFind.componentCount();
		for(int i = 1; i < mSiteIdList.size(); i++)
		{
			mUnionFind.union(site_id1, mSiteIdList.get(i));
			assertThat(mUnionFind.componentCount(), equalTo(component_count - 1));
			component_count--;
		}
		assertThat(mUnionFind.componentCount(), equalTo(1));
	}
	
	//////////////////////
	/// Subclass Impl. ///
	//////////////////////
	
	abstract UnionFind newUnionFindInstance(Set<Integer> site_ids);

	////////////////////////////////
	/// Helper Assertion Methods ///
	////////////////////////////////

	private void verifyExceptionForNonExistentSiteId(Command command, int site_id)
	{
		try
		{
			command.execute();
			fail("Expected an exception to be thrown for an invalid site id.");
		}
		catch(IllegalArgumentException exc)
		{
			assertThat(exc.getMessage(), equalTo(String.format("Site id %d does not exist in this UnionFind.", site_id)));
		}	
	}
	
	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private Set<Integer> buildSiteIds()
	{
		Set<Integer> result = new HashSet<>();

		int site_total = 50;
		int max_value = 1000;
		Random random = new Random(System.currentTimeMillis());
		while(result.size() < site_total)
			result.add(Integer.valueOf(random.nextInt(max_value)));
		
		return result;
	}

	//////////////////////
	/// Helper Classes ///
	//////////////////////
	
	private static class ConnectedCommand implements Command
	{
		private UnionFind mUnionFind;
		private int mSiteId1;
		private int mSiteId2;

		private ConnectedCommand(UnionFind union_find, int site_id1, int site_id2)
		{
			mUnionFind = union_find;
			mSiteId1 = site_id1;
			mSiteId2 = site_id2;
		}

		@Override
		public void execute()
		{
			mUnionFind.connected(mSiteId1, mSiteId2);
		}
	}
	
	private static class FindCommand implements Command
	{
		private int mSiteId;
		private UnionFind mUnionFind;

		private FindCommand(UnionFind union_find, int site_id)
		{
			mSiteId = site_id;
			mUnionFind = union_find;
		}

		@Override
		public void execute()
		{
			mUnionFind.find(mSiteId);
		}
	}
	
	private static class UnionCommand implements Command
	{
		private UnionFind mUnionFind;
		private int mSiteId1;
		private int mSiteId2;

		private UnionCommand(UnionFind union_find, int site_id1, int site_id2)
		{
			mUnionFind = union_find;
			mSiteId1 = site_id1;
			mSiteId2 = site_id2;
		}

		@Override
		public void execute()
		{
			mUnionFind.union(mSiteId1, mSiteId2);
		}
	}
}