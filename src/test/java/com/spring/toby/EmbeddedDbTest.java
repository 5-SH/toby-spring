package com.spring.toby;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

public class EmbeddedDbTest {
  EmbeddedDatabase db;
  SimpleJdbcTemplate template;

  @Before
  public void setUp() {
    db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:schema.sql")
            .addScript("classpath:data.sql")
            .build();

    template = new SimpleJdbcTemplate(db);
  }

  @After
  public void tearDown() {
    db.shutdown();
  }

  @Test
  public void initData() {
    Assert.assertThat(template.queryForInt("select count(*) from sqlmap"), Is.is(2));

    List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
    Assert.assertThat((String)list.get(0).get("key_"), Is.is("KEY1"));
    Assert.assertThat((String)list.get(0).get("sql_"), Is.is("SQL1"));
    Assert.assertThat((String)list.get(1).get("key_"), Is.is("KEY2"));
    Assert.assertThat((String)list.get(1).get("sql_"), Is.is("SQL2"));
  }

  @Test
  public void insert() {
    template.update("insert into sqlmap(key_, sql_) values (?, ?)", "KEY3", "SQL3");

    Assert.assertThat(template.queryForInt("select count(*) from sqlmap"), Is.is(3));
  }
}
