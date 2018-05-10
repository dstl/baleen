// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.components;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.transports.serialisation.JsonJCasConverter;
import uk.gov.dstl.baleen.transports.serialisation.JsonJCasConverterBuilder;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

/**
 * This class provides basic functionality for a transport collection reader. Such a collection
 * reader receives a serialised JCas from a transport system, such as a message queue. This class
 * provides the common deserialization, implementations should manage the transport by implementing
 * the abstract {@link #createQueue()} {@link #closeQueue()} and {@link #readFromQueue()}.
 *
 * @baleen.javadoc
 */
public abstract class AbstractTransportCollectionReader extends BaleenCollectionReader {

  /** A default value for a transport key parameters to be used by implementations if required. */
  public static final String PARAM_TOPIC_DEFAULT = "transport";

  /**
   * The topic for transport use
   *
   * @baleen.config {@link #PARAM_TOPIC_DEFAULT}
   */
  public static final String PARAM_TOPIC = "topic";

  @ConfigurationParameter(name = PARAM_TOPIC, defaultValue = PARAM_TOPIC_DEFAULT)
  protected String topic;

  /**
   * A list of types to blacklist (full classnames)
   *
   * @baleen.config
   */
  public static final String PARAM_BLACKLIST = "blacklist";

  @ConfigurationParameter(name = PARAM_BLACKLIST, mandatory = false)
  private List<String> blacklist = null;

  /**
   * A list of types to whitelist (full classnames)
   *
   * @baleen.config
   */
  public static final String PARAM_WHITELIST = "whitelist";

  @ConfigurationParameter(name = PARAM_WHITELIST, mandatory = false)
  private List<String> whitelist = null;

  private JsonJCasConverter converter;

  @Override
  protected void doInitialize(final UimaContext context) throws ResourceInitializationException {

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
  public boolean doHasNext() throws IOException, CollectionException {
    // Always assume that more is coming
    return true;
  }

  @Override
  protected void doGetNext(final JCas jCas) throws IOException, CollectionException {
    final String serialized = readFromQueue();
    converter.deserialise(jCas, serialized);
  }

  @Override
  protected void doClose() throws IOException {
    closeQueue();
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
   * Read the next data from the queue
   *
   * @return the data read
   * @throws IOException if reading can not be completed correctly
   */
  protected abstract String readFromQueue() throws IOException;
}
