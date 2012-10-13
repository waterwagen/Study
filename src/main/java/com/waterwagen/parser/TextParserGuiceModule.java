package com.waterwagen.parser;

import com.google.inject.AbstractModule;

public class TextParserGuiceModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		bind(TextParser.class); // unnecessary from Guice's perspective, but included here as documentation
		bind(NumberParser.class).to(IntegerParser.class);
		bind(NumberTranslator.class).to(IntegerTranslator.class);
	}
}
