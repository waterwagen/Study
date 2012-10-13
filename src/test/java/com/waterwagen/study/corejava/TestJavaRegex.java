package com.waterwagen.study.corejava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestJavaRegex
{
	@Test
	public void testMultipleEmbeddedFlags()
	{
		String str = 	"LINE1\n" + 
						"lInE2\n";
		String pattern_str = "line\\d.+line\\d.+";
		
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		assertThat(matcher.find(), is(false));
		
		matcher = Pattern.compile(pattern_str, Pattern.DOTALL).matcher(str);
		assertThat(matcher.find(), is(false));		
		
		matcher = Pattern.compile("(?s)" + pattern_str).matcher(str);
		assertThat(matcher.find(), is(false));				
		
		matcher = Pattern.compile(pattern_str, Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(str);
		assertThat(matcher.find(), is(true));		
		
		matcher = Pattern.compile("(?si)" + pattern_str).matcher(str);
		assertThat(matcher.find(), is(true));				
	}
	
	@Test
	public void testLiteralMode()
	{
		String str_containing_metacharacters = 	"line1\\sMORETEXT\\d";
		String str_containing_literals = "line1 MORETEXT9";
		String pattern_str = str_containing_metacharacters;
		
		Matcher matcher = Pattern.compile(pattern_str).matcher(str_containing_metacharacters);
		assertThat(matcher.find(), is(false));
		
		matcher = Pattern.compile(pattern_str).matcher(str_containing_literals);
		assertThat(matcher.find(), is(true));
		
		matcher = Pattern.compile(pattern_str, Pattern.LITERAL).matcher(str_containing_metacharacters);
		assertThat(matcher.find(), is(true));		
	}

	@Test
	public void testMultilineMode()
	{
		String str = 	"line1\n" +
						"line2\n";
		String pattern_str = 	"^line\\d$\\s" +
								"^line\\d$\\s";
		
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		assertThat(matcher.find(), is(false));
		
		matcher = Pattern.compile(pattern_str, Pattern.MULTILINE).matcher(str);
		assertThat(matcher.find(), is(true));		
		
		matcher = Pattern.compile("(?m)" + pattern_str).matcher(str);
		assertThat(matcher.find(), is(true));		
	}

	@Test
	public void testCommentsMode()
	{
		String str = 	"line1\n" +
						"line2\n";
		String pattern_str = 	"line\\d\\n  # this matches on the first line\n" +
								"line\\d\\n  # this matches on the second line, hey I can put comments in my pattern!\n";
		
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		assertThat(matcher.find(), is(false));
		
		matcher = Pattern.compile(pattern_str, Pattern.COMMENTS).matcher(str);
		assertThat(matcher.find(), is(true));		
		
		matcher = Pattern.compile("(?x)" + pattern_str).matcher(str);
		assertThat(matcher.find(), is(true));		
	}

	@Test
	public void testCaseInsensitiveMode()
	{
		String str = "LINE1 lInE2";
		String pattern_str = "line\\d line\\d";
		Pattern pattern = Pattern.compile(pattern_str);
		Matcher matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(false));
		
		pattern = Pattern.compile(pattern_str, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(true));		
		
		pattern = Pattern.compile("(?i)" + pattern_str);
		matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(true));		
	}

	@Test
	public void testDotallMode()
	{
		String str = 	"line1\n" + 
						"line2\n";
		String pattern_str = "line\\d.+line\\d.+";
		Pattern pattern = Pattern.compile(pattern_str);
		Matcher matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(false));
		
		pattern = Pattern.compile(pattern_str, Pattern.DOTALL);
		matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(true));		
		
		pattern = Pattern.compile("(?s)" + pattern_str);
		matcher = pattern.matcher(str);
		assertThat(matcher.find(), is(true));		
	}

	@Test
	public void testNamedCapturingGroups()
	{
		String test_value = "asdfasdfasdf";
		String str = "<tag>" + test_value + "</tag>";
		String group_name = "ValueGroup";
		String pattern_str = "<tag>(?<"+group_name+">" + test_value + ")</tag>";
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		
		assertThat(matcher.find(), is(equalTo(Boolean.TRUE)));
		assertThat(matcher.group(group_name), is(equalTo(test_value)));
	}

	@Test
	public void testCapturingGroups()
	{
		String test_value = "asdfasdfasdf";
		String str = "<tag>" + test_value + "</tag>";
		String pattern_str = "<tag>(" + test_value + ")</tag>";
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		
		assertThat(matcher.find(), is(equalTo(Boolean.TRUE)));
		assertThat(matcher.group(1), is(equalTo(test_value)));
	}

	@Test
	public void testPrefixAndSuffixNonCapturingGroups()
	{
		String test_value = "asdfasdfasdf";
		String str = "<tag>" + test_value + "</tag>";
		String pattern_str = "(?<=<tag>)" + test_value + "(?=</tag>)";
		Matcher matcher = Pattern.compile(pattern_str).matcher(str);
		
		assertThat(matcher.find(), is(equalTo(Boolean.TRUE)));
		assertThat(matcher.group(), is(equalTo(test_value)));
	}
}