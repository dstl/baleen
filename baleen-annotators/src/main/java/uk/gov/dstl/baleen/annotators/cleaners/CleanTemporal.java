// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * This cleaner performs the following actions on all Temporal entitites:
 *
 * <ul>
 *   <li>For any Temporal entity with timestamps set, checks to see if the timestamp refers to a
 *       date more than x years in the past/future and if so removes it.
 *   <li>Removes any Temporal entity that begins with a currency sign (this seems to be a common
 *       error in the OpenNLP NER model for dates).
 *   <li>Optionally removes any timestamp with a value of 0L (generally this means the timestamp has
 *       not been set)
 * </ul>
 *
 * @baleen.javadoc
 */
public class CleanTemporal extends BaleenAnnotator {
  LocalDateTime start = null;
  LocalDateTime end = null;

  /**
   * The number of years before or after the current date that we consider to be valid
   *
   * @baleen.config 50
   */
  public static final String PARAM_YEARS = "years";

  @ConfigurationParameter(name = PARAM_YEARS, defaultValue = "50")
  private String yearsString;

  /**
   * Remove entities with a timestamp set to 0L.
   *
   * <p>Generally, a timestamp will be 0L if it hasn't been set, but it will also be 0L if the
   * timestamp represents 1970-01-01 00:00:00 and therefore valid data may be removed.
   *
   * @baleen.config removeZeroTimestamp true
   */
  public static final String PARAM_REMOVE_ZERO_TIMESTAMP = "removeZeroTimestamp";

  @ConfigurationParameter(name = PARAM_REMOVE_ZERO_TIMESTAMP, defaultValue = "false")
  private Boolean removeZeroTimestamp;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    int years = ConfigUtils.stringToInteger(yearsString, 50);
    start = LocalDateTime.now().minusYears(years);
    end = LocalDateTime.now().plusYears(years);
  }

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    Collection<Temporal> tempora = JCasUtil.select(jCas, Temporal.class);
    List<Entity> toRemove = new ArrayList<>();

    for (Temporal t : tempora) {
      if (zeroTimestamp(t)
          || isMoney(t.getValue())
          || isMoney(t.getCoveredText())
          || isOutsideRange(t)) {
        toRemove.add(t);
        continue;
      }
    }

    removeFromJCasIndex(toRemove);
  }

  private boolean zeroTimestamp(Temporal t) {
    return removeZeroTimestamp && (t.getTimestampStart() == 0L || t.getTimestampStop() == 0L);
  }

  private boolean isOutsideRange(Temporal t) {
    if (t.getTimestampStart() != 0L
        && t.getTimestampStart() < start.toEpochSecond(ZoneOffset.UTC)) {
      return true;
    }

    return t.getTimestampStop() != 0L && t.getTimestampStop() > end.toEpochSecond(ZoneOffset.UTC);
  }

  private boolean isMoney(String text) {
    if (Strings.isNullOrEmpty(text)) {
      return false;
    }

    return text.startsWith("£") || text.startsWith("$") || text.startsWith("€");
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Temporal.class), Collections.emptySet());
  }
}
