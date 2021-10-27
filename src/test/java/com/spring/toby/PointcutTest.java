package com.spring.toby;

import com.spring.toby.proxy.Hello;
import com.spring.toby.proxy.HelloTarget;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class PointcutTest {
  @Test
  public void classNamePointcutAdvisor() {
    NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
      public ClassFilter getClassFilter() {
        return new ClassFilter() {
          public boolean matches(Class<?> clazz) {
            return clazz.getSimpleName().startsWith("HelloT");
          }
        };
      }
    };
    classMethodPointcut.setMappedName("sayH*");

    checkAdviced(new HelloTarget(), classMethodPointcut, true);

    class HelloWorld extends HelloTarget {};
    checkAdviced(new HelloWorld(), classMethodPointcut, false);

    class HelloToby extends HelloTarget {};
    checkAdviced(new HelloTarget(), classMethodPointcut, true);
  }

  private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(target);
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new DynamicProxyTest.UpperCaseAdvice()));
    Hello proxiedHello = (Hello) pfBean.getObject();

    if (adviced) {
      Assert.assertThat(proxiedHello.sayHello("Toby"), Is.is("HELLO TOBY"));
      Assert.assertThat(proxiedHello.sayHi("Toby"), Is.is("HI TOBY"));
      Assert.assertThat(proxiedHello.sayThankYou("Toby"), Is.is("Thank You Toby"));
    } else {
      Assert.assertThat(proxiedHello.sayHello("Toby"), Is.is("Hello Toby"));
      Assert.assertThat(proxiedHello.sayHi("Toby"), Is.is("Hi Toby"));
      Assert.assertThat(proxiedHello.sayThankYou("Toby"), Is.is("Thank You Toby"));
    }
  }
}
