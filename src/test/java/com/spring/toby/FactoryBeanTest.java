package com.spring.toby;

import com.spring.toby.factorybean.Message;
import com.spring.toby.factorybean.MessageFactoryBean;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:FactoryBeanTest-Context.xml")
public class FactoryBeanTest {
  @Autowired
  ApplicationContext context;

  @Test
  public void getMessageFromFactoryBean() {
    Object message = context.getBean("message");
    Assert.assertThat(message, Is.is(Message.class));
    Assert.assertThat(((Message) message).getText(), Is.is("Factory Bean"));
  }

  @Test
  public void getFactoryBean() throws Exception {
    Object factory = context.getBean("&message");
    Assert.assertThat(factory, Is.is(MessageFactoryBean.class));
  }
}
