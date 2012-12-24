package com.waterwagen.algorithms;

public class DoublingRatioRunner
{
	public static void main(String[] argv)
	{
//		AlgorithmExecutor executor = new QuickSortExecutor();
		AlgorithmExecutor executor = new MergeSortExecutor();
//		AlgorithmExecutor executor = new BinarySearchExecutor();
		DoublingRatioFinder.run(executor);
	}
}