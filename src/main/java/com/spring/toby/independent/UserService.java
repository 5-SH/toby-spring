package com.spring.toby.independent;

import java.util.List;

public interface UserService {
  public void add(User user);
  User get(String id);
  List<User> getAll();
  void deleteAll();
  void update(User user);
  public void upgradeLevels() throws Exception;
}
