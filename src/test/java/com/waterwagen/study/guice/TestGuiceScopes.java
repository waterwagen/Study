package com.waterwagen.study.guice;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class TestGuiceScopes
{
	private interface InterfaceOne{}
	private interface InterfaceTwo{}
	private static class NoScopeClass implements InterfaceOne, InterfaceTwo {}
	@Singleton
	private static class SingletonScopeClass{}

	@Test
	public void testScopeAppliesToBindingSourceNotTarget()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(InterfaceOne.class).to(NoScopeClass.class).in(Singleton.class);
				bind(InterfaceTwo.class).to(NoScopeClass.class).in(Singleton.class);
//				bind(NoScopeClass.class).in(Singleton.class);
			}
		});
				
		NoScopeClass example1 = injector.getInstance(NoScopeClass.class);
		NoScopeClass example2 = injector.getInstance(NoScopeClass.class);
		assertTrue("Did not get different instances of the test class from Guice, but expected to because the scope applies to the source of the binding, not the target.", example1 != example2);
		
		InterfaceOne interface1 = injector.getInstance(InterfaceOne.class);
		InterfaceTwo interface2 = injector.getInstance(InterfaceTwo.class);
		assertTrue("Did not get different instances of the concrete class for different interfaces from Guice, but expected to because the scope applies to the source of the binding, not the target.", interface1 != interface2);
		
		InterfaceOne interface1_first = injector.getInstance(InterfaceOne.class);
		InterfaceOne interface1_second = injector.getInstance(InterfaceOne.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the interface.", interface1_first == interface1_second);
		
		InterfaceTwo interface2_first = injector.getInstance(InterfaceTwo.class);
		InterfaceTwo interface2_second = injector.getInstance(InterfaceTwo.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the interface.", interface2_first == interface2_second);
		
		// let's switch things up and explicitly bind the concrete class to a specific scope
		injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(InterfaceOne.class).to(NoScopeClass.class).in(Singleton.class); // this is now unnecessary but left in for comparison to the first set of bindings above
				bind(InterfaceTwo.class).to(NoScopeClass.class).in(Singleton.class); // this is now unnecessary but left in for comparison to the first set of bindings above
				bind(NoScopeClass.class).in(Singleton.class); // this binding is all that is needed now
			}
		});
		
		example1 = injector.getInstance(NoScopeClass.class);
		example2 = injector.getInstance(NoScopeClass.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the concrete class.", example1 == example2);
		
		interface1 = injector.getInstance(InterfaceOne.class);
		interface2 = injector.getInstance(InterfaceTwo.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the concrete class.", interface1 == interface2);
		
		interface1_first = injector.getInstance(InterfaceOne.class);
		interface1_second = injector.getInstance(InterfaceOne.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the interface.", interface1_first == interface1_second);
		
		interface2_first = injector.getInstance(InterfaceTwo.class);
		interface2_second = injector.getInstance(InterfaceTwo.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to because the binding specified the scope for the interface.", interface2_first == interface2_second);
	}

	@Test
	public void testSingletonScopeByLinkedBinding()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(NoScopeClass.class).in(Singleton.class);
			}
		});
				
		NoScopeClass example1 = injector.getInstance(NoScopeClass.class);
		NoScopeClass example2 = injector.getInstance(NoScopeClass.class);
		assertTrue("Did not get the same instance of the test class from Guice, but expected to.", example1 == example2);
	}

	@Test
	public void testSingletonScopeByAnnotation()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() {}
		});
				
		SingletonScopeClass example1 = injector.getInstance(SingletonScopeClass.class);
		SingletonScopeClass example2 = injector.getInstance(SingletonScopeClass.class);
		assertTrue("Did not get different instances of the test class from Guice, but expected to.", example1 == example2);
	}

	@Test
	public void testDefaultScope()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() {}
		});
				
		NoScopeClass example1 = injector.getInstance(NoScopeClass.class);
		NoScopeClass example2 = injector.getInstance(NoScopeClass.class);
		assertTrue("Did not get different instances of the test class from Guice, but expected to.", example1 != example2);
	}
}