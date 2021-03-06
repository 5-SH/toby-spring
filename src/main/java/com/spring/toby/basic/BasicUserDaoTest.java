package com.spring.toby.basic;

import com.spring.toby.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BasicUserDaoTest {
  private static final Logger logger = LoggerFactory.getLogger(BasicUserDaoTest.class);

  public static void main(String[] args) throws ClassNotFoundException {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    BasicUserDao dao = context.getBean("userDao", BasicUserDao.class);
    try {
      User user = new User();
      user.setId("whiteship");
      user.setName("백기선");
      user.setPassword("married");

      dao.add(user);

      logger.info("{} 등록 성공", user.getId());

      User user2 = dao.get(user.getId());
      logger.info("{}", user2.getName());
      logger.info("{}", user2.getPassword());
      logger.info("{} 조회 성공", user2.getId());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
