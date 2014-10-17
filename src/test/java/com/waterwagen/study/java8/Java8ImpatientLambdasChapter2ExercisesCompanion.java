package com.waterwagen.study.java8;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Java8ImpatientLambdasChapter2ExercisesCompanion {

  public static final int WORD_LENGTH_COUNT_BOUNDARY = 8;

  private static final String STREAM_EXERCISES_ROOT_DIRECTORY = "./src/test/resources/streamExercises/";

  private static final String WORDS_TXT_FILENAME = "words.txt";

  static List<String> getWords() throws IOException {
    String wordsFilePath = STREAM_EXERCISES_ROOT_DIRECTORY + WORDS_TXT_FILENAME;
    String contents = new String(Files.readAllBytes(Paths.get(wordsFilePath)), StandardCharsets.UTF_8);
    return Arrays.asList(contents.split("[\\P{L}]+"));
  }

  static int combineValues(Queue<Future<Integer>> resultQueue) throws ExecutionException, InterruptedException {
    int combinedResults = 0;
    while(!resultQueue.isEmpty()) {
      combinedResults += resultQueue.poll().get();
    }
    return combinedResults;
  }

  static int calculateWordCount(List<String> words) {
    int expectedLargeWordCount = 0;
    for (String word : words) {
      if (word.length() > WORD_LENGTH_COUNT_BOUNDARY) {
        expectedLargeWordCount++;
      }
    }
    return expectedLargeWordCount;
  }

  static class LargeWordCounter implements Callable<Integer> {

    private final List<String> words;

    private final int startIndex;

    private final int endIndex;

    LargeWordCounter(List<String> words, int startIndex, int endIndex) {
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
