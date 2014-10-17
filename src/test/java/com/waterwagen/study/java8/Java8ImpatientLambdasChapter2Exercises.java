package com.waterwagen.study.java8;

import com.waterwagen.algorithms.evaluate.Stopwatch;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class Java8ImpatientLambdasChapter2Exercises {

  private static final String STREAM_EXERCISES_ROOT_DIRECTORY = "./src/test/resources/streamExercises/";

  private static final String WORDS_TXT_FILENAME = "words.txt";
  public static final int WORD_LENGTH_COUNT_BOUNDARY = 8;

  @Test
  public void exercise1() throws Exception {
    String wordsFilePath = STREAM_EXERCISES_ROOT_DIRECTORY + WORDS_TXT_FILENAME;
    String contents = new String(Files.readAllBytes(Paths.get(wordsFilePath)), StandardCharsets.UTF_8);
    List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
    int expectedLargeWordCount = 0;
    Stopwatch stopwatch = new Stopwatch();
    for (String word : words) {
      if (word.length() > WORD_LENGTH_COUNT_BOUNDARY) {
        expectedLargeWordCount++;
      }
    }
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

    int actualLargeWordCount = combineResults(resultQueue);
    System.out.println(String.format("elapsed time (parallel): %f", stopwatch.elapsedTime()));
    assertEquals(expectedLargeWordCount, actualLargeWordCount);
    System.out.println(String.format("expected large word count = %d, actual large word count = %d",
      expectedLargeWordCount, actualLargeWordCount));

  }

  private int combineResults(Queue<Future<Integer>> resultQueue) throws ExecutionException, InterruptedException {
    int combinedResults = 0;
    while(!resultQueue.isEmpty()) {
      combinedResults += resultQueue.poll().get();
    }
    return combinedResults;
  }

  private static class LargeWordCounter implements Callable<Integer> {

    private final List<String> words;

    private final int startIndex;

    private final int endIndex;

    private LargeWordCounter(List<String> words, int startIndex, int endIndex) {
      this.words = words;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    @Override
    public Integer call() throws Exception {
      int count = 0;
      for(int index = startIndex; index <= endIndex; index++) {
        if(words.get(index).length() > WORD_LENGTH_COUNT_BOUNDARY) {
          count++;
        }
      }
      return count;
    }

  }
}
