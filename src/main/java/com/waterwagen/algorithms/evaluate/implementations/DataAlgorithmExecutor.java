package com.waterwagen.algorithms.evaluate.implementations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.waterwagen.Utilities;
import com.waterwagen.algorithms.evaluate.AlgorithmExecutor;

public abstract class DataAlgorithmExecutor implements AlgorithmExecutor
{
	protected List<Integer> buildListOfRandomIntegers(int n, int max_value)
	{
		return new ArrayList<Integer>(Utilities.randomCollectionOfInts(n, max_value));
	}

	protected Set<Integer> buildSetOfOrderedIntegersUpTo(int n)
	{
		Set<Integer> result = new HashSet<>(n);
		
		for(int i = 0; i < n; i++)
			result.add(i);
		
		return result;
	}
}
