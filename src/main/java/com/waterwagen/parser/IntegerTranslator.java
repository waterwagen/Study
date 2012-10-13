package com.waterwagen.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Translates human readable numbers (e.g. "twothousandfivehundredtwentysix") to integers (2526). 
 * The results of any values above the maximum Java integer value are undefined.
 * 
 * @author waterwagen
 *
 */
public class IntegerTranslator implements NumberTranslator
{
	/////////////////
	/// Constants ///
	/////////////////
	
	static final String ONE = "one";
	static final String HUNDRED = "hundred";

	///////////////////
	/// API Methods ///
	///////////////////
	
	@Override
	public Integer translate(String whole_number_name)
	{
		List<String> atomic_number_names = parseAtomicNumberNames(whole_number_name);
		List<Bucket> buckets = parseThousandsBuckets(atomic_number_names);
		Integer result = 0;
		for(Bucket bucket : buckets)
			result += bucket.calculateValue();
		return result;
	}

	///////////////////////
	/// Private Methods ///
	///////////////////////

	private List<String> parseAtomicNumberNames(String number_name)
	{
		List<String> result = new ArrayList<>();

		Set<String> atomic_number_names = NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.keySet();
		int begin_index = 0;
		int end_index;
		while(begin_index < number_name.length())
		{
			// Set the end index to the end of the string being parsed so that we can find the largest name which
			// matches a number (e.g. "fourteen" and "four")
			end_index = number_name.length();

			// Look for the next individual number name (e.g. "five","hundred") from left to right in the number name string. 
			// Start with the broadest index range (i.e. the whole string minus number names already found) and move down to 
			// the narrowest (i.e. move the end_index towards the beginning index) so that we can match on the longest possible
			// number name string to avoid a problem with number names which start with the same string (e.g. "fourteen" and "four")
			while(end_index > begin_index && !atomic_number_names.contains(number_name.substring(begin_index, end_index)))
				end_index--;

			// if we moved the ending index until it is equal with the beginning index than at least part of the specified 
			// number name string could not be matched to a number. Maybe an invalid value was passed in or maybe there's a 
			// bug in this class, but either way we can't continue.
			if(end_index == begin_index)
				throw new IllegalArgumentException("Invalid number name specified. The string '" + number_name + "' can not be translated into a number.");
			
			// add the parsed atomic number name string (e.g. "five") to the result list we'll be returning
			result.add(number_name.substring(begin_index, end_index));
			
			// set the pointer to our current position in the string to the position after the last parsed number name.
			begin_index = end_index;
		}

		return result;
	}

	private List<Bucket> parseThousandsBuckets(List<String> atomic_numbers)
	{
		List<Bucket> result = new ArrayList<>();
		
		int bucket_start_index = 0;
		int curr_index = 0;
		for(String atomic_num : atomic_numbers)
		{
			if(NumberParsingConstants.THOUSANDS_BUCKET_BASENUMS.contains(atomic_num))
			{
				result.add(new Bucket(new ArrayList<>(atomic_numbers.subList(bucket_start_index, curr_index)), atomic_num));
				bucket_start_index = curr_index + 1;
			}
			curr_index++;
		}
		result.add(new Bucket(new ArrayList<>(atomic_numbers.subList(bucket_start_index, curr_index)), ONE));
		
		return result;
	}
	
	///////////////////////
	/// Utility Classes ///
	///////////////////////	

	private class Bucket
	{
		private List<String> mSubThousandNums;
		private String mBucketBaseNum;

		public Bucket(List<String> subList, String atomic_num)
		{
			mSubThousandNums = subList;
			mBucketBaseNum = atomic_num;
		}

		public Integer calculateValue()
		{
			return processSubThousandValue(getSubThousandNumbers()) * (NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.get(getBucketBaseNum()));
		}
		
		private Integer processSubThousandValue(List<String> atomic_numbers)
		{
			Integer result = 0;
			
			int curr_index = 0;
			int last_basenum_index = -1;
			for(String atomic_num : atomic_numbers)
			{
				if(atomic_num.equals(HUNDRED))
				{
					Integer multiplier = NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.get(atomic_numbers.get(curr_index - 1));
					Integer basenum = NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.get(atomic_numbers.get(curr_index));
					result += multiplier * basenum;
					last_basenum_index = curr_index;
				}
				curr_index++;
			}

			for(String atomic_number : atomic_numbers.subList(last_basenum_index + 1, atomic_numbers.size()))
				result += NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.get(atomic_number);

			return result;
		}

		private List<String> getSubThousandNumbers()
		{
			return mSubThousandNums;
		}

		private String getBucketBaseNum()
		{
			return mBucketBaseNum;
		}
	}
}
