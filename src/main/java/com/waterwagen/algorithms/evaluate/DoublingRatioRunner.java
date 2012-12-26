package com.waterwagen.algorithms.evaluate;

import com.waterwagen.algorithms.evaluate.implementations.sorts.QuickSortExecutor;

public class DoublingRatioRunner
{
	public static void main(String[] argv)
	{
		AlgorithmExecutor executor = new QuickSortExecutor();
//		AlgorithmExecutor executor = new MergeSortExecutor();
//		AlgorithmExecutor executor = new BubbleSortExecutor();
//		AlgorithmExecutor executor = new BinarySearchExecutor();
//		AlgorithmExecutor executor = new UnionQuickFindExecutorForFindMsg();
//		AlgorithmExecutor executor = new UnionQuickFindExecutorForConnectedMsg();
//		AlgorithmExecutor executor = new UnionQuickFindExecutorForUnionMsg();
		DoublingRatioFinder.run(executor);
	}
}