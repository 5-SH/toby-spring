package com.spring.toby;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class ReflectionTest {
  @Test
  public void invokeMethod() throws Exception {
    String name = "Spring";

    Assert.assertThat(name.length(), Is.is(6));

    Method lengthMethod = String.class.getMethod("length");
    Assert.assertThat((Integer) lengthMethod.invoke(name), Is.is(6));

    Assert.assertThat(name.charAt(0), Is.is('S'));

    Method charAtMethod = String.class.getMethod("charAt", int.class);
    Assert.assertThat((Character) charAtMethod.invoke(name, 0), Is.is('S'));
  }
}
