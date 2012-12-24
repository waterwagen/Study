package com.waterwagen.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.waterwagen.Utilities;

public abstract class ListDataAlgorithmExecutor implements AlgorithmExecutor
{
	List<Integer> buildListOfIntegers(int n)
	{
		return new ArrayList<Integer>(Utilities.randomCollectionOfIntsUpTo(n, n*2));
	}
}
