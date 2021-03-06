package com.spring.toby.basic;

import com.spring.toby.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class BasicUserDaoConnectionCountingTest {
  private static final Logger logger = LoggerFactory.getLogger(BasicUserDaoTest.class);

  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
    BasicUserDao dao = context.getBean("userDao", BasicUserDao.class);

    User user1 = dao.get("whiteship");
    logger.info("{}", user1.getName());
    logger.info("{}", user1.getPassword());
    logger.info("{} 조회 성공", user1.getId());

    User user2 = dao.get("whiteship");
    logger.info("{}", user2.getName());
    logger.info("{}", user2.getPassword());
    logger.info("{} 조회 성공", user2.getId());

    User user3 = dao.get("whiteship");
    logger.info("{}", user3.getName());
    logger.info("{}", user3.getPassword());
    logger.info("{} 조회 성공", user3.getId());

    CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
    logger.info("Connection counter : {}", ccm.getCounter());
  }
}
