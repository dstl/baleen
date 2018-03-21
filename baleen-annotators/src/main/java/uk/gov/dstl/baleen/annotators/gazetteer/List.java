// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.gazetteer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractAhoCorasickAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.ListGazetteer;

/**
 * Generic list-backed RadixTree Gazetteer annotator, that will use a list based gazetteer to find
 * and annotate entities.
 *
 * @baleen.javadoc
 */
public class List extends AbstractAhoCorasickAnnotator {

  /**
   * The terms to be used in the gazetteer, each term is expected to be have aliases comma-separated
   * (by default), to use as the gazetteer
   *
   * @baleen.config gazetteer.txt
   */
  public static final String PARAM_TERMS = "terms";

  @ConfigurationParameter(name = PARAM_TERMS)
  private String[] terms;

  /**
   * An alias term separator string that will override the "," default value
   *
   * @baleen.config ,
   */
  public static final String PARAM_TERM_SEPARATOR = "termSeparator";

  @ConfigurationParameter(name = PARAM_TERM_SEPARATOR, defaultValue = ",")
  private String termSeparator;

  /** Constructor */
  public List() {
    // Do nothing
  }

  @Override
  public IGazetteer configureGazetteer() throws BaleenException {
    Map<String, Object> config = new HashMap<>();
    config.put(ListGazetteer.CONFIG_CASE_SENSITIVE, caseSensitive);
    config.put(ListGazetteer.CONFIG_TERMS, ImmutableList.copyOf(terms));
    config.put(ListGazetteer.CONFIG_TERM_SEPARATOR, termSeparator);

    IGazetteer gaz = new ListGazetteer();
    gaz.init(null, config);

    return gaz;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(entityType));
  }
}
