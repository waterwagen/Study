package com.waterwagen.study.hamcrest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * To see all possible matchers check the static factory methods on the org.hamcrest.Matchers class (e.g. using content assist in Eclipse)
 */
public class TestHamcrestFeatures
{
	private String mValue;
	private String mValueDeepcopy;
	private String mOtherValue;
	private String mKey1,mVal1;
	private String mKey2,mVal2;
	private String mKey3,mVal3;
	private List<String> mValueList;
	private Map<String, String> mValueMap;
	
	@Before
	public void setUpTest()
	{
		mValue = "string1";
		mValueDeepcopy = new String(mValue);
		mOtherValue = "string2";
		mValueList = Arrays.asList(mValue, mValueDeepcopy, mOtherValue);
		mKey1 = "key1"; mVal1 = mValue;
		mKey2 = "key2"; mVal2 = mValueDeepcopy;
		mKey3 = "key3"; mVal3 = mOtherValue;
		mValueMap = new HashMap<String, String>()
		{
			private static final long serialVersionUID = 1L;

			{
				put(mKey1, mVal1);
				put(mKey2, mVal2);
				put(mKey3, mVal3);
			}
		};
	}

	@Test
	public void testCoreMatchers()
	{
		assertThat(mValue, anything());
		assertThat(mValue, is(mValueDeepcopy)); // this matcher does an equalTo match behind the scenes for convenience

		// can't actually try this matcher without failing the test, so I'll just leave it in here as a comment
//		assertThat(value, describedAs("This is a custom failure message specified using the 'describedAs' matcher.", is("string2"), new Object[0]));
	}

	@Test
	public void testLogicalMatchers()
	{
		assertThat(mValue, allOf(anything(), is(mValue)));
		assertThat(mValue, anyOf(equalTo(mValue), equalTo(mOtherValue)));
		assertThat(mValue, not(mOtherValue));
	}

	@Test
	public void testObjectMatchers()
	{
		assertThat(mValue, equalTo(mValueDeepcopy));
		assertThat(mValue, hasToString(mValueDeepcopy));
		assertThat(mValue, instanceOf(String.class));
		assertThat(mValue, notNullValue());
		assertThat(null, nullValue());
		assertThat(mValue, sameInstance(mValue));
	}

	@Test
	public void testBeanMatchers()
	{
		assertThat(mValue, hasProperty("bytes", is(equalTo(mValue.getBytes())))); // it appears that target value being evaluated doesn't have to be a strict Javabean, any Object with a property will do
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionMatchers()
	{
		assertThat(mValueList.toArray(new String[0]), array(is(equalTo("string1")), is(notNullValue()), is(anything())));

		assertThat(mValueMap, hasEntry(mKey1, mVal1));
		assertThat(mValueMap, hasEntry(mKey2, mVal2));
		assertThat(mValueMap, hasEntry(mKey3, mVal3));
		assertThat(mValueMap, allOf(hasEntry(mKey1, mVal1), hasEntry(mKey2, mVal2), hasEntry(mKey3, mVal3)));
		assertThat(mValueMap, allOf(hasKey(mKey1), hasKey(mKey2), hasKey(mKey3)));
		assertThat(mValueMap, allOf(hasValue(mVal1), hasValue(mVal2), hasValue(mVal3)));
		
		assertThat(mValueList, hasItem(mValue));
		assertThat(mValueList, hasItems(mValue, mValueDeepcopy, mOtherValue));
		
		assertThat(mValueList.toArray(new String[0]), hasItemInArray(mValue));
	}

	@Test
	public void testNumberMatchers()
	{
		assertThat(1.1, closeTo(1.1, 0.000000001));
		
		assertThat(1, greaterThan(0));
		
		assertThat(1, lessThan(2));
		
		assertThat(1, greaterThanOrEqualTo(0));	
		assertThat(1, greaterThanOrEqualTo(1));
		
		assertThat(1, lessThanOrEqualTo(2));	
		assertThat(1, lessThanOrEqualTo(1));
	}

	@Test
	public void testTextMatchers()
	{
		assertThat(mValue.toLowerCase(), equalToIgnoringCase(mValue.toUpperCase()));

		assertThat(mValue, equalToIgnoringWhiteSpace("      " + mValue + "     "));
		
		assertThat(mValue, containsString("str"));
		
		assertThat(mValue, startsWith("stri"));
		assertThat(mValueList, everyItem(startsWith("string")));
		
		assertThat(mValue, endsWith("ng1"));
	}	
}
