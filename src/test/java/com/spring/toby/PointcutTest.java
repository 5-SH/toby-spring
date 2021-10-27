package com.spring.toby;

import com.spring.toby.pointcut.Bean;
import com.spring.toby.pointcut.Target;
import com.spring.toby.proxy.Hello;
import com.spring.toby.proxy.HelloTarget;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
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

  @Test
  public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(public int com.spring.toby.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

    Assert.assertThat(pointcut.getClassFilter().matches(Target.class) &&
            pointcut.getMethodMatcher().matches(
                    Target.class.getMethod("minus", int.class, int.class), null), Is.is(true));

    Assert.assertThat(pointcut.getClassFilter().matches(Target.class) &&
            pointcut.getMethodMatcher().matches(
                    Target.class.getMethod("plus", int.class, int.class), null), Is.is(false));

    Assert.assertThat(pointcut.getClassFilter().matches(Bean.class) &&
            pointcut.getMethodMatcher().matches(
                    Target.class.getMethod("method"), null), Is.is(false));
  }

  public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression(expression);

    Assert.assertThat(pointcut.getClassFilter().matches(clazz)
            && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null),
            Is.is(expected));
  }

  public void targetClassPointcutMatches(String expression, boolean... exprected) throws Exception {
    pointcutMatches(expression, exprected[0], Target.class, "hello");
    pointcutMatches(expression, exprected[1], Target.class, "hello", String.class);
    pointcutMatches(expression, exprected[2], Target.class, "plus", int.class, int.class);
    pointcutMatches(expression, exprected[3], Target.class, "minus", int.class, int.class);
    pointcutMatches(expression, exprected[4], Target.class, "method");
    pointcutMatches(expression, exprected[5], Bean.class, "method");
  }

  @Test
  public void pointcut() throws Exception {
    targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
  }
}
