package com.waterwagen.study.guice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class TestGuiceInjections
{
	private static final String OPTIONALTEST_DEFAULT_VALUE = "DEFAULT";
	private static final String OPTIONALTEST_ANNO_NAME = "optionaltest";

	@Test
	public void testMethodInjectionOptional()
	{
		final String test_str = "this is a really dummy string";

		Injector injector_nostring = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(StringHolderMethodInjectedOptional.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
			}
		});
		Injector injector_withstring = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(StringHolderMethodInjectedOptional.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				bind(String.class).annotatedWith(Names.named(OPTIONALTEST_ANNO_NAME)).toInstance(test_str);
			}
		});
				
		StringHolderMethodInjectedOptional holder_withoutstring = injector_nostring.getInstance(StringHolderMethodInjectedOptional.class);
		assertTrue("StringHolder doesn't contain a valid String reference.", holder_withoutstring.getString() != null);
		assertEquals("Unexpected String value set on the StringHolder.", OPTIONALTEST_DEFAULT_VALUE, holder_withoutstring.getString());
		StringHolderMethodInjectedOptional holder_withstring = injector_withstring.getInstance(StringHolderMethodInjectedOptional.class);
		assertTrue("StringHolder doesn't contain a valid String reference.", holder_withstring.getString() != null);
		assertEquals("Unexpected String value set on the StringHolder.", test_str, holder_withstring.getString());
	}

	@Test
	public void testFieldInjection()
	{
		final String test_str = "this is a really dummy string";

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(StringHolderFieldInjected.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				bind(String.class).toInstance(test_str);
			}
		});
				
		StringHolderFieldInjected holder = injector.getInstance(StringHolderFieldInjected.class);
		assertTrue("StringHolder doesn't contain a valid String reference.", holder.getString() != null);
		assertEquals("Unexpected String value set on the StringHolder.", test_str, holder.getString());
	}
	
	@Test
	public void testMethodInjection()
	{
		final String test_str = "this is a really dummy string";

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(StringHolderMethodInjectedMandatory.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				bind(String.class).toInstance(test_str);
			}
		});
				
		StringHolderMethodInjectedMandatory holder = injector.getInstance(StringHolderMethodInjectedMandatory.class);
		assertTrue("StringHolder doesn't contain a valid String reference.", holder.getString() != null);
		assertEquals("Unexpected String value set on the StringHolder.", test_str, holder.getString());
	}
	
	@Test
	public void testConstructorInjection()
	{
		final String test_str = "this is a really dummy string";

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(StringHolderConstructorInjected.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				bind(String.class).toInstance(test_str);
			}
		});
				
		StringHolderConstructorInjected holder = injector.getInstance(StringHolderConstructorInjected.class);
		assertTrue("StringHolder doesn't contain a valid String reference.", holder.getString() != null);
		assertEquals("Unexpected String value set on the StringHolder.", test_str, holder.getString());
	}
	
	@Test
	public void testMembersInjector()
	{
		final List<String> test_list = Arrays.asList("str1", "str2");
		final String test_str = "this is a really dummy string";

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(MockMemberInjectorHolder.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				
				bind(String.class).toInstance(test_str);
				bind(new TypeLiteral<List<String>>() {}).toInstance(test_list);
			}
		});
				
		MockMemberInjectorHolder holder = injector.getInstance(MockMemberInjectorHolder.class);
		MembersInjector<MemberInjectableObject> members_injector = holder.getInjector();
		MemberInjectableObject injectable = new MemberInjectableObject();
		members_injector.injectMembers(injectable);
		assertTrue("Failed to get a reference to a MemberInjectableObject.", injectable != null);
		assertEquals("Unexpected first object value set on the MemberInjectableObject.", test_str, injectable.getString());
		assertEquals("Unexpected second object value set on the MemberInjectableObject.", test_list, injectable.getListOfStrings());
	}

	///////////////////////
	/// Utility Classes ///
	///////////////////////

	private static class StringHolderFieldInjected
	{
		@Inject
		private String mString = null;
		
		private String getString()
		{
			return this.mString;
		}
	}

	private static class StringHolderMethodInjectedMandatory
	{
		private String mString = null;
		
		private String getString()
		{
			return this.mString;
		}
		
		@SuppressWarnings("unused")
		@Inject
		private void setString(String string)
		{
			mString = string;
		}
	}
	
	private static class StringHolderMethodInjectedOptional
	{
		private String mString = OPTIONALTEST_DEFAULT_VALUE;
		
		private String getString()
		{
			return this.mString;
		}
		
		@SuppressWarnings("unused")
		@Inject(optional=true)
		private void setString(@Named(OPTIONALTEST_ANNO_NAME) String string)
		{
			mString = string;
		}
	}
	
	private static class StringHolderConstructorInjected
	{
		private String mString = null;
		
		@Inject
		private StringHolderConstructorInjected(String str)
		{
			mString = str;
		}

		private String getString()
		{
			return this.mString;
		}
	}
	
	private static class MockMemberInjectorHolder
	{
		private MembersInjector<MemberInjectableObject> mInjector = null;

		@Inject
		private MockMemberInjectorHolder(MembersInjector<MemberInjectableObject> injector)
		{
			this.mInjector = injector;
		}

		private MembersInjector<MemberInjectableObject> getInjector()
		{
			return this.mInjector;
		}
	}
	
	private static class MemberInjectableObject
	{
		private String mObjOne = null;
		private List<String> mObjTwo = null;
		
		private String getString()
		{
			return this.mObjOne;
		}

		private List<String> getListOfStrings()
		{
			return this.mObjTwo;
		}

		@SuppressWarnings("unused")
		@Inject
		private void setObjOne(String one)
		{
			mObjOne = one;
		}

		@SuppressWarnings("unused")
		@Inject
		private void setObjTwo(List<String> two)
		{
			mObjTwo = two;
		}
	}	
}
