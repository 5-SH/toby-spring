package com.spring.toby.independent;

public class TestUserService extends UserServiceImpl {
  private String id;

  public TestUserService(String id) {
    this.id = id;
  }

  protected void upgradeLevel(User user) {
    if (user.getId().equals(id)) throw new TestUserServiceException();
    super.upgradeLevel(user);
  }
}
