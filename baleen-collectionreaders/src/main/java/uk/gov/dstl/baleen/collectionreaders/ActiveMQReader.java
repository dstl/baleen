//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * This collection reader will process all waiting messages available on the
 * ActiveMQ message broker, and then watch for new messages.
 *
 * <p>
 * Currently limited to JMS messages of type {@link javax.jms.TextMessage}
 * </p>
 *
 *
 * @baleen.javadoc
 */
public class ActiveMQReader extends BaleenCollectionReader {

	/**
	 * Connection to ActiveMQ
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedActiveMQResource
	 */
	public static final String KEY_ACTIVEMQ = "activemq";
	@ExternalResource(key = KEY_ACTIVEMQ)
	SharedActiveMQResource activeMQ;

	/**
	 * The ActiveMQ endpoint - queue or VirtualTopic - to read data from
	 *
	 * @baleen.config input
	 */
	public static final String PARAM_ENDPOINT = "endpoint";
	@ConfigurationParameter(name = PARAM_ENDPOINT, defaultValue = "input")
	private String endpoint;

	/**
	 * The message selector with which to filter messages
	 *
	 * @baleen.config
	 */
	public static final String PARAM_MESSAGE_SELECTOR = "messageSelector";
	@ConfigurationParameter(name = PARAM_MESSAGE_SELECTOR, defaultValue = "")
	private String messageSelector;

	/**
	 * The content extractor to use to extract content from files
	 * 
	 * @baleen.config Value of BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
	 */
	public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
	@ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue=BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR)
	private String contentExtractor;

	private IContentExtractor extractor;

	private MessageConsumer consumer;

	@Override
	protected void doInitialize(final UimaContext context) throws ResourceInitializationException {
		try {
			this.extractor = getContentExtractor(contentExtractor);
		} catch (final InvalidParameterException ipe) {
			throw new ResourceInitializationException(ipe);
		}
		this.extractor.initialize(context, getConfigParameters(context));

		try {
			this.consumer = activeMQ.createConsumer(endpoint, messageSelector);
		} catch (final JMSException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	protected void doGetNext(final JCas jCas) throws IOException, CollectionException {
		final String source = String.join(".", activeMQ.getResourceName(), endpoint);

		try {
			final Message msg = this.consumer.receive();
			if (msg instanceof TextMessage) {
				final String text = ((TextMessage) msg).getText();
				this.extractor.processStream(new ByteArrayInputStream(text.getBytes(Charset.defaultCharset())), source, jCas);
			} else {
				throw new IOException(String.format("Unexpected message type for message with id %1 from source %2",
						msg.getJMSMessageID(), source));
			}
		} catch (final JMSException e) {
			throw new CollectionException(e);
		}

	}

	@Override
	protected void doClose() throws IOException {
		try {
			this.consumer.close();
		} catch (final JMSException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean doHasNext() throws IOException, CollectionException {
		try {
			return activeMQ.createQueueBrowser(endpoint, messageSelector).getEnumeration().hasMoreElements();
		} catch (final JMSException e) {
			throw new CollectionException(e);
		}
	}

}