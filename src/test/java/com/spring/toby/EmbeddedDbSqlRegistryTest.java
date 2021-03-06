package com.spring.toby;

import com.spring.toby.sqlservice.EmbeddedDbSqlRegistry;
import com.spring.toby.sqlservice.SqlRetrievalFailureException;
import com.spring.toby.sqlservice.UpdatableSqlRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashMap;
import java.util.Map;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
  EmbeddedDatabase db;

  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:schema.sql")
            .addScript("classpath:data.sql")
            .build();

    EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
    embeddedDbSqlRegistry.setDataSource(db);

    return embeddedDbSqlRegistry;
  }

  @Test
  public void transactionalUpdate() {
    checkFindResult("SQL1", "SQL2", "SQL3");

    Map<String, String> sqlmap = new HashMap<>();
    sqlmap.put("KEY1", "Modified1");
    sqlmap.put("KEY9999!@#$", "Modified9999");

    try {
      sqlRegistry.updateSql(sqlmap);
      Assert.fail("SqlRetrievalFailureException expected");
    } catch (SqlRetrievalFailureException e) { }
  }

  @After
  public void tearDown() {
    db.shutdown();
  }
}
