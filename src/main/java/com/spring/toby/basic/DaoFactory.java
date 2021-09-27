package com.spring.toby.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
  @Bean
  public BasicUserDao userDao() {
    return new BasicUserDao(new NConnectionMaker());
  }
}
