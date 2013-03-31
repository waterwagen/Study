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
		assertEquals("Unexpected string value. Each fruit should have overriden the stringValue() method.", 
						"This is an apple string value.", fruit.stringValue());
		
		fruit = Fruit.BANANA;
		assertEquals("Unexpected fruit price.", Double.valueOf(0.99), fruit.getPrice());
		assertEquals("Unexpected fruit description.", "A yellow, tube-shaped fruit.", fruit.getDescription());
		assertEquals("Unexpected string value. Each fruit should have overriden the stringValue() method.", 
						"This is a banana string value.", fruit.stringValue());		
		
		fruit = Fruit.ORANGE;
		assertEquals("Unexpected fruit price.", Double.valueOf(1.45), fruit.getPrice());
		assertEquals("Unexpected fruit description.", "An orange, sweet, juicy, round fruit.", fruit.getDescription());
		assertEquals("Unexpected string value. Each fruit should have overriden the stringValue() method.", 
						"This is an orange string value.", fruit.stringValue());
	}
	
	private static enum Fruit
	{
		APPLE (1.25, "A red, sweet, round fruit.")
		{
			@Override
			protected String stringValue() { thisIsAnAppleMethod(); return "This is an apple string value."; }
			
			void thisIsAnAppleMethod() {}
		},
		BANANA (0.99, "A yellow, tube-shaped fruit.")
		{
			@Override
			protected String stringValue() { return "This is a banana string value."; }
		},
		ORANGE (1.45, "An orange, sweet, juicy, round fruit.")
		{
			@Override
			protected String stringValue() { return "This is an orange string value."; }
		};
		
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
		
		protected String stringValue()
		{
			return "This is the default string value.";
		}
	}
}
