package com.waterwagen.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NumberParsingConstants
{

	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	static final Map<String, Integer> NAME_TO_ATOMIC_NUMBER_MAP = new HashMap() 
	{
		{
			put("zero", Integer.valueOf(0));
			put("one", Integer.valueOf(1));
			put("two", Integer.valueOf(2));
			put("three", Integer.valueOf(3));
			put("four", Integer.valueOf(4));
			put("five", Integer.valueOf(5));
			put("six", Integer.valueOf(6));
			put("seven", Integer.valueOf(7));
			put("eight", Integer.valueOf(8));
			put("nine", Integer.valueOf(9));
			put("ten", Integer.valueOf(10));
			put("eleven", Integer.valueOf(11));
			put("twelve", Integer.valueOf(12));
			put("thirteen", Integer.valueOf(13));
			put("fourteen", Integer.valueOf(14));
			put("fifteen", Integer.valueOf(15));
			put("sixteen", Integer.valueOf(16));
			put("seventeen", Integer.valueOf(17));
			put("eighteen", Integer.valueOf(18));
			put("nineteen", Integer.valueOf(19));
			put("twenty", Integer.valueOf(20));
			put("thirty", Integer.valueOf(30));
			put("forty", Integer.valueOf(40));
			put("fifty", Integer.valueOf(50));
			put("sixty", Integer.valueOf(60));
			put("seventy", Integer.valueOf(70));
			put("eighty", Integer.valueOf(80));
			put("ninety", Integer.valueOf(90));
			put("hundred", Integer.valueOf(100));
			put("thousand", Integer.valueOf(1_000));
			put("million", Integer.valueOf(1_000_000));
			put("billion", Integer.valueOf(1_000_000_000));
		}
	};
	
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	static final Set<String> THOUSANDS_BUCKET_BASENUMS = new HashSet()
	{
		{
			add("thousand");
			add("million");
			add("billion");
		}
	};

}
