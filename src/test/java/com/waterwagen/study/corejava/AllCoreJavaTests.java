package com.waterwagen.study.corejava;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({	TestJavaEnums.class, 
				TestJavaRandomQuestions.class,
				TestJavaRegex.class,
				TestJavaAnnotations.class})
public class AllCoreJavaTests {}
