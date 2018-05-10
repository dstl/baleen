// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.activemq;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;

public class ActiveMQResourceIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQResourceIT.class);

  private static final int MESSAGE_COUNT = 100;
  private static final int ACTIVEMQ_PORT = 61616;
  private static final int ACTIVEMQ_ADMIN_PORT = 8161;

  private static final String QUEUE = "queue";

  private static final String IMAGE = "webcenter/activemq:latest";

  @ClassRule
  @SuppressWarnings("rawtypes")
  public static GenericContainer container =
      new GenericContainer(IMAGE).withExposedPorts(ACTIVEMQ_PORT, ACTIVEMQ_ADMIN_PORT);

  @Test
  public void testResourceCanSendAndRecieve()
      throws ResourceInitializationException, IOException, InterruptedException, BaleenException,
          JMSException {

    SharedActiveMQResource resource = new SharedActiveMQResource();
    final CustomResourceSpecifier_impl resourceSpecifier = new CustomResourceSpecifier_impl();
    // @formatter:off
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedActiveMQResource.PARAM_HOST, container.getContainerIpAddress()),
          new Parameter_impl(
              SharedActiveMQResource.PARAM_PORT,
              Integer.toString(container.getMappedPort(ACTIVEMQ_PORT)))
        };
    // @formatter:on

    resourceSpecifier.setParameters(configParams);

    final Map<String, Object> config = Maps.newHashMap();
    resource.initialize(resourceSpecifier, config);

    final MessageProducer producer = resource.getProducer();
    final Session session = resource.getSession();
    final Queue destination = session.createQueue(QUEUE);
    final MessageConsumer consumer = resource.createConsumer(QUEUE, null);

    final CountDownLatch latch = new CountDownLatch(MESSAGE_COUNT);

    new Thread(
            () -> {
              int count = 0;
              while (count < MESSAGE_COUNT) {
                try {
                  final TextMessage message = (TextMessage) consumer.receive();
                  count++;
                  LOGGER.info("Message recieved: " + message.getText());
                } catch (JMSException e) {
                  fail(e.getMessage());
                }
                latch.countDown();
              }
            })
        .start();

    new Thread(
            () ->
                IntStream.range(0, MESSAGE_COUNT)
                    .mapToObj(Integer::toString)
                    .map(
                        (i) -> {
                          try {
                            return session.createTextMessage(i);
                          } catch (JMSException e) {
                            fail(e.getMessage());
                            return null;
                          }
                        })
                    .forEach(
                        (message) -> {
                          try {
                            producer.send(destination, message);
                            LOGGER.info("Message sent:" + message.getText());
                          } catch (JMSException e) {
                            fail(e.getMessage());
                          }
                        }))
        .start();

    assertTrue("Messages not recieved", latch.await(2, TimeUnit.MINUTES));
  }
}
