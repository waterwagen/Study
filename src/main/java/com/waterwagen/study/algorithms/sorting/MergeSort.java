package com.waterwagen.study.algorithms.sorting;

import java.util.List;

public class MergeSort<T extends Comparable<T>> implements Sort<T>
{
	///////////
	/// API ///
	///////////
	
	@Override
	public void sort(List<T> list)
	{
		mergesort(list, 0, list.size() - 1);
	}
	
	//////////////////////////////
	/// Implementation Methods ///
	//////////////////////////////
	
	private void mergesort(List<T> list, int low, int high)
	{
		if(high == low) return;
		else
		{
			int mid = (low + high)/2;
			mergesort(list, low, mid);
			mergesort(list, mid + 1, high);
			merge(list, low, mid, mid + 1, high);
		}
	}

	private void merge(List<T> list, int low1, int high1, int low2, int high2)
	{
		int length1 = high1 - low1 + 1;
		int length2 = high2 - low2 + 1;
		@SuppressWarnings("unchecked") T[] helper = (T[])new Comparable[length1 + length2];
		int merge_pointer = 0;
		int one_pointer = low1;
		int two_pointer = low2;
		while(merge_pointer < helper.length)
		{
			int lowest_value_index;
			if(one_pointer <= high1)
				if(two_pointer <= high2)
				{
					int compare_result = list.get(one_pointer).compareTo(list.get(two_pointer));
					lowest_value_index = compare_result < 1 ? one_pointer++ : two_pointer++;
				}
				else
					lowest_value_index = one_pointer++;
			else
				lowest_value_index = two_pointer++;
			helper[merge_pointer] = list.get(lowest_value_index);
			merge_pointer++;
		}
		copy(helper, list, low1);
	}

	private void copy(T[] helper, List<T> list, int insertion_point)
	{
		for(int index = 0; index < helper.length; index++)
			list.set(insertion_point + index, helper[index]);
	}
}
