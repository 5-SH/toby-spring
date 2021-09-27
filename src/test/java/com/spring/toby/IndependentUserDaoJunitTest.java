package com.spring.toby;

import com.spring.toby.independent.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
@DirtiesContext
public class IndependentUserDaoJunitTest {
  @Autowired
  UserDao dao;
  private User user1;
  private User user2;
  private User user3;

  @Before
  public void setUp() {
    this.user1 = new User("test1", "테스트일", "1234");
    this.user2 = new User("test2", "테스트이", "1234");
    this.user3 = new User("test3", "테스트삼", "1234");
  }

  @Test(expected= DataAccessException.class)
  public void duplicatedKey() {
    dao.deleteAll();

    dao.add(user1);
    dao.add(user1);
  }
}
