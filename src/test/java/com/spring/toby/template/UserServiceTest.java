package com.spring.toby.template;

import com.spring.toby.independent.Level;
import com.spring.toby.independent.User;
import com.spring.toby.independent.UserDao;
import com.spring.toby.independent.UserService;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class UserServiceTest {
  @Autowired
  UserService userService;
  @Autowired
  UserDao userDao;

  List<User> users;

  @Before
  public void setUp() {
    this.users = Arrays.asList(
            new User("test1", "테스트일", "p1", Level.BASIC, 49, 0),
            new User("test2", "테스트이", "p2", Level.BASIC, 50, 0),
            new User("test3", "테스트삼", "p3", Level.SILVER, 60, 29),
            new User("test4", "테스트사", "p4", Level.SILVER, 60, 30),
            new User("test5", "테스트오", "p5", Level.GOLD, 100, 100)
    );
  }

  @Test
  public void bean() {
    Assert.assertThat(this.userService, Is.is(IsNull.notNullValue()));
  }

  @Test
  public void upgradeLevels() {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    userService.upgradeLevels();

    checkLevel(users.get(0),Level.BASIC);
    checkLevel(users.get(1),Level.SILVER);
    checkLevel(users.get(2),Level.SILVER);
    checkLevel(users.get(3),Level.GOLD);
    checkLevel(users.get(4),Level.GOLD);
  }

  @Test
  public void add() {
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    Assert.assertThat(userWithLevelRead.getLevel(), Is.is(userWithLevel.getLevel()));
    Assert.assertThat(userWithoutLevelRead.getLevel(), Is.is(userWithoutLevel.getLevel()));
  }

  private void checkLevel(User user, Level expectedLevel) {
    User userUpdate = userDao.get(user.getId());
    Assert.assertThat(userUpdate.getLevel(), Is.is(expectedLevel));
  }
}
