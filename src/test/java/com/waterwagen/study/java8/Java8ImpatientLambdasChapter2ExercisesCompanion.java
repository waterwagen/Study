package com.waterwagen.study.java8;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Java8ImpatientLambdasChapter2ExercisesCompanion {

  public static final int WORD_LENGTH_COUNT_BOUNDARY = 8;

  private static final String STREAM_EXERCISES_ROOT_DIRECTORY = "./src/test/resources/streamExercises/";

  private static final String SMALL_WORDS_FILE_NAME = "words.txt";

  private static final String VERYBIG_WORDS_FILE_NAME = "war_and_peace.txt";

  static List<String> getWords() throws IOException {
    String wordsFilePath = STREAM_EXERCISES_ROOT_DIRECTORY + SMALL_WORDS_FILE_NAME;
    String contents = new String(Files.readAllBytes(Paths.get(wordsFilePath)), StandardCharsets.UTF_8);
    return Arrays.asList(contents.split("[\\P{L}]+"));
  }

  static List<String> getVeryBigNumberOfWords() throws IOException {
    String wordsFilePath = STREAM_EXERCISES_ROOT_DIRECTORY + VERYBIG_WORDS_FILE_NAME;
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

  static Predicate<String> getWordLengthPredicate(int wordLengthBoundary) {
    return word -> word.length() > wordLengthBoundary;
  }

  static long linearCongruentialGeneratorHelper(long x, long a, long c, long m) {
    return ((a * x) + c) % m;
  }

  static <T> boolean isInfinite(Stream<T> stream) throws ExecutionException, InterruptedException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Long> result = executor.submit(() -> stream.count());
    try {
      result.get(1, TimeUnit.SECONDS);
      return false;
    } catch (TimeoutException e) {
      return true;
    }
  }

  static Stream<Character> zip(Stream<Character> charStream1, Stream<Character> charStream2) {
    ArrayList<Iterator<Character>> streamIterators = Lists.newArrayList(charStream1.iterator(),
                                                                        charStream2.iterator());
    int nextIteratorIndex = 0;
    Stream.Builder<Character> builder = Stream.builder();
    while (streamIterators.get(0).hasNext()
           && streamIterators.get(1).hasNext()) {
      builder.add(streamIterators.get(nextIteratorIndex++ % 2).next());
    }
    return builder.build();
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
