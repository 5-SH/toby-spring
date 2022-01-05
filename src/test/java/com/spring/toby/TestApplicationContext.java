package com.spring.toby;

import com.mysql.jdbc.Driver;
import com.spring.toby.factorybean.TxProxyFactoryBean;
import com.spring.toby.independent.*;
import com.spring.toby.proxy.TransactionAdvice;
import com.spring.toby.sqlservice.EmbeddedDbSqlRegistry;
import com.spring.toby.sqlservice.OxmSqlService;
import com.spring.toby.sqlservice.SqlRegistry;
import com.spring.toby.sqlservice.SqlService;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="com.spring.toby")
// @ImportResource("classpath:test-applicationContext.xml")
public class TestApplicationContext {
  @Autowired
  UserDao userDao;
  @Autowired
  UserService userService;

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost/spring");
    dataSource.setUsername("root");
    dataSource.setPassword("root");

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager tm = new DataSourceTransactionManager();
    tm.setDataSource(dataSource());
    return tm;
  }

  @Bean
  public TxProxyFactoryBean userServiceTx() {
    TxProxyFactoryBean factoryBean = new TxProxyFactoryBean();
    factoryBean.setTarget(this.userService);
    factoryBean.setTransactionManager(transactionManager());
    factoryBean.setPattern("upgradeLevels");
    factoryBean.setServiceInterface(UserService.class);
    return factoryBean;
  }

  @Bean
  public TransactionAdvice transactionAdvice() {
    TransactionAdvice advice = new TransactionAdvice();
    advice.setTransactionManager(transactionManager());
    return advice;
  }

  @Bean
  public AspectJExpressionPointcut transactionPointcut() {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(* *..*ServiceImpl.upgrade*(..))");
    return pointcut;
  }

  @Bean
  public DefaultPointcutAdvisor transactionAdvisor() {
    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
    advisor.setAdvice(transactionAdvice());
    advisor.setPointcut(transactionPointcut());
    return advisor;
  }

  @Bean
  public ProxyFactoryBean userServiceAdvisor() {
    ProxyFactoryBean factoryBean = new ProxyFactoryBean();
    factoryBean.setTarget(this.userService);
    factoryBean.setInterceptorNames(new String[] { "transactionAdvisor" });
    return factoryBean;
  }

//  @Bean
//  public UserDao userDao() {
//    return new UserDaoJdbc();
//  }

//  @Bean
//  public UserService userService() {
//    UserServiceImpl service = new UserServiceImpl();
//    service.setUserDao(this.userDao);
//    service.setMailSender(mailSender());
//    return service;
//  }

  @Bean
  public UserService testUserService() {
    TestUserService testService = new TestUserService();
    testService.setId("test4");
    testService.setUserDao(this.userDao);
    testService.setMailSender(mailSender());
    return testService;
  }

  @Bean
  public UserServiceTest.TestUserServiceImpl testUserServiceImpl() {
    UserServiceTest.TestUserServiceImpl testUserService = new UserServiceTest.TestUserServiceImpl();
    testUserService.setUserDao(this.userDao);
    testUserService.setMailSender(mailSender());
    return testUserService;
  }

  @Bean
  public MailSender mailSender() {
    return new DummyMailSender();
  }

  @Bean
  public SqlService sqlService() {
    OxmSqlService sqlService = new OxmSqlService();
    sqlService.setUnmarshaller(unmarshaller());
    sqlService.setSqlRegistry(sqlRegistry());
    return sqlService;
  }

  @Bean
  public SqlRegistry sqlRegistry() {
    EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
    sqlRegistry.setDataSource(embeddedDatabase());
    return sqlRegistry;
  }

  @Bean
  public Jaxb2Marshaller unmarshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("com.spring.toby.sqlservice.jaxb");
    return marshaller;
  }

  @Bean
  public DataSource embeddedDatabase() {
    return new EmbeddedDatabaseBuilder()
            .setName("embeddedDatabase")
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:sqlRegistrySchema.sql")
            .build();
  }
}
