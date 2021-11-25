package com.spring.toby;

import com.spring.toby.sqlservice.ConcurrentHashMapSqlRegistry;
import com.spring.toby.sqlservice.SqlRetrievalFailureException;
import com.spring.toby.sqlservice.UpdatableSqlRegistry;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpdatableSqlRegistryTest {
  UpdatableSqlRegistry sqlRegistry;

  @Before
  public void setUp() {
    sqlRegistry = createUpdatableSqlRegistry();
  }

  abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

  @Test
  public void find() {
    checkFindResult("SQL1", "SQL2", "SQL3");
  }

  protected void checkFindResult(String expected1, String expected2, String expected3) {
    Assert.assertThat(sqlRegistry.findSql("KEY1"), Is.is(expected1));
    Assert.assertThat(sqlRegistry.findSql("KEY2"), Is.is(expected2));
    Assert.assertThat(sqlRegistry.findSql("KEY3"), Is.is(expected3));
  }

  @Test
  public void updateSingle() {
    sqlRegistry.updateSql("KEY2", "Modified2");
    checkFindResult("SQL1", "Modified2", "SQL3");
  }

  @Test
  public void updateMulti() {
    Map<String, String> sqlmap = new HashMap<>();
    sqlmap.put("KEY1", "Modified1");
    sqlmap.put("KEY3", "Modified3");

    sqlRegistry.updateSql(sqlmap);
    checkFindResult("Modified1", "SQL2", "Modified3");
  }

  @Test(expected=SqlRetrievalFailureException.class)
  public void updateWithNotExistingKey() {
    sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
  }
}
