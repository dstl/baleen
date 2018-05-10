// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.activemq;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportConsumer;

/**
 * A transport consumer using ActiveMQ.
 *
 * <p>Requires a {@link SharedActiveMQResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class ActiveMQTransportSender extends AbstractTransportConsumer {

  @ExternalResource(key = "mqResource")
  private SharedActiveMQResource mqResource;

  private MessageProducer producer;

  private Session session;

  private Queue destination;

  @Override
  protected void createQueue() throws BaleenException {
    try {
      producer = mqResource.getProducer();
      session = mqResource.getSession();
      destination = session.createQueue(topic);
    } catch (final JMSException e) {
      throw new BaleenException(e);
    }
  }

  @Override
  protected void closeQueue() throws IOException {
    if (producer != null) {
      try {
        producer.close();
      } catch (final JMSException e) {
        throw new IOException(e);
      } finally {
        producer = null;
      }
    }
  }

  @Override
  protected void writeToQueue(final String id, final String jCas) throws IOException {
    try {
      final TextMessage message = session.createTextMessage(jCas);
      producer.send(destination, message);
    } catch (final JMSException e) {
      throw new IOException(e);
    }
  }

  @Override
  protected int getQueueLength() {
    return 0;
  }
}
