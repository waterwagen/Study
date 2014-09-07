package com.waterwagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Utilities {

	public static Collection<Integer> randomCollectionOfInts(int num_ints) {
		return randomCollectionOfInts(num_ints, num_ints);
	}
	
	public static Collection<Integer> randomCollectionOfInts(int num_ints, int maximum_value) {
		Collection<Integer> result = new ArrayList<>();
		
		Random random_gen = new Random(System.currentTimeMillis());
		for(int i = 0; i < num_ints; i++)
			result.add(random_gen.nextInt(maximum_value));

		return result;
	}

  public static Runnable uncheckedRunnable(RunnableExecution runnableExecution) {
    return () -> {
      try {
        runnableExecution.runExecution();
      }
      catch (Exception exc) {
        throw new RuntimeException(exc);
      }
    };
  }

  public interface RunnableExecution {
    void runExecution() throws Exception;
  }

}
