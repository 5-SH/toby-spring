package com.spring.toby.independent;

import com.spring.toby.independent.User;

import java.util.List;

public interface UserDao {
  int add(User user);
  User get(String id);
  List<User> getAll();
  int deleteAll();
  int getCount();
  int update(User user);
}
