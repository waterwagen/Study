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
		for(int index = 0; index < helper.length; index++)
			helper[index] = list.get(low1 + index);
		
		int helper_high1 = low2 - low1 - 1;
		int merge_pointer = low1;
		int pointer_one = 0;
		int pointer_two = helper_high1 + 1;
		while(pointer_one <= helper_high1 && pointer_two < helper.length)
		{
			int compare_result = helper[pointer_one].compareTo(helper[pointer_two]);
			int lowest_value_index = compare_result <= 0 ? pointer_one++ : pointer_two++;
			list.set(merge_pointer, helper[lowest_value_index]);
			merge_pointer++;
		}
		while(pointer_one <= helper_high1)
			list.set(merge_pointer++, helper[pointer_one++]);
	}
}
