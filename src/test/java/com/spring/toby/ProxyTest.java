package com.spring.toby;

import com.spring.toby.proxy.Hello;
import com.spring.toby.proxy.HelloTarget;
import com.spring.toby.proxy.HelloUppercase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class ProxyTest {
  @Test
  public void simpleProxy() {
    Hello hello = new HelloTarget();

    Assert.assertThat(hello.sayHello("Toby"), Is.is("Hello Toby"));
    Assert.assertThat(hello.sayHi("Toby"), Is.is("Hi Toby"));
    Assert.assertThat(hello.sayThankYou("Toby"), Is.is("Thank You Toby"));
  }

  @Test
  public void uppercase() {
    Hello hello = new HelloUppercase(new HelloTarget());

    Assert.assertThat(hello.sayHello("Toby"), Is.is("HELLO TOBY"));
    Assert.assertThat(hello.sayHi("Toby"), Is.is("HI TOBY"));
    Assert.assertThat(hello.sayThankYou("Toby"), Is.is("THANK YOU TOBY"));
  }

}
