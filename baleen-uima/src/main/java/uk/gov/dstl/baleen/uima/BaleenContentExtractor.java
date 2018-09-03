// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;
import uk.gov.dstl.baleen.core.pipelines.content.ContentExtractor;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * Base implementation of a ContentExtractor.
 *
 * <p>This abstract class provides the basis for content extractors. It provides metrics and support
 * elements to help development.
 *
 * <p>Implementors should look to override doProcessStream as per {@link ContentExtractor}
 * processFile.
 */
public abstract class BaleenContentExtractor extends Resource_ImplBase implements ContentExtractor {
  private UimaMonitor monitor;
  private UimaSupport support;

  /**
   * Baleen History resource
   *
   * @baleen.resource uk.gov.dstl.baleen.core.history.BaleenHistory
   */
  public static final String KEY_HISTORY = PipelineBuilder.BALEEN_HISTORY;

  @ExternalResource(key = KEY_HISTORY, mandatory = false)
  BaleenHistory history;

  @Override
  public final boolean initialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    boolean result = super.initialize(specifier, additionalParams);

    UimaContext context = getUimaContext();
    String pipelineName = UimaUtils.getPipelineName(context);
    monitor = createMonitor(pipelineName);
    support = createSupport(pipelineName, context);
    monitor.startFunction("initialize");

    doInitialize(context, additionalParams);

    monitor.finishFunction("initialize");

    return result;
  }

  protected UimaSupport createSupport(String pipelineName, UimaContext context) {
    return new UimaSupport(
        pipelineName,
        this.getClass(),
        history,
        monitor,
        UimaUtils.isMergeDistinctEntities(context));
  }

  protected UimaMonitor createMonitor(String pipelineName) {
    return new UimaMonitor(pipelineName, this.getClass());
  }

  /**
   * Called when the content extractor is being initialized. Any required resources, for example,
   * should be opened at this point.
   *
   * @param context UimaContext object passed by the Collection Processing Engine
   */
  public abstract void doInitialize(UimaContext context, Map<String, Object> params)
      throws ResourceInitializationException;

  @Override
  public final void processStream(InputStream stream, String source, JCas jCas) throws IOException {
    monitor.startFunction("process");

    doProcessStream(stream, source, jCas);

    monitor.finishFunction("process");
    monitor.persistCounts();
  }

  /**
   * Called when the content extractor is being asked to process an inputstream and extract the
   * content.
   *
   * @param stream InputStream to process
   * @param jCas JCas to add content to
   */
  protected abstract void doProcessStream(InputStream stream, String source, JCas jCas)
      throws IOException;

  @Override
  public final void destroy() {
    monitor.startFunction("destroy");

    doDestroy();

    monitor.finishFunction("destroy");
  }

  /**
   * Called when the content extractor has finished and is closing down. Any open resources, for
   * example, should be closed at this point.
   */
  protected abstract void doDestroy();

  /**
   * Gets the UimaMonitor object associated with this ContentExtractor, for instance to log errors.
   *
   * @return UimaMonitor object
   */
  protected UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Gets the UimaSupport object associated with this ContentExtractor, for instance to log errors.
   *
   * @return UimaSupport object
   */
  protected UimaSupport getSupport() {
    return support;
  }

  // Common Support functions for quick access

  /**
   * Return the document annotation.
   *
   * @param jCas
   * @return the document annotation
   */
  protected DocumentAnnotation getDocumentAnnotation(JCas jCas) {
    return UimaSupport.getDocumentAnnotation(jCas);
  }

  /**
   * Add an annotation to the JCas index, notifying UimaMonitor of the fact we have done so
   *
   * @param annotations Annotation(s) to add
   */
  protected void addToJCasIndex(Annotation... annotations) {
    getSupport().add(annotations);
  }

  /**
   * Add an annotation to the JCas index, notifying UimaMonitor of the fact we have done so
   *
   * @param annotations Annotation(s) to add
   */
  protected void addToJCasIndex(Collection<? extends Annotation> annotations) {
    getSupport().add(annotations);
  }

  /**
   * Adds a metadata annotation to the JCas
   *
   * @param jCas The JCas object to add the annotation to
   * @param name The metadata key
   * @param value The metadata value
   */
  protected Metadata addMetadata(JCas jCas, String name, String value) {
    if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(value)) {
      Metadata md = new Metadata(jCas);

      md.setKey(name);
      md.setValue(value);

      addToJCasIndex(md);

      return md;
    }

    return null;
  }
}
