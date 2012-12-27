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
//		AlgorithmExecutor executor = new QuickFindFindMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindConnectedMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindUnionMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindComponentCountExecutor();
//		AlgorithmExecutor executor = new QuickFindQuickUnionFindMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindQuickUnionConnectedMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindQuickUnionUnionMsgExecutor();
//		AlgorithmExecutor executor = new QuickFindQuickUnionComponentCountExecutor();
		DoublingRatioFinder.run(executor);
	}
}