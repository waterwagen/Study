package com.waterwagen.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerParser implements NumberParser
{
	///////////////////
	/// API Methods ///
	///////////////////
	
	@Override
	public List<String> parse(String str)
	{
		List<FoundNumberName> found_numbernames = findNumberNamesInString(str);
		return joinAdjacentNumberNames(found_numbernames);
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////

	private List<String> joinAdjacentNumberNames(List<FoundNumberName> found_names)
	{
		List<String> result = new ArrayList<>();
		
		Collections.sort(found_names);	
		
		StringBuffer concatenated_name = new StringBuffer();
		int prev_end_index = found_names.get(0).getStartIndex(); // to skip the loop's if statement on the special case of the first iteration (with the least amount of code)
		for (ListIterator<FoundNumberName> it = found_names.listIterator(); it.hasNext();)
		{
			FoundNumberName name = it.next();
			if(name.getStartIndex() != prev_end_index) // compare this start and previous end index directly because start is inclusive and end is exclusive in Java String APIs
				result.add(removeValue(concatenated_name));
			concatenated_name.append(name.getNumName());
			prev_end_index = name.getEndIndex();
		}
		result.add(concatenated_name.toString());
		
		return result;
	}

	private List<FoundNumberName> findNumberNamesInString(String str)
	{
		List<FoundNumberName> found_names = new ArrayList<>();

		findAllNumberNameMatchesInString(str, found_names); // look for any numbers that can be matched in substrings of the specified string		
		resolveOverlappingFoundNames(found_names); // handle the case where the search for number names may be confused by overlapping names
		
		return found_names;
	}

	private void findAllNumberNameMatchesInString(String str, List<FoundNumberName> found_names)
	{
		TreeSet<String> num_names = new TreeSet<>(NumberParsingConstants.NAME_TO_ATOMIC_NUMBER_MAP.keySet());
		for(String num_name = num_names.pollLast(); num_name != null; num_name = num_names.pollLast())
		{
			Matcher matcher = Pattern.compile(num_name).matcher(str);
			while(matcher.find())
				found_names.add(new FoundNumberName(num_name, matcher.start(), matcher.end()));
		}
	}

	private String removeValue(StringBuffer buffer)
	{
		String result = buffer.toString();
		buffer.delete(0, buffer.length());
		return result;
	}

	/**
	 * Handle the case where the search for number names may be confused by overlapping names. For example, commonly "four" and "fourteen", 
	 * less commonly but confusingly "onebillioneight" might result in two "one"s being found rather than "one", "billion", "eight"
	 * 
	 * @param found_names
	 */
	private void resolveOverlappingFoundNames(List<FoundNumberName> found_names)
	{
		if(found_names.size() < 2) // there aren't enough found number names for there to be any overlaps, so return immediately
			return;
		
		Collections.sort(found_names);

		List<FoundNumberName> to_remove = new ArrayList<>();
		List<FoundNumberName> to_add = new ArrayList<>();
		FoundNumberName prev = new FoundNumberName("", -1, -1);
		for(ListIterator<FoundNumberName> it = found_names.listIterator(); it.hasNext();)
		{
			FoundNumberName name = it.next();
			if (FoundNumberName.isConflict(name, prev))
			{
				to_add.add(FoundNumberName.resolve(name, prev));
				to_remove.addAll(Arrays.asList(name, prev));
			}
			prev = name;
		}
		found_names.removeAll(to_remove);
		found_names.addAll(to_add);
	}

	///////////////////////
	/// Utility Classes ///
	///////////////////////
	
	private static class FoundNumberName implements Comparable<FoundNumberName>
	{
		/////////////////////
		/// Class Methods ///
		/////////////////////
		
		private static boolean isConflict(FoundNumberName name1, FoundNumberName name2)
		{
			return name1.getStartIndex() < name2.getEndIndex() && name1.getEndIndex() > name2.getStartIndex(); // remember, Java string start indices are inclusive and end indices are exclusive 
		}
		
		public static FoundNumberName resolve(FoundNumberName name, FoundNumberName prev)
		{
			int comparison = new Integer(name.getNumName().length()).compareTo(prev.getNumName().length());
			if (comparison < 0)
				return prev;
			else if (comparison > 0)
				return name;
			else // this shouldn't happen, based on how this class is used
				return name;
		}

		////////////////////////
		/// Member Variables ///
		////////////////////////

		private String mNumName;
		private int mStartIndex;
		private int mEndIndex;

		////////////////////
		/// Constructors ///
		////////////////////
		
		private FoundNumberName(String num_name, int start_index, int end_index)
		{
			mNumName = num_name;
			mStartIndex = start_index;
			mEndIndex = end_index;
		}

		///////////////////
		/// API Methods ///
		///////////////////

		private Integer getStartIndex()
		{
			return mStartIndex;
		}

		private Integer getEndIndex()
		{
			return mEndIndex;
		}

		private String getNumName()
		{
			return mNumName;
		}

		@Override
		public boolean equals(Object other)
		{
			if(!(other instanceof FoundNumberName))
				return false;
			
			FoundNumberName other_name = (FoundNumberName)other;
			return getNumName().equals(other_name.getNumName()) && 
					getStartIndex().equals(other_name.getStartIndex()) && 
					getEndIndex().equals(other_name.getEndIndex());
		}
		
		@Override
		public int hashCode()
		{
			int result = 17;
			result = 31 * result + mNumName.hashCode();
			result = 31 * result + mStartIndex;
			result = 31 * result + mEndIndex;
			return result;
		}
		
		@Override
		public int compareTo(FoundNumberName o)
		{
			return getStartIndex().compareTo(o.getStartIndex());
		}

		@Override
		public String toString()
		{
			return "[numname=" + getNumName() + ":startindex=" + getStartIndex() + ":endindex=" + getEndIndex() + "]";
		}
	}
}
