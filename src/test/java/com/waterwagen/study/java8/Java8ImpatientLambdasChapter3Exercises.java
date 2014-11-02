package com.waterwagen.study.java8;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.waterwagen.study.java8.Java8ImpatientLambdasChapter3ExercisesCompanion.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class Java8ImpatientLambdasChapter3Exercises {

  @Mock
  private Logger loggerMock;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void exercise1() throws Exception {
    // given
    given(loggerMock.isLoggable(Level.WARNING)).willReturn(true);
    String successfulMessage = "successful message";
    String unsuccessfulMessage = "unsuccessful message";

    // when
    AtomicInteger messagesSent = new AtomicInteger(0);
    logIf(Level.WARNING, () -> messagesSent.incrementAndGet() <= 2, successfulMessage);
    logIf(Level.INFO, () -> messagesSent.incrementAndGet() <= 2, unsuccessfulMessage);
    logIf(Level.WARNING, () -> messagesSent.incrementAndGet() <= 2, successfulMessage);
    logIf(Level.WARNING, () -> messagesSent.incrementAndGet() <= 2, unsuccessfulMessage);

    // then
    verify(loggerMock, times(2)).log(any(Level.class), eq(successfulMessage));
    verify(loggerMock, never()).log(any(Level.class), eq(unsuccessfulMessage));
  }

  private boolean logIf(Level messageLevel, BooleanSupplier condition, String logMessage) {
    if(loggerMock.isLoggable(messageLevel) && condition.getAsBoolean()) {
      loggerMock.log(messageLevel, logMessage);
      return true;
    }
    return false;
  }

  @Test
  public void exercise2() throws Exception {
    // given
    AtomicInteger someInt = new AtomicInteger(0);
    ReentrantLock lock = new ReentrantLock();

    // when
    Exception caughtException = null;
    try {
      withLock(lock, () -> {
        someInt.incrementAndGet();
        assertTrue("The lock should have been in a locked state.", lock.isLocked());
        throw new RuntimeException("test exception to confirm the lock unlock always happens");
      });
    }
    catch (Exception exc) {
      caughtException = exc;
    }

    // then
    assertTrue("The test exception should have been thrown and not caught by the method.", caughtException != null);
    assertEquals("The locked action wasn't performed.", 1, someInt.get());
    assertFalse("The lock should have been unlocked after the action.", lock.isLocked());
  }

  @Test
  public void exercise5() throws Exception {
    // given
    Image originalImage = new Image(new FileInputStream(GRAY_SQUARE_IMAGE_FILE_NAME));

    // when
    Image transformedImage = transform(originalImage, (x,y,color) -> {
      if (isWithinImageBorder(new Point(x, y), originalImage)) {
        return DEFAULT_IMAGE_BORDER_COLOR;
      }
      return color;
    });

    // then
    verifyImageBorderIsBorderColorAndCenterIsNot(transformedImage);
  }

  @Test
  public void exercise6() throws Exception {
    // given
    Image originalImage = new Image(new FileInputStream(GRAY_SQUARE_IMAGE_FILE_NAME));

    // when
    Image transformedImage = transform(originalImage,
                                       (color, brightnessFactor) -> color.deriveColor(0, 1, brightnessFactor, 1),
                                       0.5);

    // then
    verifyImageIsDarkerThanOtherImage(transformedImage, originalImage);
  }

  @Test
  public void exercise7ForNormalComparator() throws Exception {
    // when
    Comparator<String> normal = createStringComparator(string -> string);

    // then
    assertEquals(0, normal.compare("abc", "abc"));
    assertTrue(normal.compare("abc", "zyx") < 0);
    assertTrue(normal.compare("zyx", "abc") > 0);
  }


  @Test
  public void exercise7ForReversedComparator() throws Exception {
    // when
    Comparator<String> normal = createStringComparator(StringUtils::reverse);

    // then
    assertEquals(0, normal.compare("abc", "abc"));
    assertTrue(normal.compare("zya", "abz") < 0);
    assertTrue(normal.compare("abz", "zya") > 0);
  }

  @Test
  public void exercise7ForCaseSensitiveComparator() throws Exception {
    // when
    Comparator<String> caseSensitive = createStringComparator(string -> string);

    // then
    assertEquals(0, caseSensitive.compare("aBc", "aBc"));
    assertTrue(caseSensitive.compare("aBc", "abc") < 0);
    assertTrue(caseSensitive.compare("abc", "aBc") > 0);
  }

  @Test
  public void exercise7ForCaseInsensitiveComparator() throws Exception {
    // when
    Comparator<String> caseInsensitive = createStringComparator(String::toLowerCase);

    // then
    assertEquals(0, caseInsensitive.compare("abc", "aBc"));
    assertTrue(caseInsensitive.compare("abc", "abd") < 0);
    assertTrue(caseInsensitive.compare("abd", "abc") > 0);
  }

  @Test
  public void exercise7ForSpaceSensitiveComparator() {
    // when
    Comparator<String> spaceSensitive = createStringComparator(string -> string);

    // then
    assertEquals(0, spaceSensitive.compare(" abc", " abc"));
    assertTrue(spaceSensitive.compare(" abc", "abc") < 0);
    assertTrue(spaceSensitive.compare("abc", " abc") > 0);
  }

  @Test
  public void exercise7ForSpaceInsensitiveComparator() {
    // when
    Comparator<String> spaceInsensitive = createStringComparator(StringUtils::deleteWhitespace);

    // then
    assertEquals(0, spaceInsensitive.compare("abc", " ab c"));
    assertTrue(spaceInsensitive.compare("aba", " abc") < 0);
    assertTrue(spaceInsensitive.compare(" abd", "abc") > 0);
  }

  @Test
  public void exercise7ForCombinationComparator() {
    solutionForCombinationStringComparator((f1, f2) -> string -> f1.apply(f2.apply(string)));
  }

  private <T> void solutionForCombinationStringComparator(
      BiFunction<UnaryOperator<String>, UnaryOperator<String>, UnaryOperator<String>> unaryOperatorCombiner) {
    // given
    UnaryOperator<String> whitespaceInsensitive = StringUtils::deleteWhitespace;
    UnaryOperator<String> caseInsensitive = String::toLowerCase;
    UnaryOperator<String> whitespaceAndCaseInsensitive =
      unaryOperatorCombiner.apply(whitespaceInsensitive, caseInsensitive);

    // when
    Comparator<String> combinationComparator = createStringComparator(whitespaceAndCaseInsensitive);

    // then
    assertEquals(0, combinationComparator.compare("abc", " aB c"));
    assertTrue(combinationComparator.compare("aba", " Abc") < 0);
    assertTrue(combinationComparator.compare(" Abd", "abc") > 0);
  }

  @Test
  public void exercise8() throws FileNotFoundException {
    // given
    Image originalImage = new Image(new FileInputStream(GRAY_SQUARE_IMAGE_FILE_NAME));
    BorderSpec borderSpec = new BorderSpec(15, Color.CYAN);

    // when
    Image transformedImage =
      transform(originalImage, createColorTransformerForBorder(originalImage, borderSpec));

    // then
    verifyImageBorderIsBorderColorAndCenterIsNot(transformedImage, borderSpec);
  }

  @Test
  public void exercise9() throws FileNotFoundException {
    // given
    Name object1 = new Name("John", "Samuel", "Smith");
    Name object2 = new Name("John", "Tyler", "Smith");
    Name object3 = new Name("Adam", "Barnhardt", "Smith");
    Name object4 = new Name("Caleb", "Barnhardt", "Taylor");

    // when
    Comparator<Name> lexicographicComparator = lexicographicComparator("lastName", "firstName");

    // then
    assertEquals("Unexpected comparator result.", 0, lexicographicComparator.compare(object1, object2));
    assertTrue("Expected comparator result to be greater than 1.",
        0 < lexicographicComparator.compare(object1, object3));
    assertTrue("Expected comparator result to be less than 1.",
        0 > lexicographicComparator.compare(object1, object4));
  }


  /**
   * An alternate (possibly improved) approach to the combination solution for exercise 7. Gets around the mismatch
   * between the compose (and the similar andThen()) method's return value's nominal (i.e. structure AND name) type
   * (Function<String, String>) difference from what the createStringComparator() method wants (UnaryOperator<String>)
   * by taking advantage of the fact that the structural types are the same (String -> String). We do this by using a
   * method reference to use the implementation of the Function<String,String> to provide to Java 8 which will turn
   * that implementation into a UnaryOperator<String> for us.
   */
  @Test
  public void exercise10() {
    solutionForCombinationStringComparator((f1, f2) -> f1.andThen(f2)::apply);
  }

  @Test
  public void exercise11() throws FileNotFoundException {
    // given
    Image originalImage = new Image(new FileInputStream(GRAY_SQUARE_IMAGE_FILE_NAME));
    BorderSpec borderSpec = new BorderSpec(15, Color.CYAN);
    ColorTransformer borderTransformer = createColorTransformerForBorder(originalImage, borderSpec);
    UnaryOperator<Color> brightenOperator = color -> color.brighter();
    ColorTransformer brightenTransformer = toColorTransformer(brightenOperator);
    ColorTransformer brightenAndBorderTransformer = combineColorTransformers(brightenTransformer, borderTransformer);

    // when
    Image transformedImage = transform(originalImage, brightenAndBorderTransformer);

    // then
    verifyImageBorderColor(transformedImage, borderSpec);
    Point imageCenter = getCenterPointOfImage(originalImage);
    Color originalCenterColor = getImageColorAtPoint(originalImage, imageCenter);
    verifyNonBorderImageColor(transformedImage, borderSpec, brightenOperator.apply(originalCenterColor));
  }

}

