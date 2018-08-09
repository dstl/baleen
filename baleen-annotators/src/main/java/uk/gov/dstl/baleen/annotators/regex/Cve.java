// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Vulnerability;

/**
 * Extracts CVE (Common Vulnerabilities and Exposures) references from text using a regular
 * expression, and annotate them as Vulnerability entities.
 *
 * @baleen.javadoc
 */
public class Cve extends AbstractRegexAnnotator<Vulnerability> {

  /** New instance. */
  public Cve() {
    super("\\bCVE-[0-9]{4}-[0-9]+\\b", false, 1.0);
  }

  @Override
  protected Vulnerability create(JCas jCas, Matcher matcher) {
    return new Vulnerability(jCas);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Vulnerability.class));
  }
}
