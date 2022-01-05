package com.spring.toby.context;

import com.mysql.jdbc.Driver;
import com.spring.toby.sqlservice.EmbeddedDbSqlRegistry;
import com.spring.toby.sqlservice.OxmSqlService;
import com.spring.toby.sqlservice.SqlRegistry;
import com.spring.toby.sqlservice.SqlService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {
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
