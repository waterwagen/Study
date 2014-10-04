package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.waterwagen.Utilities;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestStreams {

  @Before
  public void setup() { }

  @Test
  public void testBasicStreamOperations() throws Exception {
    List<String> words = Lists.newArrayList("blah", "four", "bananas", "ninety");
    long wordsLongerThanFourCount = words.stream().filter(word -> word.length() > 4).count();
    assertEquals(2, wordsLongerThanFourCount);
    System.out.println(String.format("number of words longer than four: %s", wordsLongerThanFourCount));

    List<String> wordsLongerThanFour = words.stream().filter(word -> word.length() > 4).collect(Collectors.toList());
    System.out.println(String.format("Words longer than four: %s", wordsLongerThanFour));

    String longestWord = words.stream().max(Comparator.comparing(String::length)).orElse("");
    System.out.println(String.format("longest word (maxed) is %s", longestWord));

    longestWord = words.stream().reduce("", (first,second) -> first.length() > second.length() ? first : second);
    System.out.println(String.format("longest word (reduced) is %s", longestWord));

    List<Integer> nums = Lists.newArrayList(3,6,2,8,9,11,13);
    int sumOfEvens = nums.stream().reduce(0, (result,element) -> isIntegerEven(element) ? result + element : result);
    assertEquals(16, sumOfEvens);
    System.out.println(String.format("sum of evens (reduced only) from %s is %s", nums, sumOfEvens));

    sumOfEvens = nums.stream().filter(this::isIntegerEven).reduce(0, Math::addExact);
    assertEquals(16, sumOfEvens);
    System.out.println(String.format("sum of evens (reduced & filtered) from %s is %s", nums, sumOfEvens));

    sumOfEvens = nums.stream().filter(this::isIntegerEven).collect(Collectors.summingInt(num -> num));
    assertEquals(16, sumOfEvens);
    System.out.println(String.format("sum of evens (reduced & collected) from %s is %s", nums, sumOfEvens));

    int sumOfWordLengths = words.stream().reduce(0, (total,element) -> total + element.length(), Math::addExact);
    assertEquals(21, sumOfWordLengths);
    System.out.println(String.format("sum of word lengths (reduced[complex form]) from %s is %s",
                                     words,
                                     sumOfWordLengths));

    sumOfWordLengths = words.stream().map(String::length).reduce(0, (total,wordLength) -> total + wordLength);
    assertEquals(21, sumOfWordLengths);
    System.out.println(String.format("sum of word lengths (mappped & reduced[simple form]) from %s is %s",
                                     words,
                                     sumOfWordLengths));
  }

  @Test
  public void testLinearVsParallelProcessingTime() {
    Set<String> largeSetOfWords = new WordSetGenerator().withSetSize(100000000).generate();

    // linear
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    double avgMillisElapsed = Utilities.calculateAverageRuntime(() ->
      largeSetOfWords.stream().mapToInt(String::length).sum(),
      timeUnit);
    printElapsedTime(avgMillisElapsed, timeUnit, "linear");

    // parallel
    avgMillisElapsed = Utilities.calculateAverageRuntime(() ->
      largeSetOfWords.stream().parallel().mapToInt(String::length).sum(),
      timeUnit);
    printElapsedTime(avgMillisElapsed, timeUnit, "parallel");
  }

  private void printElapsedTime(Double timeElapsed,
                                TimeUnit timeUnit,
                                String qualifier) {
    System.out.println(
        String.format("elapsed time to calculate total words length (%s): %f %s", qualifier, timeElapsed, timeUnit));
  }

  private boolean isIntegerEven(Integer element) {
    return element % 2 == 0;
  }

  private class WordSetGenerator {

//    private Optional<Integer> wordLength = Optional.of(4);

    private Optional<Integer> setSize = Optional.empty();

//    public WordSetGenerator withWordLength(int wordLength) {
//      this.wordLength = Optional.of(wordLength);
//      return this;
//    }

    public WordSetGenerator withSetSize(int setSize) {
      this.setSize = Optional.of(setSize);
      return this;
    }

    public Set<String> generate() {
//      if(wordLength.isPresent() && setSize.isPresent()) {
      if(setSize.isPresent()) {
        Random randomNumberGenerator = new Random(System.currentTimeMillis());
        String word = StringUtils.repeat("a", randomNumberGenerator.nextInt(10));
        Set<String> result = Sets.newHashSetWithExpectedSize(setSize.get());
        for(int index = 0; index < setSize.get(); index++) {
          result.add(word);
        }
        return result;
      }
      throw new IllegalStateException("Not all required properties were set on this builder.");
    }
  }
}
