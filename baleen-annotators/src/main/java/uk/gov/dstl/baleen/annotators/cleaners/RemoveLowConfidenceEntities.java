// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Remove entities which have a low confidence
 *
 * <p>All entities are looped through, and should an entity be found that has a confidence below a
 * user specified threshold. The user can choose to ignore 0 confidence entities in this process,
 * which are likely to be entities that don't have a confidence set.
 *
 * @baleen.javadoc
 */
public class RemoveLowConfidenceEntities extends BaleenAnnotator {

  /**
   * The confidence threshold, below which entities will be removed
   *
   * @baleen.config 0.0
   */
  public static final String PARAM_THRESHOLD = "confidenceThreshold";

  @ConfigurationParameter(name = PARAM_THRESHOLD, defaultValue = "0.0")
  private String confidenceThresholdString;

  // Parse the confidenceThreshold config parameter into this variable to avoid issues with
  // parameter types
  private Float confidenceThreshold;

  /**
   * Should entities with 0 confidence, usually indicative that it hasn't been set, be ignored?
   *
   * @baleen.config true
   */
  public static final String PARAM_IGNORE_ZERO = "ignoreZeroConfidence";

  @ConfigurationParameter(name = PARAM_IGNORE_ZERO, defaultValue = "true")
  private Boolean ignoreZeroConfidence;

  /** Initialise the annotator */
  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    confidenceThreshold = ConfigUtils.stringToFloat(confidenceThresholdString, 0.0f);
  }

  @Override
  public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
    List<Entity> toRemove = new ArrayList<Entity>();

    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Entity.type).iterator();
    while (iter.hasNext()) {
      Entity e = (Entity) iter.next();

      if (e.getConfidence() < confidenceThreshold
          && (!ignoreZeroConfidence || e.getConfidence() > 0.0)) {
        toRemove.add(e);
        getMonitor()
            .debug(
                "Low confidence entity found (ID: {}) - this entity will be removed",
                e.getInternalId());
      }
    }

    removeFromJCasIndex(toRemove);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Entity.class), Collections.emptySet());
  }
}
