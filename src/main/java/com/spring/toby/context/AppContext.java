package com.spring.toby.context;

import com.spring.toby.factorybean.TxProxyFactoryBean;
import com.spring.toby.independent.*;
import com.spring.toby.proxy.TransactionAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="com.spring.toby")
@Import({ SqlServiceContext.class })
@PropertySource("classpath:database.properties")
public class AppContext {
  @Value("${db.driverClass}")
  Class<? extends Driver> driverClass;
  @Value("${db.url}")
  String url;
  @Value("${db.username}")
  String username;
  @Value("${db.password}")
  String password;

  @Autowired
  UserService userService;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(this.driverClass);
    dataSource.setUrl(this.url);
    dataSource.setUsername(this.username);
    dataSource.setPassword(this.password);

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager tm = new DataSourceTransactionManager();
    tm.setDataSource(dataSource());
    return tm;
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

  @Bean
  public TxProxyFactoryBean userServiceTx() {
    TxProxyFactoryBean factoryBean = new TxProxyFactoryBean();
    factoryBean.setTarget(this.userService);
    factoryBean.setTransactionManager(transactionManager());
    factoryBean.setPattern("upgradeLevels");
    factoryBean.setServiceInterface(UserService.class);
    return factoryBean;
  }

  @Configuration
  @Profile("production")
  public static class ProductionAppContext {
    @Bean
    public MailSender mailSender() {
      JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
      mailSender.setHost("mail.com");
      return mailSender;
    }

  }

  @Configuration
  @Profile("test")
  public static class TestAppContext {
    @Bean
    public UserService testUserService() {
      TestUserService testUserService = new TestUserService();
      testUserService.setId("test4");
      return testUserService;
    }

    @Bean
    public MailSender mailSender() {
      return new DummyMailSender();
    }
  }

}
