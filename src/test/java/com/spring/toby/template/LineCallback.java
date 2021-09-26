package com.spring.toby.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface LineCallback<T> {
  T doSomethingWithLine(String line, T initVal) throws IOException;
}
