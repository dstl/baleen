// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.util.function.Consumer;

/**
 * Consumer interface to RabbitMQ.
 *
 * <p>Consumes byte arrays and allow the queue to be closed
 */
public interface RabbitMQConsumer extends RabbitMQQueue, Consumer<byte[]> {}
