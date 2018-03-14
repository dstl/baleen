// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders.helpers;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

/**
 * A collection reader that can be represented by an iterator, where each call to next() generates a
 * new document.
 *
 * @param <T> the generic type
 */
public abstract class AbstractIteratatorCollectionReader<T> extends BaleenCollectionReader {

  private Iterator<T> iterator;

  @Override
  protected final void doInitialize(UimaContext context) throws ResourceInitializationException {
    try {
      iterator = initializeIterator(context);
    } catch (final BaleenException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected final void doGetNext(JCas jCas) throws IOException, CollectionException {
    final T next = iterator.next();
    apply(next, jCas);
  }

  @Override
  public final boolean doHasNext() throws IOException, CollectionException {
    return iterator.hasNext();
  }

  /**
   * Create the iterator (called once)
   *
   * @param context the context
   * @return the iterator
   * @throws BaleenException the baleen exception
   */
  protected abstract Iterator<T> initializeIterator(UimaContext context) throws BaleenException;

  /**
   * Convert the return item from the iterator to a jcas.
   *
   * @param next the next
   * @param jCas the j cas
   */
  protected abstract void apply(T next, JCas jCas);
}
