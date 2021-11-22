package com.spring.toby;

import com.spring.toby.sqlservice.jaxb.SqlType;
import com.spring.toby.sqlservice.jaxb.Sqlmap;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;

public class JaxbTest {
  @Test
  public void readSqlmap() throws JAXBException, IOException {
    String contextPath = Sqlmap.class.getPackage().getName();
    JAXBContext context = JAXBContext.newInstance(contextPath);
    Unmarshaller unmarshaller = context.createUnmarshaller();

    Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(this.getClass().getClassLoader().getResourceAsStream("sqlmap.xml"));
    List<SqlType> sqlList = sqlmap.getSql();

    Assert.assertThat(sqlList.size(), Is.is(3));
    Assert.assertThat(sqlList.get(0).getKey(), Is.is("add"));
    Assert.assertThat(sqlList.get(0).getValue(), Is.is("insert"));
    Assert.assertThat(sqlList.get(1).getKey(), Is.is("get"));
    Assert.assertThat(sqlList.get(1).getValue(), Is.is("select"));
    Assert.assertThat(sqlList.get(2).getKey(), Is.is("delete"));
    Assert.assertThat(sqlList.get(2).getValue(), Is.is("delete"));
  }
}
