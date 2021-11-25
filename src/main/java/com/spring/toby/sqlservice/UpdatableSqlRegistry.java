package com.spring.toby.sqlservice;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry {
  public void updateSql(String key, String sql) throws SqlRetrievalFailureException;
  public void updateSql(Map<String, String> sqlmap) throws SqlRetrievalFailureException;
}
