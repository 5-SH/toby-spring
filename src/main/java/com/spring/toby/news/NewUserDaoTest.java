package com.spring.toby.news;

import com.spring.toby.User;
import com.spring.toby.basic.UserDaoTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NewUserDaoTest {
  private static final Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

  public static void main(String[] args) throws ClassNotFoundException {
    ApplicationContext context = new AnnotationConfigApplicationContext(NewDaoFactory.class);
    NewUserDao dao = context.getBean("userDao", NewUserDao.class);
    try {
      User user = new User();
      user.setId("miniaerin");
      user.setName("주애린");
      user.setPassword("1229");

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
