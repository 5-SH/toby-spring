package com.spring.toby;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoJunitTest {
  @Test
  public void addAndGet() throws SQLException {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
    NewUserDao dao = context.getBean("newUserDao", NewUserDao.class);

    try {
      User user = new User();
      user.setId("test");
      user.setName("테스트");
      user.setPassword("1234");
      dao.add(user);
      User user2 = dao.get(user.getId());

      Assert.assertThat(user2.getName(), Is.is(user.getName()));
      Assert.assertThat(user2.getPassword(), Is.is(user.getPassword()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
