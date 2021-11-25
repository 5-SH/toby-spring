package com.spring.toby;

import com.spring.toby.sqlservice.ConcurrentHashMapSqlRegistry;
import com.spring.toby.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    UpdatableSqlRegistry sqlRegistry = new ConcurrentHashMapSqlRegistry();
    sqlRegistry.registerSql("KEY1", "SQL1");
    sqlRegistry.registerSql("KEY2", "SQL2");
    sqlRegistry.registerSql("KEY3", "SQL3");

    return sqlRegistry;
  }
}
