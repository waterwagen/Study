package com.waterwagen.study.java8;

import com.waterwagen.algorithms.evaluate.Stopwatch;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.LargeWordCounter;

import static com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.*;
import static org.junit.Assert.assertEquals;

public class Java8ImpatientLambdasChapter2Exercises {

  @Test
  public void exercise1() throws Exception {
    List<String> words = getWords();

    Stopwatch stopwatch = new Stopwatch();
    int expectedLargeWordCount = calculateWordCount(words);
    System.out.println(String.format("elapsed time (conventional): %f", stopwatch.elapsedTime()));

    int processorCount = 2;
    ExecutorService executorService = Executors.newFixedThreadPool(processorCount);
    Queue<Future<Integer>> resultQueue = new LinkedList<>();
    int nextStartIndex = 0;
    int nextEndIndex = words.size() / processorCount;
    stopwatch = new Stopwatch();
    for(int index = 1; index <= processorCount; index++) {
      resultQueue.add(executorService.submit(new LargeWordCounter(words, nextStartIndex, nextEndIndex)));
      nextStartIndex = nextEndIndex + 1;
      nextEndIndex = index == processorCount - 1 ? words.size() - 1 : (nextEndIndex + (words.size() / processorCount));
    }

    int actualLargeWordCount = combineValues(resultQueue);
    System.out.println(String.format("elapsed time (parallel): %f", stopwatch.elapsedTime()));
    assertEquals(expectedLargeWordCount, actualLargeWordCount);
    System.out.println(String.format("expected large word count = %d, actual large word count = %d",
      expectedLargeWordCount, actualLargeWordCount));
  }

}
