package com.spring.toby.context;

import com.spring.toby.sqlservice.EmbeddedDbSqlRegistry;
import com.spring.toby.sqlservice.OxmSqlService;
import com.spring.toby.sqlservice.SqlRegistry;
import com.spring.toby.sqlservice.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {
//  @Autowired
//  Environment env;
//
//  @Bean
//  public DataSource dataSource() {
//    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//
////    dataSource.setDriver(this.driverClass);
////    dataSource.setUrl(this.url);
////    dataSource.setUsername(this.username);
////    dataSource.setPassword(this.password);
//
//    try {
//      dataSource.setDriverClass((Class<? extends java.sql.Driver>)Class.forName(env.getProperty("db.driverClass")));
//    } catch(ClassNotFoundException e) {
//      throw new RuntimeException(e);
//    }
//
//    dataSource.setUrl(env.getProperty("db.url"));
//    dataSource.setUsername(env.getProperty("db.username"));
//    dataSource.setPassword(env.getProperty("db.password"));
//
//    return dataSource;
//  }

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
