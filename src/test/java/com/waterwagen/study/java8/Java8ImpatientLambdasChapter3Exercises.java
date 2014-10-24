package com.waterwagen.study.java8;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

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

}

