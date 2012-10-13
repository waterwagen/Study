package com.waterwagen.study.guice;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.waterwagen.parser.TextParser;
import com.waterwagen.parser.TextParserGuiceModule;

public class TestGuiceBindings
{
	@Test
	public void testBindingProperties() throws IOException
	{
		String hostname_val = "localhost";
		String username_val = "dummy";
		String password_val = "123456789";

		final Properties properties = new Properties();
		properties.put("hostname", hostname_val);
		properties.put("username", username_val);
		properties.put("password", password_val);
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(PropertiesExample.class); // not strictly necessary, only here to document configuration
				Names.bindProperties(binder(), properties);
			}
		});
				
		PropertiesExample example = injector.getInstance(PropertiesExample.class);
		assertEquals("Unexpected property value found in the injected object.", hostname_val, example.getHostname());
		assertEquals("Unexpected property value found in the injected object.", username_val, example.getUsername());
		assertEquals("Unexpected property value found in the injected object.", password_val, example.getPassword());
	}

	@Test
	public void testBuiltInBinding()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(BuiltInBindingsExample.class); // not strictly necessary, only here to signal Guice to eagerly prepare dependencies
				bind(ParentClass.class).to(ChildClass.class); // needed for Provider<ParentClass>, otherwise there is no concrete class known by Guice to provide
				bind(TestService.class).to(TestServiceImpl.class); // needed for Provider<ParentClass>, otherwise there is no concrete class known by Guice to provide
			}
		});
				
		BuiltInBindingsExample example = injector.getInstance(BuiltInBindingsExample.class);
		assertTrue("Failed to get an actual reference to a " + BuiltInBindingsExample.class.getSimpleName(), example != null);
		MembersInjector<FruitHolder> membersinjector = example.getMemberInjFruitholderInjector();
		assertTrue("Failed to get an actual reference to a " + membersinjector.getClass().getSimpleName(), membersinjector != null);
		Injector injected_injector = example.getInjector();
		assertTrue("Failed to get an actual reference to a " + injected_injector.getClass().getSimpleName(), injected_injector != null);
		Logger logger = example.getLogger();
		assertTrue("Failed to get an actual reference to a " + logger.getClass().getSimpleName(), logger != null);
		Provider<ParentClass> provider_parentclass = example.getProviderParentClass();
		assertTrue("Failed to get an actual reference to a " + provider_parentclass.getClass().getSimpleName(), provider_parentclass != null);
		Provider<ChildClass> provider_childclass = example.getProviderChildClass();
		assertTrue("Failed to get an actual reference to a " + provider_childclass.getClass().getSimpleName(), provider_childclass != null);
		Provider<TestService> provider_testservice = example.getProviderTestService();
		assertTrue("Failed to get an actual reference to a " + provider_testservice.getClass().getSimpleName(), provider_testservice != null);
		Provider<TestServiceImpl> provider_testserviceimpl = example.getProviderTestServiceImpl();
		assertTrue("Failed to get an actual reference to a " + provider_testserviceimpl.getClass().getSimpleName(), provider_testserviceimpl != null);
	}

	@Test
	public void testJustInTimeBinding()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
			}
		});
				
		NoInject noinject = injector.getInstance(NoInject.class); // no explicit binding in the module needed and no inject annotation on the constructor because it's no-args		
		assertTrue("No object was actually created.", noinject != null);
	}
	
	@Test
	public void testConstructorBindingType()
	{
		final String test_string = "is this a test string";
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				try 
			    {
			    	bind(TestService.class).toConstructor(TestServiceImpl.class.getConstructor(String.class));
			    	bind(ParentClass.class).toConstructor(ChildClass.class.getConstructor(String.class));
			    	bind(String.class).toInstance(test_string);
			    } 
			    catch (NoSuchMethodException e) 
			    {
			    	addError(e);
			    }
			}
		});
				
		TestService service = injector.getInstance(TestService.class);		
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", service instanceof TestServiceImpl);
		assertEquals("Unexpected String value contained in the TestService.", test_string, service.getString());
		
		ParentClass parent = injector.getInstance(ParentClass.class);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", parent instanceof ChildClass);
		assertEquals("Unexpected String value contained in the ParentClass.", test_string, parent.getString());
		
		ThirdPartyClass third = injector.getInstance(ThirdPartyClass.class);
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", third.getService() instanceof TestServiceImpl);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", third.getParent() instanceof ChildClass);
	}
	
	@Test
	public void testUntargetedBindingType()
	{
		final String test_string1 = "is this a test string";
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(ChildClass.class); // not strictly necessary, but does cause Guice to eagerly prepare dependencies
				bind(String.class).toInstance(test_string1);
				
				bind(TestServiceImpl.class).annotatedWith(TestAnno.class).to(TestServiceImpl.class); // if annotating an untargeted binding, need to take this somewhat weird approach
			}
		});
		
		ChildClass service = injector.getInstance(ChildClass.class);		
		assertEquals("Unexpected String value contained in the ChildClass.", test_string1, service.getString());
		
		AnotherThirdPartyClass third = injector.getInstance(AnotherThirdPartyClass.class);
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", third.getService() instanceof TestServiceImpl);
	}
	
	@Test
	public void testProviderBindingType()
	{
		final String test_string = "super duper test string";
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure() 
			{
				bind(TestService.class).toProvider(TestServiceProvider.class);
				bind(ParentClass.class).toProvider(ParentClassProvider.class);
				bind(String.class).toInstance(test_string);
			}
		});
		
		TestService service = injector.getInstance(TestService.class);		
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", service instanceof TestServiceImpl);
		assertEquals("Unexpected String value contained in the TestService.", test_string, service.getString());
		
		ParentClass parent = injector.getInstance(ParentClass.class);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", parent instanceof ChildClass);
		assertEquals("Unexpected String value contained in the ParentClass.", test_string, parent.getString());
		
		ThirdPartyClass third = injector.getInstance(ThirdPartyClass.class);
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", third.getService() instanceof TestServiceImpl);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", third.getParent() instanceof ChildClass);
	}

	@Test
	public void testProvidesMethodsBindingType()
	{
		final String test_string = "this is a test string";
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@SuppressWarnings("unused")
			@Provides
			protected TestService provideTestService(@TestAnno String test_param)
			{
				return new TestServiceImpl(test_param);
			}
			@SuppressWarnings("unused")
			@Provides
			protected ParentClass provideParentClass(@TestAnno String test_param)
			{
				return new ChildClass(test_param);
			}
			@Override
			protected void configure() 
			{
				bind(String.class).annotatedWith(TestAnno.class).toInstance(test_string);
			}
		});
		
		TestService service = injector.getInstance(TestService.class);		
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", service instanceof TestServiceImpl);
		assertEquals("Unexpected String value contained in the TestService.", test_string, service.getString());
		
		ParentClass parent = injector.getInstance(ParentClass.class);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", parent instanceof ChildClass);
		assertEquals("Unexpected String value contained in the ParentClass.", test_string, parent.getString());
		
		ThirdPartyClass third = injector.getInstance(ThirdPartyClass.class);
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", third.getService() instanceof TestServiceImpl);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", third.getParent() instanceof ChildClass);
	}
		
	@Test
	public void testLinkedBindingType()
	{
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(TestService.class).to(TestServiceImpl.class);
				bind(ParentClass.class).to(ChildClass.class);
			}
		});
		
		TestService service = injector.getInstance(TestService.class);		
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", service instanceof TestServiceImpl);
		
		ParentClass parent = injector.getInstance(ParentClass.class);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", parent instanceof ChildClass);
		
		ThirdPartyClass third = injector.getInstance(ThirdPartyClass.class);
		assertTrue("Expected a TestServiceImpl instance to be returned as a TestService.", third.getService() instanceof TestServiceImpl);
		assertTrue("Expected a ChildClass instance to be returned as a ParentClass.", third.getParent() instanceof ChildClass);
	}
	
	@Test
	public void testAnnotationBindingType()
	{
		final String best_string = "ice cream";
		final String worst_string = "brussel sprouts";
		final String avg_string = "cereal";
		final String my_string = "this is my annotated string";
		
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(String.class).annotatedWith(Names.named("bestString")).toInstance(best_string);
				bind(String.class).annotatedWith(Names.named("worstString")).toInstance(worst_string);
				bind(String.class).annotatedWith(Names.named("averageString")).toInstance(avg_string);
				bind(String.class).annotatedWith(TestAnno.class).toInstance(my_string);
			}
		});
		
		StringHolder holder = injector.getInstance(StringHolder.class);		
		assertEquals(best_string , holder.getBestString());
		assertEquals(worst_string , holder.getWorstString());
		assertEquals(avg_string, holder.getAverageString());
		assertEquals(my_string, holder.getMyString());
	}
	
	@Test
	public void testInstanceBindingType()
	{
		final Fruit fruit1 = Fruit.APPLE;
		final Fruit fruit2 = Fruit.ORANGE;
		final Fruit fruit3 = Fruit.GRAPE;
		final String market_name = "Fresh Fruit Market";

		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(new TypeLiteral<List<Fruit>>(){}).toInstance(Arrays.asList(fruit1, fruit2, fruit3)); // this is how you do generics, weird but necessary
				bind(String.class).toInstance(market_name);
			}
		});
		
		FruitHolder holder = injector.getInstance(FruitHolder.class);
		assertEquals(fruit1, holder.getFruits().get(0));
		assertEquals(fruit2, holder.getFruits().get(1));
		assertEquals(fruit3, holder.getFruits().get(2));
		assertEquals(market_name, holder.getMarketName());
	}

	@Test
	public void testBasicGuiceUsageThroughTextParser()
	{
		Injector injector = Guice.createInjector(new TextParserGuiceModule());
		
		TextParser parser = injector.getInstance(TextParser.class);
		List<Integer> result = parser.parseNumericalValues("asfasfasdfasgoogletreeelevengoooninethousandsevenhundredfortysixbumblebeetwentythree);lkjlj");
		assertEquals("Unexpected number of numbers found in the parsed string.", 3, result.size());
		assertEquals("Unexpected parsed value found.", Integer.valueOf(11), result.get(0));
		assertEquals("Unexpected parsed value found.", Integer.valueOf(9_746), result.get(1));
		assertEquals("Unexpected parsed value found.", Integer.valueOf(23), result.get(2));
	}

	///////////////////////
	/// Utility Classes ///
	///////////////////////
	
	@BindingAnnotation 
	@Target({FIELD,PARAMETER,METHOD}) 
	@Retention(RUNTIME)
	private @interface TestAnno {}
	
	private enum Fruit
	{
		ORANGE, APPLE, GRAPE, OTHER
	}

	private static class NoInject
	{
		private NoInject() {}
	}
	
	private interface TestService
	{
		public void runBusinessLogic();
		public String getString();
	}
	
	private static class TestServiceImpl implements TestService
	{
		private String mString;

		@Inject
		public TestServiceImpl(String test_param)
		{
			mString = test_param;
		}

		@Override
		public void runBusinessLogic()
		{
			// nothing
			
		}

		@Override
		public String getString()
		{
			return mString;
		}
		
	}

	private abstract static class ParentClass 
	{
		protected abstract String getString();
	}
	
	private static class ChildClass extends ParentClass 
	{
		private String mString;
		
		@Inject
		public ChildClass(String test_param)
		{
			mString = test_param;
		}
		
		@Override
		protected String getString()
		{
			return this.mString;
		}
	}
	
	private static class ThirdPartyClass
	{
		private TestService mService;
		private ParentClass mParent;

		@Inject
		private ThirdPartyClass(TestService service, ParentClass parent)
		{
			this.mService = service;
			this.mParent = parent;
		}

		private TestService getService()
		{
			return this.mService;
		}

		private ParentClass getParent()
		{
			return this.mParent;
		}
	}
	
	private static class AnotherThirdPartyClass
	{
		private TestService mService;

		@Inject
		private AnotherThirdPartyClass(@TestAnno TestServiceImpl service)
		{
			this.mService = service;
		}

		private TestService getService()
		{
			return this.mService;
		}
	}
	
	private static class ParentClassProvider implements Provider<ParentClass>
	{
		private String mString;
		
		@Inject
		private ParentClassProvider(String string)
		{
			this.mString = string;
		}

		@Override
		public ParentClass get()
		{
			return new ChildClass(mString);
		}	
	}

	private static class TestServiceProvider implements Provider<TestService>
	{
		private String mString;
		
		@Inject
		private TestServiceProvider(String string)
		{
			this.mString = string;
		}

		@Override
		public TestService get()
		{
			return new TestServiceImpl(mString);
		}			
	}

	private static class StringHolder
	{
		private String mBestString;
		private String mWorstString;
		private String mAverageString;
		private String myString;

		@Inject
		private StringHolder(@Named("bestString") String bestString, 
								@Named("worstString") String worstString,
								@Named("averageString") String averageString,
								@TestAnno String my_string)
		{
			this.mBestString = bestString;
			this.mWorstString = worstString;
			this.mAverageString = averageString;
			this.myString = my_string;
		}

		private String getBestString()
		{
			return this.mBestString;
		}

		private String getWorstString()
		{
			return this.mWorstString;
		}

		private String getAverageString()
		{
			return this.mAverageString;
		}

		private String getMyString()
		{
			return this.myString;
		}
	}

	private static class FruitHolder
	{
		private List<Fruit> mFruits;
		private String mMarketName;
		
		@Inject
		private FruitHolder(List<Fruit> fruits, String marketName)
		{
			this.mFruits = fruits;
			this.mMarketName = marketName;
		}

		List<Fruit> getFruits()
		{
			return mFruits;
		}

		String getMarketName()
		{
			return this.mMarketName;
		}
	}

	private static class PropertiesExample
	{
		private final String mHostname;
		private final String mUsername;
		private final String mPassword;

		@Inject
		private PropertiesExample(@Named("hostname") String hostname, @Named("username") String username, @Named("password") String password)
		{
			mHostname = hostname;
			mUsername = username;
			mPassword = password;
		}

		private String getHostname()
		{
			return mHostname;
		}

		private String getUsername()
		{
			return mUsername;
		}

		private String getPassword()
		{
			return mPassword;
		}
	}

	private static class BuiltInBindingsExample
	{
		private Logger mLogger;
		private Provider<ParentClass> mProviderParentClass;		
		private Provider<ChildClass> mProviderChildClass;
		private Provider<TestService> mProviderTestService;
		private Provider<TestServiceImpl> mProviderTestServiceImpl;
		private Injector mInjector;
		private MembersInjector<FruitHolder> mMemberInjFruitHolderInjector;

		@Inject
		private BuiltInBindingsExample(Logger logger,
										Provider<ParentClass> providerParentClass,
										Provider<ChildClass> providerChildClass,
										Provider<TestService> providerTestService,
										Provider<TestServiceImpl> providerTestServiceImpl,
										Injector injector,
										MembersInjector<FruitHolder> fruitholder_injector)
		{
			this.mLogger = logger;
			this.mProviderParentClass = providerParentClass;
			this.mProviderChildClass = providerChildClass;
			this.mProviderTestService = providerTestService;
			this.mProviderTestServiceImpl = providerTestServiceImpl;
			this.mInjector = injector;
			this.mMemberInjFruitHolderInjector = fruitholder_injector;
		}

		private Logger getLogger()
		{
			return this.mLogger;
		}

		private Provider<ParentClass> getProviderParentClass()
		{
			return this.mProviderParentClass;
		}

		private Provider<ChildClass> getProviderChildClass()
		{
			return this.mProviderChildClass;
		}

		private Provider<TestService> getProviderTestService()
		{
			return this.mProviderTestService;
		}

		private Provider<TestServiceImpl> getProviderTestServiceImpl()
		{
			return this.mProviderTestServiceImpl;
		}

		private Injector getInjector()
		{
			return this.mInjector;
		}

		private MembersInjector<FruitHolder> getMemberInjFruitholderInjector()
		{
			return this.mMemberInjFruitHolderInjector;
		}
	}
}
