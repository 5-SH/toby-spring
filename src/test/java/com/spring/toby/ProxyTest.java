package com.spring.toby;

import com.spring.toby.proxy.Hello;
import com.spring.toby.proxy.HelloTarget;
import com.spring.toby.proxy.HelloUppercase;
import com.spring.toby.proxy.UppercaseHandler;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class ProxyTest {
  @Test
  public void simpleProxy() {
    Hello hello = new HelloTarget();

    Assert.assertThat(hello.sayHello("Toby"), Is.is("Hello Toby"));
    Assert.assertThat(hello.sayHi("Toby"), Is.is("Hi Toby"));
    Assert.assertThat(hello.sayThankYou("Toby"), Is.is("Thank You Toby"));
  }

  @Test
  public void helloUppercase() {
    Hello hello = new HelloUppercase(new HelloTarget());

    Assert.assertThat(hello.sayHello("Toby"), Is.is("HELLO TOBY"));
    Assert.assertThat(hello.sayHi("Toby"), Is.is("HI TOBY"));
    Assert.assertThat(hello.sayThankYou("Toby"), Is.is("THANK YOU TOBY"));
  }

  @Test
  public void dynamicProxy() {
    Hello proxiedHandler = (Hello) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { Hello.class },
            new UppercaseHandler(new HelloTarget())
    );

    Assert.assertThat(proxiedHandler.sayHello("Toby"), Is.is("HELLO TOBY"));
    Assert.assertThat(proxiedHandler.sayHi("Toby"), Is.is("HI TOBY"));
    Assert.assertThat(proxiedHandler.sayThankYou("Toby"), Is.is("THANK YOU TOBY"));
  }
}
