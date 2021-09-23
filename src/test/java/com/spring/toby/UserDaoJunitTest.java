package com.spring.toby;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

public class UserDaoJunitTest {
  @Test
  public void addAndGet() {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
    NewUserDao dao = context.getBean("newUserDao", NewUserDao.class);

    try {
      dao.deleteAll();
      Assert.assertThat(dao.getCount(), Is.is(0));

      User user1 = new User("test1", "테스트일", "1234");
      User user2 = new User("test2", "테스트이", "1234");
      dao.add(user1);
      dao.add(user2);

      Assert.assertThat(dao.getCount(), Is.is(2));

      User userget1 = dao.get(user1.getId());
      Assert.assertThat(userget1.getName(), Is.is(user1.getName()));
      Assert.assertThat(userget1.getPassword(), Is.is(user1.getPassword()));

      User userget2 = dao.get(user2.getId());
      Assert.assertThat(userget2.getName(), Is.is(user2.getName()));
      Assert.assertThat(userget2.getPassword(), Is.is(user2.getPassword()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void count() {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
    NewUserDao dao = context.getBean("newUserDao", NewUserDao.class);

    User user1 = new User("Test1", "테스트일", "1234");
    User user2 = new User("Test2", "테스트이", "1234");
    User user3 = new User("Test3", "테스트삼", "1234");

    try {
      dao.deleteAll();
      Assert.assertThat(dao.getCount(), Is.is(0));

      dao.add(user1);
      Assert.assertThat(dao.getCount(), Is.is(1));

      dao.add(user2);
      Assert.assertThat(dao.getCount(), Is.is(2));

      dao.add(user3);
      Assert.assertThat(dao.getCount(), Is.is(3));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test(expected= EmptyResultDataAccessException.class)
  public void getUserFailure() throws SQLException, ClassNotFoundException {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
    NewUserDao dao = context.getBean("newUserDao", NewUserDao.class);

    dao.deleteAll();
    Assert.assertThat(dao.getCount(), Is.is(0));
    dao.get("unknown_id");
  }
}
