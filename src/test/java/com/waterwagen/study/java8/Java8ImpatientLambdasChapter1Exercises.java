package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.waterwagen.Utilities.uncheckedRunnable;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Java8ImpatientLambdasChapter1Exercises {

  private static final String LAMBDA_EXERCISES_ROOT_DIRECTORY = "./src/test/resources/lambdaExercises";

  private static final String LAMBDA_EXERCISES_SUB_DIRECTORY1 = "./src/test/resources/lambdaExercises/SomeDirectory1";

  private static final String LAMBDA_EXERCISES_SUB_DIRECTORY2 = "./src/test/resources/lambdaExercises/SomeDirectory2";

  private static final String FIRST_DEFAULT_RESULT = "First interface default result";

  private static final String FIRST_STATIC_RESULT = "First interface static result";

  private static final String SECOND_DEFAULT_RESULT = "Second interface default result";

  private static final String SECOND_STATIC_RESULT = "Second interface static result";

  private static final String FIRST_AND_SECOND_ABSTRACT_RESULT = "First and second interface impl abstract result";

  private static final String FIRST_AND_SECOND_DEFAULT_RESULT = "First and second interface impl default result";

  private static final String EXTENDS_PARENT_FIRST_INTERFACE_ABSTRACT_RESULT =
    "Extends parent class and first interface impl abstract result";

  private static final String PARENT_STATIC_RESULT = "Parent class static result";

  private static final String PARENT_DEFAULT_RESULT = "Parent class default result";

  @Test
  public void exercise2WithLambdaExpression() throws Exception {
    File[] files = new File(LAMBDA_EXERCISES_ROOT_DIRECTORY).listFiles(File::isDirectory);
    assertEquals(2, files.length);
  }

  @Test
  public void exercise2WithMethodExpression() throws Exception {
    File[] files = new File(LAMBDA_EXERCISES_ROOT_DIRECTORY).listFiles(File::isDirectory);
    assertEquals(2, files.length);
  }

  @Test
  public void exercise3() throws Exception {
    String fileExtension = "txt";
    String[] fileNamesWithExtension =
      new File(LAMBDA_EXERCISES_ROOT_DIRECTORY).list((dir, name) -> name.endsWith(fileExtension));
    assertEquals(3, fileNamesWithExtension.length);
  }

  @Test
  public void exercise4() throws Exception {
    List<File> allFiles = Lists.newArrayList(new File(LAMBDA_EXERCISES_SUB_DIRECTORY1).listFiles());
    allFiles.addAll(Lists.newArrayList(new File(LAMBDA_EXERCISES_SUB_DIRECTORY2).listFiles()));
    allFiles.addAll(Lists.newArrayList(new File(LAMBDA_EXERCISES_ROOT_DIRECTORY).listFiles()));
    Collections.sort(allFiles,
      (file1, file2) -> file1.isDirectory() != file2.isDirectory()
                        ? (file1.isDirectory() ? -1 : 1)
                        : file1.getPath().compareTo(file2.getPath()));

    List<String> actualFileNames = extractFileNames(allFiles);
    List<String> expectedFileNames = Lists.newArrayList("SomeDirectory1",
                                                        "SomeDirectory2",
                                                        "someDirectory1File1.txt",
                                                        "someDirectory1File2.txt",
                                                        "someDirectory2File1.jpg",
                                                        "someDirectory2File2.txt",
                                                        "someFile1.txt",
                                                        "someFile2.txt",
                                                        "someFile3.txt",
                                                        "someFile4.blah",
                                                        "someFile5.blah");
    assertEquals(expectedFileNames, actualFileNames);
  }

  private List<String> extractFileNames(List<File> allFiles) {
    return allFiles.stream().map(File::getName).collect(Collectors.toList());
  }

  @Test
  public void exercise6() throws Exception {
    int a = 2;
    int b = 3;
    BlockingQueue<Integer> result = new LinkedBlockingQueue<>(1);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(uncheckedRunnable(() -> {
      Thread.sleep(1L);
      result.add(a + b);
    }));

    assertEquals(5, result.take().intValue());
  }

  @Test
  public void exercise7() throws Exception {
    BlockingQueue<Integer> result = new LinkedBlockingQueue<>(1);
    result.add(2);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<?> threadFuture = executorService.submit(
      andThen(
        () -> result.add(result.poll() * 2),
        () -> result.add(result.poll() + 10)
    ));
    threadFuture.get();

    assertEquals(14, result.take().intValue());
  }

  private static Runnable andThen(Runnable... runnables) {
    return () -> {
      for(Runnable runnable : runnables) {
        runnable.run();
      }
    };
  }

  @Test
  public void exercise8() throws Exception {
    String[] names = { "Peter", "Paul", "Mary" };

    BlockingQueue<String> actualNamesQueue = new LinkedBlockingQueue<>();
    Runnable[] runners = new Runnable[names.length];
    int index = 0;
    for (String name : names) {
      runners[index++] = () -> actualNamesQueue.add(name);
    }
//  Traditional for loop approach does not compile because index below is not effectively final.
//  Apparently the variable in the enhanced for loop above works differently.
//  ---
//  for (int index = 0; index < names.length; index++) {
//    runners[index++] = () -> actualNamesQueue.add(names[index]);
//  }
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<?> threadFuture = executorService.submit(andThen(runners));
    threadFuture.get();

    String[] actualNames = actualNamesQueue.toArray(new String[0]);
    assertArrayEquals(names, actualNames);
  }

  @Test
  public void exercise9() throws Exception {
    MyList<Integer> numbers = new MyList<>(Lists.newArrayList(1,3,5,7,9,11,13,15,17,19));
    List<Integer> result = Lists.newArrayListWithCapacity(1);
    result.add(0);

    numbers.forEachIf(element -> result.set(0, result.get(0) + element),
                      element -> element > 9);

    assertEquals(75, result.get(0).intValue());
  }

  private static interface Collection2<E> extends Collection<E> {
    default void forEachIf(Consumer<E> action, Predicate<E> filter) {
      forEach((element) -> { if(filter.test(element)) action.accept(element); });
    }
  }

  private static class MyList<E> extends ArrayList<E> implements Collection2<E> {

    public MyList(Collection<E> elements) {
      super(elements);
    }
  }

  @Test
  public void exercise10ImplementsTwoInterfaces() {
    FirstAndSecondInterfaceImpl firstAndSecondInterfaceImpl = new FirstAndSecondInterfaceImpl();

    assertEquals(FIRST_AND_SECOND_ABSTRACT_RESULT, firstAndSecondInterfaceImpl.abstractMethod());
    assertEquals(FIRST_AND_SECOND_DEFAULT_RESULT, firstAndSecondInterfaceImpl.defaultMethod());
    // below is not valid because the static methods belong to the interfaces
    //assertEquals(???, firstAndSecondInterfaceImpl.staticMethod());
    assertEquals(FIRST_STATIC_RESULT, FirstInterface.staticMethod());
    assertEquals(SECOND_STATIC_RESULT, SecondInterface.staticMethod());
  }

  private interface FirstInterface {

    String abstractMethod();

    default String defaultMethod() { return FIRST_DEFAULT_RESULT; }

    static String staticMethod() { return FIRST_STATIC_RESULT; }

  }

  private interface SecondInterface {

    String abstractMethod();

    default String defaultMethod() { return SECOND_DEFAULT_RESULT; }

    static String staticMethod() { return SECOND_STATIC_RESULT; }

  }

  private static class FirstAndSecondInterfaceImpl implements FirstInterface, SecondInterface {
    @Override
    public String abstractMethod() {
      return FIRST_AND_SECOND_ABSTRACT_RESULT;
    }

    /**
     * Must override this method since both interfaces this class implements have the same
     * default method signature.
     */
    @Override
    public String defaultMethod() {
      return FIRST_AND_SECOND_DEFAULT_RESULT;
    }
  }

  @Test
  public void exercise10ExtendsClassAndImplementsInterface() {
    ExtendsParentClassAndFirstInterfaceImpl extendsParentClassAndFirstInterfaceImpl =
      new ExtendsParentClassAndFirstInterfaceImpl();

    assertEquals(EXTENDS_PARENT_FIRST_INTERFACE_ABSTRACT_RESULT,
                 extendsParentClassAndFirstInterfaceImpl.abstractMethod());
    assertEquals(PARENT_DEFAULT_RESULT, extendsParentClassAndFirstInterfaceImpl.defaultMethod());
    // below is not valid because the static methods belong to the interfaces
    //assertEquals(???, extendsParentClassAndFirstInterfaceImpl.staticMethod());
    assertEquals(PARENT_STATIC_RESULT, ParentClass.staticMethod());
  }

  private abstract static class ParentClass {

    public abstract String abstractMethod();

    public String defaultMethod() { return PARENT_DEFAULT_RESULT; }

    public static String staticMethod() { return PARENT_STATIC_RESULT; }

  }

  private static class ExtendsParentClassAndFirstInterfaceImpl extends ParentClass implements FirstInterface {

    @Override
    public String abstractMethod() {
      return EXTENDS_PARENT_FIRST_INTERFACE_ABSTRACT_RESULT;
    }

  }

}
