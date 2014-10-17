package com.waterwagen.study.java8;

import com.waterwagen.algorithms.evaluate.Stopwatch;
import com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void exercise2() throws Exception {
    List<String> words = getWords();

    Predicate<? super String> wordLengthFunction = word -> word.length() > 4;
    AtomicInteger filterCount = new AtomicInteger(0);
    int maxNumberOfWords = 5;
    List<String> collectedList = words.stream().sequential().filter(word -> {
//      System.out.println(String.format("filtering %s", word));
      filterCount.incrementAndGet();
      return wordLengthFunction.test(word);
    }).limit(maxNumberOfWords).collect(Collectors.toList());
    System.out.println(
      String.format("total number of long words is %d", words.stream().filter(wordLengthFunction).count()));
    System.out.println(
        String.format("filtered number of long words is %d", filterCount.get()));

    assertEquals("Failed sanity check.", 5, collectedList.size());
    assertTrue("Expected the stream to filter less than the total number of words as the number of results is limited.",
      filterCount.get() < words.size());
  }

}
