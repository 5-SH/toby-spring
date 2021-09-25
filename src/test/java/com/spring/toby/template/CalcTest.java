package com.spring.toby.template;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CalcTest {
  Calculator calculator;
  String numFilePath;

  @Before
  public void setUp() {
    this.calculator = new Calculator();
    this.numFilePath = "C:\\github\\spring\\toby-spring\\src\\test\\java\\com\\spring\\toby\\template\\numbers.txt";
  }

  @Test
  public void sumOfNumbers() throws IOException {
    Assert.assertThat(calculator.calcSum(numFilePath), Is.is(10));
  }

  @Test
  public void mulOfNumbers() throws IOException {
    Assert.assertThat(calculator.calcMul(numFilePath), Is.is(24));
  }
}
