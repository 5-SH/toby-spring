package com.spring.toby;

import com.spring.toby.context.AppContext;
import com.spring.toby.factorybean.TxProxyFactoryBean;
import com.spring.toby.independent.*;
import com.spring.toby.independent.User;
import com.spring.toby.proxy.TransactionHandler;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes=AppContext.class)
@Transactional
@TransactionConfiguration(defaultRollback = false)
//@ContextConfiguration(locations="classpath:test-applicationContext.xml")
public class UserServiceTest {
  @Autowired
  UserService userService;
  @Autowired
  UserDao userDao;
  @Autowired
  PlatformTransactionManager transactionManager;
  @Autowired
  MailSender mailSender;
  @Autowired
  ApplicationContext context;
  @Autowired
  UserService testUserService;
  @Autowired
  DefaultListableBeanFactory bf;

  List<User> users;

  @Before
  public void setUp() {
    this.users = Arrays.asList(
            new User("test1", "????????????", "p1", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER - 1, 0, "mail1@test.com"),
            new User("test2", "????????????", "p2", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER, 0, "mail2@test.com"),
            new User("test3", "????????????", "p3", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD - 1, "mail3@test.com"),
            new User("test4", "????????????", "p4", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD, "mail4@test.com"),
            new User("test5", "????????????", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "mail5@test.com")
    );
  }

  @Test
  public void beans() {
    for (String n : bf.getBeanDefinitionNames()) {
      System.out.println(n + " \t " + bf.getBean(n).getClass().getName());
    }
  }

  @Test
  public void bean() {
    Assert.assertThat(this.userService, Is.is(IsNull.notNullValue()));
  }

  @Test
  public void upgradeLevels() throws Exception {
    UserServiceImpl userServiceImpl = new UserServiceImpl();

    MockUserDao mockUserDao = new MockUserDao(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    List<User> updated = mockUserDao.getUpdated();
    Assert.assertThat(updated.size(), Is.is(2));
    checkUserAndLevel(updated.get(0), "test2", Level.SILVER);
    checkUserAndLevel(updated.get(1), "test4", Level.GOLD);

//    checkLevelUpgraded(users.get(0), false);
//    checkLevelUpgraded(users.get(1), true);
//    checkLevelUpgraded(users.get(2), false);
//    checkLevelUpgraded(users.get(3), true);
//    checkLevelUpgraded(users.get(4), false);

    List<String> requests = mockMailSender.getRequests();
    Assert.assertThat(requests.size(), Is.is(2));
    Assert.assertThat(requests.get(0), Is.is(users.get(1).getEmail()));
    Assert.assertThat(requests.get(1), Is.is(users.get(3).getEmail()));
  }

  private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
    Assert.assertThat(updated.getId(), Is.is(expectedId));
    Assert.assertThat(updated.getLevel(), Is.is(expectedLevel));
  }

  @Test
  public void mockUpgradeLevels() throws Exception {
    UserServiceImpl userServiceimpl = new UserServiceImpl();

    UserDao mockUserDao = Mockito.mock(UserDao.class);
    Mockito.when(mockUserDao.getAll()).thenReturn(this.users);
    userServiceimpl.setUserDao(mockUserDao);

    MailSender mockMailSender = Mockito.mock(MailSender.class);
    userServiceimpl.setMailSender(mockMailSender);

    userServiceimpl.upgradeLevels();

    Mockito.verify(mockUserDao, Mockito.times(2)).update(Matchers.any(User.class));
    Mockito.verify(mockUserDao).update(users.get(1));
    Assert.assertThat(users.get(1).getLevel(), Is.is(Level.SILVER));
    Mockito.verify(mockUserDao).update(users.get(3));
    Assert.assertThat(users.get(3).getLevel(), Is.is(Level.GOLD));

    ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
    Mockito.verify(mockMailSender, Mockito.times(2)).send(mailMessageArg.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
    Assert.assertThat(mailMessages.get(0).getTo()[0], Is.is(users.get(1).getEmail()));
    Assert.assertThat(mailMessages.get(1).getTo()[0], Is.is(users.get(3).getEmail()));
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
  @Transactional(propagation = Propagation.NEVER)
  public void upgradeAllOrNothing() throws Exception {
    TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
    testUserService.setUserDao(userDao);
//    testUserService.setDataSource(dataSource);
//    testUserService.setTransactionManager(transactionManager);
    testUserService.setMailSender(mailSender);

    UserServiceTx userServiceTx = new UserServiceTx();
    userServiceTx.setTransactionManager(transactionManager);
    userServiceTx.setUserService(testUserService);

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      userServiceTx.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  public void upgradeAllOrNothingProxy() throws Exception {
    TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
    testUserService.setUserDao(userDao);
    testUserService.setMailSender(mailSender);

    TransactionHandler txHandler = new TransactionHandler();
    txHandler.setTarget(testUserService);
    txHandler.setTransactionManager(transactionManager);
    txHandler.setPattern("upgradeLevels");
    UserService userService = (UserService) Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class[] { UserService.class }, txHandler
    );

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      userService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  @Test
  @DirtiesContext
  @Transactional(propagation = Propagation.NEVER)
  public void upgradeAllOrNothingFactoryBean() throws Exception {
    TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
    testUserService.setUserDao(userDao);
    testUserService.setMailSender(mailSender);

    TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userServiceTx", TxProxyFactoryBean.class);
    txProxyFactoryBean.setTarget(testUserService);
    UserService txUserService = (UserService) txProxyFactoryBean.getObject();

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      txUserService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  @Test
  @DirtiesContext
  @Transactional(propagation = Propagation.NEVER)
  public void ugradeAllOrNothingAdvisor() throws Exception {
    TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
    testUserService.setUserDao(userDao);
    testUserService.setMailSender(mailSender);

    ProxyFactoryBean txProxyFactoryBean = context.getBean("&userServiceAdvisor", ProxyFactoryBean.class);
    txProxyFactoryBean.setTarget(testUserService);
    UserService txUserService = (UserService) txProxyFactoryBean.getObject();

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      txUserService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  public void upgradeAllOrNothingAutoProxy() throws Exception {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      this.testUserService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevelUpgraded(users.get(1), false);
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  public void upgradeAllOrNothingTxAop() throws Exception {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      this.testUserService.upgradeLevels();
      Assert.fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }
  }

  @Test
  public void advisorAutoProxyCreator() {
    Assert.assertThat(testUserService, Is.is(java.lang.reflect.Proxy.class));
  }

  @Test(expected=TransientDataAccessResourceException.class)
  @Transactional(propagation = Propagation.NEVER)
  public void readOnlyTransactionAttribute() {
    testUserService.getAll();
  }

  @Test
//  @Transactional
  @Rollback
  public void transactionSync() {
    userService.deleteAll();
    userService.add(users.get(0));
    userService.add(users.get(1));
  }

  private void checkLevelUpgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      Assert.assertThat(userUpdate.getLevel(), Is.is(user.getLevel().nextLevel()));
    } else {
      Assert.assertThat(userUpdate.getLevel(), Is.is(user.getLevel()));
    }
  }

  public static class TestUserServiceImpl extends UserServiceImpl {
    private String id = "test4";

    public TestUserServiceImpl() {}

    public TestUserServiceImpl(String id) {
      this.id = id;
    }

    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) throw new TestUserServiceException();
      super.upgradeLevel(user);
    }
  }
}
