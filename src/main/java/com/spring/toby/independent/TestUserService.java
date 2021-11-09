package com.spring.toby.independent;

import java.util.List;

public class TestUserService extends UserServiceImpl {
  private String id;

  public TestUserService() { }

  public TestUserService(String id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = id;
  }

  protected void upgradeLevel(User user) {
    if (user.getId().equals(id)) throw new TestUserServiceException();
    super.upgradeLevel(user);
  }

  public List<User> getAll() {
    for (User user : super.getAll()) {
      super.update(user);
    }
    return null;
  }
}
