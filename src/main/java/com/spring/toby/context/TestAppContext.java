package com.spring.toby.context;

import com.spring.toby.independent.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

@Configuration
@Profile("test")
public class TestAppContext {
  @Bean
  public UserService testUserService() {
    TestUserService testUserService = new TestUserService();
    testUserService.setId("test4");
    return testUserService;
  }

  @Bean
  public MailSender mailSender() {
    return new DummyMailSender();
  }
}
