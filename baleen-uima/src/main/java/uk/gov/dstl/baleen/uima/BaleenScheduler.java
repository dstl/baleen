// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * Base class for Baleen Job Schedulers.
 *
 * <p>A scheduler is something which signals when to run the next job. Since we want the benefits of
 * dependency injection of UimaFit we use consider a collection reader as analagous component (it
 * signals when the next document is ready). Thus under the hood a scheduler is a collection reader.
 *
 * <p>Note that use of collection reader, and Baleen's approach to single threaded pipelines, means
 * that events can not be scheduled at the same time. This is likely not a issue for most uses of
 * the Baleen jobs.
 *
 * <p>Implementors need only provide the await() function. This should block until the next job
 * should be run. If another job should not be run then it should return false, else return true.
 *
 * <p>Await will be called again as soon as the previous job has finished.
 *
 * <p>The scheduler will pass any parameters it has defined as settings (@see {@link BaleenTask}
 * which are effectively carried by the JCas.
 *
 * <p>To implement jobs that run periodically you may wish to derive from the existing @see {@link
 * uk.gov.dstl.baleen.schedules.FixedRate} scheduler.
 *
 * @baleen.javadoc
 */
public abstract class BaleenScheduler extends JCasCollectionReader_ImplBase {

  /** The monitor. */
  private UimaMonitor monitor;

  /** The config. */
  private Map<String, String> config;

  @Override
  public final void initialize(final UimaContext context) throws ResourceInitializationException {
    // This will do initialization of resources,
    // but won't be included in the metrics
    super.initialize(context);

    final String pipelineName = UimaUtils.getPipelineName(context);
    monitor = new UimaMonitor(pipelineName, this.getClass());

    getMonitor().startFunction("initialize");

    // Pull the config parameters out for job settings
    config = BaleenScheduler.getConfigParameters(context);

    doInitialize(context);

    getMonitor().finishFunction("initialize");
  }

  /**
   * Called when the collection reader is being initialized. Any required resources, for example,
   * should be opened at this point.
   *
   * @param context The UimaContext for the collection reader
   * @throws ResourceInitializationException the resource initialization exception
   */
  protected void doInitialize(final UimaContext context) throws ResourceInitializationException {
    // Do nothing by default
  }

  @Override
  public final void getNext(final JCas jCas) throws IOException, CollectionException {
    getMonitor().startFunction("getNext");
    MetricsFactory.getInstance()
        .getPipelineMetrics(monitor.getPipelineName())
        .startDocumentProcess();

    jCas.setDocumentText(JobSettings.class.getSimpleName());
    jCas.setDocumentLanguage("en");

    final JobSettings settings = new JobSettings(jCas);
    for (final Map.Entry<String, String> e : config.entrySet()) {
      settings.set(e.getKey(), e.getValue());
    }

    getMonitor().finishFunction("getNext");
  }

  /**
   * Called when the collection reader has finished and is closing down. Any open resources, for
   * example, should be closed at this point.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void doDestroy() throws IOException {
    // Do nothing
  }

  @Override
  public void destroy() {
    super.destroy();
    try {
      doDestroy();
    } catch (final IOException e) {
      getMonitor().warn("Close on destroy", e);
    }
  }

  @Override
  public Progress[] getProgress() {
    return new Progress[0];
  }

  /**
   * Override of the UIMA hasNext() method with logic to continuously check for new documents until
   * one is found. This prevents the collection reader from exiting (unless asked to), and so
   * creates a persistent collection reader and pipeline.
   *
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CollectionException the collection exception
   */
  @Override
  public final boolean hasNext() throws IOException, CollectionException {
    return await();
  }

  /**
   * Signals time for a new job to run.
   *
   * <p>This method should block until the next job should be run (eg be on a timer).
   *
   * @return true to run the job, otherwise cancel
   */
  protected abstract boolean await();

  /**
   * Gets the monitor.
   *
   * @return the monitor
   */
  protected final UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Create a configuration map from a context.
   *
   * @param context the context
   * @return non-empty map of config param name to config param value
   */
  protected static Map<String, String> getConfigParameters(final UimaContext context) {
    // <String, String> due to limitations of Metadata
    final Map<String, String> ret = new HashMap<>();
    for (final String name : context.getConfigParameterNames()) {
      ret.put(name, context.getConfigParameterValue(name).toString());
    }

    return ret;
  }
}
