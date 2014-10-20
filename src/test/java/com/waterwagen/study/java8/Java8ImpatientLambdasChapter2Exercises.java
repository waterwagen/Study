package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import com.waterwagen.algorithms.evaluate.Stopwatch;
import com.waterwagen.study.java8.Java8ImpatientLambdasChapter2ExercisesCompanion.*;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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

  @Test
  public void exercise7() throws Exception {
    String someString = "a;sdlfjkasdlfjasfd";
    Stream<Integer> finiteStream = Stream.iterate(0, n -> n + 1).limit(someString.length());
    assertFalse("Limited stream is infinite unexpectedly.", isInfinite(finiteStream));

    Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 1);
    assertTrue("Infinite generated stream is finite unexpectedly.", isInfinite(infiniteStream));
  }

  @Test
  public void exercise8() throws Exception {
    long numberOfChars = 26;
    Stream<Character> numCharStream = Stream.iterate('1', ch -> (char)(ch + 1)).limit(numberOfChars);
    Stream<Character> letterCharStream = Stream.iterate('a', ch -> (char)(ch + 1)).limit(numberOfChars);
    Stream<Character> zipped = zip(numCharStream, letterCharStream);

    Iterator<Character> zippedIterator = zipped.iterator();
    assertEquals('1', zippedIterator.next().charValue());
    assertEquals('a', zippedIterator.next().charValue());
    assertEquals('2', zippedIterator.next().charValue());
    assertEquals('b', zippedIterator.next().charValue());
    assertEquals('3', zippedIterator.next().charValue());
    assertEquals('c', zippedIterator.next().charValue());
  }

  @Test
  public void exercise9NonReduceSolution() throws Exception {
    testExercise9Solution(streamOfLists -> {
      ArrayList<String> result = Lists.newArrayList();
      streamOfLists.forEach(list -> result.addAll(list));
      return result;
    });
  }

  private void testExercise9Solution(Function<Stream<ArrayList<String>>, ArrayList<String>> flattenFunction) {
    Stream<String> testStringsStream = Stream.iterate(1, n -> n + 1).limit(6).map(n -> "string" + n);
    ArrayList<String> testStrings = testStringsStream.collect(Collectors.toCollection(Lists::newArrayList));
//    ArrayList<String> testStrings = testStringsStream.collect(Lists::newArrayList, List::add, List::addAll);
    ArrayList<String> flattenedList =
      flattenFunction.apply(Stream.of(Lists.newArrayList(testStrings.get(0), testStrings.get(1)),
                                      Lists.newArrayList(testStrings.get(2)),
                                      Lists.newArrayList(testStrings.get(3), testStrings.get(4), testStrings.get(5))));
    assertEquals("Unexpected flattened list returned.", testStrings, flattenedList);
  }

  @Test
  public void exercise9ReduceSolution1() throws Exception {
    testExercise9Solution(streamOfLists ->
      streamOfLists.reduce(Lists.newArrayList(), this::combineArrayListsInFirst)
    );
  }

  private <T> ArrayList<T> combineArrayListsInFirst(ArrayList<T> first, ArrayList<T>... otherLists) {
    Stream.of(otherLists).sequential().forEach(list -> first.addAll(list));
    return first;
  }

  @Test
  public void exercise9ReduceSolution2() throws Exception {
    testExercise9Solution(streamOfLists ->
      streamOfLists.reduce(this::combineArrayListsInFirst).get()
    );
  }

  @Test
  public void exercise9ReduceSolution3() throws Exception {
    testExercise9Solution(streamOfLists ->
      streamOfLists.reduce(Lists.newArrayList(), this::combineArrayListsInFirst, this::combineArrayListsInFirst)
    );
  }

  @Test
  public void exercise10() throws Exception {
    testExercise10Solution(streamOfDoubles ->
      streamOfDoubles.reduce(new DoubleAverage(0, 0),
                             DoubleAverage::updateAverageWithDouble,
                             DoubleAverage::combineAverages).getAverage()
    );
  }

  private static void testExercise10Solution(Function<Stream<Double>, Double> avgFunction) {
    int numberOfDoubles = 20;
    Stream<Double> stream = Stream.iterate(0.0, n -> n + 1.7).limit(numberOfDoubles);

    //calculate expected average
    double expectedTotal = 0.0;
    double lastNum = 0;
    for(int doubleCount = 1; doubleCount <= numberOfDoubles - 1; doubleCount++) {
      Double num = lastNum + 1.7;
      expectedTotal += num;
      lastNum = num;
    }
    double expectedAverage = expectedTotal / numberOfDoubles;
    // calculate actual average
    double actualAverage = avgFunction.apply(stream);

    assertEquals(expectedAverage, actualAverage, 0.0000000000000000000001);
  }

}

