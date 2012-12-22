package com.waterwagen.study.corejava;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class BitOperationsOnNumbers
{
	@Test
	public void testBitOperatorsOnTwosComplementNumbers()
	{
		int minus_one = 0b11111111_11111111_11111111_11111111;
		assertThat(minus_one, equalTo(-1));
		assertThat(minus_one & 0, equalTo(0));
		assertThat(minus_one | 0, equalTo(-1));
		assertThat(minus_one ^ 0, equalTo(-1));
		assertThat(minus_one ^ Integer.MAX_VALUE, equalTo(-2147483648));
		int minus_two = 0b11111111_11111111_11111111_11111110;
		assertThat(minus_two, equalTo(-2));
		assertThat(minus_one ^ minus_two, equalTo(1));
		assertThat(minus_two | 1, equalTo(-1));
		assertThat(minus_two | 3, equalTo(-1));
		assertThat(minus_two | 5, equalTo(-1));
		
		assertThat(~Integer.MAX_VALUE, equalTo((int)-(Math.pow(2, 31))));
		assertThat(~Integer.MAX_VALUE, equalTo(-2147483648));
		assertThat(~0, equalTo(-1));	
	}
	
	@Test
	public void testBytesInTwosComplement()
	{
		byte val = 0b1111;
		assertThat(val, equalTo(byteValueOf(15)));
		val = (byte)0b1111_1111;
		assertThat(val, equalTo(byteValueOf(-1)));
	}
	
	@Test
	public void testGettingNegativeOfTwosComplementNumber()
	{
		int one = 0b1;
		assertThat(one, equalTo(1));
		// the negation operation for twos-complement notation (as in Java, obviously)
		assertThat(~one + 1, equalTo(-1));
		int fortyfive = 45;
		int negative_fortyfive = ~fortyfive + 1;
		assertThat(negative_fortyfive, equalTo(-45));
		assertThat(~negative_fortyfive + 1, equalTo(45));
	}
	
	//////////////////////
	/// Helper Methods ///
	//////////////////////

	private byte byteValueOf(int to_convert)
	{
		return Integer.valueOf(to_convert).byteValue();
	}
}
