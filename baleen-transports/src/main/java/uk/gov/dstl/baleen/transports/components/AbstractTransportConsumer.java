// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.components;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.transports.serialisation.JsonJCasConverter;
import uk.gov.dstl.baleen.transports.serialisation.JsonJCasConverterBuilder;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * This class provides basic functionality for a transport consumer. Such a consumer is responsible
 * for publishing the serialised JCas to a transport system, such as a message queue. This class
 * provides the common serialization, implementations should manage the transport by implementing
 * the abstract {@link #createQueue()} {@link #closeQueue()} and {@link #writeToQueue(String,
 * String)}.
 *
 * <p>It also supports providing an optional whitelist or blacklist of types to filter the
 * transported types.
 *
 * <p>A simple throttling mechanism is provided using the queue capacity and the getQueueLength
 * method, implemented with exponential backoff. This process is not threadsafe, so if your queue
 * has a hard maximum capacity it must be check in you implementation.
 *
 * @baleen.javadoc
 */
public abstract class AbstractTransportConsumer extends BaleenConsumer {

  /**
   * The topic for transport use
   *
   * @baleen.config {@link AbstractTransportCollectionReader#PARAM_TOPIC_DEFAULT}
   */
  public static final String PARAM_TOPIC = "topic";

  @ConfigurationParameter(
      name = PARAM_TOPIC,
      defaultValue = AbstractTransportCollectionReader.PARAM_TOPIC_DEFAULT)
  protected String topic;

  /**
   * A list of types to blacklist (optionally including package)
   *
   * @baleen.config
   */
  public static final String PARAM_BLACKLIST = "blacklist";

  @ConfigurationParameter(name = PARAM_BLACKLIST, mandatory = false)
  private List<String> blacklist = null;

  /**
   * A list of types to whitelist (optionally including package)
   *
   * @baleen.config
   */
  public static final String PARAM_WHITELIST = "whitelist";

  @ConfigurationParameter(name = PARAM_WHITELIST, mandatory = false)
  private List<String> whitelist = null;

  /**
   * The optional capacity of the blocking queue. This can be set larger if working with small
   * documents.
   *
   * @baleen.config
   */
  public static final String QUEUE_CAPACITY = "capacity";

  @ConfigurationParameter(name = QUEUE_CAPACITY, mandatory = false)
  private String capacity;

  private JsonJCasConverter converter;

  private int queueCapacity;

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    queueCapacity = ConfigUtils.stringToInteger(capacity, getDefaultCapacity());

    converter =
        new JsonJCasConverterBuilder(getMonitor())
            .withWhitelist(whitelist)
            .withBlacklist(blacklist)
            .build();
    try {

      createQueue();
    } catch (final BaleenException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void doDestroy() {
    try {
      closeQueue();
    } catch (IOException e) {
      getMonitor().warn("Error closing queue", e);
    }
    super.doDestroy();
  }

  /** @return the default capacity for the queue */
  protected int getDefaultCapacity() {
    return Integer.MAX_VALUE;
  }

  private void waitForQueueToBeBelowCapacity() {
    int n = 0;
    while (getQueueLength() > queueCapacity) {
      try {
        Thread.sleep((int) (Math.round(Math.pow(2, n++)) * 100));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

    try {
      final DocumentAnnotation documentAnnotation = getDocumentAnnotation(jCas);
      final String serialized = converter.serialise(jCas);
      waitForQueueToBeBelowCapacity();
      writeToQueue(documentAnnotation.getHash(), serialized);
    } catch (final IOException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  /**
   * Create the Queue for the transport of the data
   *
   * @throws BaleenException if the queue can not be created correctly
   */
  protected abstract void createQueue() throws BaleenException;

  /**
   * Close the queue and release and resources
   *
   * @throws IOException if the queue can not be closed correctly
   */
  protected abstract void closeQueue() throws IOException;

  /**
   * Write the given data to the queue
   *
   * @param id the id
   * @param data to write
   * @throws IOException if writing fails
   */
  protected abstract void writeToQueue(String id, String data) throws IOException;

  /**
   * Get the current length of the queue. This is used, with the capacity to determine if we should
   * backoff writing to the queue.
   *
   * @return the current length of the queue or 0 if this can not be determined.
   */
  protected abstract int getQueueLength();
}
