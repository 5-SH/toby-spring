package com.spring.toby;

import com.spring.toby.independent.Level;
import com.spring.toby.independent.UserDao;
import com.spring.toby.independent.User;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

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
    this.user1 = new User("test1", "테스트일", "1234", Level.BASIC, 1, 0);
    this.user2 = new User("test2", "테스트이", "1234", Level.SILVER, 55, 10);
    this.user3 = new User("test3", "테스트삼", "1234", Level.GOLD, 100, 40);
  }

  @Test(expected= DataAccessException.class)
  public void duplicatedKey() {
    dao.deleteAll();

    dao.add(user1);
    dao.add(user1);
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
      checkSameUser(userget1, user1);

      User userget2 = dao.get(user2.getId());
      checkSameUser(userget2, user2);
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

  @Test
  public void getAll() throws SQLException {
    dao.deleteAll();

    List<User> users0 = dao.getAll();
    Assert.assertThat(users0.size(), Is.is(0));

    dao.add(user1);
    List<User> users1 = dao.getAll();
    Assert.assertThat(users1.size(), Is.is(1));
    checkSameUser(user1, users1.get(0));

    dao.add(user2);
    List<User> users2 = dao.getAll();
    Assert.assertThat(users2.size(), Is.is(2));
    checkSameUser(user1, users2.get(0));
    checkSameUser(user2, users2.get(1));

    dao.add(user3);
    List<User> users3 = dao.getAll();
    Assert.assertThat(users3.size(), Is.is(3));
    checkSameUser(user1, users3.get(0));
    checkSameUser(user2, users3.get(1));
    checkSameUser(user3, users3.get(2));
  }

  @Test
  public void update() {
    dao.deleteAll();

    dao.add(user1);
    dao.add(user2);
    dao.add(user3);

    user1.setName("수정된 테스트일");
    user1.setPassword("5678");
    user1.setLevel(Level.GOLD);
    user1.setLogin(1000);
    user1.setRecommend(999);
    dao.update(user1);

    checkSameUser(user1, dao.get(user1.getId()));
    checkSameUser(user2, dao.get(user2.getId()));
    checkSameUser(user3, dao.get(user3.getId()));
  }

  private void checkSameUser(User user1, User user2) {
    Assert.assertThat(user1.getId(), Is.is(user2.getId()));
    Assert.assertThat(user1.getName(), Is.is(user2.getName()));
    Assert.assertThat(user1.getPassword(), Is.is(user2.getPassword()));
    Assert.assertThat(user1.getLevel(), Is.is(user2.getLevel()));
    Assert.assertThat(user1.getLevel(), Is.is(user2.getLevel()));
    Assert.assertThat(user1.getRecommend(), Is.is(user2.getRecommend()));
  }
}
