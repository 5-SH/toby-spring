package com.spring.toby.independent;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceTx implements UserService {
  private UserService userService;
  private PlatformTransactionManager transactionManager;

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  public void add(User user) {
    userService.add(user);
  }

  @Override
  public void upgradeLevels() throws Exception {
    TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      userService.upgradeLevels();
      this.transactionManager.commit(status);
    } catch (Exception e) {
      this.transactionManager.rollback(status);
      throw e;
    }
  }

  @Override
  public User get(String id) {
    return userService.get(id);
  }

  @Override
  public List<User> getAll() {
    return userService.getAll();
  }

  @Override
  public void deleteAll() {
    userService.deleteAll();
  }

  @Override
  public void update(User user) {
    userService.update(user);
  }
}
