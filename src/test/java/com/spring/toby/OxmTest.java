package com.spring.toby;

import com.spring.toby.sqlservice.jaxb.SqlType;
import com.spring.toby.sqlservice.jaxb.Sqlmap;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:OxmTest-context.xml")
public class OxmTest {
  @Autowired
  Unmarshaller unmarshaller;

  @Test
  public void unmarshallSqlMap() throws IOException {
    Source xmlSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("sqlmap-test-2.xml"));
    Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);

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
