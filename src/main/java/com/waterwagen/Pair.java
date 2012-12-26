package com.waterwagen;

public class Pair<T>
{
	private final T mItem1;
	private final T mItem2;

	public Pair(T item1, T item2)
	{
		mItem1 = item1;
		mItem2 = item2;
	}
	
	public T item1()
	{
		return mItem1;
	}
	
	public T item2()
	{
		return mItem2;
	}
}