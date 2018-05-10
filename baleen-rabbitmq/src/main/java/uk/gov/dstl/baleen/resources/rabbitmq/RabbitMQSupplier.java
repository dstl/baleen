// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.util.function.Supplier;

/**
 * Supplier interface to RabbitMQ.
 *
 * <p>Supplies byte arrays and allow the queue to be closed
 */
public interface RabbitMQSupplier extends RabbitMQQueue, Supplier<byte[]> {}
