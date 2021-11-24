package com.spring.toby.sqlservice;

import com.spring.toby.independent.UserDao;
import com.spring.toby.sqlservice.jaxb.SqlType;
import com.spring.toby.sqlservice.jaxb.Sqlmap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {
  private final BaseSqlService baseSqlService = new BaseSqlService();
  private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
  private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  public void setUnmarshaller(Unmarshaller unmarshaller) {
    this.oxmSqlReader.setUnmarshaller(unmarshaller);
  }

  public void setSqlmap(Resource sqlmap) {
    this.oxmSqlReader.setSqlmap(sqlmap);
  }

  @PostConstruct
  public void loadSql() {
    this.baseSqlService.setSqlReader(oxmSqlReader);
    this.baseSqlService.setSqlRegistry(sqlRegistry);

    this.baseSqlService.loadSql();
  }

  @Override
  public String getSql(String key) {
    return this.baseSqlService.getSql(key);
  }


  private class OxmSqlReader implements SqlReader {
    private Resource sqlmap = new ClassPathResource("sqlmap.xml");
    private Unmarshaller unmarshaller;

    public void setUnmarshaller(Unmarshaller unmarshaller) {
      this.unmarshaller = unmarshaller;
    }

    public void setSqlmap(Resource sqlmap) {
      this.sqlmap = sqlmap;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
      try {
        Source source = new StreamSource(sqlmap.getInputStream());
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
        for (SqlType sql : sqlmap.getSql()) {
          sqlRegistry.registerSql(sql.getKey(), sql.getValue());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
