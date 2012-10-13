package com.waterwagen.study.guice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestGuiceBindings.class, 
				TestGuiceInjections.class,
				TestGuiceScopes.class})
public class AllGuiceTests
{

}
