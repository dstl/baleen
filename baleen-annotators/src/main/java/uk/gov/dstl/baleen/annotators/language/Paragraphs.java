// Dstl (c) Crown Copyright 2018

package uk.gov.dstl.baleen.annotators.language;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Paragraph;

/**
 * Uses a simple Regular Expression to annotate paragraphs as {@link Paragraph}s so that subsequent
 * annotators may process text paragraph by paragraph
 *
 * @baleen.javadoc
 */
public class Paragraphs extends AbstractRegexAnnotator<Paragraph> {

  /** New instance. */
  public Paragraphs() {
    super("((?<=\\r?\\n)[^\\r\\n]+|((?<=^)[^\\r\\n]+))", false, 1.0);
  }

  @Override
  protected Paragraph create(JCas jCas, Matcher matcher) {
    return new Paragraph(jCas);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Paragraph.class));
  }
}
