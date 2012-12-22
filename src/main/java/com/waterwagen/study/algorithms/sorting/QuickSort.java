package com.waterwagen.study.algorithms.sorting;

import java.util.List;

public class QuickSort<T extends Comparable<T>> implements Sort<T>
{
	@Override
	public void sort(List<T> list)
	{
		quicksort(list, 0, list.size() - 1);
	}

	private void quicksort(List<T> list, int low, int high)
	{
		int pivot_point = partition(list, low, high);
		
		if(low < pivot_point - 1)
			quicksort(list, low, pivot_point - 1);
		if(pivot_point < high)
			quicksort(list, pivot_point, high);
	}

	private int partition(List<T> list, int low, int high)
	{
		T pivot_value = list.get((high - low)/2 + low);
		while(low <= high)
		{
			while(list.get(low).compareTo(pivot_value) < 0)
				low++;
			while(list.get(high).compareTo(pivot_value) > 0)
				high--;
			if(low <= high)
			{
				swap(list, low, high);
				low++;
				high--;
			}
		}
		
		return low;
	}

	private void swap(List<T> list, int index1, int index2)
	{
		T temp = list.get(index1);
		list.set(index1, list.get(index2));
		list.set(index2, temp);
	}
}
