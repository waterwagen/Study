package com.waterwagen.study.algorithms.sorting;

import java.util.List;

public interface Sort<T extends Comparable<T>>
{
	public void sort(List<T> list);
}