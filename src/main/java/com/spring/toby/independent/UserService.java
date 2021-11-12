package com.spring.toby.independent;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
  void add(User user);
  void deleteAll();
  void update(User user);
  void upgradeLevels() throws Exception;

  @Transactional(readOnly = true)
  List<User> getAll();
  @Transactional(readOnly = true)
  User get(String id);

}
