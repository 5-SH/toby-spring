package com.spring.toby;

import com.spring.toby.template.TemplateUserDao;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
@DirtiesContext
public class TemplateUserDaoJunitTest {
  @Autowired
  private TemplateUserDao dao;

  private User user1;
  private User user2;
  private User user3;

  @Before
  public void setUp() {
    DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/spring", "root", "root", true);
    dao.setDataSource(dataSource);

    this.user1 = new User("test1", "테스트일", "1234");
    this.user2 = new User("test2", "테스트이", "1234");
    this.user3 = new User("test3", "테스트삼", "1234");
  }

  @Test
  public void addAndGet() {
    try {
      dao.deleteAll();
      Assert.assertThat(dao.getCount(), Is.is(0));

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
    dao.deleteAll();
    Assert.assertThat(dao.getCount(), Is.is(0));
    dao.get("unknown_id");
  }
}
