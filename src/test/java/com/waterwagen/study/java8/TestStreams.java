package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
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

    sumOfEvens = nums.stream().filter(this::isIntegerEven).reduce(0, this::addIntegers);
    assertEquals(16, sumOfEvens);
    System.out.println(String.format("sum of evens (reduced & filtered) from %s is %s", nums, sumOfEvens));

    sumOfEvens = nums.stream().filter(this::isIntegerEven).collect(Collectors.summingInt(num -> num));
    assertEquals(16, sumOfEvens);
    System.out.println(String.format("sum of evens (reduced & collected) from %s is %s", nums, sumOfEvens));

    int sumOfWordLengths = words.stream().reduce(0, (total,element) -> total + element.length(), this::addIntegers);
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

  private boolean isIntegerEven(Integer element) {
    return element % 2 == 0;
  }

  private Integer addIntegers(Integer first, Integer second) {
    return first + second;
  }

}
