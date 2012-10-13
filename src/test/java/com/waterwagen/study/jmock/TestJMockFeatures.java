package com.waterwagen.study.jmock;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TestJMockFeatures
{
	private final Mockery context = new JUnit4Mockery();

	/////////////
	/// Tests ///
	/////////////
	
	@Test
	public void tryPurposefulFailure()
	{
		final Doer doer = context.mock(Doer.class);
		Manager manager = new Manager();
		manager.add(doer);
		
		context.checking(new Expectations() 
		{{
			final Sequence doer_steps = context.sequence("doer_steps");
			final States manager_state = context.states("manager_state").startsAs("unprocessed");

			oneOf(doer).step1(); 
				inSequence(doer_steps); 
				will(returnValue(true));
				then(manager_state.is("step1Completed"));
			oneOf(doer).step2(); 
				inSequence(doer_steps); 
				when(manager_state.is("step1Completed")); 
				will(returnValue(false));
				then(manager_state.is("step2Completed"));
			never(doer).step3(); 
		}});
		
		assertTrue("Expected value of false as a result of manage method.", !manager.manage());
	}
	
	@Test
	public void useBasicRangeOfExpectations()
	{
		final Doer doer = context.mock(Doer.class);
		Manager manager = new Manager();
		manager.add(doer);
		
		context.checking(new Expectations() 
		{{
			final Sequence doer_steps = context.sequence("doer_steps");
			final States manager_state = context.states("manager_state").startsAs("unprocessed");

			oneOf(doer).step1(); 
				inSequence(doer_steps); 
				will(returnValue(true));
				then(manager_state.is("step1Completed"));
			oneOf(doer).step2(); 
				inSequence(doer_steps); 
				when(manager_state.is("step1Completed")); 
				will(returnValue(true));
				then(manager_state.is("step2Completed"));
			oneOf(doer).step3(); 
				inSequence(doer_steps); 
				when(manager_state.is("step2Completed")); 
				will(returnValue(true));
		}});
		
		assertTrue("Expected value of true as a result of manage method.", manager.manage());
	}
	
	/////////////////////
	/// Utility Types ///
	/////////////////////
	
	private static class Manager
	{
		private Set<Doer> mDoers = new HashSet<>();

		public void add(Doer subscriber)
		{
			mDoers.add(subscriber);
		}

		public boolean manage()
		{
			boolean result = true;
			for (Doer doer : mDoers)
			{
				if(result = doer.step1() && result)
					if(result = doer.step2() && result)
						result = doer.step3() && result;
			}
			return result;
		}

	}

	public interface Doer 
	{
	    public boolean step1();
	    public boolean step2();
	    public boolean step3();
	}
}
