package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Java8ImpatientLambdasChaperExercises {

  private static final String LAMBDA_EXERCISES_ROOT_DIRECTORY = "./src/test/resources/lambdaExercises";

  private static final String LAMBDA_EXERCISES_SUB_DIRECTORY1 = "./src/test/resources/lambdaExercises/SomeDirectory1";

  private static final String LAMBDA_EXERCISES_SUB_DIRECTORY2 = "./src/test/resources/lambdaExercises/SomeDirectory2";

  @Test
  public void exercise2WithLambdaExpression() throws Exception {
    File[] files = new File(LAMBDA_EXERCISES_ROOT_DIRECTORY).listFiles((file) -> file.isDirectory());
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
    return allFiles.stream().map(file -> file.getName()).collect(Collectors.toList());
  }
}
