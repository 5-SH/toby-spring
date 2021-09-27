package com.spring.toby.independent;

import com.spring.toby.User;

import java.util.List;

public interface UserDao {
  void add(User user);
  User get(String id);
  List<User> getAll();
  void deleteAll();
  int getCount();
}
