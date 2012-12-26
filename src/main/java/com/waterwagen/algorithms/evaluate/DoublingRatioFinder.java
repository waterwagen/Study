package com.waterwagen.algorithms.evaluate;

/*************************************************************************
 *  Compilation:  javac DoublingRatio.java
 *  Execution:    java DoublingRatio
 *  Dependencies: ThreeSum.java Stopwatch.java StdRandom.java StdOut.java
 *
 *
 *  % java DoublingRatio
 *      250   0.0    2.7
 *      500   0.0    4.8
 *     1000   0.1    6.9
 *     2000   0.6    7.7
 *     4000   4.5    8.0
 *     8000  35.7    8.0
 *  ...
 *
 *************************************************************************/

public abstract class DoublingRatioFinder 
{
	private static int RUN_COUNT = 10;

	public static void run(AlgorithmExecutor executor)
	{
        double prev = timeTrial(125, executor);
        for (int N = 250; true; N += N) 
        {
        	double N_total_time = 0;
        	for(int i = 0; i < RUN_COUNT; i++)
        		N_total_time += timeTrial(N, executor);
        	double avg_time = N_total_time/RUN_COUNT;
            StdOut.printf("%6d %7.4f %5.1f\n", N, avg_time, avg_time/prev);
            prev = avg_time;
        } 
	}

    private static double timeTrial(int N, AlgorithmExecutor executor) 
    {
    	executor.prepare(N);
		Stopwatch timer = new Stopwatch();
		executor.execute();
		return timer.elapsedTime();
    }
} 