package com.waterwagen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CodeSnippetSpeedChecker
{
	public static void main(String[] args) throws Exception
	{
		int num_ints = 10_000_000;
		int run_count = 1;
		double total_time;
		Set<Integer> ints = new HashSet<>();
		Random random = null;
		
		long start_time = 0;
		long end_time = 0;
		
		// warmup...
		for(int i = 0; i < num_ints; i++)
			ints.add(Integer.valueOf(i));
		// ...done
		total_time = 0;
		for(int count = 1; count <= run_count; count++)
		{	
			ints.clear();
			start_time = System.nanoTime();
			for(int i = 0; i < num_ints; i++)
				ints.add(Integer.valueOf(i));
			end_time = System.nanoTime();
			total_time += (double)(end_time - start_time)/1_000_000_000;
		}
		printTimeReport("Non-random ints with valueOf.", total_time, run_count);
		
		ints.clear();
		// warmup...
		for(int i = 0; i < num_ints; i++)
			ints.add(i);
		// ...done
		total_time = 0;
		for(int count = 1; count <= run_count; count++)
		{	
			ints.clear();
			start_time = System.nanoTime();
			for(int i = 0; i < num_ints; i++)
				ints.add(i);
			end_time = System.nanoTime();
			total_time += (double)(end_time - start_time)/1_000_000_000;
		}
		printTimeReport("Non-random ints with autoboxing.", total_time, run_count);
		
		ints.clear();
		// warmup...
		random = new Random(System.currentTimeMillis());
		while(ints.size() < num_ints)
			ints.add(Integer.valueOf(random.nextInt()));
		// ...done
		total_time = 0;
		for(int count = 1; count <= run_count; count++)
		{	
			ints.clear();
			start_time = System.nanoTime();
			random = new Random(System.currentTimeMillis());
			while(ints.size() < num_ints)
				ints.add(Integer.valueOf(random.nextInt()));
			end_time = System.nanoTime();
			total_time += (double)(end_time - start_time)/1_000_000_000;
		}
		printTimeReport("Random ints with valueOf.", total_time, run_count);
		
		ints.clear();
		// warmup...
		random = new Random(System.currentTimeMillis());
		while(ints.size() < num_ints)
			ints.add(random.nextInt());
		// ...done
		total_time = 0;
		for(int count = 1; count <= run_count; count++)
		{	
			ints.clear();
			start_time = System.nanoTime();
			random = new Random(System.currentTimeMillis());
			while(ints.size() < num_ints)
				ints.add(random.nextInt());
			end_time = System.nanoTime();
			total_time += (double)(end_time - start_time)/1_000_000_000;
		}
		printTimeReport("Random ints with autoboxing.", total_time, run_count);
	}

	private static void printTimeReport(String string, double total_time, int run_count)
	{
		System.out.printf(string + " Time elapsed=%7f\n", total_time/run_count);
	}
}
