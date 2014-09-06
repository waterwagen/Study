package com.waterwagen.study.java8;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestInterfaces {

  private static final String DEFAULT_NAME = "Default Name";

  private static final String OVERIDDEN_NAME = "Overridden Name";

  private static final String CLASS_UNRELATED_TO_INTERFACE_NAME = "Class Unrelated To Interface Name";

  @Test
  public void testDefaultMethodDoesntHaveToBeImplemented() throws Exception {
    assertEquals(DEFAULT_NAME, new DefaultMethodInterfaceImplWithoutImplementation().getName());
  }

  private interface DefaultMethodInterface {
    default String getName() {
      return DEFAULT_NAME;
    }
  }

  private static class DefaultMethodInterfaceImplWithoutImplementation implements DefaultMethodInterface {}

  @Test
  public void testDefaultMethodCanBeOverridden() throws Exception {
    assertEquals(OVERIDDEN_NAME, new DefaultMethodInterfaceImplWithImplementation().getName());
  }

  private static class DefaultMethodInterfaceImplWithImplementation {
    //@Override
    String getName() {
      return OVERIDDEN_NAME;
    }
  }

  @Test
  public void testDefaultMethodFromParentClassOverInterface() throws Exception {
    assertEquals(CLASS_UNRELATED_TO_INTERFACE_NAME, new SubclassAndInterfaceImplementor().getName());
  }

  private static class ClassUnrelatedToInterfaceOverridesDefaultMethod {
    public String getName() {
      return CLASS_UNRELATED_TO_INTERFACE_NAME;
    }
  }

  private static class SubclassAndInterfaceImplementor extends ClassUnrelatedToInterfaceOverridesDefaultMethod
                                                       implements DefaultMethodInterface {}

  @Test
  public void testStaticMethodsCanBeInInterface() throws Exception {
    assertTrue(StaticMethodInterface.isThisAStaticMethod());
  }

  private interface StaticMethodInterface {
    static boolean isThisAStaticMethod() {
      return true;
    }
  }
}

