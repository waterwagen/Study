package com.waterwagen.study.algorithms.searching;

import java.util.List;

public class BinarySearch<T extends Comparable<T>>
{
	public int find(List<T> list, T to_find)
	{
		return search(list, to_find, 0, list.size() - 1);
	}

	private int search(List<T> list, T to_find, int low, int high)
	{
		if(low > high) return -1; // we didn't find the value
		
		int mid = (high - low)/2 + low;
		T mid_value = list.get(mid);
		int compare_result = mid_value.compareTo(to_find);
		if(compare_result == 0) return mid;
		else
		{
			if(compare_result > 0) 
				high = mid - 1;
			else 
				low = mid + 1;
			return search(list, to_find, low, high);
		}
	}

}
