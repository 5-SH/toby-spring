package com.spring.toby.sqlservice;

import javax.annotation.PostConstruct;

public class XmlSqlService implements SqlService {
  private SqlReader sqlReader;
  private SqlRegistry sqlRegistry;

  public void setSqlReader(SqlReader sqlReader) {
    this.sqlReader = sqlReader;
  }

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.sqlReader.read(this.sqlRegistry);
  }

  @Override
  public String getSql(String key) {
    return this.sqlRegistry.findSql(key);
  }
}
