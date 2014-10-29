package com.waterwagen.study.java8;

import javafx.scene.image.Image;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
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
        return IMAGE_BORDER_COLOR;
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
    Comparator<String> normal = generateStringComparator(string -> string);

    // then
    assertEquals(0, normal.compare("abc", "abc"));
    assertTrue(normal.compare("abc", "zyx") < 0);
    assertTrue(normal.compare("zyx", "abc") > 0);
  }


  @Test
  public void exercise7ForReversedComparator() throws Exception {
    // when
    Comparator<String> normal = generateStringComparator(StringUtils::reverse);

    // then
    assertEquals(0, normal.compare("abc", "abc"));
    assertTrue(normal.compare("zya", "abz") < 0);
    assertTrue(normal.compare("abz", "zya") > 0);
  }

  @Test
  public void exercise7ForCaseSensitiveComparator() throws Exception {
    // when
    Comparator<String> caseSensitive = generateStringComparator(string -> string);

    // then
    assertEquals(0, caseSensitive.compare("aBc", "aBc"));
    assertTrue(caseSensitive.compare("aBc", "abc") < 0);
    assertTrue(caseSensitive.compare("abc", "aBc") > 0);
  }

  @Test
  public void exercise7ForCaseInsensitiveComparator() throws Exception {
    // when
    Comparator<String> caseInsensitive = generateStringComparator(String::toLowerCase);

    // then
    assertEquals(0, caseInsensitive.compare("abc", "aBc"));
    assertTrue(caseInsensitive.compare("abc", "abd") < 0);
    assertTrue(caseInsensitive.compare("abd", "abc") > 0);
  }

  @Test
  public void exercise7ForSpaceSensitiveComparator() {
    // when
    Comparator<String> spaceSensitive = generateStringComparator(string -> string);

    // then
    assertEquals(0, spaceSensitive.compare(" abc", " abc"));
    assertTrue(spaceSensitive.compare(" abc", "abc") < 0);
    assertTrue(spaceSensitive.compare("abc", " abc") > 0);
  }

  @Test
  public void exercise7ForSpaceInsensitiveComparator() {
    // when
    Comparator<String> spaceInsensitive = generateStringComparator(StringUtils::deleteWhitespace);

    // then
    assertEquals(0, spaceInsensitive.compare("abc", " ab c"));
    assertTrue(spaceInsensitive.compare("aba", " abc") < 0);
    assertTrue(spaceInsensitive.compare(" abd", "abc") > 0);
  }

}

