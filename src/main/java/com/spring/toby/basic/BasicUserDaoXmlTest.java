package com.spring.toby.basic;

import com.spring.toby.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class BasicUserDaoXmlTest {
  private static final Logger logger = LoggerFactory.getLogger(BasicUserDaoTest.class);

  public static void main(String[] args) throws ClassNotFoundException, SQLException  {
    ApplicationContext context = new GenericXmlApplicationContext("classpath:applicationContext.xml");
    BasicUserDao dao = context.getBean("userDao", BasicUserDao.class);
    try {
      User user = new User();
      user.setId("osh");
      user.setName("오승호");
      user.setPassword("1234");

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
