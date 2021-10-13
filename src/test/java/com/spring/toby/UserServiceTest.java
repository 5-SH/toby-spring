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
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class UserServiceTest {
  @Autowired
  UserServiceImpl userServiceImpl;
  @Autowired
  UserDao userDao;
  @Autowired
  PlatformTransactionManager transactionManager;
  @Autowired
  MailSender mailSender;

  List<User> users;

  @Before
  public void setUp() {
    this.users = Arrays.asList(
            new User("test1", "테스트일", "p1", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER - 1, 0, "mail1@test.com"),
            new User("test2", "테스트이", "p2", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER, 0, "mail2@test.com"),
            new User("test3", "테스트삼", "p3", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD - 1, "mail3@test.com"),
            new User("test4", "테스트사", "p4", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD, "mail4@test.com"),
            new User("test5", "테스트오", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "mail5@test.com")
    );
  }

  @Test
  public void bean() {
    Assert.assertThat(this.userServiceImpl, Is.is(IsNull.notNullValue()));
  }

  @Test
  public void upgradeLevels() throws Exception {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);

    List<String> requests = mockMailSender.getRequests();
    Assert.assertThat(requests.size(), Is.is(2));
    Assert.assertThat(requests.get(0), Is.is(users.get(1).getEmail()));
    Assert.assertThat(requests.get(1), Is.is(users.get(3).getEmail()));
  }

  @Test
  public void add() {
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userServiceImpl.add(userWithLevel);
    userServiceImpl.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    Assert.assertThat(userWithLevelRead.getLevel(), Is.is(userWithLevel.getLevel()));
    Assert.assertThat(userWithoutLevelRead.getLevel(), Is.is(userWithoutLevel.getLevel()));
  }

  @Test
  public void upgradeAllOrNothing() throws Exception {
    UserServiceImpl testUserServiceImpl = new TestUserService(users.get(3).getId());
    testUserServiceImpl.setUserDao(userDao);
//    testUserService.setDataSource(dataSource);
//    testUserServiceImpl.setTransactionManager(transactionManager);
    testUserServiceImpl.setMailSender(mailSender);

    UserServiceTx userServiceTx = new UserServiceTx();
    userServiceTx.setTransactionManager(transactionManager);
    userServiceTx.setUserService(testUserServiceImpl);

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      userServiceTx.upgradeLevels();
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
