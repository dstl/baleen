// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.activemq;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.resources.SharedActiveMQResource;

public class SharedActiveMQResourceTest {

  private SharedActiveMQResource samr;

  private static String PROTOCOL_VALUE = "vm";
  private static String BROKERARGS_VALUE = "broker.persistent=false";

  @Before
  public void beforeTest() throws Exception {

    samr = new SharedActiveMQResource();
    final CustomResourceSpecifier_impl samrSpecifier = new CustomResourceSpecifier_impl();
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedActiveMQResource.PARAM_PROTOCOL, PROTOCOL_VALUE),
          new Parameter_impl(SharedActiveMQResource.PARAM_BROKERARGS, BROKERARGS_VALUE)
        };
    samrSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    samr.initialize(samrSpecifier, config);
  }

  @Test
  public void cleanInitialise() {
    // as long as there are no exceptions then pass
  }

  @Test
  public void providesValidConsumer() throws JMSException {
    final String queueName = "test";
    final String messageSelector = "";
    final MessageConsumer consumer = samr.createConsumer(queueName, messageSelector);
    // use producer to test consumer receiving messages
    // get same dest as consumer
    final Destination destination = samr.getSession().createQueue(queueName);
    final Message msg = samr.getSession().createTextMessage("hello, world");
    samr.getProducer().send(destination, msg);
    final Message received = consumer.receive();
    // should get back the same message that was sent
    assertEquals(msg, received);
  }

  @After
  public void afterTest() throws Exception {
    if (samr != null) {
      samr.destroy();
      samr = null;
    }
  }
}
