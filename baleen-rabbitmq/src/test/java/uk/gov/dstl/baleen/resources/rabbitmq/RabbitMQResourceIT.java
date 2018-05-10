// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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

/** Integration Test requires Docker */
public class RabbitMQResourceIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQResourceIT.class);

  private static final int AMPQ_PORT = 5672;
  private static final int MANAGEMENT_PORT = 15672;
  private static final String QUEUE = "queue";
  private static final String ROUTE = "route";
  private static final String EXCHANGE = "exchange";
  private static final String DEFAULT_RABBIT_USER = "guest";
  private static final String DEFAULT_RABBIT_PASS = "guest";

  private static final String IMAGE = "rabbitmq:3-management";

  @ClassRule
  @SuppressWarnings("rawtypes")
  public static GenericContainer container =
      new GenericContainer(IMAGE)
          .withExposedPorts(AMPQ_PORT)
          .withExposedPorts(MANAGEMENT_PORT)
          .withStartupTimeout(Duration.ofSeconds(120));

  @Test
  public void testResourceCanSendAndRecieve()
      throws ResourceInitializationException, IOException, InterruptedException {

    SharedRabbitMQResource resource = new SharedRabbitMQResource();
    final CustomResourceSpecifier_impl resourceSpecifier = new CustomResourceSpecifier_impl();
    // @formatter:off
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedRabbitMQResource.PARAM_HOST, container.getContainerIpAddress()),
          new Parameter_impl(
              SharedRabbitMQResource.PARAM_PORT,
              Integer.toString(container.getMappedPort(AMPQ_PORT))),
          new Parameter_impl(SharedRabbitMQResource.PARAM_USER, DEFAULT_RABBIT_USER),
          new Parameter_impl(SharedRabbitMQResource.PARAM_PASS, DEFAULT_RABBIT_PASS)
        };
    // @formatter:on

    LOGGER.info("RabbitMQ management port: " + container.getMappedPort(MANAGEMENT_PORT));
    resourceSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    resource.initialize(resourceSpecifier, config);

    final CountDownLatch latch = new CountDownLatch(10);
    final RabbitMQSupplier supplier = resource.createSupplier(EXCHANGE, ROUTE, QUEUE);
    final RabbitMQConsumer consumer = resource.createConsumer(EXCHANGE, ROUTE, QUEUE);

    new Thread(
            () ->
                IntStream.range(0, 10)
                    .forEachOrdered(
                        (i) -> {
                          byte[] bs = supplier.get();
                          LOGGER.info("Message received: " + new String(bs));
                          latch.countDown();
                        }))
        .start();

    new Thread(
            () ->
                IntStream.range(0, 10)
                    .mapToObj(Integer::toString)
                    .peek(i -> LOGGER.info("Message sent: " + i))
                    .map(String::getBytes)
                    .forEach(consumer::accept))
        .start();

    assertTrue("Messages not recieved", latch.await(1, TimeUnit.MINUTES));
  }
}
