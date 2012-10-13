package com.waterwagen.study.groovy;

import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test

class TestBuiltInCollectionMethods
{
	@Test
	public void testEach()
	{
		def int_list = [1,2,3,4,5,6]
		
		StringBuilder str = new StringBuilder()
		int_list.each
		{
			item ->
			
			str.append(item)
		}
		assertEquals('123456', str.toString())
		
		str = new StringBuilder()
		int_list.eachWithIndex
		{
			item,index ->
			
			str.append("${item}(index=${index})")
		}
		assertEquals('1(index=0)2(index=1)3(index=2)4(index=3)5(index=4)6(index=5)', 
						str.toString())
	}
	
	@Test
	public void testCollect()
	{
		def int_list = [1,2,3,4,5,6]
		def collect_result = int_list.collect
		{
			item -> 
			
			item*2
		}
								
		assertEquals([2,4,6,8,10,12], collect_result)
	}
	
	@Test
	public void testInject()
	{
		def int_list = [1,2,3,4,5,6]
		def inject_result = int_list.inject 
		{ 
			sum,item -> 
			
			sum + item
		}
		assertEquals(21, inject_result)
		
		inject_result = int_list.inject(5) 
		{ 
			sum,item -> 
			
			sum + item
		}
		assertEquals(26, inject_result)
		
		inject_result = int_list.inject(new StringBuilder()) 
		{ 
			result,item -> 
			
			result.append(item)
		}
		assertTrue(inject_result instanceof StringBuilder)
		assertEquals('123456', inject_result.toString())
	}
	
	@Test
	public void testFindAll()
	{
		def int_list = [1,2,3,4,5,6]
		def found_list = int_list.findAll 
		{ 
			item ->
			
			if(item % 2 == 0)
				return item
		}
		assertEquals([2,4,6], found_list)
		
		def string_list = ['at','bat','cat','car','bar']
		found_list = string_list.findAll 
		{
			item ->
			
			if(item.endsWith('at'))
				return item
		}
		assertEquals(['at','bat','cat'], found_list)

		found_list = string_list.findAll 
		{
			item ->
			
			if(item.startsWith('ca'))
				return item
		}
		assertEquals(['cat','car'], found_list)	
	}
	
	@Test
	public void testMax()
	{
		def int_list = [5,3,6,1,4,2]
		
		def max = int_list.max
		{
			item1,item2 ->
			
			item1.compareTo(item2)
		}
		assertEquals(6, max)
		
		max = int_list.max
		{
			item ->
			
			return item
		}
		assertEquals(6, max)
	}
	
	@Test
	public void testMin()
	{
		def int_list = [5,3,6,1,4,2]
		
		def min = int_list.min
		{
			item1,item2 ->
			
			item1.compareTo(item2)
		}
		assertEquals(1, min)
		
		min = int_list.min
		{
			item ->
			
			return item
		}
		assertEquals(1, min)
	}
}
