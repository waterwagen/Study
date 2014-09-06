package com.waterwagen.study.java8;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLambdas {

  private static final String MESSAGE = "Some message";

  private static final String PARENT_TRANSLATED_MESSAGE = "parent translated message";

  private static final String CHILD_TRANSLATED_MESSAGE = "child translated message";

  private static final String PREFIX = "PRE:";

  private MessagePrefixer messagePrefixer;

  @Before
  public void setup() {
    messagePrefixer = new MessagePrefixer();
  }

  @Test
  public void testStatementStyleLambdaIsConvertedIntoFunctionalInterface() throws Exception {
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, (String message) -> { return message.toUpperCase(); });
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  private static class MessagePrefixer {

    private String prefixMessage(String message, MessageTranslator mt) {
      return PREFIX + mt.translate(message);
    }
  }

  private interface MessageTranslator {
    String translate(String message);
  }

  @Test
  public void testStatementStyleLambdaIsConvertedIntoFunctionalInterfaceNoTypeForArgument() throws Exception {
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, message -> { return message.toUpperCase(); });
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  @Test
  public void testExpressionStyleLambdaIsConvertedIntoFunctionalInterfaceNoTypeForArgument() throws Exception {
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, message -> message.toUpperCase());
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  @Test
  public void testLambdaAsLocalVariable() {
    MessageTranslator lambda = message -> message.toUpperCase();
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, lambda);
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  @Test
  public void testLambdaAsStaticMethodReference() {
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, TestLambdas::translateMessageStatic);
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  private static String translateMessageStatic(String message) {
    return message.toUpperCase();
  }

  @Test
  public void testLambdaAsObjectInstanceMethodReference() {
    String prefixedMessage = messagePrefixer.prefixMessage(MESSAGE, new StringUpperCaser()::toUpperCase);
    assertEquals(PREFIX + MESSAGE.toUpperCase(), prefixedMessage);
  }

  private static class StringUpperCaser {
    private String toUpperCase(String string) {
      return string.toUpperCase();
    }
  }

  @Test
  public void testLambdaAsClassInstanceMethodReference() {
    assertEquals(true, new StringLessThanChecker().checkNumbers("a", "b", String::compareTo));
    assertEquals(false, new StringLessThanChecker().checkNumbers("b", "a", String::compareTo));
  }

  private class StringLessThanChecker {
    private boolean checkNumbers(String s1, String s2, StringComparer sc) {
      return sc.compare(s1, s2) < 0;
    }
  }

  private interface StringComparer {
    int compare(String string1, String string2);
  }

  @Test
  public void testLambdaAsThisMethodReference() {
    String prefixedMessage =
      messagePrefixer.prefixMessage(MESSAGE, new MessageTranslatorFactoryChildUsingThis().create());
    assertEquals(PREFIX + CHILD_TRANSLATED_MESSAGE, prefixedMessage);
  }

  private abstract static class MessageTranslatorFactoryParent {
    abstract MessageTranslator create();

    String translateMessageInstance(String message) {
      return PARENT_TRANSLATED_MESSAGE;
    }
  }

  private static class MessageTranslatorFactoryChildUsingThis extends MessageTranslatorFactoryParent {
    @Override
    MessageTranslator create() {
      return this::translateMessageInstance;
    }

    @Override
    String translateMessageInstance(String message) {
      return CHILD_TRANSLATED_MESSAGE;
    }
  }

  @Test
  public void testLambdaAsSuperMethodReference() {
    String prefixedMessage =
      messagePrefixer.prefixMessage(MESSAGE, new MessageTranslatorFactoryChildUsingSuper().create());
    assertEquals(PREFIX + PARENT_TRANSLATED_MESSAGE, prefixedMessage);
  }

  private static class MessageTranslatorFactoryChildUsingSuper extends MessageTranslatorFactoryParent {
    @Override
    MessageTranslator create() {
      return super::translateMessageInstance;
    }
  }

  @Test
  public void testLambdaIsAClosure() {
    int a = 1;
    int b = 2;
    int result = (Integer)new Processor().runProcess(() -> a + b);
    assertEquals(3, result);
  }

  private static class Processor {
    Object runProcess(Process process) {
      return process.run();
    }

    interface Process {
      Object run();
    }
  }

}
