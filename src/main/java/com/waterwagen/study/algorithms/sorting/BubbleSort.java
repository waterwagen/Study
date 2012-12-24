package com.waterwagen.study.algorithms.sorting;

import java.util.List;

public class BubbleSort<T extends Comparable<T>> implements Sort<T>
{
	@Override
	public void sort(List<T> list)
	{
		if(list.size() < 2)
			return;
		
		boolean list_changed;
		do
		{
			list_changed = false;
			T prev = list.get(0);
			for(int index = 1; index < list.size(); index++)
			{
				T curr = list.get(index);
				if(curr.compareTo(prev) < 0)
				{
					swap(list, index, index - 1);
					list_changed = true;
				}
				prev = curr;
			}
		}while(list_changed);
	}

	private void swap(List<T> list, int index1, int index2)
	{
		T temp = list.get(index1);
		list.set(index1, list.get(index2));
		list.set(index2, temp);
	}
}
