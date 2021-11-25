package com.spring.toby.sqlservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
  private Map<String, String> sqlMap = new ConcurrentHashMap<>();

  @Override
  public void registerSql(String key, String sql) {
    sqlMap.put(key, sql);
  }

  @Override
  public String findSql(String key) throws SqlRetrievalFailureException {
    String sql = sqlMap.get(key);
    if (sql == null) throw new SqlRetrievalFailureException(key + "를 이용해서 SQL 을 찾을 수 없습니다.");
    return sql;
  }

  @Override
  public void updateSql(String key, String sql) throws SqlRetrievalFailureException {
    if (sqlMap.get(key) == null) throw new SqlRetrievalFailureException(key + "에 해당하는 SQL 을 찾을 수 없습니다.");
    sqlMap.put(key, sql);
  }

  @Override
  public void updateSql(Map<String, String> sqlmap) throws SqlRetrievalFailureException {
    for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
      updateSql(entry.getKey(), entry.getValue());
    }
  }
}
