package com.spring.toby;

import com.spring.toby.independent.*;
import com.spring.toby.independent.User;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class UserServiceTest {
  @Autowired
  UserService userService;
  @Autowired
  UserDao userDao;
  @Autowired
  PlatformTransactionManager transactionManager;

  List<User> users;

  @Before
  public void setUp() {
    this.users = Arrays.asList(
            new User("test1", "테스트일", "p1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER - 1, 0),
            new User("test2", "테스트이", "p2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0),
            new User("test3", "테스트삼", "p3", Level.SILVER, 60, UserService.MIN_RECOMMEND_FOR_GOLD - 1),
            new User("test4", "테스트사", "p4", Level.SILVER, 60, UserService.MIN_RECOMMEND_FOR_GOLD),
            new User("test5", "테스트오", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
    );
  }

  @Test
  public void bean() {
    Assert.assertThat(this.userService, Is.is(IsNull.notNullValue()));
  }

  @Test
  public void upgradeLevels() throws Exception {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    userService.upgradeLevels();

    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);
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

  @Test
  public void upgradeAllOrNothing() throws Exception {
    UserService testUserService = new TestUserService(users.get(3).getId());
    testUserService.setUserDao(userDao);
//    testUserService.setDataSource(dataSource);
    testUserService.setTransactionManager(transactionManager);

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      testUserService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  private void checkLevelUpgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      Assert.assertThat(userUpdate.getLevel(), Is.is(user.getLevel().nextLevel()));
    } else {
      Assert.assertThat(userUpdate.getLevel(), Is.is(user.getLevel()));
    }
  }
}
