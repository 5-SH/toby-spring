package com.spring.toby;

import com.mysql.jdbc.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
@ImportResource("classpath:test-applicationContext.xml")
public class TestApplicationContext {
  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost/spring");
    dataSource.setUsername("root");
    dataSource.setPassword("root");

    return dataSource;
  }
}
