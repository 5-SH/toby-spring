package com.spring.toby.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface LineCallback {
  Integer doSomethingWithLine(String line, int initVal) throws IOException;
}
