package com.waterwagen.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public class TextParser 
{
	private NumberTranslator mNumberTranslator;
	private NumberParser mNumberParser;

	@Inject
	public TextParser(NumberTranslator numberTranslator, NumberParser numberParser)
	{
		this.mNumberTranslator = numberTranslator;
		this.mNumberParser = numberParser;
	}

	public List<Integer> parseNumericalValues(String string) 
	{
		List<String> number_names = mNumberParser.parse(string);
		List<Integer> result = new ArrayList<>();
		for(String number_name : number_names)
			result.add(mNumberTranslator.translate(number_name));
		return result;
	}
}