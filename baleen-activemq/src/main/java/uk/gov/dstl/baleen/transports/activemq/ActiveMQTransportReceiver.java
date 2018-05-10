// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.activemq;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportCollectionReader;

/**
 * A transport collection reader using ActiveMQ.
 *
 * <p>Requires a {@link SharedActiveMQResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class ActiveMQTransportReceiver extends AbstractTransportCollectionReader {

  @ExternalResource(key = SharedActiveMQResource.RESOURCE_KEY)
  private SharedActiveMQResource mqResource;

  private MessageConsumer consumer;

  @Override
  protected void createQueue() throws BaleenException {
    try {
      consumer = mqResource.createConsumer(topic, null);
    } catch (final JMSException e) {
      throw new BaleenException(e);
    }
  }

  @Override
  protected void closeQueue() throws IOException {
    if (consumer != null) {
      try {
        consumer.close();
      } catch (final JMSException e) {
        throw new IOException(e);
      } finally {
        consumer = null;
      }
    }
  }

  @Override
  protected String readFromQueue() throws IOException {
    try {
      final Message message = consumer.receive();
      if (message instanceof TextMessage) {
        final TextMessage textMessage = (TextMessage) message;
        return textMessage.getText();
      } else {
        throw new IOException("Unsupport message type " + message.getClass().getSimpleName());
      }
    } catch (final JMSException e) {
      throw new IOException(e);
    }
  }
}
