package com.waterwagen.study.java8;

import java.util.concurrent.locks.Lock;

public class Java8ImpatientLambdasChapter3ExercisesCompanion {

  static void withLock(Lock lock, Runnable action) {
    lock.lock();
    try {
      action.run();
    }
    finally {
      lock.unlock();
    }
  }
}
