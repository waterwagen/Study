package com.waterwagen.study.misc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOverridingMethods {

  @Test
  public void testDefaultMethodDoesntHaveToBeImplemented() throws Exception {
    ChildClass childClass = new ChildClass().setName("blah");
    assertEquals("blah", childClass.getName());
  }

  public class AbstractClass {

    private String name = "abstract name";

    public AbstractClass setName(String name) {
      this.name = name;
        return this;
    }

    public String getName() {
      return name;
    }

  }

  public class ChildClass extends AbstractClass {

    @Override
    public ChildClass setName(String name) {
      super.setName(name);
      return this;
    }

  }

}

