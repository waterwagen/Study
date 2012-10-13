package com.waterwagen.study.corejava;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class TestJavaRandomQuestions
{
	@Test
	public void testHowManyBytesDoesStringOccupy() throws UnsupportedEncodingException
	{
		String test = "%+-{}	;klajglkajg;aljgla;gjl;ajsg._ 32490t34346346//][";

		assertEquals("Unexpected number of bytes needed for the test string of " + test.length() + " chars in the default charset.", 
						(test.length() * 2) + 2, test.getBytes("utf-16").length);

		assertEquals("Unexpected number of bytes needed for the test string of " + test.length() + " chars in the utf-8 charset.", 
						test.length(), test.getBytes("utf-8").length);
	}
}
