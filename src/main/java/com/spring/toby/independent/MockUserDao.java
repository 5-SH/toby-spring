package com.spring.toby.independent;

import java.util.ArrayList;
import java.util.List;

public class MockUserDao implements UserDao {
  private List<User> users;
  private List<User> updated = new ArrayList<>();

  public MockUserDao(List<User> users) {
    this.users = users;
  }

  public List<User> getUpdated() {
    return updated;
  }

  @Override
  public List<User> getAll() {
    return this.users;
  }

  @Override
  public void update(User user) {
    updated.add(user);
  }

  @Override
  public int add(User user) {
    throw new UnsupportedOperationException();
  }

  @Override
  public User get(String id) {
    throw new UnsupportedOperationException();
  }


  @Override
  public int deleteAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getCount() {
    throw new UnsupportedOperationException();
  }
}
