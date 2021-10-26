package com.spring.toby;

import com.spring.toby.independent.TestUserServiceException;
import com.spring.toby.proxy.Hello;
import com.spring.toby.proxy.HelloTarget;
import com.spring.toby.proxy.HelloUppercase;
import com.spring.toby.proxy.UppercaseHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

public class DynamicProxyTest {
  @Test
  public void simpleProxy() {
    Hello hello = new HelloTarget();

    Assert.assertThat(hello.sayHello("Toby"), Is.is("Hello Toby"));
    Assert.assertThat(hello.sayHi("Toby"), Is.is("Hi Toby"));
    Assert.assertThat(hello.sayThankYou("Toby"), Is.is("Thank You Toby"));
  }

  @Test
  public void proxyFactoryBean() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());
    pfBean.addAdvice(new UpperCaseAdvice());

    Hello proxiedHello = (Hello) pfBean.getObject();
    Assert.assertThat(proxiedHello.sayHello("Toby"), Is.is("HELLO TOBY"));
    Assert.assertThat(proxiedHello.sayHi("Toby"), Is.is("HI TOBY"));
    Assert.assertThat(proxiedHello.sayThankYou("Toby"), Is.is("THANK YOU TOBY"));
  }

  static class UpperCaseAdvice implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
      String ret = (String) invocation.proceed();
      return ret.toUpperCase();
    }
  }

  @Test
  public void pointcutAdvisor() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());

    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");

    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));

    Hello proxiedHello = (Hello) pfBean.getObject();

    Assert.assertThat(proxiedHello.sayHello("Toby"), Is.is("HELLO TOBY"));
    Assert.assertThat(proxiedHello.sayHi("Toby"), Is.is("HI TOBY"));
    try {
      if (!"THANK YOU TOBY".equals(proxiedHello.sayThankYou("Toby"))) throw new RuntimeException();
      Assert.fail("Exception expected");
    } catch (RuntimeException e) {
    }
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
