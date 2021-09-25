package com.spring.toby.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
  public Integer lineReadTemplate(String filepath, int initVal, LineCallback callback) throws IOException {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(filepath));

      Integer res = initVal;
      String line = null;
      while((line = br.readLine()) != null) {
        res = callback.doSomethingWithLine(line, res);
      }
      return res;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(filepath));
      int ret = callback.doSomethingWithReader(br);
      return ret;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Integer calcMul(String filepath) throws IOException {
    return lineReadTemplate(filepath, 1, new LineCallback() {
      @Override
      public Integer doSomethingWithLine(String line, int initVal) throws IOException {
        return initVal * Integer.valueOf(line);
      }
    });
  }

  public Integer calcSum(String filepath) throws IOException {
//    return fileReadTemplate(filepath, new BufferedReaderCallback() {
//      @Override
//      public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//        Integer sum = 0;
//        String line = null;
//
//        while((line = br.readLine()) != null) {
//          sum += Integer.valueOf(line);
//        }
//        return sum;
//      }
//    });

    return lineReadTemplate(filepath, 0, new LineCallback() {
      @Override
      public Integer doSomethingWithLine(String line, int initVal) throws IOException {
        return initVal + Integer.valueOf(line);
      }
    });
  }
}
