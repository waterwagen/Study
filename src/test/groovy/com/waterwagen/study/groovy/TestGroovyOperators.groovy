package com.waterwagen.study.groovy;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Ignore
import org.junit.Test

class TestGroovyOperators
{
	@Test
	public void testAsOperator()
	{
		def compare_closure =
		{
			string1, string2 ->
			
			string1.compareTo(string2)
		}
		
		def comparator = compare_closure as Comparator<String,String>
		
		assertThat comparator.compare("one", "apple"), greaterThan(0)
		assertThat comparator.compare("one", "two"), lessThan(0)
		assertThat comparator.compare("one", "one"), equalTo(0)
	}
}
