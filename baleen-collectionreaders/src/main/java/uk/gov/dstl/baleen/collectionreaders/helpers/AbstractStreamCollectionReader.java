//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders.helpers;

import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A collection reader that is generated as a stream.
 *
 * @param <T>
 *            the generic type
 *
 * @baleen.javadoc
 */
public abstract class AbstractStreamCollectionReader<T> extends AbstractIteratatorCollectionReader<T> {

	/**
	 * Max number of documents to read before stopping (0 for all)
	 *
	 * @baleen.config max 0
	 */
	public static final String KEY_MAX_DOCUMENTS = "max";
	@ConfigurationParameter(name = KEY_MAX_DOCUMENTS, defaultValue = "0")
	private int maxDocuments;

	/**
	 * Skip the first x documents
	 *
	 * @baleen.config skip 0
	 */
	public static final String KEY_SKIP_DOCUMENTS = "skip";
	@ConfigurationParameter(name = KEY_SKIP_DOCUMENTS, defaultValue = "0")
	private int skipDocuments;

	protected void setSkipDocuments(int skipDocuments) {
		this.skipDocuments = skipDocuments;
	}

	protected Integer getSkipDocuments() {
		return skipDocuments;
	}

	protected Integer getMaxDocuments() {
		return maxDocuments;
	}

	protected void setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
	}

	@Override
	protected final Iterator<T> initializeIterator(UimaContext context) throws BaleenException {

		Stream<T> stream = initializeStream(context);

		if (skipDocuments > 0) {
			stream = stream.skip(skipDocuments);
		}

		if (maxDocuments > 0) {
			stream = stream.limit(maxDocuments);
		}

		return stream.iterator();
	}

	/**
	 * Initialize the stream.
	 *
	 * @param context
	 *            the context
	 * @return the stream
	 * @throws BaleenException
	 *             the baleen exception
	 */
	protected abstract Stream<T> initializeStream(UimaContext context) throws BaleenException;

}