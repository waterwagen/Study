package com.waterwagen.algorithms.evaluate.implementations;

import java.util.ArrayList;
import java.util.List;

import com.waterwagen.Utilities;
import com.waterwagen.algorithms.evaluate.AlgorithmExecutor;

public abstract class ListDataAlgorithmExecutor implements AlgorithmExecutor
{
	protected List<Integer> buildListOfIntegers(int n)
	{
		return new ArrayList<Integer>(Utilities.randomCollectionOfInts(n, n*2));
	}
}
