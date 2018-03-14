// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history.memory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import uk.gov.dstl.baleen.core.history.AbstractBaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A base class for implementing histories which hold data in memory.
 *
 * <p>It is extremely important that closeHistory() is called (either here or on {@link
 * DocumentHistory} in order to free resources at the end of processing.
 *
 * <p>By default the history is only kept for 24 hours (use set history.timeout in seconds in the
 * global config to control this). After that period the document history will return blank. Still
 * pipelines should not rely on this behaviour as it will consume excessive memory unnecessarily.
 *
 * <p>Thus history.size should be set to the maximum number of documents to be processed
 * concurrently (typically 1 but it depends on threading in annotators and batching) and
 * history.timeout should be set to (a little) longer than the maximum processing time of a single
 * document.
 *
 * <p>Implementors should override the create and load functions. The different being that load
 * existing can throw an exception and/or return null (at which point create will be called to
 * instance a fresh document history).
 *
 * <p>Implementations should ensure that super.initialise/close/destroy are called to ensure the
 * cache is maintained.
 *
 * @baleen.javadoc
 */
public abstract class AbstractCachingBaleenHistory<H extends DocumentHistory>
    extends AbstractBaleenHistory {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCachingBaleenHistory.class);

  /**
   * The amount of time, in seconds, that the history is kept in memory for
   *
   * @baleen.config 86400
   */
  public static final String PARAM_TIMEOUT = "history.timeout";

  @ConfigurationParameter(name = PARAM_TIMEOUT, defaultValue = "86400")
  private String cacheTimeoutString;

  // Parse the cacheTimeout config parameter into this variable to avoid issues with parameter types
  private long cacheTimeout;

  /**
   * The number of documents that can be kept in memory at the same time.
   *
   * @baleen.config 16
   */
  public static final String PARAM_SIZE = "history.size";

  @ConfigurationParameter(name = PARAM_SIZE, defaultValue = "16")
  private long cacheSize;

  private LoadingCache<String, H> cachedHistories;

  @Override
  protected void initialize() throws BaleenException {
    super.initialize();

    cacheTimeout = ConfigUtils.stringToLong(cacheTimeoutString, 86400L);

    cachedHistories =
        CacheBuilder.newBuilder()
            .expireAfterAccess(cacheTimeout, TimeUnit.SECONDS)
            .maximumSize(cacheSize)
            .build(
                new CacheLoader<String, H>() {

                  @Override
                  public H load(String documentId) throws Exception {
                    H dh = null;
                    try {
                      dh = loadExistingDocumentHistory(documentId);
                    } catch (Exception e) {
                      LOGGER.error(
                          "Loading caused error, so using a fresh history. This will likely overwrite the original history",
                          e);
                    }

                    if (dh == null) {
                      dh = createNewDocumentHistory(documentId);
                    }
                    return dh;
                  }
                });
  }

  @Override
  public synchronized DocumentHistory getHistory(String documentId) {
    try {
      return cachedHistories.get(documentId);
    } catch (ExecutionException e) {
      LOGGER.error("Cache threw exception, this should not happen!", e);
      // Fake the same result we'd expect from the cache
      H dh = createNewDocumentHistory(documentId);
      cachedHistories.put(documentId, dh);
      return dh;
    }
  }

  /**
   * Create a new history for the document id.
   *
   * @param documentId the document owning the history
   * @return non-null history
   */
  protected abstract H createNewDocumentHistory(String documentId);

  /**
   * Load a history for the document id.
   *
   * @param documentId the document to load
   * @return null if the history doesn't exist, otherwise a valid history
   */
  protected abstract H loadExistingDocumentHistory(String documentId) throws BaleenException;

  protected H getCachedHistoryIfPresent(String documentId) {
    return cachedHistories.getIfPresent(documentId);
  }

  @Override
  public synchronized void closeHistory(String documentId) {
    LOGGER.info("Deleting history for document {}", documentId);
    cachedHistories.invalidate(documentId);
  }

  @Override
  public void destroy() {
    super.destroy();
    if (cachedHistories != null) {
      cachedHistories.invalidateAll();
      cachedHistories = null;
    }
  }
}
