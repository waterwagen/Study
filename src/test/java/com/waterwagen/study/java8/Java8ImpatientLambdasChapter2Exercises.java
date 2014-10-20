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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.*;
import static org.junit.Assert.*;

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
    Predicate<? super String> wordLengthFunction = getWordLengthPredicate(4);

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

  @Test
  public void exercise3() throws Exception {
    List<String> words = getVeryBigNumberOfWords();
    Predicate<? super String> wordLengthFunction = getWordLengthPredicate(4);

    words.stream().count(); // warm up streaming

    // implicitly sequential stream
    Stopwatch stopwatch = new Stopwatch();
    long sequentialLongWordCount = words.stream().filter(wordLengthFunction).count();
    System.out.println(String.format("time elapsed (implicitly sequential): %f", stopwatch.elapsedTime()));

    // explicitly sequential stream
    stopwatch = new Stopwatch();
    long sequential2LongWordCount = words.stream().sequential().filter(wordLengthFunction).count();
    System.out.println(String.format("time elapsed (explicitly sequential): %f", stopwatch.elapsedTime()));

    stopwatch = new Stopwatch();
    long parallelLongWordCount = words.stream().parallel().filter(wordLengthFunction).count();
    System.out.println(String.format("time elapsed (modified to be parallel): %f", stopwatch.elapsedTime()));

    // this one is at least an order of magnitude faster than both of the above, including the other parallel stream
    stopwatch = new Stopwatch();
    long parallel2LongWordCount = words.parallelStream().filter(wordLengthFunction).count();
    System.out.println(String.format("time elapsed (created as parallel): %f", stopwatch.elapsedTime()));

    assertEquals("Failed sanity check.", sequentialLongWordCount, sequential2LongWordCount);
    assertEquals("Failed sanity check.", parallelLongWordCount, parallel2LongWordCount);
    assertEquals("Failed sanity check.", sequentialLongWordCount, parallelLongWordCount);
  }

  @Test
  public void exercise4() throws Exception {
    int[] values = {1,4,9,13};
    IntStream stream = IntStream.of(values);
    assertEquals(1, stream.filter(num -> num >= 10).count());
  }

  @Test
  public void exercise5() throws Exception {
    Stream<Long> randomNumberStream =
      Stream.iterate(0L, x -> linearCongruentialGeneratorHelper(x, 25214903917l, 11, (long) Math.pow(2, 48)));
    List<Long> twoRandomNumbers = randomNumberStream.limit(2).collect(Collectors.toList());
    assertFalse(twoRandomNumbers.get(0).equals(twoRandomNumbers.get(1)));
  }

  @Test
  public void exercise6() throws Exception {
    String someString = "a;sdlfjkasdlfjasfd";

    Stream<Character> stream = Stream.iterate(0, n -> n + 1).limit(someString.length()).map(someString::charAt);
    List<Character> charList = stream.collect(Collectors.toList());
    assertEquals('a', charList.get(0).charValue());
    assertEquals('f', charList.get(5).charValue());
    assertEquals('d', charList.get(10).charValue());
  }

}
