package com.waterwagen.study.corejava;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJavaEnums
{
	@Test
	public void testEnumBasics()
	{
		Fruit fruit;
		
		fruit = Fruit.APPLE;
		assertEquals("Unexpected fruit price.", Double.valueOf(1.25), fruit.getPrice());
		assertEquals("Unexpected fruit description.", "A red, sweet, round fruit.", fruit.getDescription());
		
		fruit = Fruit.BANANA;
		assertEquals("Unexpected fruit price.", Double.valueOf(0.99), fruit.getPrice());
		assertEquals("Unexpected fruit description.", "A yellow, tube-shaped fruit.", fruit.getDescription());
		
		fruit = Fruit.ORANGE;
		assertEquals("Unexpected fruit price.", Double.valueOf(1.45), fruit.getPrice());
		assertEquals("Unexpected fruit description.", "An orange, sweet, juicy, round fruit.", fruit.getDescription());
	}
	
	private static enum Fruit
	{
		APPLE (1.25, "A red, sweet, round fruit."),
		BANANA (0.99, "A yellow, tube-shaped fruit."),
		ORANGE (1.45, "An orange, sweet, juicy, round fruit.");
		
		private final Double mPrice;
		private final String mDescription;
		
		private Fruit(double price, String description)
		{
			mPrice = price;
			mDescription = description;
		}

		private Double getPrice()
		{
			return mPrice;
		}

		private String getDescription()
		{
			return mDescription;
		}		
	}
}
